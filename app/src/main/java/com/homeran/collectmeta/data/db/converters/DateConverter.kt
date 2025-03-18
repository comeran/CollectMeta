package com.homeran.collectmeta.data.db.converters

import androidx.room.TypeConverter
import java.time.LocalDate

/**
 * LocalDate 类型转换器
 */
class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): String? {
        return date?.toString()
    }
} 