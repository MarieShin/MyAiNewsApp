package com.hee.myainewsapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.room.util.query
import com.hee.myainewsapp.BuildConfig
import com.hee.myainewsapp.data.ai.GeminiSummarizer
import com.hee.myainewsapp.data.api.RetrofitInstance
import com.hee.myainewsapp.data.api.RetrofitInstance.api
import com.hee.myainewsapp.data.local.ArticleEntity
import com.hee.myainewsapp.data.paging.NewsPagingSource
import com.hee.myainewsapp.data.paging.SearchPagingSource
import com.hee.myainewsapp.data.repository.ArticleRepository
import com.hee.myainewsapp.domain.model.Article
import com.hee.myainewsapp.presentation.ui.state.NewsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.DEBUG_PROPERTY_VALUE_ON
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val repository: ArticleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<NewsUiState>(NewsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _selectedArticle = MutableStateFlow<Article?>(null)
    val selectedArticle = _selectedArticle.asStateFlow()

    val pager = Pager(
        config = PagingConfig(
            pageSize = 10,
            prefetchDistance = 1,
            initialLoadSize = 10,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            NewsPagingSource(api, BuildConfig.GNEWS_API_KEY)
        }
    ).flow.cachedIn(viewModelScope)

    val bookmarks = repository.getBookmarks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Gemini AI 글 요약
    private val summarizer = GeminiSummarizer()

    private val _summary = MutableStateFlow("")
    val summary: StateFlow<String> = _summary

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    val searchPager = _searchQuery
        .onEach { Log.d("SearchDebug", "pager query: $it") }
        .flatMapLatest { query ->
            Log.d("SearchDebug", "flatMapLatest 실행됨: $query")
            if (query.isBlank()) {
                flowOf(PagingData.empty())
            } else {
                Pager(
                    config = PagingConfig(
                        pageSize = 10,
                        prefetchDistance = 1
                    ),
                    pagingSourceFactory = {
                        Log.d("SearchDebug", "PagingSource 생성됨")
                        SearchPagingSource(api, BuildConfig.GNEWS_API_KEY, query)
                    }
                ).flow
            }
        }
        .cachedIn(viewModelScope)


    fun selectedArticle(article: Article) {
        _selectedArticle.value = article
    }

    fun toggleBookmark(article: Article) {
        viewModelScope.launch {

            val url = article.url ?: return@launch

            val entity = ArticleEntity(
                url = url,
                title = article.title,
                description = article.description,
                content = article.content,
                image = article.image
            )

            repository.isBookmarked(url)
                .first()
                .let { isBookmarked ->

                    if (isBookmarked) {
                        repository.delete(entity)
                    } else {
                        repository.insert(entity)
                    }
                }
        }
    }

    fun removeBookmark(article: ArticleEntity) {
        viewModelScope.launch {
            repository.delete(article)
        }
    }

    fun isBookmarked(url: String): Flow<Boolean> {
        return repository.isBookmarked(url)
    }

    fun summarizeArticle(article: Article) {
        viewModelScope.launch {
            val text = article.content ?: article.description ?: ""
//            _summary.value = "요약 중..."

            if (text.isBlank()) {
                _summary.value = "요약할 내용이 없습니다."
                return@launch
            }

            _isLoading.value = true
            _summary.value = ""

            try {
                val result = summarizer.summarize(text)
                _summary.value = result
            } catch (e: Exception) {
                _summary.value = ""
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearSummary() {
        _summary.value = ""
    }

    fun setSearchQuery(query: String) {
        Log.d("setSearch", "query : $query")
        val trimmed = query.trim()
        Log.d("setSearch", "trimmed : $trimmed")


        if (trimmed.isBlank()) {
            _isSearching.value = false
            _searchQuery.value = ""
        } else {
            if (_searchQuery.value == trimmed) return
            _isSearching.value = true
            _searchQuery.value = trimmed
        }
    }
}