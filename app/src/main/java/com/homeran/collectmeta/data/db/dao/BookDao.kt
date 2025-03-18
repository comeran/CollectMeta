package com.homeran.collectmeta.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.homeran.collectmeta.data.db.entities.BookDetailsEntity
import com.homeran.collectmeta.data.db.entities.BookWithDetails
import com.homeran.collectmeta.data.db.entities.MediaEntity
import kotlinx.coroutines.flow.Flow

/**
 * 书籍数据访问对象接口，定义对书籍数据的各种数据库操作
 */
@Dao
interface BookDao {
    @Transaction
    @Query("SELECT * FROM media WHERE type = 'BOOK'")
    fun getAllBooks(): Flow<List<BookWithDetails>>

    @Transaction
    @Query("SELECT * FROM media WHERE type = 'BOOK' AND id = :bookId")
    fun getBookById(bookId: String): Flow<BookWithDetails>
    
    @Transaction
    @Query("SELECT * FROM media WHERE type = 'BOOK' AND status = :status")
    fun getBooksByStatus(status: String): Flow<List<BookWithDetails>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaEntity(mediaEntity: MediaEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookDetails(bookDetails: BookDetailsEntity)
    
    @Transaction
    suspend fun insertBookWithDetails(mediaEntity: MediaEntity, bookDetails: BookDetailsEntity) {
        insertMediaEntity(mediaEntity)
        insertBookDetails(bookDetails)
    }
    
    @Query("DELETE FROM media WHERE id = :mediaId AND type = 'BOOK'")
    suspend fun deleteBook(mediaId: String)
    
    @Transaction
    @Query("""
        SELECT * FROM media 
        WHERE type = 'BOOK' 
        AND title LIKE '%' || :title || '%'
    """)
    fun searchBooksByTitle(title: String): Flow<List<BookWithDetails>>
    
    @Query("UPDATE media SET status = :status WHERE id = :mediaId")
    suspend fun updateBookStatus(mediaId: String, status: String)
    
    @Query("UPDATE media SET user_rating = :rating WHERE id = :mediaId")
    suspend fun updateBookRating(mediaId: String, rating: Float)
} 