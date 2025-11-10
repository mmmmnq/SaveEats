package com.example.saveeats.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.*

import com.example.saveeats.ui.home.HomeScreen
import com.example.saveeats.ui.profile.ProfileScreen
import com.example.saveeats.ui.details.OferDetailScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        containerColor = Color(0xFF1E1E1E)
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            // ========== ИЗМЕНЕНО: передаём callback ==========
            composable("home") {
                HomeScreen(
                    onOfferClick = { offerId ->
                        navController.navigate("detail/$offerId")
                    }
                )
            }
            // =================================================

            composable("profile") { ProfileScreen() }


            composable("detail/{offerId}") { backStackEntry ->
                val offerId = backStackEntry.arguments?.getString("offerId")?.toIntOrNull() ?: 0
                OferDetailScreen(
                    offerId = offerId,
                    onBackClick = { navController.popBackStack() }
                )
            }
            // ============================================
        }
    }
}

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        NavItem("home", "Home", Icons.Default.Home),
        NavItem("profile", "Profile", Icons.Default.Person)
    )

    NavigationBar(containerColor = Color(0xFF2B2B2B)) {
        val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                selected = currentDestination == item.route,
                onClick = { navController.navigate(item.route) },
                icon = { Icon(item.icon, contentDescription = item.label, tint = Color.White) },
                label = { Text(item.label, color = Color.White) }
            )
        }
    }
}

data class NavItem(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)