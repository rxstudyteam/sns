package smart.base

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.core.content.edit
import java.util.UUID.randomUUID

/**
 * <pre>
 * 아래와 같이 사용하세요
 * 변수 타입을 혼용하여 사용하면 죽음!
 * PP.sample.is();
 * PP.sample.set(true);
 *
 * PP.sample.getInt();
 * PP.sample.set(1);
 *
 * PP.sample.getLong();
 * PP.sample.set(1L);
 *
 * PP.sample.get();
 * PP.sample.getString();
 * v.sample.set(&quot;text&quot;);
 *
</pre> *
 */
enum class PP {
    IS_FIRST_RUN, // 앱 최초 실행 여부
    PUSH_TOKEN,
    IS_READ_PUSH,
    ACCESS_TOKEN,
    REFRESH_TOKEN,
    UUID,
    ;

    val boolean
        get() = getBoolean(DEFVALUE_BOOLEAN)
    val int: Int
        get() = getInt(DEFVALUE_INT)
    val long: Long
        get() = getLong(DEFVALUE_LONG)
    val float: Float
        get() = getFloat(DEFVALUE_FLOAT)
    val string: String?
        get() = getString(DEFVALUE_STRING)
    val stringSet: Set<String>?
        get() = getStringSet(null)
    val isEmpty: Boolean
        get() = get() == null || get()!!.length <= 0

    fun set(v: Boolean) {
        PREFERENCES.edit {
            putBoolean(name, v)
        }
    }

    fun set(v: Int): Int {
        val rtn = int
        PREFERENCES!!.edit().putInt(name, v).apply()
        return rtn
    }

    fun set(v: Long): Long {
        val rtn = long
        PREFERENCES!!.edit().putLong(name, v).apply()
        return rtn
    }

    fun set(v: Float): Float {
        val rtn = float
        PREFERENCES!!.edit().putFloat(name, v).apply()
        return rtn
    }

    fun set(v: String) {
        PREFERENCES.edit().putString(name, v).apply()
    }

    fun set(v: Set<String>): Set<String>? {
        val rtn = stringSet
        PREFERENCES!!.edit().putStringSet(name, v).apply()
        return rtn
    }

    fun getBoolean(defValues: Boolean) = PREFERENCES.getBoolean(name, defValues)

    fun getInt(defValues: Int): Int {
        return PREFERENCES.getInt(name, defValues)
    }

    fun getLong(defValues: Long): Long {
        return PREFERENCES.getLong(name, defValues)
    }

    fun getFloat(defValues: Float): Float {
        return PREFERENCES.getFloat(name, defValues)
    }

    fun getString(defValues: String): String? {
        return PREFERENCES.getString(name, defValues)
    }

    fun getStringSet(defValues: Set<String>?): Set<String>? {
        return PREFERENCES.getStringSet(name, defValues)
    }
    //@formatter:on

    fun first(): Boolean {
        val result = !`is`()
        set(true)
        return result
    }

    fun toggle() {
        set(!`is`())
    }

    operator fun get(defValue: String): String? {
        return getString(defValue)
    }

    fun get(): String? {
        return string
    }

    fun `is`(): Boolean {
        return boolean
    }

    fun `is`(defValues: Boolean): Boolean {
        return getBoolean(defValues)
    }

    fun contain(): Boolean {
        return PREFERENCES.contains(name)
    }

    fun remove(): Boolean {
        return PREFERENCES.edit().remove(name).commit()
    }

    companion object {
        private lateinit var PREFERENCES: SharedPreferences

        fun uuid(): String? {
            return UUID[randomUUID().toString()]
        }

        private val DEFVALUE_STRING = ""
        private val DEFVALUE_FLOAT = -1f
        private val DEFVALUE_INT = -1
        private val DEFVALUE_LONG = -1L
        private val DEFVALUE_BOOLEAN = false

        fun CREATE(context: Context) {
            PREFERENCES = PreferenceManager.getDefaultSharedPreferences(context)
            UPDATE()
        }

        fun UPDATE() {}

        //실재값에 변화가 있을때만 event가 날라온다
        fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
            PREFERENCES!!.registerOnSharedPreferenceChangeListener(listener)
        }

        fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
            PREFERENCES!!.unregisterOnSharedPreferenceChangeListener(listener)
        }

        fun clear(): Boolean {
            return PREFERENCES!!.edit().clear().commit()
        }

    }

}
