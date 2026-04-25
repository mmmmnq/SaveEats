package com.example.saveeats.ui.profile.lastOrders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Restaurant
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.saveeats.data.models.Order

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    onBackClick: () -> Unit,
    viewModel: OrdersViewModel = viewModel()
) {
    val orders by viewModel.orders.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Обновляем список при каждом входе на экран
    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.loadOrders()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("История заказов", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF8B4545)
                )
            )
        },
        containerColor = Color(0xFF1E1E1E)
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFFE57373)
                )
            } else if (error != null) {
                Text(
                    text = error ?: "",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (orders.isEmpty()) {
                EmptyOrdersView()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(orders) { order ->
                        OrderCard(order)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCard(order: Order) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Логотип бизнеса
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color(0xFF3A3A3A), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (order.businessLogoUrl != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(order.businessLogoUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.Restaurant, contentDescription = null, tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = order.businessName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "${order.offerName} x ${order.quantity}",
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
                Text(
                    text = order.date,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${order.totalPrice} ₽",
                    color = Color(0xFFE57373),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Surface(
                    color = getStatusColor(order.status).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = translateStatus(order.status),
                        color = getStatusColor(order.status),
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyOrdersView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.History,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Заказов пока нет", color = Color.White, fontSize = 18.sp)
        Text(text = "Самое время что-нибудь заказать!", color = Color.Gray, fontSize = 14.sp)
    }
}

fun getStatusColor(status: String): Color {
    return when (status.uppercase()) {
        "PENDING" -> Color(0xFFE5B02E)
        "RESERVED" -> Color(0xFF81C784)
        "CONFIRMED" -> Color(0xFF81C784)
        "CANCELLED" -> Color(0xFFE57373)
        "COMPLETED" -> Color(0xFF81C784)
        else -> Color.Gray
    }
}

fun translateStatus(status: String): String {
    return when (status.uppercase()) {
        "PENDING" -> "Ожидает"
        "RESERVED" -> "Забронирован"
        "CONFIRMED" -> "Подтвержден"
        "CANCELLED" -> "Отменен"
        "COMPLETED" -> "Завершен"
        else -> status
    }
}
