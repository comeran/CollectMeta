package com.homeran.collectmeta.data.db.converters

import androidx.room.TypeConverter
import com.homeran.collectmeta.data.db.entities.ReadingStatus
import java.util.Date

/**
 * Room数据库类型转换器
 * 用于处理枚举类型、日期和JSON等复杂类型的转换
 */
class Converters {
    /**
     * 将ReadingStatus枚举转换为存储在数据库中的字符串
     */
    @TypeConverter
    fun fromReadingStatus(status: ReadingStatus): String {
        return status.name
    }

    /**
     * 将数据库中的字符串转换为ReadingStatus枚举
     */
    @TypeConverter
    fun toReadingStatus(statusString: String): ReadingStatus {
        return try {
            ReadingStatus.valueOf(statusString)
        } catch (e: Exception) {
            ReadingStatus.WANT_TO_READ // 默认为"想读"状态
        }
    }

    /**
     * 将Date转换为Long存储在数据库中
     */
    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    /**
     * 将数据库中的Long转换为Date
     */
    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }

    /**
     * 将List<String>转换为JSON字符串存储在数据库中
     */
    @TypeConverter
    fun fromStringList(list: List<String>?): String? {
        return list?.joinToString(",")
    }

    /**
     * 将数据库中的JSON字符串转换为List<String>
     */
    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.split(",")?.map { it.trim() }
    }
} 