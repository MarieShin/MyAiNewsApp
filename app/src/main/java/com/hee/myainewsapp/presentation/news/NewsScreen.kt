package com.hee.myainewsapp.presentation.news

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.hee.myainewsapp.R
import com.hee.myainewsapp.domain.model.Article
import com.hee.myainewsapp.presentation.viewmodel.NewsViewModel
import kotlinx.coroutines.launch

@Composable
fun NewsScreen(
    navController: NavController,
    viewModel: NewsViewModel = hiltViewModel()
) {

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var searchText by remember { mutableStateOf("") }
    val isSearching by viewModel.isSearching.collectAsState()
    val newsItems = viewModel.pager.collectAsLazyPagingItems()
    val searchItems = viewModel.searchPager.collectAsLazyPagingItems()

    val items = if (isSearching) searchItems else newsItems

    val keyboardController = LocalSoftwareKeyboardController.current


    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize()) {

            // 검색창
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("검색어를 입력해주세요") },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            if (searchText.isNotBlank()) {
                                viewModel.setSearchQuery(searchText)
                                keyboardController?.hide()
                            }
                        }
                    ),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_news),
                            contentDescription = null,
                            tint = Color.LightGray
                        )
                    },
                    trailingIcon = {

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            // 텍스트가 있을 때만 X(삭제) 버튼 표시
                            if (searchText.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        searchText = ""
                                        viewModel.setSearchQuery("")
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "텍스트 삭제",
                                        tint = Color.Gray
                                    )
                                }
                            }

                            IconButton(
                                onClick = {
                                    viewModel.setSearchQuery(searchText)
                                    keyboardController?.hide()
                                }
                            ) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "검색",
                                    tint = Color(0xFF1A237E)
                                )
                            }
                        }
                    },
                    shape = CircleShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = Color(0xFF1A237E)
                    ),
                    singleLine = true
                )
            }

            val isLoading = items.loadState.refresh is LoadState.Loading
            val isError = items.loadState.refresh is LoadState.Error

            // 뉴스 리스트
            when {
                isError -> {

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("검색 중 문제가 발생했어요 😢")
                    }
                }

                isSearching && items.itemCount == 0 && !isLoading -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("검색 결과가 없습니다 😢")
                    }
                }

                else -> {
                    // 리스트
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        state = listState
                    ) {
                        items(items.itemCount) { index ->
                            items[index]?.let { article ->
                                NewsItem(article, navController, viewModel)
                            }
                        }


                        items.apply {
                            when {
                                loadState.refresh is LoadState.Loading -> {
                                    item {
                                        CircularProgressIndicator(
                                            modifier = Modifier
                                                .padding(top = 10.dp, start = 10.dp)
                                                .size(30.dp)
                                        )
                                    }
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(0)
                                    }
                                }

                                loadState.append is LoadState.Loading -> {
                                    item {
                                        CircularProgressIndicator(
                                            modifier = Modifier
                                                .padding(top = 10.dp, start = 10.dp)
                                                .size(30.dp)
                                        )
                                    }
                                }

                                loadState.refresh is LoadState.Error -> {
                                    item {
                                        Text(
                                            text = "문제가 발생했어요.\n다시 시도해주세요",
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }
                                }

                                loadState.append is LoadState.Error -> {
                                    item {
                                        Text(
                                            text = "문제가 발생했어요.\n다시 시도해주세요",
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Top 버튼
        val showButton = listState.firstVisibleItemIndex > 0

        if (showButton) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        listState.animateScrollToItem(0)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp)
            ) {
                Text("Top")
            }
        }

    }
}


@Composable
fun NewsItem(
    article: Article,
    navController: NavController,
    viewModel: NewsViewModel
) {
    val isBookmarked by viewModel
        .isBookmarked(article.url ?: "")
        .collectAsState(initial = false)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                viewModel.selectedArticle(article)
                navController.navigate("detail")
            },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp,
            pressedElevation = 2.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {

        Column {

            Box {

                // 기사 이미지
                AsyncImage(
                    model = article.image,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.placeholder),
                    error = painterResource(R.drawable.placeholder)
                )

                IconButton(
                    onClick = { viewModel.toggleBookmark(article) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(
                            Color.Black.copy(alpha = 0.4f),
                            shape = CircleShape
                        )
                        .size(48.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_bookmark_star),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = if (isBookmarked) Color(0xFFFFC107) else Color.White
                    )
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {

                // 제목 텍스트
                Text(
                    text = article.title ?: "",
                    style = TextStyle(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 24.sp,
                        letterSpacing = (-0.3).sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                // 설명 텍스트
                Text(
                    text = article.description ?: "",
                    style = TextStyle(
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = Color.DarkGray
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}