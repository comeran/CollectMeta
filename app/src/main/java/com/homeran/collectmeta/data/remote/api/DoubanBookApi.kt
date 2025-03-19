package com.homeran.collectmeta.data.remote.api

import com.homeran.collectmeta.data.remote.model.DoubanBookResponse
import com.homeran.collectmeta.data.remote.model.DoubanSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 豆瓣图书API接口
 */
interface DoubanBookApi {
    /**
     * 通过ISBN获取书籍信息
     * @param isbn 书籍ISBN号
     * @return 书籍详细信息
     */
    @GET("book/isbn/{isbn}")
    suspend fun getBookByIsbn(@Path("isbn") isbn: String): Response<DoubanBookResponse>
    
    /**
     * 通过书名搜索书籍
     * @param query 搜索关键词
     * @param start 起始位置（用于分页）
     * @param count 每页数量
     * @return 搜索结果
     */
    @GET("book/search")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("start") start: Int = 0,
        @Query("count") count: Int = 20
    ): Response<DoubanSearchResponse>
    
    /**
     * 获取书籍详情
     * @param id 豆瓣书籍ID
     * @return 书籍详细信息
     */
    @GET("book/{id}")
    suspend fun getBookById(@Path("id") id: String): Response<DoubanBookResponse>
} 