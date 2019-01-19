package com.teamrx.rxtargram

import android.log.Log
import kotlinx.coroutines.delay
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
    fun t2() {
        runBlocking {
            val jobs = List(10) {
                launch {
                    delay(1000L)
                    Log.e("aaa")
                }
            }
            Log.e("End runBlock ")
        }
        Log.e("End function")
    }
}