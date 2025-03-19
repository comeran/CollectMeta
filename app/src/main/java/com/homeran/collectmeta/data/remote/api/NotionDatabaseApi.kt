package com.homeran.collectmeta.data.remote.api

import com.homeran.collectmeta.data.remote.model.notion.NotionDatabaseResponse
import com.homeran.collectmeta.data.remote.model.notion.NotionPageResponse
import com.homeran.collectmeta.data.remote.model.notion.NotionQueryRequest
import com.homeran.collectmeta.data.remote.model.notion.NotionUpdatePageRequest
import retrofit2.Response
import retrofit2.http.*

/**
 * Notion数据库API接口
 */
interface NotionDatabaseApi {
    /**
     * 查询数据库中的页面（书籍）
     * @param databaseId Notion数据库ID
     * @param requestBody 查询请求体
     * @return 数据库查询结果
     */
    @POST("databases/{database_id}/query")
    suspend fun queryDatabase(
        @Path("database_id") databaseId: String,
        @Body requestBody: NotionQueryRequest
    ): Response<NotionDatabaseResponse>
    
    /**
     * 获取单个页面（书籍）详情
     * @param pageId 页面ID
     * @return 页面详情
     */
    @GET("pages/{page_id}")
    suspend fun getPage(
        @Path("page_id") pageId: String
    ): Response<NotionPageResponse>
    
    /**
     * 更新页面（书籍）
     * @param pageId 页面ID
     * @param requestBody 更新请求体
     * @return 更新后的页面
     */
    @PATCH("pages/{page_id}")
    suspend fun updatePage(
        @Path("page_id") pageId: String,
        @Body requestBody: NotionUpdatePageRequest
    ): Response<NotionPageResponse>
    
    /**
     * 创建页面（书籍）
     * @param requestBody 创建请求体
     * @return 创建的页面
     */
    @POST("pages")
    suspend fun createPage(
        @Body requestBody: NotionUpdatePageRequest
    ): Response<NotionPageResponse>
} 