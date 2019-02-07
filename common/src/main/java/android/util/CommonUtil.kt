package android.util

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

@Suppress("LocalVariableName", "NAME_SHADOWING", "NonAsciiCharacters", "FunctionName", "ObjectPropertyName", "unused")
val CharSequence.이가: (이: Char, 가: Char) -> CharSequence
    get() = { 이: Char, 가: Char ->
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
        when {
            (lastName < '가' || lastName > '힣') -> this
            (lastName - '가') % JT > 0 -> "$this$이"
            else -> "$this$가"
        }
    }