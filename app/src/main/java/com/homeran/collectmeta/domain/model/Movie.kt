package com.homeran.collectmeta.domain.model

import com.homeran.collectmeta.data.db.entities.MediaStatus
import java.util.Date

/**
 * 电影领域模型
 */
data class Movie(
    val id: String,
    val title: String,
    val originalTitle: String?,
    val year: Int,
    val director: String?,
    val cast: List<String>?,
    val duration: Int?,
    val region: String?,
    val genres: List<String>,
    val overview: String?,
    val cover: String?,
    val tmdbId: String?,
    val status: MediaStatus,
    val userRating: Float?,
    val userComment: String?,
    val notionPageId: String?,
    val lastModified: Long
) 