package com.homeran.collectmeta.data.db.entities

/**
 * 书籍阅读状态枚举
 */
enum class ReadingStatus {
    WANT_TO_READ, // 想读（橙色）
    READING,      // 在读（蓝色）
    READ,         // 读过（绿色）
    ABANDONED     // 放弃（红色）
} 