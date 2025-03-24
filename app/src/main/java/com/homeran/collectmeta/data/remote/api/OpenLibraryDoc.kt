package com.homeran.collectmeta.data.remote.api

/**
 * OpenLibrary搜索结果文档
 * 字段名称与OpenLibrary API返回的字段保持一致
 * 参考文档: https://openlibrary.org/dev/docs/api/search
 */
data class OpenLibraryDoc(
    val key: String? = null,                  // 书籍唯一标识，例如"/works/OL1234W"
    val title: String? = null,                // 书籍标题
    val subtitle: String? = null,             // 副标题
    val author_name: List<String>? = null,    // 作者姓名列表
    val author_key: List<String>? = null,     // 作者ID列表
    val publisher: List<String>? = null,      // 出版商列表
    val publish_year: List<Int>? = null,      // 出版年份列表
    val first_publish_year: Int? = null,      // 首次出版年份
    val isbn: List<String>? = null,           // ISBN列表
    val language: List<String>? = null,       // 语言
    val subject: List<String>? = null,        // 主题/分类列表
    val cover_i: Int? = null,                 // 封面图片ID
    val cover_edition_key: String? = null,    // 封面版本键
    val edition_count: Int? = null,           // 版本数量
    val ebook_count_i: Int? = null,           // 电子书数量
    val number_of_pages_median: Int? = null,  // 页数中位数
    val description: String? = null,          // 描述

    // 以下字段在API中可能不存在，但为了与BookEntity匹配添加
    val chineseTitle: String? = null,         // 中文标题
    val originalTitle: String? = null,        // 原标题
    val translator: List<String>? = null,     // 译者
    val series: String? = null,               // 丛书系列
    val binding: String? = null,              // 装帧
    val price: String? = null,                // 价格
    val doubanRating: Float? = null,          // 豆瓣评分
    val doubanUrl: String? = null,            // 豆瓣链接
    val fileAttachment: String? = null        // 文件附件
) 