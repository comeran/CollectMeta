package com.homeran.collectmeta.data.mapper

import com.homeran.collectmeta.data.db.entities.MediaStatus
import com.homeran.collectmeta.data.db.entities.TvShowStatus
import com.homeran.collectmeta.data.db.entities.WatchStatus
import com.homeran.collectmeta.data.remote.api.*
import com.homeran.collectmeta.domain.model.Movie
import com.homeran.collectmeta.domain.model.TvShow
import java.text.SimpleDateFormat
import java.util.*

/**
 * 将电影转换为Notion页面创建请求
 */
fun Movie.toNotionPageCreateRequest(parent: NotionParent): NotionPageCreateRequest {
    val properties = mutableMapOf<String, NotionProperty>()
    
    // 标题
    properties["标题"] = NotionTitleProperty(listOf(NotionRichText(
        type = "text",
        text = NotionText(content = title)
    )))
    
    // 状态
    properties["状态"] = NotionSelectProperty(NotionSelectOption(
        name = when(status) {
            MediaStatus.CONSUMED -> "看过"
            MediaStatus.CONSUMING -> "在看"
            MediaStatus.WANT_TO_CONSUME -> "想看"
            else -> "其他"
        }
    ))
    
    // 类型
    properties["类型"] = NotionSelectProperty(NotionSelectOption(name = "电影"))
    
    // 评分
    userRating?.let {
        properties["评分"] = NotionNumberProperty(it.toDouble())
    }
    
    // 发行年份
    properties["发行年份"] = NotionNumberProperty(year.toDouble())
    
    // 导演
    properties["导演"] = NotionRichTextProperty(listOf(NotionRichText(
        type = "text",
        text = NotionText(content = director ?: "")
    )))
    
    // 主演
    val castText = cast?.joinToString(", ") ?: ""
    properties["主演"] = NotionRichTextProperty(listOf(NotionRichText(
        type = "text",
        text = NotionText(content = castText)
    )))
    
    // 片长
    duration?.let {
        properties["片长"] = NotionNumberProperty(it.toDouble())
    }
    
    // 原始标题
    properties["原始标题"] = NotionRichTextProperty(listOf(NotionRichText(
        type = "text",
        text = NotionText(content = originalTitle ?: "")
    )))
    
    // 用户评论
    userComment?.let {
        properties["评论"] = NotionRichTextProperty(listOf(NotionRichText(
            type = "text",
            text = NotionText(content = it)
        )))
    }
    
    // 封面图
    val coverUrl = cover
    val children = if (!coverUrl.isNullOrEmpty()) {
        listOf(
            NotionParagraphBlock(
                paragraph = NotionParagraph(
                    rich_text = listOf(
                        NotionRichText(
                            type = "text",
                            text = NotionText(content = overview ?: "")
                        )
                    )
                )
            )
        )
    } else {
        listOf()
    }
    
    // 封面图片
    val icon = if (!coverUrl.isNullOrEmpty()) {
        NotionIcon(
            type = "external",
            external = NotionExternal(url = coverUrl)
        )
    } else {
        null
    }
    
    return NotionPageCreateRequest(
        parent = parent,
        properties = properties,
        children = children,
        icon = icon
    )
}

/**
 * 将电影转换为Notion页面更新请求
 */
fun Movie.toNotionPageUpdateRequest(): NotionPageUpdateRequest {
    val properties = mutableMapOf<String, NotionProperty>()
    
    // 标题
    properties["标题"] = NotionTitleProperty(listOf(NotionRichText(
        type = "text",
        text = NotionText(content = title)
    )))
    
    // 状态
    properties["状态"] = NotionSelectProperty(NotionSelectOption(
        name = when(status) {
            MediaStatus.CONSUMED -> "看过"
            MediaStatus.CONSUMING -> "在看"
            MediaStatus.WANT_TO_CONSUME -> "想看"
            else -> "其他"
        }
    ))
    
    // 评分
    userRating?.let {
        properties["评分"] = NotionNumberProperty(it.toDouble())
    }
    
    // 用户评论
    userComment?.let {
        properties["评论"] = NotionRichTextProperty(listOf(NotionRichText(
            type = "text",
            text = NotionText(content = it)
        )))
    }
    
    // 封面图片
    val coverUrl = cover
    val icon = if (!coverUrl.isNullOrEmpty()) {
        NotionIcon(
            type = "external",
            external = NotionExternal(url = coverUrl)
        )
    } else {
        null
    }
    
    return NotionPageUpdateRequest(
        properties = properties,
        icon = icon
    )
}

/**
 * 将电视剧转换为Notion页面创建请求
 */
