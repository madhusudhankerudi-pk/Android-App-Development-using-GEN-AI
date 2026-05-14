package com.example.shale_nammapride

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.shale_nammapride.data.model.UserRole
import com.example.shale_nammapride.navigation.Screen
import com.example.shale_nammapride.ui.auth.AuthViewModel
import com.example.shale_nammapride.ui.auth.LoginScreen
import com.example.shale_nammapride.ui.auth.RegisterScreen
import com.example.shale_nammapride.ui.facility.FacilityTourScreen
import com.example.shale_nammapride.ui.feedback.FeedbackScreen
import com.example.shale_nammapride.ui.home.HomeScreen
import com.example.shale_nammapride.ui.language.LanguageScreen
import com.example.shale_nammapride.ui.meal.DailyMealScreen
import com.example.shale_nammapride.ui.profile.ProfileScreen
import com.example.shale_nammapride.ui.splash.SplashScreen
import com.example.shale_nammapride.ui.stars.StudentStarsScreen
import com.example.shale_nammapride.ui.stars.StudentStarsViewModel
import com.example.shale_nammapride.ui.announcements.AnnouncementsScreen
import com.example.shale_nammapride.ui.theme.ShaleNammaPrideTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShaleNammaPrideTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel()
                val currentUser by authViewModel.currentUser.collectAsState()
                
                var currentLanguage by remember { mutableStateOf("English") }

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                // Navigation logic: Hide bottom bar on login, splash, and register screens
                val showBottomBar = when (currentDestination?.route) {
                    Screen.Home.route, Screen.DailyMeal.route, Screen.FacilityTour.route, 
                    Screen.StudentStars.route, Screen.Profile.route -> true
                    else -> false
                }

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar {
                                val items = listOf(
                                    Triple(Screen.Home, Icons.Default.Home, "Home"),
                                    Triple(Screen.DailyMeal, Icons.Default.Restaurant, "Meals"),
                                    Triple(Screen.FacilityTour, Icons.Default.Apartment, "Facilities"),
                                    Triple(Screen.StudentStars, Icons.Default.EmojiEvents, "Stars"),
                                    Triple(Screen.Profile, Icons.Default.Person, "Profile")
                                )
                                items.forEach { (screen, icon, label) ->
                                    NavigationBarItem(
                                        icon = { Icon(icon, contentDescription = label) },
                                        label = { Text(label) },
                                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                        onClick = {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Splash.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Splash.route) {
                            SplashScreen {
                                if (currentUser != null) {
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(Screen.Splash.route) { inclusive = true }
                                    }
                                } else {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(Screen.Splash.route) { inclusive = true }
                                    }
                                }
                            }
                        }

                        composable(Screen.Login.route) {
                            LoginScreen(
                                onLoginClick = { role ->
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                    }
                                },
                                onRegisterClick = {
                                    navController.navigate(Screen.Register.route)
                                }
                            )
                        }

                        composable(Screen.Register.route) {
                            RegisterScreen(
                                onRegisterClick = { user, password ->
                                    authViewModel.register(user, password)
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(Screen.Register.route) { inclusive = true }
                                    }
                                },
                                onBackToLogin = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable(Screen.Home.route) {
                            HomeScreen(
                                userName = currentUser?.fullName ?: "Namma Student",
                                onNavigate = { route -> navController.navigate(route) }
                            )
                        }

                        composable(Screen.DailyMeal.route) {
                            DailyMealScreen(
                                userRole = currentUser?.role ?: UserRole.PARENT,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable(Screen.FacilityTour.route) {
                            FacilityTourScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable(Screen.StudentStars.route) {
                            val starsViewModel: StudentStarsViewModel = viewModel()
                            StudentStarsScreen(
                                userRole = currentUser?.role ?: UserRole.PARENT,
                                onBack = { navController.popBackStack() },
                                viewModel = starsViewModel
                            )
                        }

                        composable(Screen.Feedback.route) {
                            FeedbackScreen(
                                userId = currentUser?.uid ?: "",
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable(Screen.Announcements.route) {
                            AnnouncementsScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable(Screen.Language.route) {
                            LanguageScreen(
                                currentLanguage = currentLanguage,
                                onLanguageSelected = { currentLanguage = it },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable(Screen.Profile.route) {
                            ProfileScreen(
                                user = currentUser,
                                onNavigate = { route -> navController.navigate(route) },
                                onLogout = {
                                    authViewModel.logout()
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(Screen.Home.route) { inclusive = true }
                                    }
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
