package com.hee.myainewsapp.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hee.myainewsapp.presentation.main.MainScreen
import com.hee.myainewsapp.presentation.news.NewsDetailScreen
import com.hee.myainewsapp.presentation.viewmodel.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()

        setContent {

            val navController = rememberNavController()
            val startDesc = "main"

            NavHost(
                navController = navController,
                startDestination = startDesc
            ) {

                composable(startDesc) {

                    val viewModel: NewsViewModel = hiltViewModel()

                    MainScreen(
                        navController = navController,
                        viewModel = viewModel
                    )
                }

                composable("detail") { backStackEntry ->

                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry(startDesc)
                    }

                    val viewModel: NewsViewModel = hiltViewModel(parentEntry)

                    NewsDetailScreen(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}