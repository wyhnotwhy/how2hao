package com.how2hao.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import data.model.UserLocation
import ui.auth.AuthScreen
import ui.home.HomeScreen
import ui.location.LocationPickerScreen
import ui.settings.SettingsScreen

/**
 * 应用主入口
 * 管理页面路由状态
 */
@Composable
fun App() {
    MaterialTheme {
        // 页面路由状态
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Auth) }
        var currentLocation by remember { mutableStateOf(UserLocation("北京市", "北京市")) }
        
        when (currentScreen) {
            is Screen.Auth -> {
                AuthScreen(
                    onLoginSuccess = {
                        currentScreen = Screen.Home
                    }
                )
            }
            is Screen.Home -> {
                MainScreen(
                    currentTab = BottomNavTab.Home,
                    currentLocation = currentLocation,
                    onLocationChange = { location ->
                        currentLocation = location
                    },
                    onTabChange = { tab ->
                        when (tab) {
                            BottomNavTab.Home -> currentScreen = Screen.Home
                            BottomNavTab.Settings -> currentScreen = Screen.Settings
                        }
                    },
                    onLogout = {
                        currentScreen = Screen.Auth
                    }
                )
            }
            is Screen.Settings -> {
                MainScreen(
                    currentTab = BottomNavTab.Settings,
                    currentLocation = currentLocation,
                    onLocationChange = { location ->
                        currentLocation = location
                    },
                    onTabChange = { tab ->
                        when (tab) {
                            BottomNavTab.Home -> currentScreen = Screen.Home
                            BottomNavTab.Settings -> currentScreen = Screen.Settings
                        }
                    },
                    onLogout = {
                        currentScreen = Screen.Auth
                    }
                )
            }
            is Screen.LocationPicker -> {
                LocationPickerScreen(
                    currentLocation = currentLocation,
                    onLocationSelected = { location ->
                        currentLocation = location
                        currentScreen = Screen.Home
                    },
                    onBackClick = {
                        currentScreen = Screen.Home
                    }
                )
            }
        }
    }
}

/**
 * 主屏幕（带底部导航）
 */
@Composable
fun MainScreen(
    currentTab: BottomNavTab,
    currentLocation: UserLocation,
    onLocationChange: (UserLocation) -> Unit,
    onTabChange: (BottomNavTab) -> Unit,
    onLogout: () -> Unit
) {
    var showLocationPicker by remember { mutableStateOf(false) }
    
    if (showLocationPicker) {
        LocationPickerScreen(
            currentLocation = currentLocation,
            onLocationSelected = { location ->
                onLocationChange(location)
                showLocationPicker = false
            },
            onBackClick = {
                showLocationPicker = false
            }
        )
    } else {
        Scaffold(
            bottomBar = {
                BottomNavigation {
                    BottomNavigationItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "首页") },
                        label = { Text("首页") },
                        selected = currentTab == BottomNavTab.Home,
                        onClick = { onTabChange(BottomNavTab.Home) }
                    )
                    BottomNavigationItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = "设置") },
                        label = { Text("设置") },
                        selected = currentTab == BottomNavTab.Settings,
                        onClick = { onTabChange(BottomNavTab.Settings) }
                    )
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                when (currentTab) {
                    BottomNavTab.Home -> {
                        HomeScreen(
                            currentLocation = currentLocation,
                            onLocationClick = { showLocationPicker = true },
                            onNavigateToSettings = {
                                onTabChange(BottomNavTab.Settings)
                            }
                        )
                    }
                    BottomNavTab.Settings -> {
                        SettingsScreen(
                            onLogout = onLogout
                        )
                    }
                }
            }
        }
    }
}

/**
 * 页面路由密封类
 */
sealed class Screen {
    object Auth : Screen()
    object Home : Screen()
    object Settings : Screen()
    object LocationPicker : Screen()
}

/**
 * 底部导航标签
 */
enum class BottomNavTab {
    Home,
    Settings
}
