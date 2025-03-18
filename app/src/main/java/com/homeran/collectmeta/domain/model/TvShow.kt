package com.homeran.collectmeta.domain.model

import com.homeran.collectmeta.data.db.entities.TvShowStatus
import com.homeran.collectmeta.data.db.entities.WatchStatus

/**
 * 电视剧领域模型
 */
data class TvShow(
    val id: String,
    val title: String,
    val originalTitle: String?,
    val overview: String?,
    val posterPath: String?,
    val backdropPath: String?,
    val firstAirYear: Int,
    val lastAirYear: Int?,
    val tvShowStatus: TvShowStatus,
    val genres: List<String>,
    val userRating: Float?,
    val userComment: String?,
    val status: WatchStatus,
    val seasons: List<TvSeason>
)

/**
 * 电视剧季领域模型
 */
data class TvSeason(
    val id: String,
    val tvShowId: String,
    val seasonNumber: Int,
    val name: String,
    val overview: String?,
    val posterPath: String?,
    val airDate: String?,
    val episodeCount: Int,
    val episodes: List<TvEpisode>
)

/**
 * 电视剧集领域模型
 */
data class TvEpisode(
    val id: String,
    val tvShowId: String,
    val seasonNumber: Int,
    val episodeNumber: Int,
    val name: String,
    val overview: String?,
    val airDate: String?,
    val stillPath: String?,
    val voteAverage: Float,
    val voteCount: Int
) 