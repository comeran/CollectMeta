package com.homeran.collectmeta.di

import com.homeran.collectmeta.data.remote.api.GoogleBooksApi
import com.homeran.collectmeta.data.remote.api.OpenLibraryApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * 处理空API Key的拦截器
 * 如果key参数为空，则从URL中移除该参数
 */
class EmptyApiKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url
        
        // 查找key参数
        val keyValue = originalUrl.queryParameter("key")
        
        // 如果key参数不存在或为空，则不需要修改
        if (keyValue == null || keyValue.isNotEmpty()) {
            return chain.proceed(originalRequest)
        }
        
        // 创建一个新的URL，移除空的key参数
        val newUrlBuilder = originalUrl.newBuilder()
        val parameterNames = originalUrl.queryParameterNames
        
        // 清除所有参数，然后重新添加除了空key之外的所有参数
        newUrlBuilder.query(null)
        for (name in parameterNames) {
            if (name != "key") {
                // 获取指定名称的所有参数值
                originalUrl.queryParameter(name)?.let { value ->
                    newUrlBuilder.addQueryParameter(name, value)
                }
            }
        }
        
        // 创建新的请求
        val newRequest = originalRequest.newBuilder()
            .url(newUrlBuilder.build())
            .build()
        
        return chain.proceed(newRequest)
    }
}

/**
 * Google Books API工厂，用于创建API实例
 */
class GoogleBooksApiFactory(private val retrofitBuilder: Retrofit.Builder) {
    /**
     * 创建GoogleBooksApi实例
     * @param baseUrl API基础URL
     * @return GoogleBooksApi实例
     */
    fun create(baseUrl: String): GoogleBooksApi {
        // 确保baseUrl以/结尾
        val normalizedBaseUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        
        return retrofitBuilder
            .baseUrl(normalizedBaseUrl)
            .build()
            .create(GoogleBooksApi::class.java)
    }
}

/**
 * OpenLibrary API工厂，用于创建API实例
 */
class OpenLibraryApiFactory(private val retrofitBuilder: Retrofit.Builder) {
    /**
     * 创建OpenLibraryApi实例
     * @param baseUrl API基础URL
     * @return OpenLibraryApi实例
     */
    fun create(baseUrl: String): OpenLibraryApi {
        // 确保baseUrl以/结尾
        val normalizedBaseUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        
        return retrofitBuilder
            .baseUrl(normalizedBaseUrl)
            .build()
            .create(OpenLibraryApi::class.java)
    }
}

/**
 * 网络模块，提供API服务依赖
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * 提供处理空API Key的拦截器
     */
    @Provides
    @Singleton
    fun provideEmptyApiKeyInterceptor(): EmptyApiKeyInterceptor {
        return EmptyApiKeyInterceptor()
    }

    /**
     * 提供OkHttpClient
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(emptyApiKeyInterceptor: EmptyApiKeyInterceptor): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(emptyApiKeyInterceptor) // 先处理空API Key
            .addInterceptor(loggingInterceptor)     // 再记录日志
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }
    
    /**
     * 提供Retrofit构建器
     */
    @Provides
    @Singleton
    fun provideRetrofitBuilder(okHttpClient: OkHttpClient): Retrofit.Builder {
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
    }
    
    /**
     * 提供GoogleBooksApiFactory
     * 调用方可以通过factory.create(baseUrl)创建具有特定baseUrl的API实例
     */
    @Provides
    @Singleton
    fun provideGoogleBooksApiFactory(retrofitBuilder: Retrofit.Builder): GoogleBooksApiFactory {
        return GoogleBooksApiFactory(retrofitBuilder)
    }
    
    /**
     * 提供OpenLibraryApiFactory
     * 调用方可以通过factory.create(baseUrl)创建具有特定baseUrl的API实例
     */
    @Provides
    @Singleton
    fun provideOpenLibraryApiFactory(retrofitBuilder: Retrofit.Builder): OpenLibraryApiFactory {
        return OpenLibraryApiFactory(retrofitBuilder)
    }
} 