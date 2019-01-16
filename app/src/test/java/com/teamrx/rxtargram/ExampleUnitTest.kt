package com.teamrx.rxtargram

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
//    fun getTotalX(a: Array<Int>, b: Array<Int>): Int {
    @Test
    fun getTotalX() {
        var a = arrayOf(3, 4)
        var b = arrayOf(24, 48)
        var result = ArrayList<Int>()
        var max1 = a.max()
        var max = b.min()
        for (i in max1!!..max!! ) {
            var isGood = true
            for (j in 0 until b.size) {
                if (b[j] % i != 0) {
                    isGood = false
                    continue

                }
            }
            if (isGood) {
                for (j in 0 until a.size) {
                    if (i % a[j] != 0) {
                        isGood = false
                        break
                    }
                }
            }
            if (isGood) {
                result.add(i)
            }
        }
        System.out.println( result.toString())
        assert(result.size == 3)
//        return result.size
    }

}
