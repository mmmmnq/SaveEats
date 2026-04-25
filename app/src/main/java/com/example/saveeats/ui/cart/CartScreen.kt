package com.example.saveeats.ui.cart

import androidx.activity.result.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.saveeats.data.models.CartItem
import com.example.saveeats.data.models.CartSummary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState

@Composable
fun CartScreen(
    onBackClick: () -> Unit,
    viewModel: CartViewModel = viewModel()
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val cartSummary by viewModel.cartSummary.collectAsState(initial = null)
    val isLoading by viewModel.isLoading.collectAsState()
    val paymentState by viewModel.paymentState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1E1E1E)),
            containerColor = Color(0xFF1E1E1E),
            bottomBar = {
                // Кнопка всегда внизу
                cartSummary?.let {
                    ConfirmButton(
                        total = it.total,
                        isLoading = isLoading,
                        onClick = { viewModel.confirmOrder() }
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = paddingValues.calculateLeftPadding(LayoutDirection.Ltr),
                        end = paddingValues.calculateRightPadding(LayoutDirection.Ltr),
                        bottom = paddingValues.calculateBottomPadding(),
                        top = 0.dp
                    )
            ) {
                // Верхний блок с градиентом
                CartHeader(
                    itemCount = cartItems.size,
                    onBackClick = onBackClick
                )

                if (cartItems.isEmpty()) {
                    // Пустая корзина
                    EmptyCartView()
                } else {
                    // Весь контент в одной прокручиваемой колонке
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 16.dp,
                            bottom = 16.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Товары
                        items(
                            items = cartItems,
                            key = { it.offerId }
                        ) { item ->
                            AnimatedCartItem(
                                item = item,
                                onRemove = { viewModel.removeItem(item.offerId) },
                                onTimeSelected = { viewModel.updatePickupTime(item.offerId, it) }
                            )
                        }

                        // Итоговая информация
                        item {
                            cartSummary?.let {
                                CartSummaryCard(it)
                            }
                        }

                        // Информация о получении
                        item {
                            PickupInfoCard(cartItems)
                        }
                    }
                }
            }
        }

        // === ОВЕРЛЕЙ ОПЛАТЫ ===
        if (paymentState != PaymentState.IDLE) {
            PaymentOverlay(
                state = paymentState,
                cartItems = cartItems,
                onDismiss = { viewModel.dismissPayment() }
            )
        }
    }
}

