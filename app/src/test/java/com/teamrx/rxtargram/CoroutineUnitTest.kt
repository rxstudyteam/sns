package com.teamrx.rxtargram

import android.log.nano
import android.log.sano
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.junit.Test
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

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

    @Test
    fun `GlobalScope and runBlocking`() {
        GlobalScope.launch {
            delay(1000L)
            println("world!")
        }
        println("#1 Hello, ")
        runBlocking {
            delay(2000L)
        }
    }

    @Test
    fun `runBlocking and launch`() = runBlocking {
        launch {
            delay(1000L)
            println("world!")
        }
        println("#2 Hello, ")
    }

    @Test
    fun `join`() = runBlocking {
        val job = GlobalScope.launch {
            delay(1000L)
            println("world!")
        }
        println("#3 Hello, ")
        job.join()
    }

    @Test
    fun `launch and coroutineScope call`() = runBlocking {
        launch {
            delay(200L)
            println("Task from runBlocking")
        }

        coroutineScope {
            launch {
                delay(500L)
                println("Task from nested launch")
            }
            delay(100L)
            println("Task from coroutine scope")
        }
        println("Coroutine scope is over")
    }

    @Test
    fun `coroutineScope and launch test`() {
        GlobalScope.launch {
            println("#0-0")
            delay(1500L)
            println("#0")
            println("--------------------------")
        }
        runBlocking {
            coroutineScope {
                println("#2-0")
                delay(3000L)
                println("#2")
                println("--------------------------")
            }
            println("#1-0")
            delay(10L)
            println("#1")
            println("--------------------------")
        }
        GlobalScope.launch {
            println("#3-0")
            delay(2000L)
            println("#3")
            println("--------------------------")
        }
        runBlocking {
            launch {
                println("#4-0")
                delay(500L)
                println("#4")
                println("--------------------------")
            }
            launch {
                println("#9-0")
                delay(100L)
                println("#9")
                println("--------------------------")
            }
            launch {
                println("#10-0")
                delay(1000L)
                println("#10")
                println("--------------------------")
            }
            coroutineScope {
                println("#5-0")
                delay(3000L)
                println("#5")
                println("--------------------------")
            }
            launch {
                println("#8-0")
                delay(1000L)
                println("#8")
                println("--------------------------")
            }
        }
        println("#6")
    }

    suspend fun doWorld() {
        runBlocking {
            launch {
                delay(100L)
                println("success!")
            }
        }
        delay(1000L)
        println("world!")
    }

    @Test
    fun `suspend fun call`() = runBlocking {
        launch {
            doWorld()
        }
        println("#10 Hello, ")
    }

    @Test
    fun `Simple Repeat`() = runBlocking {
        repeat(100_000) {
            launch {
                delay(1000L)
                println(".")
            }
        }
    }

    @Test
    fun `Thread Repeat`() {
        for(i in 0..100_000) {
            Thread(Runnable {
                Thread.sleep(1000L)
                println(".")
            }).start()
        }
    }

    @Test
    fun `longRunnungTask`() = runBlocking {
        coroutineScope {
            repeat(2) { times ->
                launch {
                    longRunnungTask(times, times + 1)
                }
            }
        }
    }

    suspend fun longRunnungTask(input1: Int, input2: Int) {
        println("longRunnungTask")
        delay(2000)
        println("input1 : $input1")
        println("input2 : $input2")
        delay(5000)
        val result = input1 + input2
        println("result : $result")
    }

    @Test
    fun `launch cancel`() = runBlocking {
        val job = launch(Dispatchers.IO) {
            try {
                repeat(10) {
                    println("I'm sleeping .. $it")
                    delay(500L)
                }
            } catch (e: CancellationException) {
                e.printStackTrace()
            } finally {
                withContext(NonCancellable) {
                    delay(1000L)
                    println("main : I'm running finally!")
                }
            }
        }

        delay(1300L)
        println("main : I'm tired of waiting!")
        job.cancelAndJoin()
        println("main : now I can quit.")
    }

    @Test
    fun `timeout`() = runBlocking {
        withTimeout(1300L) {
            launch(Dispatchers.Default) {
                try {
                    repeat(1000) {
                        println("I'm sleeping .. $it")
                        delay(500L)
                    }
                } finally {
                    println("main : I'm running finally!")
                }
            }
        }
        println("main : now I can quit.")
    }

    @Test
    fun `channel`() = runBlocking {
        val channel = Channel<String>()
        launch {
            repeat(5) {
                channel.send("#1 ${it * it}")
            }
        }

        repeat(10) {
            println(channel.receive())
        }

        println("done!")
    }

    @Test
    fun `very long time work`() = runBlocking {
        var time = measureTimeMillis {  // 순차적으로 실행한 결과값
            val result = work1() + work2()
            println(result)
        }
        println("[${Thread.currentThread().name}] time : $time")

        time = measureTimeMillis {
            val work1 = async { work1() }
            val work2 = async { work2() }
            println(work1.await() + work2.await())
        }
        println("[${Thread.currentThread().name}] time : $time")

        time = measureTimeMillis {
            println(concurrentSum())
        }
        println("[${Thread.currentThread().name}] time : $time")
    }

    suspend fun work1(): Int {
        delay(2000L)
        return 1
    }

    suspend fun work2(): Int {
        delay(1000L)
        return 2
    }

    suspend fun concurrentSum(): Int = coroutineScope {
        val one = async { work1() }
        val two = async { work2() }
        one.await() + two.await()
    }

    @Test
    fun `cancel test`() = runBlocking {
        try {
            val time = measureTimeMillis {
                println("[${Thread.currentThread().name}] the answer is ${failedConcurrentSum()}")
            }
            println("[${Thread.currentThread().name}] completed in $time ms")
        } catch (throwable: Throwable) {
            println("[${Thread.currentThread().name}] computation failed with $throwable")
        }
    }

    suspend fun failedConcurrentSum(): Int = coroutineScope {
        val one = async {
            try {
                delay(Long.MAX_VALUE)
                work1()
            } finally {
                println("[${Thread.currentThread().name}] First child was cancled")
            }
        }

        val two = async<Int> {
            delay(2000L)
            println("[${Thread.currentThread().name}] second child throw an exception")
            work2()
            throw ArithmeticException("[${Thread.currentThread().name}] Exception on purpose")
        }

        one.await() + two.await()
    }

    val channel = Channel<Int>()

    @Test
    fun `channel test`() = runBlocking {

        test()

        repeat(2) {
            println(channel.receive())
        }

        //val value = test()
        println("testr2")
    }

    suspend fun test()= GlobalScope.launch {
        delay(1000)

        launch {
            channel.send(10)
            channel.send(20)
        }

        println("test")
    }

    @Test
    fun `Observable zip Test`() {

        val observe1 = Observable.zip(
            Observable.just(1, 2),
            Observable.just(1, 2),
            BiFunction<Int,Int,String> { s , n -> "$s $n" }
        )

        val observe2 = Observable.interval(1000, TimeUnit.MILLISECONDS)
            .zipWith(observe1, BiFunction<Long, String, String> { t1, t2 -> t2 })
            .subscribe { s ->
                println(s)
            }



        Thread.sleep(5000)
    }

}