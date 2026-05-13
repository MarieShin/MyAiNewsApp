package com.hee.myainewsapp.data.repository

import com.hee.myainewsapp.data.local.ArticleDao
import com.hee.myainewsapp.data.local.ArticleEntity
import kotlinx.coroutines.flow.Flow

class ArticleRepository(
    private val dao: ArticleDao
) {

    suspend fun insert(article: ArticleEntity) {
        dao.insert(article)
    }

    suspend fun delete(article: ArticleEntity) {
        dao.delete(article)
    }

    fun isBookmarked(url: String): Flow<Boolean> {
        return dao.isBookmarked(url)
    }

    fun getBookmarks(): Flow<List<ArticleEntity>> {
        return dao.getAll()
    }
}