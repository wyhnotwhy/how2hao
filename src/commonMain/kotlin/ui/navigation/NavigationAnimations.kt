package ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * 页面切换动画配置
 * 标准：二级页面使用push动画，返回使用pop动画
 */
object NavigationAnimations {
    
    // Push动画：新页面从右侧进入
    val pushEnter = slideInHorizontally(
        initialOffsetX = { fullWidth -> fullWidth },
        animationSpec = tween(durationMillis = 300)
    ) + fadeIn(animationSpec = tween(durationMillis = 300))
    
    // Pop动画：当前页面从右侧退出
    val pushExit = slideOutHorizontally(
        targetOffsetX = { fullWidth -> -fullWidth / 3 },
        animationSpec = tween(durationMillis = 300)
    ) + fadeOut(animationSpec = tween(durationMillis = 300))
    
    // 返回进入：上一个页面从左侧进入
    val popEnter = slideInHorizontally(
        initialOffsetX = { fullWidth -> -fullWidth / 3 },
        animationSpec = tween(durationMillis = 300)
    ) + fadeIn(animationSpec = tween(durationMillis = 300))
    
    // 返回退出：当前页面从右侧退出
    val popExit = slideOutHorizontally(
        targetOffsetX = { fullWidth -> fullWidth },
        animationSpec = tween(durationMillis = 300)
    ) + fadeOut(animationSpec = tween(durationMillis = 300))
    
    // 底部导航切换动画（淡入淡出）
    val bottomNavEnter = fadeIn(animationSpec = tween(durationMillis = 200))
    val bottomNavExit = fadeOut(animationSpec = tween(durationMillis = 200))
}
