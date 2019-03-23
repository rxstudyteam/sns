package com.teamrx.rxtargram

import io.reactivex.Observable
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class TestRx {
    @Test
    fun testRx() {


//        Observable.interval(1, TimeUnit.SECONDS)
//            .map { it % 2 }
//            .subscribe {
//                //todo doit animation
//            }
//
//        Thread.sleep(100000L)


        val time = SimpleDateFormat("yyyy-MM-dd").format(Date(Long.MAX_VALUE))

        println(time)


    }
}