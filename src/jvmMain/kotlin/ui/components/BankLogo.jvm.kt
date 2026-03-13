package ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image
import java.io.File

@Composable
actual fun BankSvgImage(
    svgFileName: String,
    modifier: Modifier,
    contentDescription: String?
) {
    val bitmap = remember(svgFileName) {
        loadSvgAsBitmap(svgFileName)
    }
    
    if (bitmap != null) {
        Image(
            bitmap = bitmap,
            contentDescription = contentDescription,
            modifier = modifier
        )
    } else {
        Text("?")
    }
}

private fun loadSvgAsBitmap(svgFileName: String): androidx.compose.ui.graphics.ImageBitmap? {
    return try {
        // JVM平台使用Skia加载SVG
        val resourcePath = "src/commonMain/resources/bank_logos/$svgFileName"
        val file = File(resourcePath)
        if (!file.exists()) return null
        
        val svgContent = file.readText()
        // 简化处理：这里应该使用Skia的SVG支持
        // 暂时返回null，实际项目中需要完整实现
        null
    } catch (e: Exception) {
        null
    }
}
