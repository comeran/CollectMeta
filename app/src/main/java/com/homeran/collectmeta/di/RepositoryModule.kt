package com.homeran.collectmeta.di

import com.homeran.collectmeta.data.repository.ApiConfigRepositoryImpl
import com.homeran.collectmeta.data.repository.BookRepositoryImpl
import com.homeran.collectmeta.domain.repository.ApiConfigRepository
import com.homeran.collectmeta.domain.repository.BookRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 仓库模块
 * 提供所有仓库的依赖注入
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * 绑定书籍仓库实现
     */
    @Binds
    @Singleton
    abstract fun bindBookRepository(
        bookRepositoryImpl: BookRepositoryImpl
    ): BookRepository

    /**
     * 绑定API配置仓库实现
     */
    @Binds
    @Singleton
    abstract fun bindApiConfigRepository(
        apiConfigRepositoryImpl: ApiConfigRepositoryImpl
    ): ApiConfigRepository
} 