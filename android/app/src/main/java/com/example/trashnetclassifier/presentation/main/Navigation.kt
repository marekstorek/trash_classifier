package com.example.trashnetclassifier.presentation.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.trashnetclassifier.presentation.capture.CaptureScreen
import com.example.trashnetclassifier.presentation.history.HistoryScreen
import com.example.trashnetclassifier.presentation.home.HomeScreen

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController, startDestination = Screen.Home) {
        composable<Screen.Home> {
            HomeScreen(
                onNavigateToCamera = {
                    navController.navigate(Screen.Capture)
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History)
                }
            )
        }
        composable<Screen.Capture> {
            CaptureScreen(
                onBack = {
                    navController.navigateUp()
                },
            )
        }
        composable<Screen.History> {
            HistoryScreen(
                onBack = {
                    navController.navigateUp()
                },
                onExperimentClick = {

                },
            )
        }
    }
}
