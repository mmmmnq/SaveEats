package com.example.saveeats.ui.profile.adressPage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun AddressScreen(viewModel: addressPageViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))  // Добавь фон как в других экранах
            .padding(16.dp)  // Добавь padding
    ) {
        Text(
            text = "TEST ADRESS PAGE",
            color = Color.White,  // Добавь цвет текста
            fontSize = 20.sp
        )
    }
}