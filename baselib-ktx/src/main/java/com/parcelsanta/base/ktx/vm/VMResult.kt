/*
 * Copyright 2010-2018 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 *
 * 参考 Result 代码修改
 * @author yan
 */
package com.parcelsanta.base.ktx.vm

/**
 * A discriminated union that encapsulates successful outcome with a value of type [T]
 * or a failure with an arbitrary [Throwable] exception.
 */
class VMResult<out T> @PublishedApi internal constructor(
    @PublishedApi
    internal val value: Any?
) {

    val isLoading: Boolean get() = value is Loading

    /**
     * Returns `true` if this instance represents successful outcome.
     * In this case [isFailure] returns `false`.
     */
    val isSuccess: Boolean get() = !isLoading && value !is Failure

    /**
     * Returns `true` if this instance represents failed outcome.
     * In this case [isSuccess] returns `false`.
     */
    val isFailure: Boolean get() = value is Failure

    // value & exception retrieval

    /**
     * Returns the encapsulated value if this instance represents [success][VMResult.isSuccess] or `null`
     * if it is [failure][VMResult.isFailure].
     */
    fun getOrNull(): T? =
        when {
            isFailure -> null
            else -> value as T
        }

    /**
     * Returns the encapsulated exception if this instance represents [failure][isFailure] or `null`
     * if it is [success][isSuccess].
     */
    fun exceptionOrNull(): Throwable? =
        when (value) {
            is Failure -> value.exception
            else -> null
        }

    /**
     * Returns a string `Success(v)` if this instance represents [success][VMResult.isSuccess]
     * where `v` is a string representation of the value or a string `Failure(x)` if
     * it is [failure][isFailure] where `x` is a string representation of the exception.
     */
    override fun toString(): String =
        when (value) {
            is Failure -> value.toString() // "Failure($exception)"
            else -> "Success($value)"
        }

    // companion with constructors

    /**
     * Companion object for [VMResult] class that contains its constructor functions
     * [success] and [failure].
     */
    companion object {

        fun <T> loading() =
            VMResult<T>(createLoading())

        fun <T> success(value: T): VMResult<T> =
            VMResult(value)

        fun <T> failure(exception: Throwable): VMResult<T> =
            VMResult(createFailure(exception))
    }

    internal class Failure(
        @JvmField
        val exception: Throwable
    ) {
        override fun equals(other: Any?): Boolean = other is Failure && exception == other.exception
        override fun hashCode(): Int = exception.hashCode()
        override fun toString(): String = "Failure($exception)"
    }

    internal class Loading
}

@PublishedApi
internal fun createLoading(): Any = VMResult.Loading()

/**
 * Creates an instance of internal marker [VMResult.Failure] class to
 * make sure that this class is not exposed in ABI.
 */
@PublishedApi
internal fun createFailure(exception: Throwable): Any =
    VMResult.Failure(exception)

// -- extensions ---

/**
 * Performs the given [action] on encapsulated exception if this instance represents [failure][VMResult.isFailure].
 * Returns the original `VMResult` unchanged.
 */
inline fun <T> VMResult<T>.onFailure(action: (exception: Throwable) -> Unit): VMResult<T> {
    exceptionOrNull()?.let { action(it) }
    return this
}

/**
 * Performs the given [action] on encapsulated value if this instance represents [success][VMResult.isSuccess].
 * Returns the original `VMResult` unchanged.
 */
inline fun <T> VMResult<T>.onSuccess(action: (value: T) -> Unit): VMResult<T> {
    if (isSuccess) action(value as T)
    return this
}

inline fun <T> VMResult<T>.onLoading(action: () -> Unit): VMResult<T> {
    if (isLoading) action()
    return this
}

// -------------------
