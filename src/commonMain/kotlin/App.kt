package com.how2hao.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import data.model.UserLocation
import ui.auth.AuthScreen
import ui.bankcard.AddBankCardScreen
import ui.bankcard.BankCardListScreen
import ui.finance.FinanceScreen
import ui.home.HomeScreen
import ui.location.LocationPickerScreen
import ui.settings.SettingsScreen
import ui.task.AddTaskScreen
import ui.task.TaskScreen

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
        var currentTab by remember { mutableStateOf(BottomNavTab.Home) }

        when (currentScreen) {
            is Screen.Auth -> {
                AuthScreen(
                    onLoginSuccess = {
                        currentScreen = Screen.Home
                    }
                )
            }
            is Screen.Home,
            is Screen.Task,
            is Screen.Finance,
            is Screen.Settings -> {
                MainScreen(
                    currentTab = when (currentScreen) {
                        is Screen.Home -> BottomNavTab.Home
                        is Screen.Task -> BottomNavTab.Task
                        is Screen.Finance -> BottomNavTab.Finance
                        is Screen.Settings -> BottomNavTab.Settings
                        else -> BottomNavTab.Home
                    },
                    currentLocation = currentLocation,
                    onLocationChange = { location ->
                        currentLocation = location
                    },
                    onTabChange = { tab ->
                        currentTab = tab
                        currentScreen = when (tab) {
                            BottomNavTab.Home -> Screen.Home
                            BottomNavTab.Task -> Screen.Task
                            BottomNavTab.Finance -> Screen.Finance
                            BottomNavTab.Settings -> Screen.Settings
                        }
                    },
                    onNavigateToAddTask = {
                        currentScreen = Screen.AddTask
                    },
                    onLogout = {
                        currentScreen = Screen.Auth
                    },
                    onNavigateToBankCards = {
                        currentScreen = Screen.BankCardList
                    }
                )
            }
            is Screen.AddTask -> {
                AddTaskScreen(
                    onBackClick = {
                        currentScreen = Screen.Task
                    },
                    onSaveClick = {
                        currentScreen = Screen.Task
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
            is Screen.BankCardList -> {
                BankCardListScreen(
                    onBackClick = {
                        currentScreen = Screen.Settings
                    },
                    onAddClick = {
                        currentScreen = Screen.AddBankCard
                    }
                )
            }
            is Screen.AddBankCard -> {
                AddBankCardScreen(
                    onBackClick = {
                        currentScreen = Screen.BankCardList
                    },
                    onSaveSuccess = {
                        currentScreen = Screen.BankCardList
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
    onNavigateToAddTask: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToBankCards: () -> Unit = {}
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
                        icon = { Icon(Icons.Default.CheckCircle, contentDescription = "任务") },
                        label = { Text("任务") },
                        selected = currentTab == BottomNavTab.Task,
                        onClick = { onTabChange(BottomNavTab.Task) }
                    )
                    BottomNavigationItem(
                        icon = { Icon(Icons.Default.AccountBalance, contentDescription = "财务") },
                        label = { Text("财务") },
                        selected = currentTab == BottomNavTab.Finance,
                        onClick = { onTabChange(BottomNavTab.Finance) }
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
                    BottomNavTab.Task -> {
                        TaskScreen(
                            onAddTaskClick = onNavigateToAddTask
                        )
                    }
                    BottomNavTab.Finance -> {
                        FinanceScreen()
                    }
                    BottomNavTab.Settings -> {
                        SettingsScreen(
                            onLogout = onLogout,
                            onNavigateToBankCards = onNavigateToBankCards
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
    object Task : Screen()
    object AddTask : Screen()
    object Finance : Screen()
    object Settings : Screen()
    object LocationPicker : Screen()
    object BankCardList : Screen()
    object AddBankCard : Screen()
}

/**
 * 底部导航标签
 */
enum class BottomNavTab {
    Home,
    Task,
    Finance,
    Settings
}
