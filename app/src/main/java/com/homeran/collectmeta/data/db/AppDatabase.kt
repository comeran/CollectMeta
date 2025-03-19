package com.homeran.collectmeta.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.homeran.collectmeta.data.db.converters.Converters
import com.homeran.collectmeta.data.db.dao.BookDao
import com.homeran.collectmeta.data.db.entities.BookEntity

/**
 * Room数据库实例
 */
@Database(
    entities = [
        BookEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    /**
     * 书籍数据访问对象
     */
    abstract fun bookDao(): BookDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * 获取数据库实例
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "collectmeta_database"
                )
                .fallbackToDestructiveMigration() // 在升级数据库时，如果没有提供迁移策略，将会删除旧表并创建新表
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 