@Composable
fun PaymentOverlay(
    state: PaymentState,
    cartItems: List<CartItem>,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
            .clickable(enabled = state == PaymentState.ERROR || state == PaymentState.SUCCESS) { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Иконка/Анимация
                when (state) {
                    PaymentState.PROCESSING -> {
                        CircularProgressIndicator(color = Color(0xFFE57373))
                    }
                    PaymentState.BANK_RESPONSE -> {
                        Icon(
                            Icons.Default.AccountBalance,
                            null,
                            tint = Color(0xFFE5B02E),
                            modifier = Modifier.size(64.dp)
                        )
                    }
                    PaymentState.SUCCESS -> {
                        Icon(
                            Icons.Default.CheckCircle,
                            null,
                            tint = Color(0xFF81C784),
                            modifier = Modifier.size(64.dp)
                        )
                    }
                    PaymentState.ERROR -> {
                        Icon(
                            Icons.Default.Error,
                            null,
                            tint = Color(0xFFE57373),
                            modifier = Modifier.size(64.dp)
                        )
                    }
                    else -> {}
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Текст заголовка
                val title = when (state) {
                    PaymentState.PROCESSING -> "Ожидаем ответа от банка..."
                    PaymentState.BANK_RESPONSE -> "Ответ получен!"
                    PaymentState.SUCCESS -> "Заказ подтвержден"
                    PaymentState.ERROR -> "Ошибка оплаты"
                    else -> ""
                }

                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                if (state == PaymentState.SUCCESS) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Показываем время для каждого товара
                    cartItems.forEach { item ->
                        Text(
                            text = "Ждем вас в ${item.businessName} к ${item.selectedPickupTime}",
                            color = Color.LightGray,
                            fontSize = 14.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B4545))
                    ) {
                        Text("Отлично!")
                    }
                }
                
                if (state == PaymentState.ERROR) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Что-то пошло не так. Попробуйте еще раз.",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B4545))
                    ) {
                        Text("Закрыть")
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedCartItem(
    item: CartItem,
    onRemove: () -> Unit,
    onTimeSelected: (String) -> Unit
) {
    var visible by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    AnimatedVisibility(
        visible = visible,
        exit = shrinkVertically(animationSpec = tween(300)) + fadeOut()
    ) {
        CartItemCard(
            item = item,
            onRemoveClick = {
                scope.launch {
                    visible = false
                    delay(300)
                    onRemove()
                }
            },
            onTimeSelected = onTimeSelected
        )
    }
}

@Composable
fun CartItemCard(
    item: CartItem,
    onRemoveClick: () -> Unit,
    onTimeSelected: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Иконка
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            color = Color(0xFF3A3A3A),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color(0xFFE57373),
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Информация
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = item.offerName,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = item.businessName,
                        color = Color(0xFFE57373),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "%.1f км".format(item.distance),
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = item.pickupTimeRange,
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Выбор времени
            Text(
                text = "Когда заберете?",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            val timeSlots = remember(item.pickupTimeRange) { generateTimeSlots(item.pickupTimeRange) }
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(timeSlots) { time ->
                    val isSelected = item.selectedPickupTime == time
                    FilterChip(
                        selected = isSelected,
                        onClick = { onTimeSelected(time) },
                        label = { Text(time) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color(0xFF3A3A3A),
                            selectedContainerColor = Color(0xFF8B4545),
                            labelColor = Color.LightGray,
                            selectedLabelColor = Color.White
                        ),
                        border = null
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Цена и кнопка удаления
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "${item.discountedPrice} ₽",
                        color = Color(0xFFE57373),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${item.originalPrice} ₽",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        style = TextStyle(
                            textDecoration = TextDecoration.LineThrough
                        )
                    )
                }

                IconButton(
                    onClick = onRemoveClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = Color(0xFFFF5252).copy(alpha = 0.2f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove",
                        tint = Color(0xFFFF5252),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

fun generateTimeSlots(range: String): List<String> {
    try {
        val parts = range.split("-").map { it.trim() }
        if (parts.size != 2) return listOf(range)
        
        val startStr = parts[0]
        val endStr = parts[1]
        
        val startH = startStr.split(":")[0].toInt()
        val startM = startStr.split(":")[1].toInt()
        val endH = endStr.split(":")[0].toInt()
        val endM = endStr.split(":")[1].toInt()
        
        val slots = mutableListOf<String>()
        var currentH = startH
        var currentM = startM
        
        while (currentH < endH || (currentH == endH && currentM <= endM)) {
            slots.add("%02d:%02d".format(currentH, currentM))
            currentM += 30
            if (currentM >= 60) {
                currentH += 1
                currentM = 0
            }
        }
        return slots
    } catch (e: Exception) {
        return listOf(range)
    }
}

@Composable
fun CartSummaryCard(summary: CartSummary) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Итого",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            SummaryRow("Обычная цена", "${summary.subtotal} ₽", false)
            SummaryRow("Скидка", "-${summary.discount} ₽", true)

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color.Gray.copy(alpha = 0.3f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "К оплате",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${summary.total} ₽",
                    color = Color(0xFFE57373),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Вы экономите ${summary.savings} ₽!",
                color = Color(0xFFE57373),
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String, isDiscount: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 14.sp
        )
        Text(
            text = value,
            color = if (isDiscount) Color(0xFFE57373) else Color.Gray,
            fontSize = 14.sp
        )
    }
}

@Composable
fun PickupInfoCard(cartItems: List<CartItem>) {
    val uniqueBusinesses = remember(cartItems) {
        cartItems.map { it.businessName }.distinct()
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Место получения",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Заказы нужно забрать в указанное время в каждом ресторане отдельно",
                color = Color.Gray,
                fontSize = 14.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            uniqueBusinesses.forEach { businessName ->
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFFE57373),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = businessName,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ConfirmButton(
    total: Int,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF1E1E1E),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(
                    top = 12.dp,
                    bottom = 8.dp
                )
        ) {
            Button(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8B4545)
                ),
                shape = RoundedCornerShape(14.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Подтвердить заказ • $total ₽",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Оплата при получении",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

fun pluralizeBox(n: Int): String =
    when {
        n % 10 == 1 && n % 100 != 11 -> "$n коробка"
        n % 10 in 2..4 && (n % 100 !in 12..14) -> "$n коробки"
        else -> "$n коробок"
    }

@Composable
fun CartHeader(
    itemCount: Int,
    onBackClick: () -> Unit
) {
    val countText = pluralizeBox(itemCount)
    Card(
        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier.fillMaxWidth().height(100.dp)
    )
    {
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
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.3f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Назад",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Ваши Коробки",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        countText,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyCartView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Корзина пуста",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Добавьте коробки из главного экрана",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
