package com.how2hao.app

import android.os.Bundle
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
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        setContent {
            App()
        }
    }
}
