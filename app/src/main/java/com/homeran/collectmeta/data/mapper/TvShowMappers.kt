package com.homeran.collectmeta.data.mapper

import com.homeran.collectmeta.data.db.entities.*
import com.homeran.collectmeta.domain.model.TvEpisode
import com.homeran.collectmeta.domain.model.TvSeason
import com.homeran.collectmeta.domain.model.TvShow

/**
 * 将电视剧数据实体转换为领域模型
 */
fun MediaEntity.toTvShow(
    details: TvShowDetailsEntity,
    watchProgress: TvShowWatchProgressEntity?,
    seasons: List<TvSeasonEntity>
): TvShow {
    // 解析第一次播出日期获取年份
    val firstAirYear = try {
        val parts = details.firstAirDate.split("-")
        if (parts.isNotEmpty()) parts[0].toInt() else 0
    } catch (e: Exception) {
        0
    }

    // 解析最后播出日期获取年份
    val lastAirYear = try {
        details.lastAirDate?.split("-")?.get(0)?.toInt()
    } catch (e: Exception) {
        null
    }

    // 解析genres JSON字符串到列表
    val genresList = try {
        if (genres.isNotEmpty()) {
            genres.removePrefix("[").removeSuffix("]")
                .split(",")
                .map { it.trim('"') }
        } else {
            emptyList()
        }
    } catch (e: Exception) {
        emptyList()
    }

    return TvShow(
        id = id,
        title = title,
        originalTitle = originalTitle,
        firstAirYear = firstAirYear,
        lastAirYear = lastAirYear,
        tmdbId = details.tmdbId,
        cover = cover,
        overview = description,
        status = WatchStatus.valueOf(status.name),
        tvShowStatus = details.showStatus,
        userRating = userRating,
        userComment = userComment,
        genres = genresList,
        seasons = seasons.map { it.toTvSeason() },
        lastModified = lastModified,
        notionPageId = notionPageId
    )
}

/**
 * 将电视剧季数据实体转换为领域模型
 */
fun TvSeasonEntity.toTvSeason(
    episodes: List<TvEpisodeEntity> = emptyList()
): TvSeason {
    return TvSeason(
        id = id,
        seasonNumber = seasonNumber,
        name = name,
        overview = overview,
        airDate = airDate,
        episodeCount = episodeCount,
        posterPath = posterPath,
        episodes = episodes.map { it.toTvEpisode() }
    )
}

/**
 * 将电视剧集数据实体转换为领域模型
 */
fun TvEpisodeEntity.toTvEpisode(): TvEpisode {
    return TvEpisode(
        id = id,
        episodeNumber = episodeNumber,
        name = name,
        overview = overview,
        airDate = airDate,
        runtime = runtime,
        stillPath = stillPath,
        voteAverage = voteAverage,
        voteCount = voteCount
    )
}

/**
 * 将电视剧领域模型转换为数据实体
 */
fun TvShow.toMediaEntity(): MediaEntity {
    // 将genres列表转换为JSON字符串
    val genresJson = genres?.let { 
        "[${it.joinToString(",") { "\"$it\"" }}]" 
    } ?: "[]"
    
    return MediaEntity(
        id = id,
        title = title,
        originalTitle = originalTitle ?: "",
        description = overview ?: "",
        cover = cover ?: "",
        year = firstAirYear,
        rating = 0.0f,
        overallRating = 0.0f,
        genres = genresJson,
        lastModified = lastModified,
        notionPageId = notionPageId,
        status = MediaStatus.valueOf(status.name),
        userRating = userRating,
        userComment = userComment,
        userTags = "[]",
        doubanUrl = null,
        createdAt = System.currentTimeMillis(),
        releaseDate = null,
        releaseStatus = "RELEASED",
        type = MediaType.TV
    )
}

/**
 * 将电视剧领域模型转换为详情数据实体
 */
fun TvShow.toTvShowDetailsEntity(): TvShowDetailsEntity {
    return TvShowDetailsEntity(
        mediaId = id,
        tmdbId = tmdbId ?: "",
        totalEpisodes = seasons.sumOf { it.episodeCount },
        totalSeasons = seasons.size,
        showStatus = tvShowStatus ?: TvShowStatus.UNKNOWN,
        firstAirDate = seasons.minByOrNull { it.airDate }?.airDate ?: "",
        lastAirDate = seasons.maxByOrNull { it.airDate }?.airDate,
        network = "", // 需要从其他地方获取，这里暂时设为空
        episodeRuntime = seasons.firstOrNull()?.episodes?.firstOrNull()?.runtime ?: 0
    )
}

/**
 * 将电视剧领域模型转换为观看进度数据实体
 */
fun TvShow.toTvShowWatchProgressEntity(): TvShowWatchProgressEntity {
    return TvShowWatchProgressEntity(
        tvShowId = id,
        watchStatus = status,
        currentSeason = 1,  // 默认值
        currentEpisode = 1, // 默认值
        watchedEpisodes = emptySet() // 默认无观看记录
    )
}

/**
 * 将电视剧季领域模型转换为数据实体
 */
fun TvSeason.toTvSeasonEntity(tvShowId: String): TvSeasonEntity {
    return TvSeasonEntity(
        id = id,
        tvShowId = tvShowId,
        seasonNumber = seasonNumber,
        name = name,
        overview = overview,
        airDate = airDate,
        episodeCount = episodeCount,
        posterPath = posterPath
    )
}

/**
 * 将电视剧集领域模型转换为数据实体
 */
fun TvEpisode.toTvEpisodeEntity(seasonId: String): TvEpisodeEntity {
    return TvEpisodeEntity(
        id = id,
        seasonId = seasonId,
        episodeNumber = episodeNumber,
        name = name,
        overview = overview,
        airDate = airDate,
        runtime = runtime,
        stillPath = stillPath,
        voteAverage = voteAverage,
        voteCount = voteCount
    )
} 