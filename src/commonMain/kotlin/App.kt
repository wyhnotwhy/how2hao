package com.how2hao.app

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
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
import ui.navigation.BackHandler
import ui.settings.SettingsScreen
import ui.task.AddTaskScreen
import ui.task.TaskScreen

/**
 * 应用主入口
 * 管理页面路由状态和动画
 */
@Composable
fun App() {
    MaterialTheme {
        // 页面路由状态
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Auth) }
        var previousScreen by remember { mutableStateOf<Screen?>(null) }
        var currentLocation by remember { mutableStateOf(UserLocation("北京市", "北京市")) }
        var currentTab by remember { mutableStateOf(BottomNavTab.Home) }
        
        // 导航栈管理（用于返回）
        var navigationStack by remember { mutableStateOf<List<Screen>>(emptyList()) }

        // 处理返回
        val handleBack: () -> Unit = {
            when {
                navigationStack.isNotEmpty() -> {
                    // 返回上一个页面
                    val lastScreen = navigationStack.last()
                    navigationStack = navigationStack.dropLast(1)
                    previousScreen = currentScreen
                    currentScreen = lastScreen
                }
                currentScreen is Screen.Home || 
                currentScreen is Screen.Task || 
                currentScreen is Screen.Finance || 
                currentScreen is Screen.Settings -> {
                    // 在主页面，不处理（让系统处理，如退出应用）
                }
                else -> {
                    // 其他情况返回首页
                    previousScreen = currentScreen
                    currentScreen = Screen.Home
                }
            }
        }

        // 处理导航
        val navigateTo: (Screen) -> Unit = { targetScreen ->
            if (currentScreen != targetScreen) {
                // 将当前页面加入栈（如果是二级页面）
                if (currentScreen.isSecondaryScreen()) {
                    navigationStack = navigationStack + currentScreen
                }
                previousScreen = currentScreen
                currentScreen = targetScreen
            }
        }

        // 根据页面类型决定动画方向
        val isPush = when {
            previousScreen == null -> true
            currentScreen.isSecondaryScreen() && !previousScreen.isSecondaryScreen() -> true
            !currentScreen.isSecondaryScreen() && previousScreen.isSecondaryScreen() -> false
            else -> true
        }

        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
                if (isPush) {
                    // Push动画：新页面从右侧进入
                    (slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(300)
                    ) + fadeIn(tween(300))) togetherWith
                    (slideOutHorizontally(
                        targetOffsetX = { fullWidth -> -fullWidth / 3 },
                        animationSpec = tween(300)
                    ) + fadeOut(tween(300)))
                } else {
                    // Pop动画：返回时从左侧进入
                    (slideInHorizontally(
                        initialOffsetX = { fullWidth -> -fullWidth / 3 },
                        animationSpec = tween(300)
                    ) + fadeIn(tween(300))) togetherWith
                    (slideOutHorizontally(
                        targetOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(300)
                    ) + fadeOut(tween(300)))
                }
            },
            label = "ScreenTransition"
        ) { screen ->
            when (screen) {
                is Screen.Auth -> {
                    AuthScreen(
                        onLoginSuccess = { navigateTo(Screen.Home) }
                    )
                }
                is Screen.Home,
                is Screen.Task,
                is Screen.Finance,
                is Screen.Settings -> {
                    MainScreen(
                        currentTab = when (screen) {
                            is Screen.Home -> BottomNavTab.Home
                            is Screen.Task -> BottomNavTab.Task
                            is Screen.Finance -> BottomNavTab.Finance
                            is Screen.Settings -> BottomNavTab.Settings
                            else -> BottomNavTab.Home
                        },
                        currentLocation = currentLocation,
                        onLocationChange = { currentLocation = it },
                        onTabChange = { tab ->
                            currentTab = tab
                            navigateTo(when (tab) {
                                BottomNavTab.Home -> Screen.Home
                                BottomNavTab.Task -> Screen.Task
                                BottomNavTab.Finance -> Screen.Finance
                                BottomNavTab.Settings -> Screen.Settings
                            })
                        },
                        onNavigateToAddTask = { navigateTo(Screen.AddTask) },
                        onLogout = { navigateTo(Screen.Auth) },
                        onNavigateToBankCards = { navigateTo(Screen.BankCardList) }
                    )
                }
                is Screen.AddTask -> {
                    // 二级页面：添加返回手势处理
                    BackHandler(onBack = handleBack)
                    AddTaskScreen(
                        onBackClick = handleBack,
                        onSaveClick = {
                            // 保存后返回
                            handleBack()
                        }
                    )
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
                    BankCardListScreen(
                        onBackClick = handleBack,
                        onAddClick = { navigateTo(Screen.AddBankCard) }
                    )
                }
                is Screen.AddBankCard -> {
                    BackHandler(onBack = handleBack)
                    AddBankCardScreen(
                        onBackClick = handleBack,
                        onSaveSuccess = {
                            // 保存后返回银行卡列表
                            previousScreen = currentScreen
                            currentScreen = Screen.BankCardList
                            navigationStack = navigationStack.dropLast(1)
                        }
                    )
                }
            }
        }
    }
}

/**
 * 判断是否为二级页面（需要返回按钮和手势）
 */
private fun Screen?.isSecondaryScreen(): Boolean {
    return when (this) {
        is Screen.AddTask,
        is Screen.LocationPicker,
        is Screen.BankCardList,
        is Screen.AddBankCard -> true
        else -> false
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

enum class BottomNavTab {
    Home, Task, Finance, Settings
}
