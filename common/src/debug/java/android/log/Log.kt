package android.log

import android.content.res.Resources
import java.util.*

var keep = 0L
private val Long.r: String get() = String.format(Locale.getDefault(), "%,25d", this)
fun nano() = (if (keep == 0L) 0L else System.nanoTime() - keep).r.also { keep = System.nanoTime() }
fun sano() = 0L.r.also { keep = System.nanoTime() }
