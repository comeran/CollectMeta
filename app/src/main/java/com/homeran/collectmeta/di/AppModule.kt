package com.homeran.collectmeta.di

import android.content.Context
import com.homeran.collectmeta.data.remote.api.DoubanApi
import com.homeran.collectmeta.data.remote.api.IgdbApi
import com.homeran.collectmeta.data.remote.api.NotionApi
import com.homeran.collectmeta.data.remote.api.TmdbApi
import com.homeran.collectmeta.data.repository.SettingsRepositoryImpl
import com.homeran.collectmeta.data.repository.SyncRepositoryImpl
import com.homeran.collectmeta.domain.repository.MovieRepository
import com.homeran.collectmeta.domain.repository.SettingsRepository
import com.homeran.collectmeta.domain.repository.SyncRepository
import com.homeran.collectmeta.domain.repository.TvShowRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Hilt依赖注入模块，提供网络和同步相关依赖
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTmdbApi(): TmdbApi {
        return Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TmdbApi::class.java)
    }

    @Provides
    @Singleton
    fun provideIgdbApi(): IgdbApi {
        return Retrofit.Builder()
            .baseUrl("https://api.igdb.com/v4/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IgdbApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDoubanApi(): DoubanApi {
        return Retrofit.Builder()
            .baseUrl("https://api.douban.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DoubanApi::class.java)
    }

    @Provides
    @Singleton
    fun provideNotionApi(): NotionApi {
        return Retrofit.Builder()
            .baseUrl("https://api.notion.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NotionApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(@ApplicationContext context: Context): SettingsRepository {
        return SettingsRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideSyncRepository(
        @ApplicationContext context: Context,
        notionApi: NotionApi,
        movieRepository: MovieRepository,
        tvShowRepository: TvShowRepository,
        settingsRepository: SettingsRepository
    ): SyncRepository {
        return SyncRepositoryImpl(
            context,
            notionApi,
            movieRepository,
            tvShowRepository,
            settingsRepository
        )
    }
} 