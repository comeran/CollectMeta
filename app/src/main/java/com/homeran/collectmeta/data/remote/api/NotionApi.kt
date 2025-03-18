package com.homeran.collectmeta.data.remote.api

import retrofit2.http.*

interface NotionApi {
    @POST("v1/pages")
    suspend fun createPage(
        @Header("Authorization") token: String,
        @Header("Notion-Version") version: String = "2022-06-28",
        @Body pageCreateRequest: NotionPageCreateRequest
    ): NotionPageResponse
    
    @PATCH("v1/pages/{page_id}")
    suspend fun updatePage(
        @Header("Authorization") token: String,
        @Header("Notion-Version") version: String = "2022-06-28",
        @Path("page_id") pageId: String,
        @Body pageUpdateRequest: NotionPageUpdateRequest
    ): NotionPageResponse
    
    @GET("v1/databases/{database_id}/query")
    suspend fun queryDatabase(
        @Header("Authorization") token: String,
        @Header("Notion-Version") version: String = "2022-06-28",
        @Path("database_id") databaseId: String,
        @Body queryRequest: NotionDatabaseQueryRequest
    ): NotionDatabaseQueryResponse
    
    @GET("v1/pages/{page_id}")
    suspend fun getPage(
        @Header("Authorization") token: String,
        @Header("Notion-Version") version: String = "2022-06-28",
        @Path("page_id") pageId: String
    ): NotionPageResponse
}

data class NotionPageCreateRequest(
    val parent: NotionParent,
    val properties: Map<String, NotionProperty>,
    val children: List<NotionBlock> = emptyList(),
    val icon: NotionIcon? = null,
    val cover: NotionCover? = null
)

data class NotionPageUpdateRequest(
    val properties: Map<String, NotionProperty>,
    val archived: Boolean? = null,
    val icon: NotionIcon? = null,
    val cover: NotionCover? = null
)

data class NotionDatabaseQueryRequest(
    val filter: NotionFilter? = null,
    val sorts: List<NotionSort>? = null,
    val start_cursor: String? = null,
    val page_size: Int? = null
)

data class NotionDatabaseQueryResponse(
    val results: List<NotionPageResponse>,
    val next_cursor: String?,
    val has_more: Boolean
)

data class NotionPageResponse(
    val id: String,
    val created_time: String,
    val last_edited_time: String,
    val created_by: NotionUser,
    val last_edited_by: NotionUser,
    val cover: NotionCover?,
    val icon: NotionIcon?,
    val parent: NotionParent,
    val archived: Boolean,
    val properties: Map<String, NotionProperty>,
    val url: String
)

data class NotionParent(
    val type: String, // "database_id", "page_id", or "workspace"
    val database_id: String? = null,
    val page_id: String? = null
)

data class NotionUser(
    val id: String,
    val name: String?,
    val avatar_url: String?,
    val type: String,
    val person: NotionPerson?
)

data class NotionPerson(
    val email: String
)

data class NotionIcon(
    val type: String, // "emoji" or "external"
    val emoji: String? = null,
    val external: NotionExternal? = null
)

data class NotionCover(
    val type: String, // "external"
    val external: NotionExternal? = null
)

data class NotionExternal(
    val url: String
)

data class NotionFilter(
    val property: String? = null,
    val or: List<NotionFilter>? = null,
    val and: List<NotionFilter>? = null
)

data class NotionSort(
    val property: String,
    val direction: String // "ascending" or "descending"
)

// Different property types
sealed class NotionProperty

data class NotionTitleProperty(
    val title: List<NotionRichText>
) : NotionProperty()

data class NotionRichTextProperty(
    val rich_text: List<NotionRichText>
) : NotionProperty()

data class NotionNumberProperty(
    val number: Double?
) : NotionProperty()

data class NotionSelectProperty(
    val select: NotionSelectOption?
) : NotionProperty()

data class NotionMultiSelectProperty(
    val multi_select: List<NotionSelectOption>
) : NotionProperty()

data class NotionDateProperty(
    val date: NotionDate?
) : NotionProperty()

data class NotionFilesProperty(
    val files: List<NotionFile>
) : NotionProperty()

data class NotionCheckboxProperty(
    val checkbox: Boolean
) : NotionProperty()

data class NotionUrlProperty(
    val url: String?
) : NotionProperty()

data class NotionEmailProperty(
    val email: String?
) : NotionProperty()

data class NotionPhoneNumberProperty(
    val phone_number: String?
) : NotionProperty()

data class NotionRelationProperty(
    val relation: List<NotionRelation>
) : NotionProperty()

// Supporting classes
data class NotionRichText(
    val type: String, // "text", "mention", or "equation"
    val text: NotionText? = null,
    val annotations: NotionAnnotations? = null,
    val plain_text: String? = null,
    val href: String? = null
)

data class NotionText(
    val content: String,
    val link: NotionLink? = null
)

data class NotionLink(
    val url: String
)

data class NotionAnnotations(
    val bold: Boolean = false,
    val italic: Boolean = false,
    val strikethrough: Boolean = false,
    val underline: Boolean = false,
    val code: Boolean = false,
    val color: String = "default"
)

data class NotionSelectOption(
    val id: String? = null,
    val name: String,
    val color: String? = null
)

data class NotionDate(
    val start: String,
    val end: String? = null,
    val time_zone: String? = null
)

data class NotionFile(
    val type: String, // "external" or "file"
    val name: String? = null,
    val external: NotionExternal? = null,
    val file: NotionFileDetails? = null
)

data class NotionFileDetails(
    val url: String,
    val expiry_time: String
)

data class NotionRelation(
    val id: String
)

// Block types
sealed class NotionBlock {
    abstract val type: String
    abstract val object_type: String
}

data class NotionParagraphBlock(
    override val type: String = "paragraph",
    override val object_type: String = "block",
    val paragraph: NotionParagraph
) : NotionBlock()

data class NotionParagraph(
    val rich_text: List<NotionRichText>,
    val color: String = "default"
) 