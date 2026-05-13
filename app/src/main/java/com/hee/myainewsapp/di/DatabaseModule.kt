package com.hee.myainewsapp.di

import android.content.Context
import androidx.room.Room
import com.hee.myainewsapp.data.local.AppDatabase
import com.hee.myainewsapp.data.local.ArticleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_db"
        ).build()
    }

    @Provides
    fun provideArticleDao(
        db: AppDatabase
    ): ArticleDao {
        return db.articleDao()
    }
}