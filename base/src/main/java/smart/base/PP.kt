@file:Suppress("unused", "SpellCheckingInspection", "MemberVisibilityCanBePrivate", "FunctionName")

package smart.base

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.core.content.edit
import java.util.*

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
@Suppress("EnumEntryName")
enum class PP {
    IS_FIRST_RUN, // 앱 최초 실행 여부
    PUSH_TOKEN,
    IS_READ_PUSH,
    ACCESS_TOKEN,
    REFRESH_TOKEN,
    user_id,
    ;

    companion object {
        private lateinit var PREFERENCES: SharedPreferences

        private const val DEFAULT_STRING = ""
        private const val DEFAULT_FLOAT = -1f
        private const val DEFAULT_INT = -1
        private const val DEFAULT_LONG = -1L
        private const val DEFAULT_BOOLEAN = false

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

        val deviceid: String get() = PREFERENCES.run { getString("deviceid", null) ?: java.util.UUID.randomUUID().toString().also { edit().putString("deviceid", it).apply() } }
        fun getUserID(): String = user_id.get(deviceid)!!

    }

    //@formatter:off
    fun getBoolean   (DEFAULT : Boolean      = DEFAULT_BOOLEAN )                =  PREFERENCES.getBoolean  (name, DEFAULT)
    fun isit         (DEFAULT : Boolean      = DEFAULT_BOOLEAN )                =              getBoolean  (      DEFAULT)
    fun getInt       (DEFAULT : Int          = DEFAULT_INT     )                =  PREFERENCES.getInt      (name, DEFAULT)
    fun getLong      (DEFAULT : Long         = DEFAULT_LONG    )                =  PREFERENCES.getLong     (name, DEFAULT)
    fun getFloat     (DEFAULT : Float        = DEFAULT_FLOAT   )                =  PREFERENCES.getFloat    (name, DEFAULT)
    fun getString    (DEFAULT : String?      = DEFAULT_STRING  ) : String?      =  PREFERENCES.getString   (name, DEFAULT)
    fun get          (DEFAULT : String?      = DEFAULT_STRING  ) : String?      =              getString   (      DEFAULT)
    fun getStringSet (DEFAULT : Set<String>? = null            ) : Set<String>? = PREFERENCES.getStringSet(name, DEFAULT)

    fun set(v: Boolean     ) = PREFERENCES.edit { putBoolean  (name, v) }
    fun set(v: Int         ) = PREFERENCES.edit { putInt      (name, v) }
    fun set(v: Long        ) = PREFERENCES.edit { putLong     (name, v) }
    fun set(v: Float       ) = PREFERENCES.edit { putFloat    (name, v) }
    fun set(v: String     ?) = PREFERENCES.edit { putString   (name, v) }
    fun set(v: Set<String>?) = PREFERENCES.edit { putStringSet(name, v) }

    fun toggle() = set(!getBoolean())
    fun contain() = PREFERENCES.contains(name)
    fun remove() = PREFERENCES.edit().remove(name).commit()

    fun first(): Boolean  = getBoolean().apply { set(true) }
}
