package com.homeran.collectmeta.data.remote.api

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface DoubanApi {
    @GET("v2/book/search")
    suspend fun searchBooks(
        @Header("Authorization") apiKey: String,
        @Query("q") query: String,
        @Query("start") start: Int = 0,
        @Query("count") count: Int = 20
    ): DoubanSearchResponse

    @GET("v2/book/{id}")
    suspend fun getBookDetails(
        @Header("Authorization") apiKey: String,
        @Query("id") bookId: String
    ): DoubanBookDetails
}

data class DoubanSearchResponse(
    val count: Int,
    val start: Int,
    val total: Int,
    val books: List<DoubanBook>
)

data class DoubanBook(
    val id: String,
    val title: String,
    val subtitle: String?,
    val author: List<String>,
    val pubdate: String,
    val tags: List<DoubanTag>,
    val origin_title: String?,
    val image: String,
    val binding: String?,
    val translator: List<String>?,
    val catalog: String?,
    val pages: String?,
    val images: DoubanImages,
    val alt: String,
    val id_class: String?,
    val price: String,
    val publisher: String?,
    val isbn10: String?,
    val isbn13: String?,
    val url: String,
    val alt_title: String?,
    val author_intro: String?,
    val summary: String?,
    val ebook_price: String?,
    val ebook_url: String?,
    val douban_url: String?
)

data class DoubanBookDetails(
    val id: String,
    val title: String,
    val subtitle: String?,
    val author: List<String>,
    val pubdate: String,
    val tags: List<DoubanTag>,
    val origin_title: String?,
    val image: String,
    val binding: String?,
    val translator: List<String>?,
    val catalog: String?,
    val pages: String?,
    val images: DoubanImages,
    val alt: String,
    val id_class: String?,
    val price: String,
    val publisher: String?,
    val isbn10: String?,
    val isbn13: String?,
    val url: String,
    val alt_title: String?,
    val author_intro: String?,
    val summary: String?,
    val ebook_price: String?,
    val ebook_url: String?,
    val douban_url: String?,
    val rating: DoubanRating,
    val series: DoubanSeries?
)

data class DoubanTag(
    val count: Int,
    val name: String,
    val title: String
)

data class DoubanImages(
    val small: String,
    val large: String,
    val medium: String
)

data class DoubanRating(
    val max: Int,
    val numRaters: Int,
    val average: String,
    val min: Int
)

data class DoubanSeries(
    val id: String,
    val title: String
) 