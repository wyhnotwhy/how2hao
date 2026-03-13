package ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
actual fun SystemBarsPadding(content: @Composable (PaddingValues) -> Unit) {
    // Desktop平台无系统栏
    content(PaddingValues())
}

@Composable
actual fun statusBarHeight(): Dp = 0.dp

@Composable
actual fun navigationBarHeight(): Dp = 0.dp
