package com.example.saveeats.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.saveeats.R
import com.example.saveeats.data.models.Offer


@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onOfferClick: (Int) -> Unit = {}
) {
    val offers by viewModel.offers.collectAsState()
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
            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = offers,
                key = { offer -> offer.id }
            ) { offer ->
                OfferCard(
                    offer = offer,
                    onClick = { onOfferClick(offer.id) }
                )
            }
        }
    }
}

@Composable
fun OfferCard(
    offer: Offer,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color(0xFF3A3A3A))
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = null,
                    tint = Color(0xFF5A5A5A),
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.Center)
                )

                //успей забрать
                if (offer.isAlmostGone) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .background(
                                color = Color(0xFFFF5252),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.almost_gone),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }


            // инфа под картинкой
            Column(modifier = Modifier.padding(16.dp)) {
                // Название и категория
                Text(
                    text = offer.name,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = offer.category,
                    color = Color.LightGray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Строка с расстоянием и временем
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Расстояние
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.distance_km, offer.distance),
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }

                    // Время (можно добавить в Offer позже)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "20:00 - 20:30",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Коробки осталось
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.boxes_left, offer.boxesLeft),
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Цена и скидка
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Цены
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "${offer.newPrice} ₽",
                            color = Color(0xFFE57373),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "900 ₽",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            style = androidx.compose.ui.text.TextStyle(
                                textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                            )
                        )
                    }

                    // Процент скидки
                    Box(
                        modifier = Modifier
                            .background(
                                color = Color(0xFFE57373).copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "-${offer.discount}%",
                            color = Color(0xFFE57373),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            // ==============================================
        }
    }

}
@Composable
fun HomeHeader(
    searchQuery: String,
    currentAddress:String,
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
                )
                {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.padding(horizontal = 10.dp)
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
