package com.homeran.collectmeta.data.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * 列表转换器，用于Room数据库实体中的List类型与数据库支持的String类型之间的转换
 */
class ListConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return if (value == null) "[]" else gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value == null || value == "[]") return emptyList()
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromIntList(value: List<Int>?): String {
        return if (value == null) "[]" else gson.toJson(value)
    }

    @TypeConverter
    fun toIntList(value: String?): List<Int> {
        if (value == null || value == "[]") return emptyList()
        val listType = object : TypeToken<List<Int>>() {}.type
        return gson.fromJson(value, listType)
    }
} 