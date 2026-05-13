package com.hee.myainewsapp.presentation.ui.state

import com.hee.myainewsapp.domain.model.Article

sealed class NewsUiState {
    object Loading: NewsUiState()
    data class Success(val articles: List<Article>) : NewsUiState()
    data class Error(val message: String) : NewsUiState()
}