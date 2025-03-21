package com.homeran.collectmeta.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * API配置实体类
 * 用于存储外部API的配置信息（如API密钥、数据库ID等）
 */
@Entity(tableName = "api_configs")
data class ApiConfigEntity(
    /**
     * 配置唯一标识符，通常为API类型
     * 例如：
     * - "notion" - Notion API
     * - "notion_book" - Notion书籍API
     * - "notion_movie" - Notion电影API
     * - "notion_tvshow" - Notion电视剧API
     * - "notion_game" - Notion游戏API
     * - "douban" - 豆瓣API
     * - "open_library" - Open Library API
     * - "google_books" - Google Books API
     * - "tmdb" - TMDB API
     * - "igdb" - IGDB API
     */
    @PrimaryKey
    val configId: String,
    
    /**
     * API密钥或访问令牌
     * - Notion APIs: 集成令牌
     * - Google Books: API密钥
     * - TMDB: API令牌
     * - IGDB: 客户端ID
     */
    val apiKey: String,
    
    /**
     * 是否启用该API
     */
    val isEnabled: Boolean = true,
    
    /**
     * API的基础URL
     * - Notion APIs: Notion API URL
     * - Open Library: API URL
     * - Google Books: API URL
     * - TMDB: API URL
     * - IGDB: API URL
     */
    val baseUrl: String,
    
    /**
     * 自定义参数1
     * - Notion APIs: 数据库ID
     * - IGDB: 客户端密钥
     */
    val param1: String? = null,
    
    /**
     * 自定义参数2
     * - 保留字段，用于未来扩展
     */
    val param2: String? = null,
    
    /**
     * 自定义参数3
     * - 保留字段，用于未来扩展
     */
    val param3: String? = null,
    
    /**
     * 自定义参数4
     * - 保留字段，用于未来扩展
     */
    val param4: String? = null,
    
    /**
     * 自定义参数5
     * - 保留字段，用于未来扩展
     */
    val param5: String? = null,
    
    /**
     * 最后更新时间（毫秒时间戳）
     */
    val lastUpdated: Long = System.currentTimeMillis()
) 