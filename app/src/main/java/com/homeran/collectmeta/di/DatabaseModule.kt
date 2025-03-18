package com.homeran.collectmeta.di

import android.content.Context
import com.homeran.collectmeta.data.db.MediaDatabase
import com.homeran.collectmeta.data.db.dao.BookDao
import com.homeran.collectmeta.data.db.dao.GameDao
import com.homeran.collectmeta.data.db.dao.MovieDao
import com.homeran.collectmeta.data.db.dao.TvShowDao
import com.homeran.collectmeta.data.repository.TvShowRepositoryImpl
import com.homeran.collectmeta.domain.repository.TvShowRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt依赖注入模块，提供数据库相关依赖
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideMediaDatabase(@ApplicationContext context: Context): MediaDatabase {
        return MediaDatabase.getDatabase(context)
    }
    
    @Provides
    @Singleton
    fun provideBookDao(database: MediaDatabase): BookDao {
        return database.bookDao()
    }
    
    @Provides
    @Singleton
    fun provideGameDao(database: MediaDatabase): GameDao {
        return database.gameDao()
    }
    
    @Provides
    @Singleton
    fun provideMovieDao(database: MediaDatabase): MovieDao {
        return database.movieDao()
    }
    
    @Provides
    @Singleton
    fun provideTvShowDao(database: MediaDatabase): TvShowDao {
        return database.tvShowDao()
    }
    
    @Provides
    @Singleton
    fun provideTvShowRepository(tvShowDao: TvShowDao): TvShowRepository {
        return TvShowRepositoryImpl(tvShowDao)
    }
    
    // 其他Provides方法...
} 