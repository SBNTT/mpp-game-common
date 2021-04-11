package me.sbntt.mppgame.common

sealed class Result<out S, out E> {

    data class Success<S>(val value: S) : Result<S, Nothing>()
    data class Error<E>(val value: E) : Result<Nothing, E>()

    fun isSuccess() = this is Success

    fun isError() = this is Error

    fun <T> fold(success: (value: S) -> T, error: (value: E) -> T): T {
        return when (this) {
            is Success -> success(value)
            is Error -> error(value)
        }
    }

    fun <T> onSuccess(success: (value: S) -> T): T? {
        return if (this is Success) success(value) else null
    }

    fun <T> onError(error: (value: E) -> T): T? {
        return if (this is Error) error(value) else null
    }

}