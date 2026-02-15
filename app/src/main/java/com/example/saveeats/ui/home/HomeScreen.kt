package com.example.saveeats.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.saveeats.R
import com.example.saveeats.data.models.Offer

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onOfferClick: (Int) -> Unit = {}
) {
    // 👇 Подписываемся на СГРУППИРОВАННЫЙ список (Map<Int, List<Offer>>)
    val groupedOffers by viewModel.groupedOffers.collectAsState()

    val searchQuery by viewModel.searchQuery.collectAsState()
    val address by viewModel.userAdress.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
    ) {
        HomeHeader(
            searchQuery = searchQuery,
            onSearchQueryChange = { viewModel.updateSearchQuery(it) },
            onClearClick = { viewModel.updateSearchQuery("") },
            currentAddress = address
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp), // Отступ снизу для навигации
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 👇 ИТЕРИРУЕМСЯ ПО РЕСТОРАНАМ (ГРУППАМ), А НЕ ПО ОФФЕРАМ
            items(groupedOffers.keys.toList()) { businessId ->
                // Получаем список офферов для этого ресторана
                val offersInThisRestaurant = groupedOffers[businessId] ?: emptyList()

                if (offersInThisRestaurant.isNotEmpty()) {
                    // Рисуем одну большую карточку ресторана со списком еды внутри
                    RestaurantGroupCard(
                        offers = offersInThisRestaurant,
                        onOfferClick = onOfferClick
                    )
                }
            }
        }
    }
}

@Composable
fun HomeHeader(
    searchQuery: String,
    currentAddress: String,
    onSearchQueryChange: (String) -> Unit,
    onClearClick: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Card(
        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF8B4545),
                            Color(0xFF6B3535)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.available_nearby),
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(
                        text = currentAddress,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    placeholder = {
                        Text("Найдите нужное...", color = Color.White.copy(alpha = 0.7f))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = onClearClick) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = Color.White
                                )
                            }
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Black.copy(alpha = 0.2f),
                        unfocusedContainerColor = Color.Black.copy(alpha = 0.2f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedIndicatorColor = Color.White,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = { keyboardController?.hide() }
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

// === НОВАЯ КАРТОЧКА: ГРУППА РЕСТОРАНА ===
@Composable
fun RestaurantGroupCard(
    offers: List<Offer>, // Список офферов ЭТОГО ресторана
    onOfferClick: (Int) -> Unit
) {
    // Берем информацию о бизнесе из первого оффера в списке
    val business = offers.first().business

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column {
            // === 1. ШАПКА РЕСТОРАНА ===
            Box(modifier = Modifier.height(140.dp).fillMaxWidth()) {
                // Картинка ресторана
                if (!business.cover_image_url.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(business.cover_image_url)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(Modifier.fillMaxSize().background(Color(0xFF3A3A3A)))
                }

                // Затемнение снизу для текста
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f))
                            )
                        )
                )

                // Название и Лого
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Круглый логотип
                    if (!business.logo_url.isNullOrBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(business.logo_url)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.Gray),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // Заглушка для лого
                        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.Gray))
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = business.name,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        // Форматируем рейтинг и дистанцию
                        val ratingText = if (business.rating != null) "⭐ ${business.rating}" else "New"
                        val distText = String.format("%.1f км", business.distance_km)

                        Text(
                            text = "$ratingText • $distText",
                            color = Color.LightGray,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // === 2. СПИСОК ОФФЕРОВ ВНУТРИ ===
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Доступно сегодня:",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Проходимся по всем офферам этого ресторана
                offers.forEach { offer ->
                    OfferRowItem(offer = offer, onClick = { onOfferClick(offer.id) })
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

// === МАЛЕНЬКАЯ СТРОЧКА С ОФФЕРОМ ===
@Composable
fun OfferRowItem(
    offer: Offer,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF3A3A3A), RoundedCornerShape(12.dp))
            .clickable { onClick() } // Клик по строчке ведет к деталям
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = offer.name, // "Magic Bag"
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = "${offer.boxesLeft} шт. осталось",
                color = if (offer.boxesLeft < 3) Color(0xFFFF5252) else Color.Gray,
                fontSize = 12.sp
            )
        }

        // Цена
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${offer.newPrice} ₽",
                color = Color(0xFFE57373),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            // Старая цена (если есть)
            if (offer.oldPrice > 0) {
                Text(
                    text = "${offer.oldPrice} ₽",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    style = androidx.compose.ui.text.TextStyle(
                        textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                    )
                )
            }
        }
    }
}