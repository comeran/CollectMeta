package com.homeran.collectmeta.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.homeran.collectmeta.data.db.converters.Converters
import com.homeran.collectmeta.data.db.converters.DateConverter
import com.homeran.collectmeta.data.db.dao.BookDao
import com.homeran.collectmeta.data.db.dao.GameDao
import com.homeran.collectmeta.data.db.dao.MovieDao
import com.homeran.collectmeta.data.db.dao.TvShowDao
import com.homeran.collectmeta.data.db.entities.BookDetailsEntity
import com.homeran.collectmeta.data.db.entities.GameDLCEntity
import com.homeran.collectmeta.data.db.entities.GameDetailsEntity
import com.homeran.collectmeta.data.db.entities.GameEntity
import com.homeran.collectmeta.data.db.entities.GamePlatformEntity
import com.homeran.collectmeta.data.db.entities.MediaEntity
import com.homeran.collectmeta.data.db.entities.MovieDetailsEntity
import com.homeran.collectmeta.data.db.entities.TvEpisodeEntity
import com.homeran.collectmeta.data.db.entities.TvSeasonEntity
import com.homeran.collectmeta.data.db.entities.TvShowDetailsEntity
import com.homeran.collectmeta.data.db.entities.TvShowWatchProgressEntity

/**
 * Room数据库类，定义数据库结构和提供DAO访问
 */
@Database(
    entities = [
        MediaEntity::class,
        MovieDetailsEntity::class,
        TvShowDetailsEntity::class,
        BookDetailsEntity::class,
        GameDetailsEntity::class,
        GamePlatformEntity::class,
        GameDLCEntity::class,
        TvShowWatchProgressEntity::class,
        TvSeasonEntity::class,
        TvEpisodeEntity::class
    ],
    version = 5,
    exportSchema = true
)
@TypeConverters(Converters::class, DateConverter::class)
abstract class MediaDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun tvShowDao(): TvShowDao
    abstract fun bookDao(): BookDao
    abstract fun gameDao(): GameDao
    
    companion object {
        @Volatile
        private var INSTANCE: MediaDatabase? = null
        
        fun getDatabase(context: Context): MediaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MediaDatabase::class.java,
                    "media_database"
                )
                // Simply recreate the database when version changes
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 