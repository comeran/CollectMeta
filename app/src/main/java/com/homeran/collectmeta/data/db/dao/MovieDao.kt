package com.homeran.collectmeta.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import androidx.room.Update
import com.homeran.collectmeta.data.db.entities.MediaEntity
import com.homeran.collectmeta.data.db.entities.MovieDetailsEntity
import com.homeran.collectmeta.data.db.entities.MovieWithDetails
import kotlinx.coroutines.flow.Flow

/**
 * 电影数据访问对象接口
 */
@Dao
interface MovieDao {
    @Transaction
    @Query("SELECT * FROM media WHERE type = 'MOVIE'")
    fun getAllMovies(): Flow<List<MovieWithDetails>>

    @Transaction
    @Query("SELECT * FROM media WHERE type = 'MOVIE' AND id = :movieId")
    fun getMovieById(movieId: String): Flow<MovieWithDetails>
    
    @Transaction
    @Query("SELECT * FROM media WHERE type = 'MOVIE' AND status = :status")
    fun getMoviesByStatus(status: String): Flow<List<MovieWithDetails>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaEntity(mediaEntity: MediaEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovieDetails(movieDetails: MovieDetailsEntity)
    
    @Transaction
    suspend fun insertMovieWithDetails(mediaEntity: MediaEntity, movieDetails: MovieDetailsEntity) {
        insertMediaEntity(mediaEntity)
        insertMovieDetails(movieDetails)
    }
    
    @Query("DELETE FROM media WHERE id = :mediaId AND type = 'MOVIE'")
    suspend fun deleteMovie(mediaId: String)
    
    @Transaction
    @Query("SELECT * FROM media WHERE type = 'MOVIE' AND title LIKE '%' || :query || '%'")
    fun searchMoviesByTitle(query: String): Flow<List<MovieWithDetails>>
    
    @Query("UPDATE media SET status = :status WHERE id = :mediaId")
    suspend fun updateMovieStatus(mediaId: String, status: String)
    
    @Query("UPDATE media SET user_rating = :rating WHERE id = :mediaId")
    suspend fun updateMovieRating(mediaId: String, rating: Float)
    
    @Transaction
    @Query("SELECT * FROM media WHERE type = 'MOVIE' AND genres LIKE '%' || :genre || '%'")
    fun getMoviesByGenre(genre: String): Flow<List<MovieWithDetails>>
    
    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM media m JOIN movie_details md ON m.id = md.mediaId WHERE m.type = 'MOVIE' AND md.region = :region")
    fun getMoviesByRegion(region: String): Flow<List<MovieWithDetails>>
    
    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM media m JOIN movie_details md ON m.id = md.mediaId WHERE m.type = 'MOVIE' AND md.director LIKE '%' || :director || '%'")
    fun getMoviesByDirector(director: String): Flow<List<MovieWithDetails>>
    
    @Transaction
    @Query("SELECT * FROM media WHERE notion_page_id = :notionId AND type = 'MOVIE'")
    suspend fun getMovieByNotionId(notionId: String): MovieWithDetails?
    
    @Query("UPDATE media SET notion_page_id = :notionPageId WHERE id = :movieId")
    suspend fun updateMovieNotionPageId(movieId: String, notionPageId: String)
} 