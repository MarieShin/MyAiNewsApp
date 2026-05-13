package com.hee.myainewsapp.presentation.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.hee.myainewsapp.presentation.bookmark.BookmarkScreen
import com.hee.myainewsapp.presentation.news.NewsScreen
import com.hee.myainewsapp.presentation.viewmodel.NewsViewModel

@Composable
fun MainScreen(
    navController: NavController,
    viewModel: NewsViewModel
) {
    var selectedTab by rememberSaveable { mutableStateOf(0) }


    Column(modifier = Modifier.fillMaxSize()) {

        val Navy = Color(0xFF1A237E)

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = Navy,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    height = 2.dp,
                    color = Navy
                )
            }
        ) {

            // 뉴스 탭
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                selectedContentColor = Navy,
                unselectedContentColor = Color(0xFFBBBBBB),
                text = {
                    Text(
                        text = "뉴스",
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    )
                }
            )

            // 북마크 탭
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                selectedContentColor = Navy,
                unselectedContentColor = Color(0xFFBBBBBB),
                text = {
                    Text(
                        text = "북마크",
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    )
                }
            )
        }

        when (selectedTab) {
            0 -> Box(modifier = Modifier.weight(1f)) {
                NewsScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            }

            1 -> Box(modifier = Modifier.weight(1f)) {
                BookmarkScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }
    }
}