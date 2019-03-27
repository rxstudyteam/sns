package com.teamrx.rxtargram

import android.log.Log
import io.reactivex.Observable
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class RxUnitTest {
    @Test
    fun `rx error test 한글`() {
        Log.MODE = Log.eMODE.SYSTEMOUT
        val d = Observable.just("String")
                .map { Integer.parseInt(it) }
                .filter { it < 10 }
                .map { it.toString() }
                .subscribe({ Log.i(it) }, { Log.e(it) })
    }

}