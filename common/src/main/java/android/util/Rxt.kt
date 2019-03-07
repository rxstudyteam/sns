package android.util

import android.content.Context
import android.graphics.Bitmap
import android.widget.TextView
import android.widget.Toast
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

fun TextView.check(): Boolean {
    if (text.isNullOrBlank()) {
        Toast.makeText(context, " ${hint.이가('이', '가')} 없습니다.", Toast.LENGTH_SHORT).show()
        requestFocus()
        return false
    }
    return true
}

fun Bitmap.toStream(): InputStream {
    val bos = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.JPEG, 100, bos)
    val bytes = bos.toByteArray()
    return ByteArrayInputStream(bytes)
}

@Suppress("LocalVariableName", "NAME_SHADOWING", "NonAsciiCharacters", "FunctionName")
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

    var lastName = last()
    return when {
        (lastName < '가' || lastName > '힣') -> this
        (lastName - '가') % JT > 0 -> "$this$이"
        else -> "$this$가"
    }
}

fun Context.getStringArray(id: Int): Array<String> {
    return resources.getStringArray(id)
}
