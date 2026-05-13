package com.hee.myainewsapp.presentation.news

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.hee.myainewsapp.R
import com.hee.myainewsapp.presentation.viewmodel.NewsViewModel

@Composable
fun NewsDetailScreen(
    navController: NavController,
    viewModel: NewsViewModel
) {

    val article by viewModel.selectedArticle.collectAsState()
    val summary by viewModel.summary.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()


    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            IconButton(
                onClick = { navController.popBackStack() }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_back),
                    contentDescription = null,
                    modifier = Modifier
                        .size(28.dp)
                        .absoluteOffset((-10).dp, 0.dp)
                )
            }
        }


        article?.let { article ->

            LaunchedEffect(article) {
                viewModel.clearSummary()
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(top = 0.dp, start = 16.dp, end = 16.dp, bottom = 20.dp)
            ) {

                // 제목 텍스트
                Text(
                    text = article.title,
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.4).sp,
                        lineHeight = 28.sp
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { // 무료 버전이기 때문에 사용 초과 오류 날 수 있음.

                        if (summary.isNotEmpty() || summary.contains("오류")) return@Button

                        viewModel.summarizeArticle(article)

                              },
                    enabled = !isLoading
                ) {
                    Text("AI 요약 ✨")
                }

                if (isLoading) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 5.dp, start = 5.dp)
                    ) {
                        Text(
                            text = "요약 중...",
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (summary.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFE6E6FA))
                    ) {
                        Text(
                            modifier = Modifier.padding(10.dp),
                            text = summary,
                            style = TextStyle(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                lineHeight = 20.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 이미지
                AsyncImage(
                    model = article.image,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth(),
//                        .height(200.dp),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.placeholder),
                    error = painterResource(R.drawable.placeholder)
                )

                Spacer(modifier = Modifier.height(25.dp))

                // 본문 텍스트
                Text(
                    text = article.content ?: "",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 0.sp,
                        lineHeight = 24.sp,
                        color = Color(0xFF222222)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}