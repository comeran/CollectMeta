package com.homeran.collectmeta.data.remote.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

/**
 * OpenLibrary API 接口定义
 * 官方文档: https://openlibrary.org/dev/docs/api/search
 * 基础URL应为: https://openlibrary.org/
 */
interface OpenLibraryApi {
    
    /**
     * 搜索书籍
     * 完整URL示例: https://openlibrary.org/search.json?q=the+lord+of+the+rings
     * @param queryMap 查询参数Map，支持以下参数:
     * - q: 通用搜索查询
     * - title: 标题搜索
     * - author: 作者搜索
     * - subject: 主题搜索
     * - place: 地点搜索
     * - person: 人物搜索
     * - language: 语言搜索
     * - publisher: 出版商搜索
     * - isbn: ISBN搜索
     * - limit: 返回结果数量 (默认为10)
     * - page: 分页页码 (默认为1)
     * - offset: 结果偏移量
     * - sort: 排序方式 (如 "new", "old", "title", "title_asc", "title_desc")
     * - fields: 返回的字段列表 (逗号分隔)
     * @return API响应
     */
    @GET("search.json")
    suspend fun searchBooks(
        @QueryMap queryMap: Map<String, String>
    ): Response<OpenLibrarySearchResponse>
    
    /**
     * 获取书籍详情
     * 完整URL示例: https://openlibrary.org/works/OL27448W.json
     * @param bookId 书籍ID（OpenLibrary的works ID），格式如"OL27448W"，不需要包含"/works/"前缀
     * @return API响应
     */
    @GET("works/{bookId}.json")
    suspend fun getBookDetails(
        @Path("bookId") bookId: String
    ): Response<OpenLibraryBookDetails>
    
    /**
     * 通过ISBN获取书籍详情
     * 完整URL示例: https://openlibrary.org/api/books?bibkeys=ISBN:9780451524935&format=json&jscmd=data
     * @param bibkeys 书籍标识符，格式如"ISBN:9780451524935"
     * @param format 响应格式，默认为json
     * @param jscmd 命令参数，指定返回数据的详细程度，data为完整数据
     * @return 映射，键为bibkeys (如"ISBN:9780451524935")，值为对应的书籍数据
     */
    @GET("api/books")
    suspend fun getBookByIsbn(
        @Query("bibkeys") bibkeys: String,
        @Query("format") format: String = "json",
        @Query("jscmd") jscmd: String = "data"
    ): Response<Map<String, OpenLibraryIsbnResponse>>
}

/**
 * OpenLibrary搜索响应
 * 符合OpenLibrary API返回的结构
 */
data class OpenLibrarySearchResponse(
    val numFound: Int = 0,
    val start: Int = 0,
    val numFoundExact: Boolean = false,
    val docs: List<OpenLibraryDoc>? = null
)

/**
 * OpenLibrary书籍详情
 */
data class OpenLibraryBookDetails(
    val title: String? = null,
    val key: String? = null,
    val description: Description? = null,
    val authors: List<AuthorReference>? = null,
    val publish_date: String? = null,
    val publishers: List<Publisher>? = null,
    val number_of_pages: Int? = null,
    val covers: List<Int>? = null,
    val subjects: List<Any>? = null,
    val identifiers: Identifiers? = null,
    // 以下字段在API中可能不存在，但为了与BookEntity匹配添加
    val translators: List<Translator>? = null,     // 译者
    val chinese_title: String? = null,             // 中文标题
    val original_title: String? = null,            // 原标题
    val series: Series? = null,                    // 丛书系列
    val physical_format: String? = null,           // 装帧类型
    val price: String? = null,                     // 价格
    val douban_rating: Float? = null,              // 豆瓣评分
    val douban_url: String? = null,                // 豆瓣链接
    val file_attachment: String? = null            // 文件附件
)

/**
 * 书籍描述，可以是字符串或对象
 */
data class Description(
    val value: String? = null,
    val type: String? = null
) {
    override fun toString(): String {
        return value ?: ""
    }
}

/**
 * 译者信息
 */
data class Translator(
    val key: String? = null,
    val name: String? = null
)

/**
 * 系列信息
 */
