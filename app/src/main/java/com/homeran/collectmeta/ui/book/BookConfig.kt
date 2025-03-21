package com.homeran.collectmeta.ui.book

/**
 * Data class representing the configuration for book APIs and integration.
 */
data class BookConfig(
    val notionDatabaseId: String = "",
    val openLibraryApiUrl: String = "https://openlibrary.org/api/books",
    val googleBooksApiUrl: String = "https://www.googleapis.com/books/v1",
    val googleBooksApiKey: String = ""
) 