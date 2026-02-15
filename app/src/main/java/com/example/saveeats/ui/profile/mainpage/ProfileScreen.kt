package com.example.saveeats.ui.profile.mainpage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import android.util.Log
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.saveeats.ui.profile.adressPage.addressPageViewModel
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import com.example.saveeats.data.models.User
@Composable
fun ProfileScreen(navController: NavController,
                  viewModel: ProfileViewModel = viewModel(),
                  onLogout: (() -> Unit)? = null) {

    val user by viewModel.user.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    if(isLoading)
    {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E)), contentAlignment = Alignment.Center)
        {
            CircularProgressIndicator(color = Color(0xFFE57373))

        }
        return
    }

    if (error != null)
    {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E)), contentAlignment = Alignment.Center)
        {
            Text(text = error ?: "Упс что-то пошло не так", color = Color.White)

        }
        return
    }


    user?.let { currentUser ->
        ProfileContent(
        user = currentUser,
            onFavoritesClick = { navController.navigate("favorites") },
            onOrdersClick = { navController.navigate("orders") },
            onAddressClick = { navController.navigate("address") },
            onAchievementsClick = { navController.navigate("achievements") },
            onSettingsClick = { navController.navigate("settings") },
            onLogout = { onLogout?.invoke()}
                )
    }
}
@Composable
fun ProfileContent(
    user: User,
    onFavoritesClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onAddressClick: () -> Unit,
    onAchievementsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogout: () -> Unit = {}
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E)).verticalScroll(rememberScrollState()))
    {
        ProfileHeader(
            name = user.full_name,
            email = user.email,
            avatarUrl = user.avatar_url

        )
        StatisticSection(
            savedBoxes = 12,      // TODO
            moneySaved = 3500,    //
            co2Saved = 84
        )
        Spacer(modifier = Modifier.height(16.dp))
        ProfileOptions(
            user = user,
            onFavoritesClick = onFavoritesClick,
            onOrdersClick = onOrdersClick,
            onAddressClick = onAddressClick,
            onAchievementsClick = onAchievementsClick,
            onSettingsClick = onSettingsClick
        )
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE57373)
            )
        ) {
            Text("Выйти из аккаунта")
        }

        Spacer(modifier = Modifier.height(80.dp))
    }

}
@Composable
fun ProfileHeader(
    name: String,
    email: String,
    avatarUrl: String? = null
) {
    Card(
        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
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
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (!avatarUrl.isNullOrBlank()) {
                        // Логируем URL для отладки
                        Log.d("ProfileHeader", "Загрузка аватара: $avatarUrl")

                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(avatarUrl)
                                .decoderFactory(SvgDecoder.Factory())  // ← Для SVG
                                .crossfade(true)
                                .listener(
                                    onError = { _, result ->
                                        Log.e("ProfileHeader", "❌ Ошибка загрузки: ${result.throwable.message}")
                                    },
                                    onSuccess = { _, _ ->
                                        Log.d("ProfileHeader", "✅ Аватар загружен")
                                    }
                                )
                                .build(),
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,
                            placeholder = rememberVectorPainter(image = Icons.Default.Person),
                            error = rememberVectorPainter(image = Icons.Default.Person)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = name,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = email,
                    color = Color.White.copy(0.8f),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
@Composable
fun StatisticSection(savedBoxes: Int, moneySaved: Int, co2Saved: Int)
{
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 16.dp)
            .offset(y = (-30).dp)
    )
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly)
        {
            StatisticItem(
                icon = Icons.Default.ShoppingCart,
                value = savedBoxes.toString(),
                label = "Коробок\nзаказано"
            )
            VerticalDivider(
                modifier = Modifier.height(60.dp),
                color = Color.Gray.copy(0.3f)
            )

            StatisticItem(
                icon = Icons.Default.AttachMoney,
                value = "${moneySaved} ₽",
                label = "Сэкономлено\nденег"

            )
            VerticalDivider(
                modifier = Modifier.height(60.dp),
                color = Color.Gray.copy(0.3f)
            )
            StatisticItem(
                icon = Icons.Default.Eco,
                value = "${co2Saved} кг",
                label = "Сохранено\nCO₂"
            )


        }


    }

}
@Composable
fun StatisticItem(icon: ImageVector,value:String, label:String)
{
    Column(horizontalAlignment = Alignment.CenterHorizontally)
    {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFFE57373),
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = value,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            lineHeight = 14.sp
        )
    }
}

@Composable
fun ProfileOptions(
    user: User,
    onFavoritesClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onAddressClick: () -> Unit,
    onAchievementsClick: () -> Unit,
    onSettingsClick: () -> Unit,
)
{
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    )
    {
        ProfileCard(
            icon = Icons.Default.Eco,
            title = "Ваш вклад в экологию",
            subtitle = "Вы помогли предотвратить выброс 84 кг CO₂ в атмосферу",
            iconTint = Color(0xFF4CAF50)
        )
        //избранные места
        ProfileOptionItem(
            icon = Icons.Default.Favorite,
            title = "Любимые места",
            subtitle = "$12 мест",
            onClick = onFavoritesClick
        )
        ProfileOptionItem(
            icon = Icons.Default.History,
            title = "Вы уже заказывали",
            subtitle = "5 заказов",
            onClick = onOrdersClick
        )
        ProfileOptionItem(
            icon = Icons.Default.Person,
            title = "Адрес доставки",
            subtitle = "Иваново Шубиных 27",
            onClick = onAddressClick
        )
        ProfileOptionItem(
            icon = Icons.Default.EmojiEvents,
            title = "Достижения",
            subtitle = "Скоро добавим...",
            onClick = onAchievementsClick
        )
        ProfileOptionItem(
            icon = Icons.Default.Settings,
            title = "Настройки",
            subtitle = "",
            onClick = onSettingsClick
        )

    }
}
@Composable
fun ProfileCard(icon: ImageVector,
                title:String,
                subtitle: String,
                iconTint: Color = Color(0xFFE57373)
)
{
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()


    )
    {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Box(
                modifier = Modifier.size(48.dp).background(color = iconTint.copy(alpha = 0.2f), shape = CircleShape
            ),
                contentAlignment = Alignment.Center
            )
            {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)

                )
            }
            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                if (subtitle.isNotEmpty()) {
                    Text(
                        text = subtitle,
                        color = Color.Gray,
                        fontSize = 14.sp,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                }
            }


            }


    }
}
@Composable
fun ProfileOptionItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit)
{
    Card(onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    )
    {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Icon(imageVector = icon,
                contentDescription = null,
                tint = Color(0xFFE57373),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium

                )
                if (subtitle.isNotEmpty()) {
                    Text(
                        text = subtitle,
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Open",
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun VerticalDivider(
    modifier: Modifier = Modifier,
    color: Color = Color.Gray)
{
    Box(
        modifier=modifier.width(1.dp).background(color)
    )
}

