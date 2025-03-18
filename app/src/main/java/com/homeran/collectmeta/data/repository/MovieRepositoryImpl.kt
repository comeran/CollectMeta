package com.homeran.collectmeta.data.repository

import com.homeran.collectmeta.data.db.dao.MovieDao
import com.homeran.collectmeta.data.db.entities.MediaStatus
import com.homeran.collectmeta.data.mapper.toDomainModel
import com.homeran.collectmeta.data.mapper.toMediaEntity
import com.homeran.collectmeta.data.mapper.toMovieDetailsEntity
import com.homeran.collectmeta.domain.model.Movie
import com.homeran.collectmeta.domain.repository.MovieRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * 电影仓库实现类，实现电影数据的存取逻辑
 */
class MovieRepositoryImpl @Inject constructor(
    private val movieDao: MovieDao,
    private val notionClient: Any? = null, // 暂时用Any替代，后续实现Notion客户端
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : MovieRepository {
    
    override fun getAllMovies(): Flow<List<Movie>> {
        return movieDao.getAllMovies().map { movieEntities ->
            movieEntities.map { it.toDomainModel() }
        }
    }
    
    override fun getMovieById(id: String): Flow<Movie?> {
        return movieDao.getMovieById(id).map { it?.toDomainModel() }
    }
    
    override fun getMoviesByStatus(status: MediaStatus): Flow<List<Movie>> {
        return movieDao.getMoviesByStatus(status.name).map { movieEntities ->
            movieEntities.map { it.toDomainModel() }
        }
    }
    
    override suspend fun insertMovie(movie: Movie) = withContext(ioDispatcher) {
        val mediaEntity = movie.toMediaEntity()
        val movieDetailsEntity = movie.toMovieDetailsEntity()
        movieDao.insertMovieWithDetails(mediaEntity, movieDetailsEntity)
        
        // 如果配置了Notion同步，则同步到Notion
        notionClient?.let {
            // 同步逻辑，后续实现
        }
    }
    
    override suspend fun deleteMovie(id: String) = withContext(ioDispatcher) {
        movieDao.deleteMovie(id)
    }
    
    override suspend fun updateMovieStatus(id: String, status: MediaStatus) = withContext(ioDispatcher) {
        movieDao.updateMovieStatus(id, status.name)
    }
    
    override suspend fun updateMovieRating(id: String, rating: Float) = withContext(ioDispatcher) {
        movieDao.updateMovieRating(id, rating)
    }
    
    override suspend fun updateMovieUserComment(id: String, comment: String) = withContext(ioDispatcher) {
        // 实现更新评论的逻辑
        val movie = movieDao.getMovieById(id).firstOrNull()?.toDomainModel()
        movie?.let { 
            val updatedMovie = it.copy(userComment = comment)
            insertMovie(updatedMovie)
        }
    }
    
    override fun searchMovies(query: String): Flow<List<Movie>> {
        return movieDao.searchMoviesByTitle(query).map { movieEntities ->
            movieEntities.map { it.toDomainModel() }
        }
    }
    
    override fun getMoviesByGenre(genre: String): Flow<List<Movie>> {
        return movieDao.getMoviesByGenre(genre).map { movieEntities ->
            movieEntities.map { it.toDomainModel() }
        }
    }
    
    override fun getMoviesByRegion(region: String): Flow<List<Movie>> {
        return movieDao.getMoviesByRegion(region).map { movieEntities ->
            movieEntities.map { it.toDomainModel() }
        }
    }
    
    override fun getMoviesByDirector(director: String): Flow<List<Movie>> {
        return movieDao.getMoviesByDirector(director).map { movieEntities ->
            movieEntities.map { it.toDomainModel() }
        }
    }
    
    override suspend fun getMovieByNotionId(notionId: String): Movie? = withContext(ioDispatcher) {
        movieDao.getMovieByNotionId(notionId)?.toDomainModel()
    }
    
    override suspend fun updateMovieNotionId(id: String, notionPageId: String) {
        val movie = movieDao.getMovieById(id).firstOrNull() ?: return
        val updatedMovie = movie.copy(notionPageId = notionPageId, lastModified = System.currentTimeMillis())
        movieDao.insertMovie(updatedMovie)
    }
} 