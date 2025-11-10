package com.example.saveeats.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.saveeats.data.models.Offer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.example.saveeats.R
import com.example.saveeats.ui.theme.*
@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel(),onOfferClick: (Int) -> Unit = {}) {
    val offers by viewModel.offers.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val keyboardController = LocalSoftwareKeyboardController.current


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
            .padding(20.dp)
    ) {
        Text(
            text = stringResource(R.string.available_nearby),
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = {viewModel.updateSearchQuery(it)},
            modifier = Modifier.fillMaxWidth().height(56.dp),
            placeholder = {
                Text("Найдите нужное...", color = Color.Gray)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {

                    defaultKeyboardAction(ImeAction.Search)
                }
            )

            )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(
                items = offers,
                key = { offer -> offer.id }
            ) { offer ->
                OfferCard(offer = offer,onClick = {onOfferClick(offer.id)})
            }
        }
    }
}

@Composable
fun OfferCard(offer: Offer, onClick: (Int) -> Unit) {
    Card(
        onClick = { onClick(offer.id) },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        ),
        shape = CardDefaults.shape,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(offer.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(offer.category, color = Color.LightGray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(stringResource(R.string.distance_km, offer.distance), color = Color.Gray, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(stringResource(R.string.boxes_left, offer.boxesLeft), color = Color.White, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.price_with_discount, offer.newPrice, offer.discount),
                color = Color(0xFFE57373),
                fontSize = 16.sp
            )
            if (offer.isAlmostGone) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.almost_gone),
                    color = Color(0xFFFF5252),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
