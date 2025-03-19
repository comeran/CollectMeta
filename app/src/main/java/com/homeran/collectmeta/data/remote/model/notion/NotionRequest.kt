package com.homeran.collectmeta.data.remote.model.notion

import com.google.gson.annotations.SerializedName

/**
 * Notion数据库查询请求
 */
data class NotionQueryRequest(
    @SerializedName("filter") val filter: NotionFilter? = null,
    @SerializedName("sorts") val sorts: List<NotionSort>? = null,
    @SerializedName("page_size") val pageSize: Int = 100,
    @SerializedName("start_cursor") val startCursor: String? = null
)

/**
 * Notion过滤器
 */
data class NotionFilter(
    @SerializedName("property") val property: String,
    @SerializedName("rich_text") val richText: NotionTextFilter? = null,
    @SerializedName("number") val number: NotionNumberFilter? = null,
    @SerializedName("select") val select: NotionSelectFilter? = null,
    @SerializedName("multi_select") val multiSelect: NotionMultiSelectFilter? = null,
    @SerializedName("date") val date: NotionDateFilter? = null,
    @SerializedName("checkbox") val checkbox: NotionCheckboxFilter? = null
)

/**
 * Notion文本过滤器
 */
data class NotionTextFilter(
    @SerializedName("equals") val equals: String? = null,
    @SerializedName("does_not_equal") val doesNotEqual: String? = null,
    @SerializedName("contains") val contains: String? = null,
    @SerializedName("does_not_contain") val doesNotContain: String? = null,
    @SerializedName("starts_with") val startsWith: String? = null,
    @SerializedName("ends_with") val endsWith: String? = null
)

/**
 * Notion数字过滤器
 */
data class NotionNumberFilter(
    @SerializedName("equals") val equals: Double? = null,
    @SerializedName("does_not_equal") val doesNotEqual: Double? = null,
    @SerializedName("greater_than") val greaterThan: Double? = null,
    @SerializedName("less_than") val lessThan: Double? = null,
    @SerializedName("greater_than_or_equal_to") val greaterThanOrEqualTo: Double? = null,
    @SerializedName("less_than_or_equal_to") val lessThanOrEqualTo: Double? = null
)

/**
 * Notion选择项过滤器
 */
data class NotionSelectFilter(
    @SerializedName("equals") val equals: String? = null,
    @SerializedName("does_not_equal") val doesNotEqual: String? = null
)

/**
 * Notion多选过滤器
 */
data class NotionMultiSelectFilter(
    @SerializedName("contains") val contains: String? = null,
    @SerializedName("does_not_contain") val doesNotContain: String? = null
)

/**
 * Notion日期过滤器
 */
data class NotionDateFilter(
    @SerializedName("equals") val equals: String? = null,
    @SerializedName("before") val before: String? = null,
    @SerializedName("after") val after: String? = null,
    @SerializedName("on_or_before") val onOrBefore: String? = null,
    @SerializedName("on_or_after") val onOrAfter: String? = null
)

/**
 * Notion复选框过滤器
 */
data class NotionCheckboxFilter(
    @SerializedName("equals") val equals: Boolean? = null,
    @SerializedName("does_not_equal") val doesNotEqual: Boolean? = null
)

/**
 * Notion排序
 */
data class NotionSort(
    @SerializedName("property") val property: String,
    @SerializedName("direction") val direction: String = "ascending"
)

/**
 * Notion页面更新请求
 */
data class NotionUpdatePageRequest(
    @SerializedName("properties") val properties: Map<String, NotionProperty>
) 