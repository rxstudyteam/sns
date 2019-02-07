package com.teamrx.rxtargram

import android.content.res.Resources
import kotlinx.coroutines.*
import org.junit.Test
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class CoroutineUnitTest {
    @Test
    fun coroutineScope() {
        runBlocking {
            launch {
                println("01_job")
            }
            println("00_start")
        }
        println("99_end")
    }

    @Test
    fun `Thread run vs test`() {
        Thread {
            println("-Thread--- start : ${Thread.currentThread().name} ${nano()}")
            Thread.sleep(1000)
            println("-Thread--- ended : ${Thread.currentThread().name} ${nano()}")
        }.run()

        run {
            println("-Run------ start : ${Thread.currentThread().name} ${nano()}")
            Thread.sleep(200)
            println("-Run------ ended : ${Thread.currentThread().name} ${nano()}")
        }
        println("--end--")
        Thread.sleep(3000)
        println("--wait--")

    }

    @Test
    fun `Thread start vs run`() {
        Thread {
            println("-Thread--- start : ${Thread.currentThread().name} ${nano()}")
            Thread.sleep(1000)
            println("-Thread--- ended : ${Thread.currentThread().name} ${nano()}")
        }.start()

        run {
            println("-Run------ start : ${Thread.currentThread().name} ${nano()}")
            Thread.sleep(200)
            println("-Run------ ended : ${Thread.currentThread().name} ${nano()}")
        }
        println("--end--")
        Thread.sleep(3000)
        println("--wait--")

    }

    @Test
    fun `runBlocking vs run`() {
        runBlocking {
            println("-Blocking- start : ${Thread.currentThread().name} ${nano()}")
            Thread.sleep(1000)
            println("-Blocking- ended : ${Thread.currentThread().name} ${nano()}")
        }

        run {
            println("-Run------ start : ${Thread.currentThread().name} ${nano()}")
            Thread.sleep(200)
            println("-Run------ ended : ${Thread.currentThread().name} ${nano()}")
        }
        println("--end--")
        Thread.sleep(3000)
        println("--wait--")

    }

    @Test
    fun `runBlocking CommonPool vs run`() {
        runBlocking(Dispatchers.Default) {
            println("-Blocking- start : ${Thread.currentThread().name} ${nano()}")
            Thread.sleep(1000)
            println("-Blocking- ended : ${Thread.currentThread().name} ${nano()}")
        }

        run {
            println("-Run------ start : ${Thread.currentThread().name} ${nano()}")
            Thread.sleep(200)
            println("-Run------ ended : ${Thread.currentThread().name} ${nano()}")
        }
        println("--end--")
        Thread.sleep(3000)
        println("--wait--")

    }

    @Test
    fun `launch vs run`() {
        CoroutineScope(Dispatchers.Default).launch {
            println("--Launch-- start : ${Thread.currentThread().name} ${nano()}")
            Thread.sleep(1000)
            println("--Launch-- ended : ${Thread.currentThread().name} ${nano()}")
        }

        run {
            println("--Run----- start : ${Thread.currentThread().name} ${nano()}")
            Thread.sleep(200)
            println("--Run----- ended : ${Thread.currentThread().name} ${nano()}")
        }
        println("--end--")
        Thread.sleep(3000)
        println("--wait--")

    }

    @Test
    fun `runBlocking in launch and run`() {
        runBlocking {
            launch(coroutineContext) {
                println("--Launch-- start : ${Thread.currentThread().name} ${nano()}")
                Thread.sleep(1000)
                println("--Launch-- ended : ${Thread.currentThread().name} ${nano()}")
            }

            run {
                println("--Run----- start : ${Thread.currentThread().name} ${nano()}")
                Thread.sleep(200)
                println("--Run----- ended : ${Thread.currentThread().name} ${nano()}")
            }
        }
        println("--end--")
        Thread.sleep(3000)
        println("--wait--")
    }

    @Test
    fun `runBlocking in launch join and run`() {
        runBlocking {
            launch(coroutineContext) {
                println("--Launch-- start : ${Thread.currentThread().name} ${sano()}")
                Thread.sleep(1000)
                println("--Launch-- ended : ${Thread.currentThread().name} ${nano()}")
            }.join()

            run {
                println("--Run----- start : ${Thread.currentThread().name} ${nano()}")
                Thread.sleep(200)
                println("--Run----- ended : ${Thread.currentThread().name} ${nano()}")
            }
        }
        println("--end--")
        Thread.sleep(3000)
        println("--wait--")
    }

    @Test
    fun `runBlocking in launch delay and run`() {
        runBlocking {
            launch(coroutineContext) {
                println("--Launch-- start : ${Thread.currentThread().name} ${nano()}")
                delay(1000)
                println("--Launch-- ended : ${Thread.currentThread().name} ${nano()}")
            }

            run {
                println("--Run----- start : ${Thread.currentThread().name} ${nano()}")
                delay(200)
                println("--Run----- ended : ${Thread.currentThread().name} ${nano()}")
            }
        }
        println("--end--")
        Thread.sleep(3000)
        println("--wait--")
    }

    @Test
    fun `runBlocking in launch join delay and run`() {
        runBlocking {
            launch(coroutineContext) {
                println("--Launch-- start : ${Thread.currentThread().name} ${nano()}")
                delay(1000)
                println("--Launch-- ended : ${Thread.currentThread().name} ${nano()}")
            }.join()

            run {
                println("--Run----- start : ${Thread.currentThread().name} ${nano()}")
                delay(200)
                println("--Run----- ended : ${Thread.currentThread().name} ${nano()}")
            }
        }
        println("--end--")
        Thread.sleep(3000)
        println("--wait--")
    }

    @Test
    fun `runBlocking in launch job delay and run`() {
        runBlocking {
            val job = launch(coroutineContext) {
                println("--Launch-- start : ${Thread.currentThread().name} ${nano()}")
                delay(1000)
                println("--Launch-- ended : ${Thread.currentThread().name} ${nano()}")
            }

            run {
                println("--Run----- start : ${Thread.currentThread().name} ${nano()}")
                delay(200)
                job.cancel()
                println("--Run----- ended : ${Thread.currentThread().name} ${nano()}")
            }
        }
        println("--end--")
        Thread.sleep(3000)
        println("--wait--")
    }

    var keep = 0L
    val Int.dp: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()
    val Long.cn: String get() = String.format(Locale.getDefault(), "%,25d", this)

    fun nano() = (if (keep == 0L) 0L.cn else (System.nanoTime() - keep).cn).apply { keep = System.nanoTime() }
    fun sano() = (0L.cn).apply { keep = System.nanoTime() }
}