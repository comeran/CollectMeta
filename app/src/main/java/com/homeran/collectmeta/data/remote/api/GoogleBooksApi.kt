package com.homeran.collectmeta.data.remote.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Google Books API 接口定义
 * 注意：当apiKey为空字符串时，网络拦截器会自动移除该参数，不会将空值添加到URL中
 */
interface GoogleBooksApi {
    
    /**
     * 搜索书籍
     * @param query 搜索关键字
     * @param maxResults 结果数量上限
     * @param startIndex 起始索引
     * @param apiKey API密钥（可选，为空时会从URL中移除）
     * @return API响应
     */
    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int,
        @Query("startIndex") startIndex: Int,
        @Query("key") apiKey: String?
    ): Response<GoogleBooksSearchResponse>
    
    /**
     * 获取书籍详情
     * @param volumeId 书籍ID
     * @param apiKey API密钥（可选，为空时会从URL中移除）
     * @return API响应
     */
    @GET("volumes/{volumeId}")
    suspend fun getBookDetails(
        @Path("volumeId") volumeId: String,
        @Query("key") apiKey: String?
    ): Response<GoogleBooksVolumeResponse>
}

/**
 * Google Books 搜索响应
 */
data class GoogleBooksSearchResponse(
    val kind: String,
    val totalItems: Int,
    val items: List<GoogleBooksVolumeResponse>?
)

/**
 * Google Books 卷响应（单本书）
 */
data class GoogleBooksVolumeResponse(
    val id: String,
    val volumeInfo: VolumeInfo
)

/**
 * 书籍信息
 * 包含与BookEntity对应的所有字段，即使API没有这些数据
 */
data class VolumeInfo(
    // Google Books API提供的字段
    val title: String,
    val subtitle: String? = null,
    val authors: List<String>? = null,
    val publisher: String? = null,
    val publishedDate: String? = null,
    val description: String? = null,
    val pageCount: Int? = null,
    val categories: List<String>? = null,
    val imageLinks: ImageLinks? = null,
    val language: String? = null,
    val industryIdentifiers: List<IndustryIdentifier>? = null,
    
    // 与BookEntity对应的附加字段
    val cover: String? = null,                     // 对应BookEntity的cover，可从imageLinks生成
    val category: String? = null,                  // 对应BookEntity的category，可从categories生成
    val date: String? = null,                      // 对应BookEntity的date
    val personalRating: Float? = null,             // 对应BookEntity的personalRating
    val status: String? = null,                    // 对应BookEntity的status
    val doubanRating: Float? = null,               // 对应BookEntity的doubanRating
    val overallRating: Float? = null,              // 对应BookEntity的overallRating
    val doubanUrl: String? = null,                 // 对应BookEntity的doubanUrl
    val fileAttachment: String? = null,            // 对应BookEntity的fileAttachment
    val isbn: String? = null,                      // 可从industryIdentifiers生成
    val createdAt: String? = null,                 // 对应BookEntity的createdAt
    val recommendationReason: String? = null,      // 对应BookEntity的recommendationReason
    val chineseTitle: String? = null,              // 对应BookEntity的chineseTitle
    val originalTitle: String? = null,             // 对应BookEntity的originalTitle
    val translator: String? = null,                // 对应BookEntity的translator
    val series: String? = null,                    // 对应BookEntity的series
    val binding: String? = null,                   // 对应BookEntity的binding
    val price: String? = null,                     // 对应BookEntity的price
    val publishDate: String? = null,               // 对应BookEntity的publishDate，可从publishedDate转换
    val lastModified: Long? = null,                // 对应BookEntity的lastModified
    val isSaved: Boolean? = null                   // 对应BookEntity的isSaved
)

/**
 * 图书封面链接
 */
data class ImageLinks(
    val smallThumbnail: String? = null,
    val thumbnail: String? = null
)

/**
 * 行业标识符（ISBN等）
 */
data class IndustryIdentifier(
    val type: String,
    val identifier: String
) 