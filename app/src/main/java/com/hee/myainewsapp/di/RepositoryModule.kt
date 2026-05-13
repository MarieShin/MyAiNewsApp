package com.hee.myainewsapp.di

import com.hee.myainewsapp.data.local.ArticleDao
import com.hee.myainewsapp.data.repository.ArticleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideArticleRepository(
        dao: ArticleDao
    ): ArticleRepository {
        return ArticleRepository(dao)
    }
}