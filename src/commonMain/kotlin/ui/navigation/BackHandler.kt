package ui.navigation

import androidx.compose.runtime.Composable

/**
 * 跨平台返回手势处理
 * Android: 使用系统BackHandler
 * iOS/Desktop: 使用自定义实现或空实现
 */
@Composable
expect fun BackHandler(enabled: Boolean = true, onBack: () -> Unit)
