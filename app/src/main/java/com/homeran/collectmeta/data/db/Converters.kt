package com.homeran.collectmeta.data.db

import androidx.room.TypeConverter
import com.homeran.collectmeta.data.db.entities.GameStatus
import com.homeran.collectmeta.data.db.entities.MediaStatus
import com.homeran.collectmeta.data.db.entities.MediaType
import com.homeran.collectmeta.data.db.entities.PlayStatus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date
import com.homeran.collectmeta.data.model.TvSeason
import com.homeran.collectmeta.data.model.TvEpisode

/**
 * Room数据库类型转换器，用于处理复杂类型与数据库支持类型之间的转换
 */
class Converters {
    private val gson = Gson()
    
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }
    
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
    
    @TypeConverter
    fun mediaTypeToString(mediaType: MediaType): String {
        return mediaType.name
    }
    
    @TypeConverter
    fun stringToMediaType(value: String): MediaType {
        return MediaType.valueOf(value)
    }
    
    @TypeConverter
    fun mediaStatusToString(status: MediaStatus): String {
        return status.name
    }
    
    @TypeConverter
    fun stringToMediaStatus(value: String): MediaStatus {
        return MediaStatus.valueOf(value)
    }
    
    @TypeConverter
    fun gameStatusToString(status: GameStatus): String {
        return status.name
    }
    
    @TypeConverter
    fun stringToGameStatus(value: String): GameStatus {
        return GameStatus.valueOf(value)
    }
    
    @TypeConverter
    fun playStatusToString(status: PlayStatus): String {
        return status.name
    }
    
    @TypeConverter
    fun stringToPlayStatus(value: String): PlayStatus {
        return PlayStatus.valueOf(value)
    }
    
    @TypeConverter
    fun fromStringMap(value: Map<String, String>): String {
        return gson.toJson(value)
    }
    
    @TypeConverter
    fun toStringMap(value: String): Map<String, String> {
        val mapType = object : TypeToken<Map<String, String>>() {}.type
        return gson.fromJson(value, mapType)
    }

    @TypeConverter
    fun fromTvSeasonList(value: List<TvSeason>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toTvSeasonList(value: String): List<TvSeason> {
        val listType = object : TypeToken<List<TvSeason>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromTvEpisodeList(value: List<TvEpisode>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toTvEpisodeList(value: String): List<TvEpisode> {
        val listType = object : TypeToken<List<TvEpisode>>() {}.type
        return gson.fromJson(value, listType)
    }
} 