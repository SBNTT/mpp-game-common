package me.sbntt.mppgame.common

import me.sbntt.mppgame.common.Result
import kotlin.test.Test
import kotlin.test.assertTrue

class ResultTests {

    companion object {
        private const val SUCCESS_VALUE = ":D"
        private val ERROR_VALUE = Exception("uuh")
    }

    @Test
    fun successInstanciationTest() {
        val result = getSuccessResult()
        assertTrue { result is Result.Success }
        assertTrue { result.isSuccess() }
        assertTrue { result == Result.Success(SUCCESS_VALUE) }
    }

    @Test
    fun errorInstanciationTest() {
        val result = getErrorResult()
        assertTrue { result is Result.Error }
        assertTrue { result.isError() }
        assertTrue { result == Result.Error(ERROR_VALUE) }
    }

    @Test
    fun onSuccessTest() {
        assertTrue { getSuccessResult().onSuccess { it } == SUCCESS_VALUE }
        assertTrue { getErrorResult().onSuccess { it } == null }
    }

    @Test
    fun onErrorTest() {
        assertTrue { getSuccessResult().onError { it } == null }
        assertTrue { getErrorResult().onError { it } == ERROR_VALUE }
    }

    @Test
    fun foldSuccessTest() {
        assertTrue {getSuccessResult().fold(success = { it }, error = { null }) == SUCCESS_VALUE}
        assertTrue {getErrorResult().fold(success = { it }, error = { null }) == null}
    }

    @Test
    fun foldErrorTest() {
        assertTrue {getErrorResult().fold(success = { null }, error = { it }) == ERROR_VALUE}
        assertTrue {getSuccessResult().fold(success = { null }, error = { it }) == null}
    }

    private fun getSuccessResult(): Result<String, Exception> {
        return Result.Success(SUCCESS_VALUE)
    }

    private fun getErrorResult(): Result<String, Exception> {
        return Result.Error(ERROR_VALUE)
    }

}