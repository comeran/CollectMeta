package com.homeran.collectmeta.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.homeran.collectmeta.data.mapper.toNotionPageCreateRequest
import com.homeran.collectmeta.data.mapper.toNotionPageUpdateRequest
import com.homeran.collectmeta.data.remote.api.NotionApi
import com.homeran.collectmeta.data.remote.api.NotionDatabaseQueryRequest
import com.homeran.collectmeta.data.remote.api.NotionParent
import com.homeran.collectmeta.domain.model.Movie
import com.homeran.collectmeta.domain.model.SyncResult
import com.homeran.collectmeta.domain.model.TvShow
import com.homeran.collectmeta.domain.repository.MovieRepository
import com.homeran.collectmeta.domain.repository.SettingsRepository
import com.homeran.collectmeta.domain.repository.SyncRepository
import com.homeran.collectmeta.domain.repository.TvShowRepository
import com.homeran.collectmeta.presentation.viewmodel.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.syncDataStore: DataStore<Preferences> by preferencesDataStore(name = "sync_settings")

@Singleton
class SyncRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notionApi: NotionApi,
    private val movieRepository: MovieRepository,
    private val tvShowRepository: TvShowRepository,
    private val settingsRepository: SettingsRepository
) : SyncRepository {
    
    private object PreferencesKeys {
        val LAST_SYNC_TIME = longPreferencesKey("last_sync_time")
    }
    
    override fun syncMoviesToNotion(): Flow<SyncResult> = flow {
        try {
            val settings = settingsRepository.getSettings()
            val token = "Bearer ${settings.notionApiKey}"
            val databaseId = settings.notionDatabaseId
            
            if (token.isEmpty() || databaseId.isEmpty()) {
                emit(SyncResult.Error("Notion API设置未配置", 0, 0))
                return@flow
            }
            
            val movies = movieRepository.getAllMovies().first()
            emit(SyncResult.Started(movies.size))
            
            var syncedCount = 0
            var failedCount = 0
            
            movies.forEachIndexed { index, movie ->
                try {
                    // 检查电影是否已经有Notion页面ID
                    if (movie.notionPageId.isNullOrEmpty()) {
                        // 创建新的Notion页面
                        val createRequest = movie.toNotionPageCreateRequest(NotionParent("database_id", databaseId))
                        val response = notionApi.createPage(token, pageCreateRequest = createRequest)
                        
                        // 更新本地电影的Notion页面ID
                        movieRepository.updateMovieNotionId(movie.id, response.id)
                        
                        syncedCount++
                        emit(SyncResult.Progress(index + 1, movies.size, movie.title))
                        emit(SyncResult.ItemSynced(movie.title))
                    } else {
                        // 更新已有的Notion页面
                        val updateRequest = movie.toNotionPageUpdateRequest()
                        notionApi.updatePage(token, movie.notionPageId, updateRequest)
                        
                        syncedCount++
                        emit(SyncResult.Progress(index + 1, movies.size, movie.title))
                        emit(SyncResult.ItemSynced(movie.title))
                    }
                } catch (e: Exception) {
                    failedCount++
                    emit(SyncResult.ItemFailed(movie.title, e.message ?: "未知错误"))
                    emit(SyncResult.Progress(index + 1, movies.size, movie.title))
                }
            }
            
            // 更新最后同步时间
            updateLastSyncTime(System.currentTimeMillis())
            
            emit(SyncResult.Success(syncedCount, movies.size))
        } catch (e: Exception) {
            emit(SyncResult.Error(e.message ?: "同步失败", 0, 0))
        }
    }
    
    override suspend fun syncMovieToNotion(movie: Movie): SyncResult {
        return try {
            val settings = settingsRepository.getSettings()
            val token = "Bearer ${settings.notionApiKey}"
            val databaseId = settings.notionDatabaseId
            
            if (token.isEmpty() || databaseId.isEmpty()) {
                SyncResult.Error("Notion API设置未配置", 0, 1)
            } else if (movie.notionPageId.isNullOrEmpty()) {
                // 创建新的Notion页面
                val createRequest = movie.toNotionPageCreateRequest(NotionParent("database_id", databaseId))
                val response = notionApi.createPage(token, pageCreateRequest = createRequest)
                
                // 更新本地电影的Notion页面ID
                movieRepository.updateMovieNotionId(movie.id, response.id)
                
                // 更新最后同步时间
                updateLastSyncTime(System.currentTimeMillis())
                
                SyncResult.ItemSynced(movie.title)
            } else {
                // 更新已有的Notion页面
                val updateRequest = movie.toNotionPageUpdateRequest()
                notionApi.updatePage(token, movie.notionPageId, updateRequest)
                
                // 更新最后同步时间
                updateLastSyncTime(System.currentTimeMillis())
                
                SyncResult.ItemSynced(movie.title)
            }
        } catch (e: Exception) {
            SyncResult.ItemFailed(movie.title, e.message ?: "同步失败")
        }
    }
    
    override fun syncMoviesFromNotion(): Flow<SyncResult> = flow {
        try {
            val settings = settingsRepository.getSettings()
            val token = "Bearer ${settings.notionApiKey}"
            val databaseId = settings.notionDatabaseId
            
            if (token.isEmpty() || databaseId.isEmpty()) {
                emit(SyncResult.Error("Notion API设置未配置", 0, 0))
                return@flow
            }
            
            // 查询Notion数据库中的电影页面
            val queryRequest = NotionDatabaseQueryRequest()
            val response = notionApi.queryDatabase(token, databaseId = databaseId, queryRequest = queryRequest)
            
            emit(SyncResult.Started(response.results.size))
            
            // TODO: 实现从Notion页面到Movie对象的转换
            // 这需要一个将Notion属性映射到Movie字段的逻辑
            
            emit(SyncResult.Success(0, response.results.size))
            
            // 更新最后同步时间
            updateLastSyncTime(System.currentTimeMillis())
        } catch (e: Exception) {
            emit(SyncResult.Error(e.message ?: "从Notion同步失败", 0, 0))
        }
    }
    
    override fun syncTvShowsToNotion(): Flow<SyncResult> = flow {
        try {
            val settings = settingsRepository.getSettings()
            val token = "Bearer ${settings.notionApiKey}"
            val databaseId = settings.notionDatabaseId
            
            if (token.isEmpty() || databaseId.isEmpty()) {
                emit(SyncResult.Error("Notion API设置未配置", 0, 0))
                return@flow
            }
            
            val tvShows = tvShowRepository.getAllTvShows().first()
            emit(SyncResult.Started(tvShows.size))
            
            var syncedCount = 0
            var failedCount = 0
            
            tvShows.forEachIndexed { index, tvShow ->
                try {
                    // TODO: 实现电视剧的Notion转换逻辑
                    // 这部分代码类似于syncMoviesToNotion的处理逻辑
                    
                    syncedCount++
                    emit(SyncResult.Progress(index + 1, tvShows.size, tvShow.title))
                    emit(SyncResult.ItemSynced(tvShow.title))
                } catch (e: Exception) {
                    failedCount++
                    emit(SyncResult.ItemFailed(tvShow.title, e.message ?: "未知错误"))
                    emit(SyncResult.Progress(index + 1, tvShows.size, tvShow.title))
                }
            }
            
            // 更新最后同步时间
            updateLastSyncTime(System.currentTimeMillis())
            
            emit(SyncResult.Success(syncedCount, tvShows.size))
        } catch (e: Exception) {
            emit(SyncResult.Error(e.message ?: "同步失败", 0, 0))
        }
    }
    
    override suspend fun syncTvShowToNotion(tvShow: TvShow): SyncResult {
        return try {
            val settings = settingsRepository.getSettings()
            val token = "Bearer ${settings.notionApiKey}"
            val databaseId = settings.notionDatabaseId
            
            if (token.isEmpty() || databaseId.isEmpty()) {
                SyncResult.Error("Notion API设置未配置", 0, 1)
            } else {
                // TODO: 实现单个电视剧的Notion同步逻辑
                // 这部分代码类似于syncMovieToNotion的处理逻辑
                
                // 更新最后同步时间
                updateLastSyncTime(System.currentTimeMillis())
                
                SyncResult.ItemSynced(tvShow.title)
            }
        } catch (e: Exception) {
            SyncResult.ItemFailed(tvShow.title, e.message ?: "同步失败")
        }
    }
    
    override fun syncTvShowsFromNotion(): Flow<SyncResult> = flow {
        try {
            val settings = settingsRepository.getSettings()
            val token = "Bearer ${settings.notionApiKey}"
            val databaseId = settings.notionDatabaseId
            
            if (token.isEmpty() || databaseId.isEmpty()) {
                emit(SyncResult.Error("Notion API设置未配置", 0, 0))
                return@flow
            }
            
            // 查询Notion数据库中的电视剧页面
            val queryRequest = NotionDatabaseQueryRequest()
            val response = notionApi.queryDatabase(token, databaseId = databaseId, queryRequest = queryRequest)
            
            emit(SyncResult.Started(response.results.size))
            
            // TODO: 实现从Notion页面到TvShow对象的转换
            // 这需要一个将Notion属性映射到TvShow字段的逻辑
            
            emit(SyncResult.Success(0, response.results.size))
            
            // 更新最后同步时间
            updateLastSyncTime(System.currentTimeMillis())
        } catch (e: Exception) {
            emit(SyncResult.Error(e.message ?: "从Notion同步失败", 0, 0))
        }
    }
    
    override suspend fun getLastSyncTime(): Long {
        val preferences = context.syncDataStore.data.first()
        return preferences[PreferencesKeys.LAST_SYNC_TIME] ?: 0
    }
    
    override suspend fun updateLastSyncTime(time: Long) {
        context.syncDataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_SYNC_TIME] = time
        }
    }
    
    override suspend fun testNotionConnection(apiKey: String, databaseId: String): Boolean {
        return try {
            val token = "Bearer $apiKey"
            val queryRequest = NotionDatabaseQueryRequest(page_size = 1)
            val response = notionApi.queryDatabase(token, databaseId = databaseId, queryRequest = queryRequest)
            true
        } catch (e: HttpException) {
            false
        } catch (e: IOException) {
            false
        }
    }
} 