package ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Picture
import android.graphics.drawable.PictureDrawable
import com.caverock.androidsvg.SVG

@Composable
actual fun BankSvgImage(
    svgFileName: String,
    modifier: Modifier,
    contentDescription: String?
) {
    val context = LocalContext.current
    val bitmap = remember(svgFileName) {
        loadSvgAsBitmap(context, "bank_logos/$svgFileName")
    }
    
    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = contentDescription,
            modifier = modifier
        )
    } else {
        // 加载失败
        Text("?")
    }
}

private fun loadSvgAsBitmap(context: android.content.Context, path: String): Bitmap? {
    return try {
        val svgContent = context.assets.open(path).bufferedReader().use { it.readText() }
        val svg = SVG.getFromString(svgContent)
        val picture = svg.renderToPicture()
        val drawable = PictureDrawable(picture)
        
        val width = drawable.intrinsicWidth.coerceAtLeast(1)
        val height = drawable.intrinsicHeight.coerceAtLeast(1)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, width, height)
        drawable.draw(canvas)
        bitmap
    } catch (e: Exception) {
        null
    }
}
