package com.example.saveeats.ui.home

import android.content.Context
import android.location.Geocoder
import java.util.Locale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saveeats.data.models.Offer
import com.example.saveeats.data.repository.ProfileRepository
import com.example.saveeats.data.repository.RestCardRepository
import com.example.saveeats.data.repository.CartRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.lang.Exception

class HomeViewModel : ViewModel() {

    private val offersRepository = RestCardRepository()
    private val profileRepository = ProfileRepository()

    private val _currentRadiusFilter = MutableStateFlow(10)
    val currentRadiusFilter = _currentRadiusFilter.asStateFlow()

    private var currentUserLat: Double? = null
    private var currentUserLon: Double? = null
    fun applyRadiusFilter(radiusKm: Int, userLat:Double, userLon:Double) {


        _currentRadiusFilter.value = radiusKm
        currentUserLat = userLat
        currentUserLon = userLon
        
        // Обновляем дистанции в корзине
        CartRepository.updateDistances(userLat, userLon)
        
        loadData()
    }


    private val _allOffers = MutableStateFlow<List<Offer>>(emptyList())
    private val _filteredOffers = MutableStateFlow<List<Offer>>(emptyList())
    private val _favoriteBusinessIds = MutableStateFlow<Set<Int>>(emptySet())

    val favoriteBusinessIds: StateFlow<Set<Int>> = _favoriteBusinessIds.asStateFlow()
    val offers: StateFlow<List<Offer>> = _filteredOffers.asStateFlow()


    val groupedOffers: StateFlow<Map<Int, List<Offer>>> = combine(
        _filteredOffers,
        _favoriteBusinessIds
    ) { offers, favorites ->
        offers.groupBy { it.business.id }
            .toList()
            .sortedWith(compareBy(
                // 1. Сначала те, у кого есть хотя бы один доступный оффер (boxesLeft > 0)
                // false (0) пойдет выше чем true (1)
                { pair -> pair.second.all { it.boxesLeft == 0 } },
                // 2. Затем те, кто в избранном
                // false (0) пойдет выше чем true (1)
                { pair -> !favorites.contains(pair.first) }
            ))
            .toMap()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyMap()
    )


    private var _userAdress = MutableStateFlow("Загрузка адреса...")
    val userAdress: StateFlow<String> = _userAdress.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadData()
    }

     fun loadData() {
        viewModelScope.launch {
            // Загружаем избранные заведения
            try {
                val favorites = profileRepository.getFavorites()
                _favoriteBusinessIds.value = favorites.map { it.id }.toSet()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Если у нас уже есть координаты пользователя, передадим их в репозиторий
            val rawOffers = if (currentUserLat != null && currentUserLon != null) {
                offersRepository.getRestCards(
                    lat = currentUserLat!!,
                    lon = currentUserLon!!,
                    radiusKm = _currentRadiusFilter.value.toDouble()
                )
            } else {
                offersRepository.getRestCards()
            }


            _allOffers.value = rawOffers.sortedBy { it.boxesLeft == 0 }
            updateFilteredOffers()

            try {
                val profile = profileRepository.getProfile()

            } catch (e: Exception) {
                _userAdress.value = "Адрес не найден"
            }
        }
    }

    fun toggleFavorite(businessId: Int) {
        viewModelScope.launch {
            try {
                val isFavorite = profileRepository.toggleFavorite(businessId)
                val currentFavorites = _favoriteBusinessIds.value.toMutableSet()
                if (isFavorite) {
                    currentFavorites.add(businessId)
                } else {
                    currentFavorites.remove(businessId)
                }
                _favoriteBusinessIds.value = currentFavorites
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        updateFilteredOffers()
    }



    fun applyRadiusAndGetAddress(context: Context, radiusKm: Int, lat: Double, lon: Double) {

        applyRadiusFilter(radiusKm, lat, lon)


        viewModelScope.launch(Dispatchers.IO) {
            try {

                val geocoder = Geocoder(context, Locale("ru", "RU"))


                val addresses = geocoder.getFromLocation(lat, lon, 1)

                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]


                    val city = address.locality ?: ""
                    val street = address.thoroughfare ?: ""


                    val displayAddress = if (street.isNotEmpty() && city.isNotEmpty()) {
                        "$city, $street"
                    } else if (city.isNotEmpty()) {
                        city
                    } else {
                        "Адрес определен"
                    }

                    _userAdress.value = displayAddress
                } else {
                    _userAdress.value = "Адрес не найден"
                }
            } catch (e: Exception) {

                _userAdress.value = "Координаты получены"
            }
        }
    }

    private fun updateFilteredOffers() {
        val currentQuery = _searchQuery.value.trim()
        val currentRadius = _currentRadiusFilter.value

        // 1. Сначала рассчитываем дистанцию для ВСЕХ офферов
        val offersWithDistance = _allOffers.value.map { offer ->
            var distanceInKm = offer.business.distance_km
            
            // Если дистанция от сервера 0.0 и у нас есть координаты, пробуем рассчитать локально
            if (distanceInKm == 0.0 && currentUserLat != null && currentUserLon != null) {
                val restLat = offer.business.latitude
                val restLon = offer.business.longitude

                if (restLat != 0.0 || restLon != 0.0) {
                    val results = FloatArray(1)
                    android.location.Location.distanceBetween(
                        currentUserLat!!,
                        currentUserLon!!,
                        restLat, restLon,
                        results
                    )
                    distanceInKm = (results[0] / 1000).toDouble()
                }
            }

            // Создаем новый объект Business с обновленной дистанцией
            val updatedBusiness = offer.business.copy(distance_km = distanceInKm)
            // Создаем новый объект Offer с обновленным бизнесом
            offer.copy(business = updatedBusiness)
        }

        // 2. Затем фильтруем уже обновленный список
        val filteredList = offersWithDistance.filter { offer ->
            val matchesSearch = if (currentQuery.isBlank()) {
                true
            } else {
                offer.name.contains(currentQuery, ignoreCase = true) ||
                        offer.business.name.contains(currentQuery, ignoreCase = true)
            }

            val distance = offer.business.distance_km
            val matchesRadius = distance <= currentRadius

            matchesSearch && matchesRadius
        }

        _filteredOffers.value = filteredList
    }
}