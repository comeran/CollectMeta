package com.homeran.collectmeta.data.mapper

import com.homeran.collectmeta.data.db.entities.MediaEntity
import com.homeran.collectmeta.data.db.entities.MediaType
import com.homeran.collectmeta.data.db.entities.MovieDetailsEntity
import com.homeran.collectmeta.data.db.entities.MovieWithDetails
import com.homeran.collectmeta.domain.model.Movie
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * 电影实体映射器，实现领域模型与数据库实体之间的转换
 */

/**
 * 将MovieWithDetails实体转换为Movie领域模型
 */
fun MovieWithDetails.toDomainModel(): Movie {
    return Movie(
        id = this.media.id,
        title = this.media.title,
        originalTitle = this.media.originalTitle,
        year = this.media.year,
        cover = this.media.cover,
        description = this.media.description,
        rating = this.media.rating,
        overallRating = this.media.overallRating,
        genres = Gson().fromJson(this.media.genres, object : TypeToken<List<String>>() {}.type),
        lastModified = this.media.lastModified,
        notionPageId = this.media.notionPageId,
        status = this.media.status,
        userRating = this.media.userRating,
        userComment = this.media.userComment,
        userTags = Gson().fromJson(this.media.userTags, object : TypeToken<List<String>>() {}.type),
        doubanUrl = this.media.doubanUrl,
        createdAt = this.media.createdAt,
        releaseDate = this.media.releaseDate,
        releaseStatus = this.media.releaseStatus,
        director = this.movieDetails.director,
        cast = Gson().fromJson(this.movieDetails.cast, object : TypeToken<List<String>>() {}.type),
        duration = this.movieDetails.duration,
        region = this.movieDetails.region,
        episodeCount = this.movieDetails.episodeCount,
        episodeDuration = this.movieDetails.episodeDuration,
        tmdbId = this.movieDetails.tmdbId,
        traktUrl = this.movieDetails.traktUrl
    )
}

/**
 * 将Movie领域模型转换为MediaEntity实体
 */
fun Movie.toMediaEntity(): MediaEntity {
    return MediaEntity(
        id = this.id,
        type = MediaType.MOVIE,
        title = this.title,
        originalTitle = this.originalTitle,
        year = this.year,
        cover = this.cover,
        description = this.description,
        rating = this.rating,
        overallRating = this.overallRating,
        genres = Gson().toJson(this.genres),
        lastModified = this.lastModified,
        notionPageId = this.notionPageId,
        status = this.status,
        userRating = this.userRating,
        userComment = this.userComment,
        userTags = Gson().toJson(this.userTags),
        doubanUrl = this.doubanUrl,
        createdAt = this.createdAt,
        releaseDate = this.releaseDate,
        releaseStatus = this.releaseStatus
    )
}

/**
 * 将Movie领域模型转换为MovieDetailsEntity实体
 */
fun Movie.toMovieDetailsEntity(): MovieDetailsEntity {
    return MovieDetailsEntity(
        mediaId = this.id,
        director = this.director,
        cast = Gson().toJson(this.cast),
        duration = this.duration,
        region = this.region,
        episodeCount = this.episodeCount,
        episodeDuration = this.episodeDuration,
        tmdbId = this.tmdbId,
        traktUrl = this.traktUrl
    )
} 