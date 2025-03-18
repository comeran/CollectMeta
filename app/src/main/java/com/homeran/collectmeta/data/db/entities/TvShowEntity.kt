package com.homeran.collectmeta.data.db.entities

import androidx.room.*
import com.homeran.collectmeta.data.db.entities.TvShowStatus

@Entity(tableName = "tv_shows")
data class TvShowEntity(
    @PrimaryKey
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
    val status: MediaStatus
)

@Entity(tableName = "tv_seasons")
data class TvSeasonEntity(
    @PrimaryKey
    val id: String,
    
    @ForeignKey(
        entity = TvShowEntity::class,
        parentColumns = ["id"],
        childColumns = ["tvShowId"],
        onDelete = ForeignKey.CASCADE
    )
    val tvShowId: String,
    
    val seasonNumber: Int,
    val name: String,
    val overview: String?,
    val posterPath: String?,
    val airDate: String?,
    val episodeCount: Int
)

@Entity(tableName = "tv_episodes")
data class TvEpisodeEntity(
    @PrimaryKey
    val id: String,
    
    @ForeignKey(
        entity = TvShowEntity::class,
        parentColumns = ["id"],
        childColumns = ["tvShowId"],
        onDelete = ForeignKey.CASCADE
    )
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

data class TvShowWithDetails(
    @Embedded
    val tvShow: TvShowEntity,
    
    @Relation(
        parentColumn = "id",
        entityColumn = "tvShowId"
    )
    val seasons: List<TvSeasonWithEpisodes>
)

data class TvSeasonWithEpisodes(
    @Embedded
    val season: TvSeasonEntity,
    
    @Relation(
        parentColumn = "id",
        entityColumn = "tvShowId"
    )
    val episodes: List<TvEpisodeEntity>
) 