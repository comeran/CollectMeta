package com.homeran.collectmeta.data.remote.model.notion

import com.google.gson.annotations.SerializedName

/**
 * Notion数据库查询响应
 */
data class NotionDatabaseResponse(
    @SerializedName("object") val objectType: String = "list",
    @SerializedName("results") val results: List<NotionPageResponse> = emptyList(),
    @SerializedName("next_cursor") val nextCursor: String? = null,
    @SerializedName("has_more") val hasMore: Boolean = false
)

/**
 * Notion页面响应
 */
data class NotionPageResponse(
    @SerializedName("id") val id: String,
    @SerializedName("object") val objectType: String = "page",
    @SerializedName("created_time") val createdTime: String,
    @SerializedName("last_edited_time") val lastEditedTime: String,
    @SerializedName("properties") val properties: Map<String, NotionProperty>
)

/**
 * Notion属性
 */
sealed class NotionProperty {
    data class Title(
        @SerializedName("title") val title: List<NotionRichText>
    ) : NotionProperty()

    data class RichText(
        @SerializedName("rich_text") val richText: List<NotionRichText>
    ) : NotionProperty()

    data class Number(
        @SerializedName("number") val number: Double?
    ) : NotionProperty()

    data class Select(
        @SerializedName("select") val select: NotionSelect?
    ) : NotionProperty()

    data class MultiSelect(
        @SerializedName("multi_select") val multiSelect: List<NotionSelect>
    ) : NotionProperty()

    data class Date(
        @SerializedName("date") val date: NotionDate?
    ) : NotionProperty()

    data class Checkbox(
        @SerializedName("checkbox") val checkbox: Boolean
    ) : NotionProperty()

    data class Url(
        @SerializedName("url") val url: String?
    ) : NotionProperty()

    data class Email(
        @SerializedName("email") val email: String?
    ) : NotionProperty()

    data class PhoneNumber(
        @SerializedName("phone_number") val phoneNumber: String?
    ) : NotionProperty()
}

/**
 * Notion富文本
 */
data class NotionRichText(
    @SerializedName("type") val type: String = "text",
    @SerializedName("text") val text: NotionText,
    @SerializedName("annotations") val annotations: NotionAnnotations = NotionAnnotations(),
    @SerializedName("plain_text") val plainText: String
)

/**
 * Notion文本
 */
data class NotionText(
    @SerializedName("content") val content: String,
    @SerializedName("link") val link: NotionLink?
)

/**
 * Notion链接
 */
data class NotionLink(
    @SerializedName("url") val url: String
)

/**
 * Notion文本样式
 */
data class NotionAnnotations(
    @SerializedName("bold") val bold: Boolean = false,
    @SerializedName("italic") val italic: Boolean = false,
    @SerializedName("strikethrough") val strikethrough: Boolean = false,
    @SerializedName("underline") val underline: Boolean = false,
    @SerializedName("code") val code: Boolean = false,
    @SerializedName("color") val color: String = "default"
)

/**
 * Notion选择项
 */
data class NotionSelect(
    @SerializedName("name") val name: String,
    @SerializedName("color") val color: String? = null
)

/**
 * Notion日期
 */
data class NotionDate(
    @SerializedName("start") val start: String?,
    @SerializedName("end") val end: String? = null
) 