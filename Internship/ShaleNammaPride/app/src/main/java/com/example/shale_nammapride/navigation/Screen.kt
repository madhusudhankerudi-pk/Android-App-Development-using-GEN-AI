package com.example.shale_nammapride.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object DailyMeal : Screen("daily_meal")
    object FacilityTour : Screen("facility_tour")
    object StudentStars : Screen("student_stars")
    object Feedback : Screen("feedback")
    object Announcements : Screen("announcements")
    object Profile : Screen("profile")
    object Language : Screen("language")
}
