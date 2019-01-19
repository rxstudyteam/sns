package com.teamrx.rxtargram

import android.log.Log
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun coroutineScope() {
        Log.MODE = Log.eMODE.SYSTEMOUT
        runBlocking {
            val job = launch {
                Log.e("01_job")
            }
            Log.e("00_start")
        }
        Log.e("99_end")
    }
}