package com.homeran.collectmeta.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.homeran.collectmeta.presentation.ui.screens.*

/**
 * 导航图
 */
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "tv_show_list"
    ) {
        // TV Show Screens
        composable("tv_show_list") {
            TvShowListScreen(
                onAddTvShow = { navController.navigate("add_tv_show") },
                onTvShowClick = { tvShowId ->
                    navController.navigate("tv_show_detail/$tvShowId")
                }
            )
        }
        
        composable("add_tv_show") {
            AddTvShowScreen(
                onNavigateBack = { navController.popBackStack() },
                onTvShowAdded = { navController.popBackStack() }
            )
        }
        
        composable(
            route = "tv_show_detail/{tvShowId}",
            arguments = listOf(
                navArgument("tvShowId") { type = NavType.StringType }
            )
        ) {
            TvShowDetailScreen(
                tvShowId = it.arguments?.getString("tvShowId") ?: "",
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Movie Screens
        composable("movie_list") {
            MovieListScreen(
                onAddMovie = { navController.navigate("add_movie") },
                onMovieClick = { movieId ->
                    navController.navigate("movie_detail/$movieId")
                }
            )
        }
        
        composable("add_movie") {
            AddMovieScreen(
                onNavigateBack = { navController.popBackStack() },
                onMovieAdded = { navController.popBackStack() }
            )
        }
        
        composable(
            route = "movie_detail/{movieId}",
            arguments = listOf(
                navArgument("movieId") { type = NavType.StringType }
            )
        ) {
            MovieDetailScreen(
                movieId = it.arguments?.getString("movieId") ?: "",
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = "edit_movie/{movieId}",
            arguments = listOf(
                navArgument("movieId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId") ?: ""
            // TODO: 实现编辑页面
            // EditMovieScreen(navController, movieId)
            // 暂时使用添加页面代替
            AddMovieScreen(
                onNavigateBack = { navController.popBackStack() },
                onMovieAdded = { navController.popBackStack() }
            )
        }
        
        composable("search_movie") {
            // TODO: 实现搜索页面
            // SearchMovieScreen(navController)
            // 暂时回到列表页面
            MovieListScreen(navController)
        }
        
        // Settings screen
        composable("settings") {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Sync screen
        composable("sync") {
            SyncScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }
    }
} 