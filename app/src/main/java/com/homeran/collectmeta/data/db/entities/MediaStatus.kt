package com.homeran.collectmeta.data.db.entities

/**
 * 媒体状态枚举，用于定义媒体的消费状态
 */
enum class MediaStatus {
    WANT_TO_CONSUME,  // 想读/想看/想玩
    CONSUMING,        // 在读/在看/在玩
    COMPLETED,         // 已读/已看/已玩
    DROPPED           // 已放弃
} 