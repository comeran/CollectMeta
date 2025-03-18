package com.homeran.collectmeta.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import androidx.room.ColumnInfo
import androidx.room.Index
import com.homeran.collectmeta.data.db.converters.Converters
import com.homeran.collectmeta.data.db.converters.DateConverter
import java.time.LocalDate

/**
 * 游戏实体
 */
@Entity(tableName = "game")
@TypeConverters(DateConverter::class, Converters::class)
data class GameEntity(
    @PrimaryKey
    val id: String,
    val igdbId: String,
    val title: String,
    val originalTitle: String? = null,
    val description: String? = null,
    val coverImageUrl: String? = null,
    val backdropImageUrl: String? = null,
    val releaseDate: LocalDate? = null,
    val genres: List<String> = emptyList(),
    val publishers: List<String> = emptyList(),
    val playStatus: PlayStatus = PlayStatus.PLAN_TO_PLAY,
    val userRating: Float? = null,
    val userComment: String? = null,
    val metacriticScore: Int? = null,
    val igdbRating: Float? = null,
    val lastPlayedDate: LocalDate? = null,
    val dateAdded: LocalDate = LocalDate.now(),
    val dateModified: LocalDate = LocalDate.now()
)

/**
 * 游戏详情实体
 */
@Entity(tableName = "game_details")
data class GameDetailsEntity(
    @PrimaryKey
    val gameId: String,
    val developers: List<String> = emptyList(),
    val playtime: Int? = null,
    val esrbRating: String? = null,
    val website: String? = null,
    val additionalInfo: String? = null
)

/**
 * 游戏平台实体
 */
@Entity(
    tableName = "game_platform",
    primaryKeys = ["id", "gameId"],
    indices = [Index(value = ["gameId"])]
)
@TypeConverters(DateConverter::class)
data class GamePlatformEntity(
    val id: String,
    val gameId: String,
    val name: String,
    val owned: Boolean = false,
    val isDigital: Boolean = false,
    val store: String? = null,
    
    @ColumnInfo(name = "purchase_date")
    @TypeConverters(DateConverter::class)
    val purchaseDate: LocalDate? = null,
    
    @ColumnInfo(name = "purchase_price")
    val purchasePrice: Double? = null
)

/**
 * 游戏DLC实体
 */
@Entity(
    tableName = "game_dlc",
    primaryKeys = ["id", "gameId"],
    indices = [Index(value = ["gameId"])]
)
@TypeConverters(DateConverter::class)
data class GameDLCEntity(
    val id: String,
    val gameId: String,
    val name: String,
    val description: String? = null,
    
    @ColumnInfo(name = "release_date")
    @TypeConverters(DateConverter::class)
    val releaseDate: LocalDate? = null,
    
    val owned: Boolean = false,
    val completed: Boolean = false,
    
    @ColumnInfo(name = "date_added")
    @TypeConverters(DateConverter::class)
    val dateAdded: LocalDate = LocalDate.now()
) 