fun TvShow.toNotionPageCreateRequest(parent: NotionParent): NotionPageCreateRequest {
    val properties = mutableMapOf<String, NotionProperty>()
    
    // 标题
    properties["标题"] = NotionTitleProperty(listOf(NotionRichText(
        type = "text",
        text = NotionText(content = title)
    )))
    
    // 状态
    properties["状态"] = NotionSelectProperty(NotionSelectOption(
        name = when(status) {
            WatchStatus.COMPLETED -> "看过"
            WatchStatus.WATCHING -> "在看"
            WatchStatus.PLAN_TO_WATCH -> "想看"
            else -> "其他"
        }
    ))
    
    // 类型
    properties["类型"] = NotionSelectProperty(NotionSelectOption(name = "电视剧"))
    
    // 评分
    userRating?.let {
        properties["评分"] = NotionNumberProperty(it.toDouble())
    }
    
    // 首播年份
    properties["首播年份"] = NotionNumberProperty(firstAirYear.toDouble())
    
    // 季数
    properties["季数"] = NotionNumberProperty(seasons.size.toDouble())
    
    // 集数
    val totalEpisodes = seasons.sumOf { it.episodes.size }
    properties["集数"] = NotionNumberProperty(totalEpisodes.toDouble())
    
    // 原始标题
    properties["原始标题"] = NotionRichTextProperty(listOf(NotionRichText(
        type = "text",
        text = NotionText(content = originalTitle ?: "")
    )))
    
    // 用户评论
    userComment?.let {
        properties["评论"] = NotionRichTextProperty(listOf(NotionRichText(
            type = "text",
            text = NotionText(content = it)
        )))
    }
    
    // 剧集状态
    properties["剧集状态"] = NotionSelectProperty(NotionSelectOption(
        name = when(tvShowStatus) {
            TvShowStatus.AIRING -> "连载中"
            TvShowStatus.ENDED -> "已完结"
            TvShowStatus.CANCELLED -> "已取消"
            TvShowStatus.ANNOUNCED -> "已宣布"
            else -> "未知"
        }
    ))
    
    // 封面图
    val coverUrl = cover
    val children = if (!coverUrl.isNullOrEmpty()) {
        listOf(
            NotionParagraphBlock(
                paragraph = NotionParagraph(
                    rich_text = listOf(
                        NotionRichText(
                            type = "text",
                            text = NotionText(content = overview ?: "")
                        )
                    )
                )
            )
        )
    } else {
        listOf()
    }
    
    // 封面图片
    val icon = if (!coverUrl.isNullOrEmpty()) {
        NotionIcon(
            type = "external",
            external = NotionExternal(url = coverUrl)
        )
    } else {
        null
    }
    
    return NotionPageCreateRequest(
        parent = parent,
        properties = properties,
        children = children,
        icon = icon
    )
}

/**
 * 将电视剧转换为Notion页面更新请求
 */
fun TvShow.toNotionPageUpdateRequest(): NotionPageUpdateRequest {
    val properties = mutableMapOf<String, NotionProperty>()
    
    // 标题
    properties["标题"] = NotionTitleProperty(listOf(NotionRichText(
        type = "text",
        text = NotionText(content = title)
    )))
    
    // 状态
    properties["状态"] = NotionSelectProperty(NotionSelectOption(
        name = when(status) {
            WatchStatus.COMPLETED -> "看过"
            WatchStatus.WATCHING -> "在看"
            WatchStatus.PLAN_TO_WATCH -> "想看"
            else -> "其他"
        }
    ))
    
    // 评分
    userRating?.let {
        properties["评分"] = NotionNumberProperty(it.toDouble())
    }
    
    // 用户评论
    userComment?.let {
        properties["评论"] = NotionRichTextProperty(listOf(NotionRichText(
            type = "text",
            text = NotionText(content = it)
        )))
    }
    
    // 剧集状态
    properties["剧集状态"] = NotionSelectProperty(NotionSelectOption(
        name = when(tvShowStatus) {
            TvShowStatus.AIRING -> "连载中"
            TvShowStatus.ENDED -> "已完结"
            TvShowStatus.CANCELLED -> "已取消"
            TvShowStatus.ANNOUNCED -> "已宣布"
            else -> "未知"
        }
    ))
    
    // 封面图片
    val coverUrl = cover
    val icon = if (!coverUrl.isNullOrEmpty()) {
        NotionIcon(
            type = "external",
            external = NotionExternal(url = coverUrl)
        )
    } else {
        null
    }
    
    return NotionPageUpdateRequest(
        properties = properties,
        icon = icon
    )
} 