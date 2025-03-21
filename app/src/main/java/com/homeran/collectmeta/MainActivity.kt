package com.homeran.collectmeta

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.homeran.collectmeta.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * 主活动
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up navigation
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        
        // 设置底部导航
        binding.bottomNavigation.setupWithNavController(navController)
        
        // 处理欢迎页面
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // 在欢迎页面隐藏底部导航栏
            if (destination.id == R.id.welcomeFragment) {
                binding.bottomNavigation.visibility = android.view.View.GONE
            } else {
                binding.bottomNavigation.visibility = android.view.View.VISIBLE
            }
        }
    }
} 