package com.homeran.collectmeta.data.repository

import com.homeran.collectmeta.data.db.dao.TvShowDao
import com.homeran.collectmeta.data.db.entities.MediaEntity
import com.homeran.collectmeta.data.db.entities.TvShowDetailsEntity
import com.homeran.collectmeta.data.db.entities.TvShowWatchProgressEntity
import com.homeran.collectmeta.data.db.entities.TvSeasonEntity
import com.homeran.collectmeta.data.db.entities.TvEpisodeEntity
import com.homeran.collectmeta.domain.model.TvShow
import com.homeran.collectmeta.domain.model.TvSeason
import com.homeran.collectmeta.domain.model.TvEpisode
import com.homeran.collectmeta.domain.repository.TvShowRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * 电视剧仓库实现类
 */
class TvShowRepositoryImpl @Inject constructor(
    private val tvShowDao: TvShowDao
) : TvShowRepository {
    
    override fun getAllTvShows(): Flow<List<TvShow>> {
        return tvShowDao.getAllTvShows()
            .map { mediaList ->
                mediaList.map { it.toTvShow() }
            }
    }
    
    override fun getTvShowById(id: String): Flow<TvShow?> {
        return flow {
            val tvShowWithDetails = tvShowDao.getTvShowWithRelations(id)
            
            tvShowDao.getTvShowWatchProgressById(id).collect { progressEntity ->
                tvShowWithDetails?.let {
                    emit(
                        TvShow(
                            id = it.media.id,
                            title = it.media.title,
                            originalTitle = it.media.originalTitle,
                            overview = it.media.overview,
                            posterPath = it.media.posterPath,
                            backdropPath = it.media.backdropPath,
                            firstAirYear = it.details?.firstAirDate?.substring(0, 4)?.toIntOrNull(),
                            lastAirYear = it.details?.lastAirDate?.substring(0, 4)?.toIntOrNull(),
                            tvShowStatus = it.details?.showStatus?.toTvShowStatus(),
                            genres = it.media.genres,
                            userRating = progressEntity?.rating,
                            userComment = progressEntity?.comment,
                            status = progressEntity?.watchStatus,
                            seasons = it.seasons.map { it.toTvSeason() }
                        )
                    )
                }
            }
        }
    }
    
    override suspend fun addTvShow(tvShow: TvShow) {
        val mediaEntity = MediaEntity(
            id = tvShow.id,
            title = tvShow.title,
            originalTitle = tvShow.originalTitle,
            overview = tvShow.overview,
            posterPath = tvShow.posterPath,
            backdropPath = tvShow.backdropPath,
            type = "TV_SHOW",
            genres = tvShow.genres
        )
        tvShowDao.insertTvShow(mediaEntity)

        val detailsEntity = TvShowDetailsEntity(
            mediaId = tvShow.id,
            tmdbId = tvShow.id,
            totalEpisodes = tvShow.seasons.sumOf { it.episodes.size },
            totalSeasons = tvShow.seasons.size,
            showStatus = tvShow.tvShowStatus?.toTvShowStatusEntity(),
            firstAirDate = tvShow.firstAirYear?.toString() ?: "",
            lastAirDate = tvShow.lastAirYear?.toString(),
            network = "",
            episodeRuntime = 0
        )
        tvShowDao.insertTvShowDetails(detailsEntity)

        val progressEntity = TvShowWatchProgressEntity(
            mediaId = tvShow.id,
            watchStatus = tvShow.status?.toString() ?: "PLAN_TO_WATCH",
            userRating = tvShow.userRating,
            userComment = tvShow.userComment
        )
        tvShowDao.insertTvShowWatchProgress(progressEntity)

        tvShow.seasons.forEach { season ->
            val seasonEntity = TvSeasonEntity(
                id = "${tvShow.id}_${season.seasonNumber}",
                mediaId = tvShow.id,
                seasonNumber = season.seasonNumber,
                name = season.name,
                overview = season.overview,
                posterPath = season.posterPath,
                airDate = season.airDate
            )
            tvShowDao.insertTvSeason(seasonEntity)

            season.episodes.forEach { episode ->
                val episodeEntity = TvEpisodeEntity(
                    id = "${seasonEntity.id}_${episode.episodeNumber}",
                    seasonId = seasonEntity.id,
                    episodeNumber = episode.episodeNumber,
                    name = episode.name,
                    overview = episode.overview,
                    airDate = episode.airDate,
                    stillPath = episode.stillPath,
                    voteAverage = episode.voteAverage,
                    voteCount = episode.voteCount,
                    watchStatus = "UNWATCHED"
                )
                tvShowDao.insertTvEpisode(episodeEntity)
            }
        }
    }
    
    override suspend fun deleteTvShow(id: String) {
        tvShowDao.deleteTvShow(MediaEntity(id = id, type = "TV_SHOW"))
        tvShowDao.deleteTvShowDetails(id)
        tvShowDao.deleteTvShowWatchProgress(id)
        tvShowDao.deleteTvShowSeasons(id)
        tvShowDao.deleteTvShowEpisodes(id)
    }
    
    override suspend fun updateWatchStatus(id: String, status: String) {
        tvShowDao.updateWatchStatus(id, status)
    }
    
    override suspend fun updateRating(id: String, rating: Float) {
        tvShowDao.updateRating(id, rating)
    }
    
    override suspend fun updateComment(id: String, comment: String) {
        tvShowDao.updateComment(id, comment)
    }
    
    override suspend fun updateEpisodeWatchStatus(episodeId: String, status: String) {
        tvShowDao.updateEpisodeWatchStatus(episodeId, status)
    }
    
    override fun searchTvShows(query: String): Flow<List<TvShow>> {
        return flow {
            val result = tvShowDao.searchTvShows(query)
            emit(result.map { it.toTvShow() })
        }
    }
    
    override fun getTvShowsByWatchStatus(status: String): Flow<List<TvShow>> {
        return flow {
            val result = tvShowDao.getTvShowsByWatchStatus(status)
            emit(result.map { it.toTvShow() })
        }
    }
    
    private fun MediaEntity.toTvShow(): TvShow {
        return TvShow(
            id = id,
            title = title,
            originalTitle = originalTitle,
            overview = overview,
            posterPath = posterPath,
            backdropPath = backdropPath,
            genres = genres
        )
    }
    
    private fun TvSeasonEntity.toTvSeason(): TvSeason {
        return TvSeason(
            seasonNumber = seasonNumber,
            name = name,
            overview = overview,
            posterPath = posterPath,
            airDate = airDate,
            episodes = emptyList() // Episodes will be loaded separately
        )
    }
    
    private fun TvEpisodeEntity.toTvEpisode(): TvEpisode {
        return TvEpisode(
            episodeNumber = episodeNumber,
            name = name,
            overview = overview,
            airDate = airDate,
            stillPath = stillPath,
            voteAverage = voteAverage,
            voteCount = voteCount
        )
    }
} 