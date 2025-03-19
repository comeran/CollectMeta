package com.homeran.collectmeta.domain.usecase

/**
 * 用例接口
 * @param P 输入参数类型
 * @param R 返回结果类型
 */
interface UseCase<in P, R> {
    suspend operator fun invoke(parameters: P): R
}

/**
 * 无参数的用例接口
 * @param R 返回结果类型
 */
interface NoParamsUseCase<R> {
    suspend operator fun invoke(): R
}

/**
 * 用例基类
 * @param P 输入参数类型
 * @param R 返回结果类型
 */
abstract class BaseUseCase<in P, R> : UseCase<P, R> {
    override suspend operator fun invoke(parameters: P): R {
        return execute(parameters)
    }

    protected abstract suspend fun execute(parameters: P): R
}

/**
 * 无参数的用例基类
 * @param R 返回结果类型
 */
abstract class BaseNoParamsUseCase<R> : NoParamsUseCase<R> {
    override suspend operator fun invoke(): R {
        return execute()
    }

    protected abstract suspend fun execute(): R
} 