package com.homeran.collectmeta.data.db.converters

import androidx.room.TypeConverter
import com.homeran.collectmeta.data.db.entities.MediaStatus
import com.homeran.collectmeta.data.db.entities.MediaType
import com.homeran.collectmeta.data.db.entities.PlayStatus
import com.homeran.collectmeta.data.db.entities.TvShowStatus
import com.homeran.collectmeta.data.db.entities.WatchStatus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

/**
 * Room数据库类型转换器
 */
class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
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
    fun tvShowStatusToString(status: TvShowStatus): String {
        return status.name
    }
    
    @TypeConverter
    fun stringToTvShowStatus(value: String): TvShowStatus {
        return TvShowStatus.valueOf(value)
    }
    
    @TypeConverter
    fun watchStatusToString(status: WatchStatus): String {
        return status.name
    }
    
    @TypeConverter
    fun stringToWatchStatus(value: String): WatchStatus {
        return WatchStatus.valueOf(value)
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
    fun fromStringSet(value: Set<String>): String {
        return Gson().toJson(value)
    }
    
    @TypeConverter
    fun toStringSet(value: String): Set<String> {
        val setType = object : TypeToken<Set<String>>() {}.type
        return Gson().fromJson(value, setType)
    }
} 