data class Series(
    val name: String? = null,
    val position: Int? = null
)

/**
 * 作者引用
 */
data class AuthorReference(
    val author: Author? = null,
    val type: TypeInfo? = null
)

/**
 * 作者信息
 */
data class Author(
    val key: String? = null,
    val name: String? = null
)

/**
 * 类型信息
 */
data class TypeInfo(
    val key: String? = null
)

/**
 * 出版商
 */
data class Publisher(
    val name: String? = null
)

/**
 * 主题
 */
data class Subject(
    val name: String? = null
)

/**
 * 标识符（ISBN等）
 */
data class Identifiers(
    val isbn_10: List<String>? = null,
    val isbn_13: List<String>? = null,
    val goodreads: List<String>? = null,
    val librarything: List<String>? = null
)

/**
 * OpenLibrary ISBN API响应
 * 通过ISBN获取的书籍详情数据结构
 */
data class OpenLibraryIsbnResponse(
    val title: String? = null,
    val subtitle: String? = null,
    val authors: List<IsbnAuthor>? = null,
    val publishers: List<IsbnPublisher>? = null,
    val publish_date: String? = null,
    val publish_places: List<IsbnPublishPlace>? = null,
    val number_of_pages: Int? = null,
    val weight: String? = null,
    val dimensions: IsbnDimensions? = null,
    val subjects: List<IsbnSubject>? = null,
    val subject_places: List<IsbnSubjectPlace>? = null,
    val subject_people: List<IsbnSubjectPerson>? = null,
    val subject_times: List<IsbnSubjectTime>? = null,
    val notes: String? = null,
    val cover: IsbnCover? = null,
    val ebooks: List<IsbnEbook>? = null,
    val url: String? = null,
    val identifiers: IsbnIdentifiers? = null,
    val key: String? = null,
    val excerpt: IsbnExcerpt? = null,
    val classifications: IsbnClassifications? = null,
    val links: List<IsbnLink>? = null,
    val by_statement: String? = null,
    val description: String? = null
)

/**
 * ISBN API作者信息
 */
data class IsbnAuthor(
    val name: String? = null,
    val url: String? = null
)

/**
 * ISBN API出版商信息
 */
data class IsbnPublisher(
    val name: String? = null
)

/**
 * ISBN API出版地信息
 */
data class IsbnPublishPlace(
    val name: String? = null
)

/**
 * ISBN API书籍尺寸信息
 */
data class IsbnDimensions(
    val height: String? = null,
    val width: String? = null,
    val thickness: String? = null
)

/**
 * ISBN API主题信息
 */
data class IsbnSubject(
    val name: String? = null,
    val url: String? = null
)

/**
 * ISBN API地点主题
 */
data class IsbnSubjectPlace(
    val name: String? = null,
    val url: String? = null
)

/**
 * ISBN API人物主题
 */
data class IsbnSubjectPerson(
    val name: String? = null,
    val url: String? = null
)

/**
 * ISBN API时间主题
 */
data class IsbnSubjectTime(
    val name: String? = null,
    val url: String? = null
)

/**
 * ISBN API封面信息
 */
data class IsbnCover(
    val small: String? = null,
    val medium: String? = null,
    val large: String? = null
)

/**
 * ISBN API电子书信息
 */
data class IsbnEbook(
    val preview_url: String? = null,
    val availability: String? = null,
    val formats: Map<String, String>? = null,
    val read_url: String? = null
)

/**
 * ISBN API标识符信息
 */
data class IsbnIdentifiers(
    val goodreads: List<String>? = null,
    val librarything: List<String>? = null,
    val isbn_10: List<String>? = null,
    val isbn_13: List<String>? = null,
    val oclc: List<String>? = null,
    val lccn: List<String>? = null
)

/**
 * ISBN API摘录信息
 */
data class IsbnExcerpt(
    val text: String? = null,
    val comment: String? = null
)

/**
 * ISBN API分类信息
 */
data class IsbnClassifications(
    val dewey_decimal_class: List<String>? = null,
    val lc_classifications: List<String>? = null
)

/**
 * ISBN API链接信息
 */
data class IsbnLink(
    val title: String? = null,
    val url: String? = null
) 