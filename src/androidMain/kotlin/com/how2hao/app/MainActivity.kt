package com.how2hao.app

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import data.repository.AppContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 初始化AppContext
        AppContext.init(this)
        
        // 设置沉浸式状态栏
        setupImmersiveMode()
        
        setContent {
            App()
        }
    }
    
    private fun setupImmersiveMode() {
        // 基础沉浸式设置
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        // 刘海屏适配
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            val layoutParams = window.attributes
            layoutParams.layoutInDisplayCutoutMode = 
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = layoutParams
        }
    }
}
