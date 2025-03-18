package com.homeran.collectmeta.domain.repository

import com.homeran.collectmeta.data.db.entities.MediaStatus
import com.homeran.collectmeta.domain.model.Movie
import kotlinx.coroutines.flow.Flow

/**
 * 电影仓库接口，定义电影数据访问的操作
 */
interface MovieRepository {
    fun getAllMovies(): Flow<List<Movie>>
    fun getMovieById(id: String): Flow<Movie?>
    fun getMoviesByStatus(status: MediaStatus): Flow<List<Movie>>
    suspend fun insertMovie(movie: Movie)
    suspend fun deleteMovie(id: String)
    suspend fun updateMovieStatus(id: String, status: MediaStatus)
    suspend fun updateMovieRating(id: String, rating: Float)
    suspend fun updateMovieUserComment(id: String, comment: String)
    fun searchMovies(query: String): Flow<List<Movie>>
    fun getMoviesByGenre(genre: String): Flow<List<Movie>>
    fun getMoviesByRegion(region: String): Flow<List<Movie>>
    fun getMoviesByDirector(director: String): Flow<List<Movie>>
    suspend fun getMovieByNotionId(notionId: String): Movie?
    suspend fun updateMovieNotionPageId(id: String, notionPageId: String)
} 