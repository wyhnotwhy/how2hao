package ui.components

import androidx.compose.foundation.Image
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Picture
import android.graphics.drawable.PictureDrawable

@Composable
actual fun BankSvgImage(
    svgFileName: String,
    modifier: Modifier,
    contentDescription: String?
) {
    // TODO: 需要添加 AndroidSVG 依赖才能正确渲染SVG
    // 暂时显示占位符
    Text("?")
}
