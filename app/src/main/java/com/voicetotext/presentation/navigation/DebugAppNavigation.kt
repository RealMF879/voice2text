package com.voicetotext.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.voicetotext.presentation.ui.screens.*

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object SpeechDebug : BottomNavItem("speech_debug", "语音调试", Icons.Default.Mic)
    object PolishDebug : BottomNavItem("polish_debug", "润色调试", Icons.Default.AutoAwesome)
    object Settings : BottomNavItem("settings", "设置", Icons.Default.Settings)
}

@Composable
fun DebugAppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val items = listOf(
        BottomNavItem.SpeechDebug,
        BottomNavItem.PolishDebug,
        BottomNavItem.Settings
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
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
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.SpeechDebug.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.SpeechDebug.route) {
                SpeechDebugScreen()
            }
            composable(BottomNavItem.PolishDebug.route) {
                PolishDebugScreen()
            }
            composable(BottomNavItem.Settings.route) {
                SettingsScreen(
                    onNavigateBack = {
                        navController.navigate(BottomNavItem.SpeechDebug.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}
