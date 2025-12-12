package com.example.saveeats.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saveeats.data.models.Offer
import com.example.saveeats.data.repository.ProfileRepository
import com.example.saveeats.data.repository.RestCardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class HomeViewModel : ViewModel() {

    private val offersRepository = RestCardRepository()

    private val profileRepository = ProfileRepository()

    private val _allOffers = MutableStateFlow<List<Offer>>(emptyList())
    private val _filteredOffers = MutableStateFlow<List<Offer>>(emptyList())
    val offers: StateFlow<List<Offer>> = _filteredOffers.asStateFlow()

    private var _userAdress = MutableStateFlow("Загрузка адресса...")
    val userAdress: StateFlow<String> = _userAdress.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()



    init {


        loadData()
    }


    private fun loadData()
    {
        viewModelScope.launch {
            _allOffers.value = offersRepository.getRestCards()
            updateFilteredOffers()

            try {
                val profile = profileRepository.getProfile()


            }
            catch (e: Exception)
            {
                _userAdress.value = "Адрес не найден"
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        updateFilteredOffers()
    }


    private fun updateFilteredOffers() {
        val filteredList = if (_searchQuery.value.isBlank()) {

            _allOffers.value
        } else {

            _allOffers.value.filter { offer ->
                offer.name.contains(_searchQuery.value, ignoreCase = true) ||
                        offer.category.contains(_searchQuery.value, ignoreCase = true)
            }
        }

        _filteredOffers.value = filteredList
    }


}
