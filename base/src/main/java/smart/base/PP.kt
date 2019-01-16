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
 * PP.sample.isit();
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
 * PP.sample.set(&quot;text&quot;);
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
    user_id,
    ;

    companion object {
        private lateinit var PREFERENCES: SharedPreferences

        private val DEFVALUE_STRING = ""
        private val DEFVALUE_FLOAT = -1f
        private val DEFVALUE_INT = -1
        private val DEFVALUE_LONG = -1L
        private val DEFVALUE_BOOLEAN = false

        val uuid get() = UUID.get(randomUUID().toString())

        fun CREATE(context: Context) {
            PREFERENCES = PreferenceManager.getDefaultSharedPreferences(context)
        }

        //실재값에 변화가 있을때만 event가 날라온다
        fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
            PREFERENCES.registerOnSharedPreferenceChangeListener(listener)
        }

        fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
            PREFERENCES.unregisterOnSharedPreferenceChangeListener(listener)
        }

        fun clear() = PREFERENCES.edit().clear().commit()
    }

    //@formatter:off
    fun getBoolean   (defValues: Boolean      = DEFVALUE_BOOLEAN ) =  PREFERENCES.getBoolean  (name, defValues)
    fun getInt       (defValues: Int          = DEFVALUE_INT     ) =  PREFERENCES.getInt      (name, defValues)
    fun getLong      (defValues: Long         = DEFVALUE_LONG    ) =  PREFERENCES.getLong     (name, defValues)
    fun getFloat     (defValues: Float        = DEFVALUE_FLOAT   ) =  PREFERENCES.getFloat    (name, defValues)
    fun getString    (defValues: String       = DEFVALUE_STRING  ) =  PREFERENCES.getString   (name, defValues)
    fun getStringSet (defValues: Set<String>? = null             ) =  PREFERENCES.getStringSet(name, defValues)

    fun set(v: Boolean     ) = PREFERENCES.edit { putBoolean  (name, v) }
    fun set(v: Int         ) = PREFERENCES.edit { putInt      (name, v) }
    fun set(v: Long        ) = PREFERENCES.edit { putLong     (name, v) }
    fun set(v: Float       ) = PREFERENCES.edit { putFloat    (name, v) }
    fun set(v: String     ?) = PREFERENCES.edit { putString   (name, v) }
    fun set(v: Set<String>?) = PREFERENCES.edit { putStringSet(name, v) }

    fun toggle() = set(!getBoolean())
    fun get(defValue: String = DEFVALUE_STRING) = getString(defValue)
    fun isit(defValue: Boolean = DEFVALUE_BOOLEAN) = getBoolean(defValue)

    fun contain() = PREFERENCES.contains(name)
    fun remove() = PREFERENCES.edit().remove(name).commit()

    fun first(): Boolean {
        val result = getBoolean(false)
        set(true)
        return result
    }
}
