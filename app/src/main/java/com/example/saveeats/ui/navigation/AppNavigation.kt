package com.example.saveeats.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.*

import com.example.saveeats.ui.home.HomeScreen
import com.example.saveeats.ui.profile.mainpage.ProfileScreen
import com.example.saveeats.ui.details.OferDetailScreen
import com.example.saveeats.ui.cart.CartScreen
import com.example.saveeats.ui.profile.adressPage.AddressScreen
import com.example.saveeats.ui.auth.login.LoginScreen
import com.example.saveeats.ui.auth.register.RegisterScreen

import com.example.saveeats.utils.TokenManager

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    // Динамически проверяем авторизацию с логированием
    val isLoggedIn = remember { tokenManager.isLoggedIn() }
    val startDestination = if (isLoggedIn) "home" else "login"

    // Логируем стартовый экран
    LaunchedEffect(Unit) {
        Log.d("AppNavigation", "🚀 Запуск приложения: startDestination = $startDestination, isLoggedIn = $isLoggedIn")
    }

    Scaffold(
        bottomBar = {
            // Показываем BottomBar только если пользователь авторизован
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            if (currentRoute != "login" && currentRoute != "register") {
                BottomNavBar(navController)
            }
        },
        containerColor = Color(0xFF1E1E1E)
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            // ============ ЭКРАН АВТОРИЗАЦИИ ============
            composable("login") {
                LoginScreen(
                    onLoginSuccess = {
                        // После успешного входа переходим на главный экран
                        Log.d("AppNavigation", "✅ Успешный вход, переход на home")
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        Log.d("AppNavigation", "📝 Переход на экран регистрации")
                        navController.navigate("register")
                    }
                )
            }

            composable("register") {
                RegisterScreen(
                    onRegisterSuccess = {
                        // После успешной регистрации переходим на главный экран
                        Log.d("AppNavigation", "✅ Успешная регистрация, переход на home")
                        navController.navigate("home") {
                            popUpTo("register") { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        Log.d("AppNavigation", "🔙 Возврат на экран входа")
                        navController.popBackStack()
                    }
                )
            }

            // ============ ОСНОВНЫЕ ЭКРАНЫ ============
            composable("home") {
                HomeScreen(
                    onOfferClick = { offerId ->
                        navController.navigate("detail/$offerId")
                    }
                )
            }

            composable("cart") {
                CartScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable("profile") {
                ProfileScreen(
                    navController = navController,
                    onLogout = {
                        // Выход из аккаунта
                        Log.d("AppNavigation", "🚪 Выход из аккаунта")
                        tokenManager.clearToken()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable("address") {
                AddressScreen()
            }

            composable("detail/{offerId}") { backStackEntry ->
                val offerId = backStackEntry.arguments?.getString("offerId")?.toIntOrNull() ?: 0
                OferDetailScreen(
                    offerId = offerId,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // ============================================
            // Добавьте другие экраны здесь:
            // composable("favorites") { FavoritesScreen() }
            // composable("orders") { OrdersScreen() }
            // composable("achievements") { AchievementsScreen() }
            // composable("settings") { SettingsScreen() }
        }
    }
}

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        NavItem("home", "Главная", Icons.Default.Home),
        NavItem("cart", "Корзина", Icons.Default.ShoppingCart),
        NavItem("profile", "Профиль", Icons.Default.Person)
    )

    Box(
        modifier = Modifier.height(110.dp)
    ) {
        NavigationBar(containerColor = Color(0xFF2B2B2B)) {
            val currentDestination =
                navController.currentBackStackEntryAsState().value?.destination?.route
            items.forEach { item ->
                NavigationBarItem(
                    selected = currentDestination == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            item.icon,
                            contentDescription = item.label,
                            tint = Color.White
                        )
                    },
                    label = {
                        Text(
                            item.label,
                            color = Color.White
                        )
                    }
                )
            }
        }
    }
}

data class NavItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)