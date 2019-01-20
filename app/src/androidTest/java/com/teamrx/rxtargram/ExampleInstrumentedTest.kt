package com.teamrx.rxtargram

import android.log.Log
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.teamrx.rxtargram.repository.RemoteAppDataSource

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("com.teamrx.rxtargram", appContext.packageName)
    }
    @Test
    fun getProfile2() {
        Log.MODE = Log.eMODE.SYSTEMOUT
        RemoteAppDataSource.getProfile2("")
    }

}
