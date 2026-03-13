package com.how2hao.app

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import data.model.UserLocation
import ui.auth.AuthScreen
import ui.bankcard.AddBankCardScreen
import ui.bankcard.BankCardListScreen
import ui.finance.FinanceScreen
import ui.home.HomeScreen
import ui.location.LocationPickerScreen
import ui.navigation.BackHandler
import ui.settings.SettingsScreen
import ui.task.AddTaskScreen
import ui.task.TaskScreen

@Composable
fun App() {
    MaterialTheme {
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Auth) }
        var previousScreen by remember { mutableStateOf<Screen?>(null) }
        var currentLocation by remember { mutableStateOf(UserLocation("北京市", "北京市")) }
        var currentTab by remember { mutableStateOf(BottomNavTab.Home) }
        var navigationStack by remember { mutableStateOf<List<Screen>>(emptyList()) }

        val handleBack: () -> Unit = {
            when {
                navigationStack.isNotEmpty() -> {
                    val lastScreen = navigationStack.last()
                    navigationStack = navigationStack.dropLast(1)
                    previousScreen = currentScreen
                    currentScreen = lastScreen
                }
                currentScreen is Screen.Main -> { }
                else -> {
                    previousScreen = currentScreen
                    currentScreen = Screen.Main(BottomNavTab.Home)
                }
            }
        }

        val navigateTo: (Screen) -> Unit = { targetScreen ->
            if (currentScreen != targetScreen) {
                if (currentScreen is Screen.Main && targetScreen.isSecondaryScreen()) {
                    navigationStack = navigationStack + currentScreen
                } else if (currentScreen.isSecondaryScreen()) {
                    navigationStack = navigationStack + currentScreen
                }
                previousScreen = currentScreen
                currentScreen = targetScreen
            }
        }

        val isPush = run {
            val prev = previousScreen
            when {
                prev == null -> true
                currentScreen.isSecondaryScreen() && !(prev?.isSecondaryScreen() ?: false) -> true
                !(currentScreen.isSecondaryScreen()) && (prev?.isSecondaryScreen() ?: false) -> false
                else -> true
            }
        }

        if (currentScreen is Screen.Main) {
            MainScreenContent(
                currentTab = (currentScreen as Screen.Main).tab,
                currentLocation = currentLocation,
                onLocationChange = { currentLocation = it },
                onTabChange = { tab ->
                    currentTab = tab
                    currentScreen = Screen.Main(tab)
                },
                onNavigateToAddTask = { navigateTo(Screen.AddTask) },
                onLogout = { currentScreen = Screen.Auth },
                onNavigateToBankCards = { navigateTo(Screen.BankCardList) }
            )
        } else {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    if (isPush) {
                        (slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) + fadeIn(tween(300))) togetherWith
                        (slideOutHorizontally(targetOffsetX = { -it / 3 }, animationSpec = tween(300)) + fadeOut(tween(300)))
                    } else {
                        (slideInHorizontally(initialOffsetX = { -it / 3 }, animationSpec = tween(300)) + fadeIn(tween(300))) togetherWith
                        (slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) + fadeOut(tween(300)))
                    }
                },
                label = "ScreenTransition"
            ) { screen ->
                when (screen) {
                    is Screen.Auth -> AuthScreen(onLoginSuccess = { navigateTo(Screen.Main(BottomNavTab.Home)) })
                    is Screen.Main -> MainScreenContent(
                        currentTab = screen.tab,
                        currentLocation = currentLocation,
                        onLocationChange = { currentLocation = it },
                        onTabChange = { tab ->
                            currentTab = tab
                            currentScreen = Screen.Main(tab)
                        },
                        onNavigateToAddTask = { navigateTo(Screen.AddTask) },
                        onLogout = { currentScreen = Screen.Auth },
                        onNavigateToBankCards = { navigateTo(Screen.BankCardList) }
                    )
                    is Screen.AddTask -> {
                        BackHandler(onBack = handleBack)
                        AddTaskScreen(onBackClick = handleBack, onSaveClick = handleBack)
                    }
                    is Screen.LocationPicker -> {
                        BackHandler(onBack = handleBack)
                        LocationPickerScreen(
                            currentLocation = currentLocation,
                            onLocationSelected = { location ->
                                currentLocation = location
                                handleBack()
                            },
                            onBackClick = handleBack
                        )
                    }
                    is Screen.BankCardList -> {
                        BackHandler(onBack = handleBack)
                        BankCardListScreen(onBackClick = handleBack, onAddClick = { navigateTo(Screen.AddBankCard) })
                    }
                    is Screen.AddBankCard -> {
                        BackHandler(onBack = handleBack)
                        AddBankCardScreen(onBackClick = handleBack, onSaveSuccess = {
                            previousScreen = currentScreen
                            currentScreen = Screen.BankCardList
                            navigationStack = navigationStack.dropLast(1)
                        })
                    }
                }
            }
        }
    }
}

private fun Screen.isSecondaryScreen(): Boolean = when (this) {
    is Screen.AddTask, is Screen.LocationPicker, is Screen.BankCardList, is Screen.AddBankCard -> true
    else -> false
}

@Composable
fun MainScreenContent(
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
            onBackClick = { showLocationPicker = false }
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
                    BottomNavTab.Home -> HomeScreen(
                        currentLocation = currentLocation,
                        onLocationClick = { showLocationPicker = true },
                        onNavigateToSettings = { onTabChange(BottomNavTab.Settings) }
                    )
                    BottomNavTab.Task -> TaskScreen(onAddTaskClick = onNavigateToAddTask)
                    BottomNavTab.Finance -> FinanceScreen()
                    BottomNavTab.Settings -> SettingsScreen(onLogout = onLogout, onNavigateToBankCards = onNavigateToBankCards)
                }
            }
        }
    }
}

sealed class Screen {
    object Auth : Screen()
    data class Main(val tab: BottomNavTab) : Screen()
    object AddTask : Screen()
    object LocationPicker : Screen()
    object BankCardList : Screen()
    object AddBankCard : Screen()
}

enum class BottomNavTab {
    Home, Task, Finance, Settings
}
