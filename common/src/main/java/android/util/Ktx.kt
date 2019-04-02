@file:Suppress("NonAsciiCharacters", "FunctionName", "NAME_SHADOWING", "LocalVariableName", "unused", "SpellCheckingInspection")

package android.util

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

val Int.dp: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Context.deviceid: String get() = PreferenceManager.getDefaultSharedPreferences(this).run { getString("deviceid", null) ?: java.util.UUID.randomUUID().toString().also { edit().putString("deviceid", it).apply() } }

fun Context.toast(text: CharSequence) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
fun Context.toast(@StringRes text: Int) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
fun Context.toastLong(text: CharSequence) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()
fun Context.toastLong(@StringRes text: Int) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()

fun TextView.check(): Boolean {
    if (text.isNullOrBlank()) {
        Toast.makeText(context, " ${hint.이가('이', '가')} 없습니다.", Toast.LENGTH_SHORT).show()
        requestFocus()
        return false
    }
    return true
}

fun Bitmap.toStream(quality: Int): InputStream {
    val bos = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.JPEG, quality, bos)
    val bytes = bos.toByteArray()
    return ByteArrayInputStream(bytes)
}

val Bitmap.jpegstream: InputStream
    get() {
        val bos = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 100, bos)
        val bytes = bos.toByteArray()
        return ByteArrayInputStream(bytes)
    }

fun CharSequence.이가(이: Char, 가: Char): CharSequence {
    var 이 = 이
    var 가 = 가
    val JT = 28
    val M = 21
    if ((이 - '가') / JT / M != 11/*ㅇ*/) {
        val t = 이
        이 = 가
        가 = t
    }

    val lastName = last()
    return when {
        (lastName < '가' || lastName > '힣') -> this
        (lastName - '가') % JT > 0 -> "$this$이"
        else -> "$this$가"
    }
}

fun Button.disableButton() {
    isEnabled = false
}

fun Button.enableButton() {
    isEnabled = true
}