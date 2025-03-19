package com.homeran.collectmeta.data.remote.model

import com.google.gson.annotations.SerializedName

/**
 * 豆瓣图书响应模型
 */
data class DoubanBookResponse(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("subtitle") val subtitle: String? = null,
    @SerializedName("origin_title") val originTitle: String? = null,
    @SerializedName("author") val authors: List<String> = emptyList(),
    @SerializedName("translator") val translators: List<String> = emptyList(),
    @SerializedName("publisher") val publisher: String? = null,
    @SerializedName("pubdate") val pubDate: String? = null,
    @SerializedName("rating") val rating: DoubanRating? = null,
    @SerializedName("images") val images: BookImages? = null,
    @SerializedName("binding") val binding: String? = null,
    @SerializedName("price") val price: String? = null,
    @SerializedName("isbn10") val isbn10: String? = null,
    @SerializedName("isbn13") val isbn13: String? = null,
    @SerializedName("pages") val pages: String? = null,
    @SerializedName("summary") val summary: String? = null,
    @SerializedName("catalog") val catalog: String? = null,
    @SerializedName("series") val series: Series? = null,
    @SerializedName("url") val url: String? = null,
    @SerializedName("tags") val tags: List<Tag> = emptyList()
)

/**
 * 豆瓣搜索响应模型
 */
data class DoubanSearchResponse(
    @SerializedName("count") val count: Int = 0,
    @SerializedName("start") val start: Int = 0,
    @SerializedName("total") val total: Int = 0,
    @SerializedName("books") val books: List<DoubanBookResponse> = emptyList()
)

/**
 * 豆瓣图书评分
 */
data class DoubanRating(
    @SerializedName("max") val max: Int = 10,
    @SerializedName("average") val average: Float = 0f,
    @SerializedName("numRaters") val numRaters: Int = 0
)

/**
 * 豆瓣图书封面图片
 */
data class BookImages(
    @SerializedName("small") val small: String? = null,
    @SerializedName("medium") val medium: String? = null,
    @SerializedName("large") val large: String? = null
)

/**
 * 豆瓣图书系列
 */
data class Series(
    @SerializedName("id") val id: String? = null,
    @SerializedName("title") val title: String? = null
)

/**
 * 豆瓣图书标签
 */
data class Tag(
    @SerializedName("count") val count: Int = 0,
    @SerializedName("name") val name: String = "",
    @SerializedName("title") val title: String = ""
) 