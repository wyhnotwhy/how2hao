package ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp

/**
 * 系统栏padding处理（跨平台）
 * Android: 使用WindowInsets
 * iOS: 使用SafeArea
 * Desktop: 无系统栏
 */
@Composable
expect fun SystemBarsPadding(content: @Composable (PaddingValues) -> Unit)

@Composable
expect fun statusBarHeight(): Dp

@Composable
expect fun navigationBarHeight(): Dp
