package smart.util

import android.util.이가
import android.widget.TextView
import android.widget.Toast

fun TextView.check(): Boolean {
    if (text.isNullOrBlank()) {
        Toast.makeText(context, " ${hint.이가('이', '가')} 없습니다.", Toast.LENGTH_SHORT).show()
        requestFocus()
        return false
    }
    return true
}