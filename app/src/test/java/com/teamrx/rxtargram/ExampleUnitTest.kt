package com.teamrx.rxtargram

import android.log.Log
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

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
    fun `byte 26개 짜르기`() {
        val subject = PublishSubject.create<Array<Int>>()

        subject.subscribeOn(Schedulers.io())
                .concatMap { Observable.fromIterable(it.asIterable()) }
                .buffer(5, 1)
                .filter { it.first() == 100 }
                .map { it.toList() }

                .subscribe { println(Arrays.toString(it.toTypedArray())) }

        subject.onNext(intArrayOf(1, 5, 10, 22, 25, 1, 1, 1, 100, 100, 28, 22, 25, 1, 6, 15, 22, 25, 1, 5, 10, 22, 25, 1, 100, 28, 22, 25, 1, 6, 15, 22, 25).toTypedArray())
        subject.onComplete()
    }

    @Test
    fun `1 부터 시작하는 5개 자르기`() {
        Log.MODE = Log.eMODE.SYSTEMOUT

        Observable.fromArray('0', '0', '0', '0', '1', '3', '1', '5', '6', '7', '1', '4', 'a', '0', '0')
                .buffer(5, 1)
                .filter {
                    it.first() == '1' }
                .map { it.toList() }
                .subscribe {
                    println(Arrays.toString(it.toCharArray()))
                }
    }

//    Observable<Integer> observable1 = Observable.create(it -> {
//        for (int i = 0; i < 5; i++) {
//            Thread.sleep(1000);
//            it.onNext(i);
//        }
//        it.onComplete();
//    })
//
//    Observable<Integer> observable2 = Observable.timer(3, TimeUnit.SECONDS)
//    .flatMap(__ -> Observable.just(11, 22, 33, 44, 55));
//
//    observable1.skipUntil(observable2)
//    .subscribe(onNext -> {
//        Log.w("OnNext: ", "next: " + onNext);
//    });

}