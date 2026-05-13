package com.hee.myainewsapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey
    val url: String,

    val title: String,
    val description: String?,
    val content: String?,
    val image: String?
)