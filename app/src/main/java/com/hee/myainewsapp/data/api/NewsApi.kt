package com.hee.myainewsapp.data.api


import com.hee.myainewsapp.domain.model.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET("api/v4/top-headlines")
    suspend fun getTopHeadlines(
        @Query("lang") lang: String = "ko",
        @Query("page") page: Int,
        @Query("max") size: Int = 10,
        @Query("apikey") apiKey: String
    ): NewsResponse

    // 검색
    @GET("api/v4/search")
    suspend fun searchNews(
        @Query("q") query: String,
        @Query("lang") lang: String = "ko",
        @Query("page") page: Int,
        @Query("max") max: Int = 10,
        @Query("apikey") apiKey: String
    ): NewsResponse
}