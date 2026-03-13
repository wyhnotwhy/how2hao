package ui.navigation

import androidx.compose.runtime.Composable

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    // JVM/Desktop平台暂不支持系统返回手势
    // 可以通过监听按键事件实现
}
