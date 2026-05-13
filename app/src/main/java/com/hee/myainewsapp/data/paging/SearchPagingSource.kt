package com.hee.myainewsapp.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hee.myainewsapp.data.api.NewsApi
import com.hee.myainewsapp.domain.model.Article

class SearchPagingSource(
    private val api: NewsApi,
    private val apiKey: String,
    private val query: String,
) : PagingSource<Int, Article>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {

        return try {
            val page = params.key ?: 1

            val response = api.searchNews(
                query = query,
                page = page,
                max = 10, //params.loadSize,
                apiKey = apiKey
            )

            LoadResult.Page(
                data = response.articles,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (page >= 3) null else page + 1 //if (response.articles.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            Log.e("PagingError", "에러 발생", e)
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? = null
}