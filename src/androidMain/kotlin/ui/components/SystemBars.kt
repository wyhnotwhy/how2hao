package ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Android平台系统栏处理
 * 提供状态栏和导航栏的padding值
 */
@Composable
actual fun SystemBarsPadding(content: @Composable (PaddingValues) -> Unit) {
    val view = LocalView.current
    val density = LocalDensity.current
    
    // 获取系统栏insets
    val windowInsets = ViewCompat.getRootWindowInsets(view)
    
    val statusBarHeight = with(density) {
        windowInsets?.getInsets(WindowInsetsCompat.Type.statusBars())?.top?.toDp() ?: 0.dp
    }
    
    val navigationBarHeight = with(density) {
        windowInsets?.getInsets(WindowInsetsCompat.Type.navigationBars())?.bottom?.toDp() ?: 0.dp
    }
    
    content(
        PaddingValues(
            top = statusBarHeight,
            bottom = navigationBarHeight
        )
    )
}

/**
 * 状态栏高度
 */
@Composable
actual fun statusBarHeight(): androidx.compose.ui.unit.Dp {
    val view = LocalView.current
    val density = LocalDensity.current
    val windowInsets = ViewCompat.getRootWindowInsets(view)
    return with(density) {
        windowInsets?.getInsets(WindowInsetsCompat.Type.statusBars())?.top?.toDp() ?: 0.dp
    }
}

/**
 * 导航栏高度
 */
@Composable
actual fun navigationBarHeight(): androidx.compose.ui.unit.Dp {
    val view = LocalView.current
    val density = LocalDensity.current
    val windowInsets = ViewCompat.getRootWindowInsets(view)
    return with(density) {
        windowInsets?.getInsets(WindowInsetsCompat.Type.navigationBars())?.bottom?.toDp() ?: 0.dp
    }
}
