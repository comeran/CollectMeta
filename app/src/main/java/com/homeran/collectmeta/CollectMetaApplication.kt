package com.homeran.collectmeta

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * 应用程序主类
 */
@HiltAndroidApp
class CollectMetaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 初始化代码
    }
} 