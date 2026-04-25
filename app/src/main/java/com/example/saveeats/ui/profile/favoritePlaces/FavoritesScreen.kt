package com.example.saveeats.ui.profile.favoritePlaces

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.saveeats.data.models.offers.Business

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onBackClick: () -> Unit,
    onBusinessClick: (Int) -> Unit,
    viewModel: FavoritesViewModel = viewModel()
) {
    val favorites by viewModel.favorites.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Любимые места", color = Color.White, fontWeight = FontWeight.Bold) },
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
            } else if (favorites.isEmpty()) {
                EmptyFavoritesView()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(favorites) { business ->
                        BusinessCard(
                            business = business,
                            onClick = { onBusinessClick(business.id) },
                            onFavoriteToggle = { viewModel.toggleFavorite(business.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BusinessCard(
    business: Business,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit
) {
    Card(
        onClick = onClick,
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
                if (business.logo_url != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(business.logo_url)
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
                    text = business.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = business.address,
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    maxLines = 1
                )
                if (business.rating != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFE5B02E),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = business.rating.toString(),
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            IconButton(onClick = onFavoriteToggle) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Remove from favorites",
                    tint = Color(0xFFE57373)
                )
            }
        }
    }
}

@Composable
fun EmptyFavoritesView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.FavoriteBorder,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Список пуст", color = Color.White, fontSize = 18.sp)
        Text(text = "Добавляйте заведения в избранное, чтобы не терять их", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 32.dp))
    }
}
