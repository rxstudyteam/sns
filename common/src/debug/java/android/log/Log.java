package android.log

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.*
import android.graphics.Bitmap.CompressFormat
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject

import java.io.*
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.Map.Entry


object Log {


    val VERBOSE = android.util.Log.VERBOSE
    val DEBUG = android.util.Log.DEBUG
    val INFO = android.util.Log.INFO
    val WARN = android.util.Log.WARN
    val ERROR = android.util.Log.ERROR
    val ASSERT = android.util.Log.ASSERT

    var LOG = true

    var MODE = eMODE.STUDIO
    var FLOG = false
    val LOG_ROOTPATH = "_flog"

    private val PREFIX = "``"
    private val PREFIX_MULTILINE = "$PREFIX▼"
    private val LF = "\n"
    private val MAX_LOG_LINE_BYTE_SIZE = 3600
    private val LOG_CLASS = Log::class.java.name
    private val ANDROID_CLASS =
        "^android\\.app\\..+|^android\\.os\\..+|^com\\.android\\..+|^java\\..+|^android\\.view\\.BWebView\\\$BWebViewClient"

    private//		String methodName = null;
    //            final String fileName = info.getFileName();
    //            final int lineNumber = info.getLineNumber();
    //            final String methodName = info.getMethodName();
    //            android.util.Log.d("DEBUG", className + "," + fileName + "," + methodName + "," + lineNumber);
    //            final String fileName = info.getFileName();
    //            final int lineNumber = info.getLineNumber();
    //            final String methodName = info.getMethodName();
    //            android.util.Log.d("DEBUG", className + "," + fileName + "," + methodName + "," + lineNumber);
    //			final String className = info.getClassName();
    //			final String fileName = info.getFileName();
    //			final String methodName = info.getMethodName();
    //			android.util.Log.e("DEBUG", className + "," + fileName + "," + methodName + "," + lineNumber);
    val stack: StackTraceElement
        get() {
            val stackTraceElements = Exception().stackTrace
            var i = 0
            val N = stackTraceElements.size
            var info = stackTraceElements[i]
            while (i < N) {
                info = stackTraceElements[i]
                val className = info.className
                if (className.startsWith(LOG_CLASS)) {
                    i++
                    continue
                }
                break
                i++
            }

            while (i < N) {
                info = stackTraceElements[i]
                val className = info.className
                if (className.matches(ANDROID_CLASS.toRegex())) {
                    i++
                    continue
                }
                break
                i++
            }

            while (i >= N) i--

            while (i >= 0) {
                info = stackTraceElements[i]
                val lineNumber = info.lineNumber
                if (lineNumber < 0) {
                    i--
                    continue
                }
                break
                i--
            }

            return info
        }

    private var last_filter: Long = 0

    private val HEX_CHARS = "0123456789abcdef".toCharArray()

    private val sf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS", Locale.getDefault())
    private val LOGN_FORMAT = "%" + java.lang.Long.toString(java.lang.Long.MAX_VALUE).length + "d"

    private val yyyymmddhhmmss = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())

    //tic
    private val SEED_S = HashMap<Any, Long>()

    //keep loger
    var _LOGER = Loger()

    var CLZS = ArrayList<Class<*>>()

    private var LAST_ACTION_MOVE: Long = 0

    enum class eMODE {
        STUDIO, SYSTEMOUT
    }

    fun p(priority: Int, vararg args: Any): Int {
        if (!LOG)
            return -1

        val info = stack
        val tag = getTag(info)
        val locator = getLocator(info)
        val msg = _MESSAGE(*args)
        return println(priority, tag, locator, msg)
    }

    fun ps(priority: Int, info: StackTraceElement, vararg args: Any): Int {
        if (!LOG)
            return -1
        val tag = getTag(info)
        val locator = getLocator(info)
        val msg = _MESSAGE(*args)
        return println(priority, tag, locator, msg)
    }

    fun println(priority: Int, tag: String, locator: String, msg: String): Int {
        if (!LOG)
            return -1

        val sa = ArrayList<String>(100)
        val st = StringTokenizer(msg, LF, false)
        while (st.hasMoreTokens()) {
            val byte_text = st.nextToken().toByteArray()
            var offset = 0
            val N = byte_text.size
            while (offset < N) {
                val count = safeCut(byte_text, offset, MAX_LOG_LINE_BYTE_SIZE)
                sa.add(PREFIX + String(byte_text, offset, count))
                offset += count
            }
        }
        if (MODE == eMODE.STUDIO) {
            val sb = StringBuilder(".....................................................................")

            sb.replace(0, tag.length, tag)
            sb.replace(sb.length - locator.length, sb.length, locator)
            val adj_tag = sb.toString()

            val N = sa.size
            if (N <= 0)
                return android.util.Log.println(priority, adj_tag, PREFIX)

            if (N == 1)
                return android.util.Log.println(priority, adj_tag, sa[0])

            var sum = android.util.Log.println(priority, adj_tag, PREFIX_MULTILINE)
            for (s in sa)
                sum += android.util.Log.println(priority, adj_tag, s)

            return sum
        }
        if (MODE == eMODE.SYSTEMOUT) {
            val sb = StringBuilder(".....................................................................")

            sb.replace(0, tag.length, tag)
            sb.replace(sb.length - locator.length, sb.length, locator)
            val adj_tag = sb.toString()

            val N = sa.size
            if (N <= 0) {
                println(adj_tag + PREFIX)
                return 0
            }

            if (N == 1) {
                println(adj_tag + sa[0])
                return 0
            }

            println(adj_tag + PREFIX_MULTILINE)
            for (s in sa)
                println(adj_tag + s)

            return 0
        }

        return 0
    }

    private fun getLocator(info: StackTraceElement?): String {
        return if (info == null) "" else String.format(
            Locale.getDefault(),
            "(%s:%d)",
            info.fileName,
            info.lineNumber
        )

//android studio
    }

    private fun getTag(info: StackTraceElement?): String {
        if (info == null)
            return ""
        var tag = info.methodName
        try {
            val name = info.className
            tag = name.substring(name.lastIndexOf('.') + 1) + "." + info.methodName
        } catch (e: Exception) {
        }

        return tag.replace("\\$".toRegex(), "_")
    }

    private fun getStack(methodNameKey: String): StackTraceElement {
        val stackTraceElements = Exception().stackTrace
        var info = stackTraceElements[0]
        val N = stackTraceElements.size
        var s = 0
        while (s < N) {
            info = stackTraceElements[s]
            val className = info.className
            //            final String fileName = info.getFileName();
            //            final int lineNumber = info.getLineNumber();
            //            final String methodName = info.getMethodName();
            //            android.util.Log.d("DEBUG", className + "," + fileName + "," + methodName + "," + lineNumber);
            if (className.startsWith(LOG_CLASS)) {
                //                android.util.Log.i("pass", className + "," + methodName + "," + fileName + " " + lineNumber);
                s++
                continue
            }
            //            android.util.Log.e("stop", className + "," + methodName + "," + fileName + " " + lineNumber);
            break
            s++
        }

        var e = N - 1
        while (e >= s) {
            info = stackTraceElements[e]
            val methodName = info.methodName
            val className = info.className
            //            final String fileName = info.getFileName();
            //            final int lineNumber = info.getLineNumber();
            //            android.util.Log.d("DEBUG", className + "," + methodName + "," + fileName + " " + lineNumber);
            if (methodNameKey == methodName && !className.matches(ANDROID_CLASS.toRegex())) {
                //                android.util.Log.e("stop", className + "," + methodName + "," + fileName + " " + lineNumber);
                break
            }
            e--
            //            android.util.Log.i("pass", className + "," + methodName + "," + fileName + " " + lineNumber);
        }
        return info
    }

    private fun getStackC(methodNameKey: String): StackTraceElement {
        val stackTraceElements = Exception().stackTrace

        var last_info = stackTraceElements[0]
        val N = stackTraceElements.size
        var s = 0
        while (s < N) {
            val info = stackTraceElements[s]
            last_info = info
            val className = info.className
            //            final String fileName = info.getFileName();
            //            final int lineNumber = info.getLineNumber();
            //            final String methodName = info.getMethodName();
            //            android.util.Log.d("DEBUG", className + "," + fileName + "," + methodName + "," + lineNumber);
            if (className.startsWith(LOG_CLASS)) {
                s++
                continue
            }
            break
            s++
        }

        var e = N - 1

        while (e >= s) {
            val info = stackTraceElements[e]
            val methodName = info.methodName
            //            final String className = info.getClassName();
            //            final String fileName = info.getFileName();
            //            final int lineNumber = info.getLineNumber();
            //            android.util.Log.w("DEBUG", className + "," + methodName + "," + fileName + " " + lineNumber);
            if (methodNameKey == methodName)
                break
            last_info = info
            e--
        }
        return last_info
    }
    //    private static StackTraceElement getStackC2(String methodNameKey) {
    //        final StackTraceElement[] stackTraceElements = new Exception().getStackTrace();
    //        int N = stackTraceElements.length;
    //
    //        StackTraceElement info = stackTraceElements[0];
    //        int i = 0;
    //        for (; i < N; i++) {
    //            info = stackTraceElements[i];
    //            final String methodName = info.getMethodName();
    //            final String className = info.getClassName();
    //            final String fileName = info.getFileName();
    //            final int lineNumber = info.getLineNumber();
    //            android.util.Log.w("DEBUG", className + "," + methodName + "," + fileName + " " + lineNumber);
    //            if (methodNameKey.equals(methodName)) {
    //                android.util.Log.i("break", className + "," + methodName + "," + fileName + " " + lineNumber);
    //                break;
    //            }
    //        }
    //
    //        for (; i < N; i++) {
    //            info = stackTraceElements[i];
    //            final String methodName = info.getMethodName();
    //            final String fileName = info.getFileName();
    //            final String className = info.getClassName();
    //            final int lineNumber = info.getLineNumber();
    //            final boolean isNativeMethod = info.isNativeMethod();
    //            android.util.Log.d("DEBUG", className + "," + methodName + "," + isNativeMethod + "," + fileName + " " + lineNumber);
    //            if (methodName.contains("access$")) {
    //                android.util.Log.i("pass", className + "," + methodName + "," + fileName + " " + lineNumber);
    //                continue;
    //            }
    //
    //            if (fileName == null || fileName.length() <= 0) {
    //                android.util.Log.i("pass", className + "," + methodName + "," + fileName + " " + lineNumber);
    //                continue;
    //            }
    //
    //            if (lineNumber <= 0) {
    //                android.util.Log.i("pass", className + "," + methodName + "," + fileName + " " + lineNumber);
    //                continue;
    //            }
    //
    //            if (className.contains("android.") && methodNameKey.equals(methodName)) {
    //                android.util.Log.i("pass", className + "," + methodName + "," + fileName + " " + lineNumber);
    //                continue;
    //            }
    //
    //            android.util.Log.w("break", className + "," + methodName + "," + fileName + " " + lineNumber);
    //            break;
    //        }
    //
    ////        for (; i < N; i++) {
    ////            StackTraceElement rr = stackTraceElements[i];
    ////            final String methodName = rr.getMethodName();
    ////            final String className = rr.getClassName();
    ////            final String fileName = rr.getFileName();
    ////            final int lineNumber = rr.getLineNumber();
    ////            android.util.Log.w("LOG", className + "," + methodName + "," + fileName + " " + lineNumber);
    ////        }
    //
    //        return info;
    //    }

    private fun safeCut(byte_text: ByteArray, byte_start_index: Int, byte_length: Int): Int {
        val text_length = byte_text.size
        if (text_length <= byte_start_index)
            throw ArrayIndexOutOfBoundsException("!!text_length <= start_byte_index")

        if (byte_length <= 0)
            throw UnsupportedOperationException("!!must length > 0 ")

        if (byte_text[byte_start_index] and 0xc0.toByte() == 0x80.toByte().toInt())
            throw UnsupportedOperationException("!!start_byte_index must splited index")

        var po = byte_start_index + byte_length
        if (text_length <= po)
            return text_length - byte_start_index

        while (byte_start_index <= po) {
            if (byte_text[po] and 0xc0.toByte() != 0x80.toByte().toInt())
                break
            po--
        }

        if (po <= byte_start_index)
            throw UnsupportedOperationException("!!byte_length too small")

        return po - byte_start_index
    }

    private fun flog(logfile: File, info: StackTraceElement, log: String) {
        if (!FLOG)
            return

        try {
            val parentfile = logfile.parentFile
            if (!parentfile.isDirectory && !parentfile.exists())
                parentfile.mkdirs()
            if (!logfile.exists())
                logfile.createNewFile()

            val buf = BufferedWriter(OutputStreamWriter(FileOutputStream(logfile, true), "UTF-8"))

            val tag = String.format("%-80s %-100s ``", _DUMP_milliseconds(), info.toString())
            val tagspace = String.format("%80s %100s ``", " ", " ")

            val st = StringTokenizer(log, LF, false)
            if (st.hasMoreTokens()) {
                val token = st.nextToken()
                buf.append(tag).append(token).append(LF)
            }

            while (st.hasMoreTokens()) {
                val token = st.nextToken()
                buf.append(tagspace).append(token).append(LF)
            }
            buf.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun e_filter(nano: Long, vararg args: Any): Int {
        if (!LOG)
            return -1

        if (last_filter < System.nanoTime() - nano)
            return -1

        last_filter = System.nanoTime()

        return p(android.util.Log.ERROR, *args)
    }

    fun a(vararg args: Any): Int {
        return if (!LOG) -1 else p(android.util.Log.ASSERT, *args)
    }

    fun e(vararg args: Any): Int {
        return if (!LOG) -1 else p(android.util.Log.ERROR, *args)
    }

    fun w(vararg args: Any): Int {
        return if (!LOG) -1 else p(android.util.Log.WARN, *args)
    }

    fun i(vararg args: Any): Int {
        return if (!LOG) -1 else p(android.util.Log.INFO, *args)
    }

    fun d(vararg args: Any): Int {
        return if (!LOG) -1 else p(android.util.Log.DEBUG, *args)
    }

    fun v(vararg args: Any): Int {
        return if (!LOG) -1 else p(android.util.Log.VERBOSE, *args)
    }

    fun json(json: String): Int {
        return if (!LOG) -1 else e(_DUMP_json(json))
    }

    fun `object`(o: Any): Int {
        return if (!LOG) -1 else e(_DUMP_object(o))
    }

    fun milliseconds(milliseconds: Long): Int {
        return if (!LOG) -1 else e(_DUMP_milliseconds(milliseconds))
    }

    fun provider(context: Context?, uri: Uri?): Int {
        if (!LOG)
            return -1
        if (context == null || uri == null) {
            e("context==null || uri == null")
            return -1

        }
        val c = context.contentResolver.query(uri, null, null, null, null) ?: return -1

        val result = e(c)
        c.close()
        return result
    }

    fun pn(priority: Int, depth: Int, vararg args: Any): Int {
        if (!LOG)
            return -1
        val info = Exception().stackTrace[1 + depth]
        val tag = getTag(info)
        val locator = getLocator(info)
        val msg = _MESSAGE(*args)
        return println(priority, tag, locator, msg)
    }

    fun viewtree(parent: View, vararg depth: Int): Int {
        if (!LOG)
            return -1
        val d = if (depth.size > 0) depth[0] else 0

        if (parent !is ViewGroup) {
            return pn(android.util.Log.ERROR, d + 2, _DUMP(parent, 0))
        }

        val N = parent.childCount
        var result = 0
        for (i in 0 until N) {
            val child = parent.getChildAt(i)
            result += pn(android.util.Log.ERROR, d + 2, _DUMP(child, d))

            if (child is ViewGroup)
                result += viewtree(child, d + 1)
        }
        return result
    }

    fun clz(clz: Class<*>): Int {
        if (!LOG)
            return -1
        var retult = e(clz)
        retult += i("getName", clz.name)
        retult += i("getPackage", clz.getPackage())
        retult += i("getCanonicalName", clz.canonicalName)
        retult += i("getDeclaredClasses", Arrays.toString(clz.declaredClasses))
        retult += i("getClasses", Arrays.toString(clz.classes))
        retult += i("getSigners", Arrays.toString(clz.signers))
        retult += i("getEnumConstants", Arrays.toString(clz.enumConstants))
        retult += i("getTypeParameters", Arrays.toString(clz.typeParameters))
        retult += i("getGenericInterfaces", Arrays.toString(clz.genericInterfaces))
        retult += i("getInterfaces", Arrays.toString(clz.interfaces))
        //@formatter:off
        if (clz.isAnnotation) retult += i("classinfo", clz.isAnnotation, "isAnnotation")
        if (clz.isAnonymousClass) retult += i(clz.isAnonymousClass, "isAnonymousClass")
        if (clz.isArray) retult += i(clz.isArray, "isArray")
        if (clz.isEnum) retult += i(clz.isEnum, "isEnum")
        if (clz.isInstance(CharSequence::class.java)) retult += i(
            clz.isInstance(CharSequence::class.java),
            "isInstance"
        )
        if (clz.isAssignableFrom(CharSequence::class.java)) retult += i(
            clz.isAssignableFrom(CharSequence::class.java),
            "isAssignableFrom"
        )
        if (clz.isInterface) retult += i(clz.isInterface, "isInterface")
        if (clz.isLocalClass) retult += i(clz.isLocalClass, "isLocalClass")
        if (clz.isMemberClass) retult += i(clz.isMemberClass, "isMemberClass")
        if (clz.isPrimitive) retult += i(clz.isPrimitive, "isPrimitive")
        if (clz.isSynthetic) retult += i(clz.isSynthetic, "isSynthetic")
        //@formatter:on
        return retult
    }

    ////////////////////////////////////////////////////////////////////////////
    fun toast(context: Context?, vararg args: Any) {
        if (!LOG)
            return
        if (context == null)
            return

        e(*args)
        Toast.makeText(context, _MESSAGE(*args), Toast.LENGTH_SHORT).show()
    }

    ////////////////////////////////////////////////////////////////////////////
    //_DUMP
    ////////////////////////////////////////////////////////////////////////////
    fun _MESSAGE(vararg args: Any): String {
        return _INTERNAL_MESSAGE(args)
    }

    private fun _INTERNAL_MESSAGE(args: Array<Any>?): String {
        if (args == null)
            return "null[]"

        val sb = StringBuilder()
        for (`object` in args) {
            try {
                //@formatter:off
                if (`object` == null)
                    sb.append("null")
                else if (`object` is Class<*>)
                    sb.append(_DUMP(`object`))
                else if (`object` is Cursor)
                    sb.append(_DUMP(`object`))
                else if (`object` is View)
                    sb.append(_DUMP(`object`))
                else if (`object` is Intent)
                    sb.append(_DUMP(`object`))
                else if (`object` is Bundle)
                    sb.append(_DUMP(`object`))
                else if (`object` is ContentValues)
                    sb.append(_DUMP(`object`))
                else if (`object` is Throwable)
                    sb.append(_DUMP(`object`))
                else if (`object` is Method)
                    sb.append(_DUMP(`object`))
                else if (`object` is JSONObject)
                    sb.append(`object`.toString(2))
                else if (`object` is JSONArray)
                    sb.append(`object`.toString(2))
                else if (`object` is CharSequence)
                    sb.append(_DUMP(`object`.toString()))
                else if (`object`.javaClass.isArray)
                    sb.append(_DUMP_array(`object`))
                else
                    sb.append(`object`.toString())//                else if (object instanceof Uri)           sb.append(_DUMP((Uri) object));

                //@formatter:on
                sb.append(",")
            } catch (e: Exception) {
            }

        }
        if (sb.length > 0)
            sb.setLength(sb.length - 1)
        return sb.toString()
    }

    fun _DUMP_json(json: String): String {
        try {
            if (json.length > 0) {
                if (json[0] == '{') {
                    return JSONObject(json).toString(4)
                }
                if (json[0] == '[') {
                    return JSONArray(json).toString(4)
                }
            }
        } catch (e: Exception) {
        }

        return json
    }

    fun _DUMP(`object`: String): String {
        val sb = StringBuilder()
        try {
            val s = `object`[0]
            val e = `object`[`object`.length - 1]
            if (s == '[' && e == ']') {
                val ja = JSONArray(`object`).toString(2)
                sb.append("\nJA\n")
                sb.append(ja)
            } else if (s == '{' && e == '}') {
                val jo = JSONObject(`object`).toString(2)
                sb.append("\nJO\n")
                sb.append(jo)
            } else if (s == '<' && e == '>') {
                val xml = PrettyXml.format(`object`)
                sb.append("\nXML\n")
                sb.append(xml)
            } else {
                sb.append(`object`)
            }
        } catch (e: Exception) {
            sb.append(`object`)
        }

        return sb.toString()
    }

    fun _DUMP(method: Method): String {
        val result = StringBuilder(Modifier.toString(method.modifiers))
        if (result.length != 0) {
            result.append(' ')
        }
        result.append(method.returnType.simpleName)
        result.append("                           ")
        result.setLength(20)
        result.append(method.declaringClass.simpleName)
        result.append('.')
        result.append(method.name)
        result.append("(")
        val parameterTypes = method.parameterTypes
        for (parameterType in parameterTypes) {
            result.append(parameterType.simpleName)
            result.append(',')
        }
        if (parameterTypes.size > 0)
            result.setLength(result.length - 1)
        result.append(")")

        val exceptionTypes = method.exceptionTypes
        if (exceptionTypes.size != 0) {
            result.append(" throws ")
            for (exceptionType in exceptionTypes) {
                result.append(exceptionType.simpleName)
                result.append(',')
            }
            if (exceptionTypes.size > 0)
                result.setLength(result.length - 1)
        }
        return result.toString()
    }

    private fun _DUMP(v: View, depth: Int = 0): String {
        val SP = "                    "
        val out = StringBuilder(128)
        out.append(SP)

        if (v is WebView)
            out.insert(depth, "W:" + Integer.toHexString(System.identityHashCode(v)) + ":" + v.title)
        else if (v is TextView)
            out.insert(depth, "T:" + Integer.toHexString(System.identityHashCode(v)) + ":" + v.text)
        else
            out.insert(depth, "N:" + Integer.toHexString(System.identityHashCode(v)) + ":" + v.javaClass.simpleName)
        out.setLength(SP.length)
        appendViewInfo(out, v)
        return out.toString()
    }

    private fun appendViewInfo(out: StringBuilder, v: View) {
        //		out.append('{');
        //		out.append(String.format("#%08x", System.identityHashCode(v)));
        //		out.append(' ');
        //		switch (v.getVisibility()) {
        //			case View.VISIBLE :
        //				out.append('V');
        //				break;
        //			case View.INVISIBLE :
        //				out.append('I');
        //				break;
        //			case View.GONE :
        //				out.append('G');
        //				break;
        //			default :
        //				out.append('.');
        //				break;
        //		}
        //		out.append(v.isShown() ? 'S' : '.');
        //		out.append(' ');
        //		out.append(v.isFocusable() ? 'F' : '.');
        //		out.append(v.isClickable() ? 'C' : '.');
        //		out.append(' ');
        //		out.append(v.isFocused() ? 'F' : '.');
        //		out.append(v.isPressed() ? 'P' : '.');
        //		out.append(v.isSelected() ? 'S' : '.');
        //		out.append('}');
        val id = v.id
        if (id != View.NO_ID) {
            // out.append(String.format(" #%08x", id));
            val r = v.resources
            if (id.ushr(24) != 0 && r != null) {
                try {
                    val pkgname: String
                    when (id and -0x1000000) {
                        0x7f000000 -> pkgname = "app"
                        0x01000000 -> pkgname = "android"
                        else -> pkgname = r.getResourcePackageName(id)
                    }
                    val typename = r.getResourceTypeName(id)
                    val entryname = r.getResourceEntryName(id)
                    out.append(" ")
                    out.append(pkgname)
                    out.append(":")
                    out.append(typename)
                    out.append("/")
                    out.append(entryname)
                } catch (e: Resources.NotFoundException) {
                }

            }
        }
    }

    private fun _DUMP(c: Cursor?): String {
        if (c == null)
            return "null_Cursor"

        val sb = StringBuilder()
        val count = c.count
        sb.append("<$count>")

        try {
            val columns = c.columnNames
            sb.append(Arrays.toString(columns))
            sb.append("\n")
        } catch (e: Exception) {
        }

        val countColumns = c.columnCount
        if (!c.isBeforeFirst) {
            for (i in 0 until countColumns) {
                try {
                    sb.append(c.getString(i) + ",")
                } catch (e: Exception) {
                    sb.append("BLOB,")
                }

            }
        } else {
            val org_pos = c.position
            while (c.moveToNext()) {
                for (i in 0 until countColumns) {
                    try {
                        sb.append(c.getString(i) + ",")
                    } catch (e: Exception) {
                        sb.append("BLOB,")
                    }

                }
                sb.append("\n")
            }
            c.moveToPosition(org_pos)
        }
        return sb.toString()
    }

    private fun _DUMP(values: ContentValues?): String {
        if (values == null)
            return "null_ContentValues"

        val sb = StringBuilder()
        for ((key, value1) in values.valueSet()) {
            val value = value1.toString()
            val type = value1.javaClass.simpleName
            sb.append("$key,$type,$value").append("\n")
        }

        return sb.toString()
    }

    private fun _DUMP(bundle: Bundle?): String {
        if (bundle == null)
            return "null_Bundle"

        val sb = StringBuilder()
        val keys = bundle.keySet()

        for (key in keys) {
            val o = bundle.get(key)
            if (o == null) {
                sb.append("Object $key;//null")
            } else if (o.javaClass.isArray) {
                sb.append(o.javaClass.simpleName + " " + key + ";//" + _DUMP_array(o))
            } else {
                sb.append(o.javaClass.simpleName + " " + key + ";//" + o.toString())
            }
            sb.append("\n")
        }

        return sb.toString()
    }

    private fun _DUMP_array(o: Any?): String {

        //@formatter:off
        if (o == null)
            return "null"

        if (!o.javaClass.isArray)
            return ""

        val elemElemClass = o.javaClass.componentType
        return if (elemElemClass!!.isPrimitive) {
            if (Boolean::class.javaPrimitiveType == elemElemClass)
                Arrays.toString(o as BooleanArray?)
            else if (Char::class.javaPrimitiveType == elemElemClass)
                Arrays.toString(o as CharArray?)
            else if (Double::class.javaPrimitiveType == elemElemClass)
                Arrays.toString(o as DoubleArray?)
            else if (Float::class.javaPrimitiveType == elemElemClass)
                Arrays.toString(o as FloatArray?)
            else if (Int::class.javaPrimitiveType == elemElemClass)
                Arrays.toString(o as IntArray?)
            else if (Long::class.javaPrimitiveType == elemElemClass)
                Arrays.toString(o as LongArray?)
            else if (Short::class.javaPrimitiveType == elemElemClass)
                Arrays.toString(o as ShortArray?)
            else if (Byte::class.javaPrimitiveType == elemElemClass)
                _DUMP(o as ByteArray?)
            else
                throw AssertionError()
        } else
            Arrays.toString(o as Array<Any>?)
        //@formatter:on

    }

    private fun _DUMP(cls: Class<*>?): String {
        return if (cls == null) "null_Class<?>" else cls.simpleName
//		return cls.getSimpleName() + ((cls.getSuperclass() != null) ? (">>" + cls.getSuperclass().getSimpleName()) : "");
    }

    private fun _DUMP(uri: Uri?): String {
        if (uri == null)
            return "null_Uri"

        //		return uri.toString();
        val sb = StringBuilder()
        sb.append("\r\n Uri                       ").append(uri.toString())
        sb.append("\r\n Scheme                    ").append(if (uri.scheme != null) uri.scheme else "null")
        sb.append("\r\n Host                      ").append(if (uri.host != null) uri.host else "null")
        //        sb.append("\r\n Port                      ").append(uri.getPort());
        sb.append("\r\n Path                      ").append(if (uri.path != null) uri.path else "null")
        sb.append("\r\n LastPathSegment           ")
            .append(if (uri.lastPathSegment != null) uri.lastPathSegment else "null")
        sb.append("\r\n Query                     ").append(if (uri.query != null) uri.query else "null")
        //        sb.append("\r\n");
        sb.append("\r\n Fragment                  ").append(if (uri.fragment != null) uri.fragment else "null")
        //        sb.append("\r\n SchemeSpecificPart        ").append(uri.getSchemeSpecificPart() != null ? uri.getSchemeSpecificPart().toString() : "null");
        //        sb.append("\r\n UserInfo                  ").append(uri.getUserInfo() != null ? uri.getUserInfo().toString() : "null");
        //        sb.append("\r\n PathSegments              ").append(uri.getPathSegments() != null ? uri.getPathSegments().toString() : "null");
        //        sb.append("\r\n Authority                 ").append(uri.getAuthority() != null ? uri.getAuthority().toString() : "null");
        //        sb.append("\r\n");
        //        sb.append("\r\n EncodedAuthority          ").append(uri.getEncodedAuthority() != null ? uri.getEncodedAuthority().toString() : "null");
        //        sb.append("\r\n EncodedPath               ").append(uri.getEncodedPath() != null ? uri.getEncodedPath().toString() : "null");
        //        sb.append("\r\n EncodedQuery              ").append(uri.getEncodedQuery() != null ? uri.getEncodedQuery().toString() : "null");
        //        sb.append("\r\n EncodedFragment           ").append(uri.getEncodedFragment() != null ? uri.getEncodedFragment().toString() : "null");
        //        sb.append("\r\n EncodedSchemeSpecificPart ").append(uri.getEncodedSchemeSpecificPart() != null ? uri.getEncodedSchemeSpecificPart().toString() : "null");
        //        sb.append("\r\n EncodedUserInfo           ").append(uri.getEncodedUserInfo() != null ? uri.getEncodedUserInfo().toString() : "null");
        //        sb.append("\r\n");
        return sb.toString()
    }

    fun _DUMP(intent: Intent?): String {
        if (intent == null)
            return "null_Intent"
        val sb = StringBuilder()
        //@formatter:off
        sb.append(if (intent.action != null) (if (sb.length > 0) "\n" else "") + "Action     " + intent.action!!.toString() else "")
        sb.append(if (intent.data != null) (if (sb.length > 0) "\n" else "") + "Data       " + intent.data!!.toString() else "")
        sb.append(if (intent.categories != null) (if (sb.length > 0) "\n" else "") + "Categories " + intent.categories.toString() else "")
        sb.append(if (intent.type != null) (if (sb.length > 0) "\n" else "") + "Type       " + intent.type!!.toString() else "")
        sb.append(if (intent.scheme != null) (if (sb.length > 0) "\n" else "") + "Scheme     " + intent.scheme!!.toString() else "")
        sb.append(if (intent.getPackage() != null) (if (sb.length > 0) "\n" else "") + "Package    " + intent.getPackage()!!.toString() else "")
        sb.append(if (intent.component != null) (if (sb.length > 0) "\n" else "") + "Component  " + intent.component!!.toString() else "")
        sb.append(
            if (intent.flags != 0x00) (if (sb.length > 0) "\n" else "") + "Flags      " + Integer.toHexString(
                intent.flags
            ) else ""
        )
        //@formatter:on

        if (intent.extras != null)
            sb.append((if (sb.length > 0) "\n" else "") + _DUMP(intent.extras))

        return sb.toString()
    }

    private fun _DUMP(bytearray: ByteArray?): String {
        if (bytearray == null)
            return "null_bytearray"
        try {
            val chars = CharArray(2 * bytearray.size)
            for (i in bytearray.indices) {
                chars[2 * i] = HEX_CHARS[(bytearray[i] and 0xF0).ushr(4)]
                chars[2 * i + 1] = HEX_CHARS[bytearray[i] and 0x0F]
            }
            return String(chars)
        } catch (e: Exception) {
            return "!!byte[]"
        }

    }

    fun _DUMP_StackTrace(tr: Throwable): String {
        return android.util.Log.getStackTraceString(tr)
    }

    fun _DUMP(th: Throwable): String {
        var message = "Throwable"
        try {
            var cause: Throwable? = th
            while (cause != null) {
                message = cause.javaClass.simpleName + "," + cause.message
                cause = cause.cause
            }
        } catch (e: Exception) {
        }

        return message
    }

    @JvmOverloads
    fun _DUMP_milliseconds(milliseconds: Long = System.currentTimeMillis()): String {
        return String.format("<%s,$LOGN_FORMAT>", sf.format(Date(milliseconds)), SystemClock.elapsedRealtime())
    }

    fun _DUMP_yyyymmddhhmmss(milliseconds: Long): String {
        return yyyymmddhhmmss.format(Date(milliseconds))
    }

    fun _DUMP_elapsed(elapsedRealtime: Long): String {
        return _DUMP_milliseconds(System.currentTimeMillis() - (SystemClock.elapsedRealtime() - elapsedRealtime))
    }

    fun _h2s(bytes: ByteArray): String {
        return _DUMP(bytes)
    }

    fun _s2h(bytes_text: String): ByteArray {
        val bytes = ByteArray(bytes_text.length / 2)
        try {
            for (i in bytes.indices) {
                bytes[i] = Integer.parseInt(bytes_text.substring(2 * i, 2 * i + 2), 16).toByte()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return bytes
    }

    fun _DUMP_object(o: Any): String {
        return _DUMP_object("", o, HashSet())
    }

    private fun _DUMP_object(name: String, value: Any?, duplication: MutableSet<Any>): String {
        val sb = StringBuilder()
        try {
            if (value == null) {
                sb.append(name).append("=null\n")
                return sb.toString()
            }

            if (value.javaClass.isArray) {
                sb.append(name).append('<').append(value.javaClass.simpleName).append('>').append(" = ")
                //@formatter:off
                val componentType = value.javaClass.componentType
                if (Boolean::class.javaPrimitiveType!!.isAssignableFrom(componentType!!))
                    sb.append(Arrays.toString(value as BooleanArray?))
                else if (Byte::class.javaPrimitiveType!!.isAssignableFrom(componentType))
                    sb.append(if ((value as ByteArray).size < MAX_LOG_LINE_BYTE_SIZE) String((value as ByteArray?)!!) else "[" + value.size + "]")
                else if (Char::class.javaPrimitiveType!!.isAssignableFrom(componentType))
                    sb.append(String((value as CharArray?)!!))
                else if (Double::class.javaPrimitiveType!!.isAssignableFrom(componentType))
                    sb.append(Arrays.toString(value as DoubleArray?))
                else if (Float::class.javaPrimitiveType!!.isAssignableFrom(componentType))
                    sb.append(Arrays.toString(value as FloatArray?))
                else if (Int::class.javaPrimitiveType!!.isAssignableFrom(componentType))
                    sb.append(Arrays.toString(value as IntArray?))
                else if (Long::class.javaPrimitiveType!!.isAssignableFrom(componentType))
                    sb.append(Arrays.toString(value as LongArray?))
                else if (Short::class.javaPrimitiveType!!.isAssignableFrom(componentType))
                    sb.append(Arrays.toString(value as ShortArray?))
                else
                    sb.append(Arrays.toString(value as Array<Any>?))
                //@formatter:on
            } else if (value.javaClass.isPrimitive//

                //					|| (value.getClass().getMethod("toString").getDeclaringClass() != Object.class)// toString이 정의된경우만
                || value.javaClass.isEnum//

                || value is Rect//

                || value is RectF//

                || value is Point//

                || value is Number//

                || value is Boolean//

                || value is CharSequence
            )
            //
            {
                sb.append(name).append('<').append(value.javaClass.simpleName).append('>').append(" = ")
                sb.append(value.toString())
            } else {
                if (duplication.contains(value)) {
                    sb.append(name).append('<').append(value.javaClass.simpleName).append('>').append(" = ")
                    sb.append("[duplication]\n")
                    return sb.toString()
                }
                duplication.add(value)

                if (value is Collection<*>) {
                    sb.append(name).append('<').append(value.javaClass.simpleName).append('>').append(" = ")
                        .append(":\n")
                    val it = value.iterator()
                    while (it.hasNext())
                        sb.append(_DUMP_object("  $name[item]", it.next(), duplication))
                } else {
                    val clz = value.javaClass
                    sb.append(name).append('<').append(value.javaClass.simpleName).append('>').append(" = ")
                        .append(":\n")
                    for (f in clz.declaredFields) {
                        f.isAccessible = true
                        sb.append(_DUMP_object("  " + name + "." + f.name, f.get(value), duplication))
                    }
                }
            }
            sb.append("\n")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return sb.toString()
    }

    // Line Logger
    object Line1Logger {
        private var LOGGER: StringBuilder? = null

        fun append(vararg args: Any) {
            if (LOGGER == null || LOGGER!!.length > 1024 * 4)
                LOGGER = StringBuilder(1024)
            LOGGER!!.append(_MESSAGE(*args))
        }

        fun pop(): String {
            val log = LOGGER!!.toString()
            LOGGER = null
            return log
        }
    }

    // String Logger
    object LineNLogger {
        private val LOGGER = StringBuilder(1024)

        fun insert(vararg args: Any) {
            LOGGER.insert(0, _MESSAGE(*args)).append("\n")
            LOGGER.setLength(1024 * 4)
        }

        fun get(): String {
            return LOGGER.toString()
        }

        fun clear() {
            LOGGER.delete(0, LOGGER.length)
        }
    }

    // LIST_LOGGER
    object ListLogger {
        private val LOGGER = ArrayList<String>(100)

        fun insert(vararg args: Any) {
            LOGGER.add(0, _MESSAGE(*args))
            while (LOGGER.size > 1024)
                LOGGER.removeAt(1024)
        }

        fun pop(): ArrayList<String> {
            val result = peek()
            clear()
            return result
        }

        fun get(): ArrayList<String> {
            return peek()
        }

        fun peek(): ArrayList<String> {
            return LOGGER
        }

        fun clear() {
            LOGGER.clear()
        }
    }

    fun tic_s() {
        if (!LOG)
            return
        val seed = Exception().stackTrace[2].fileName
        val e = System.nanoTime()
        SEED_S[seed] = e
    }

    fun tic() {
        if (!LOG)
            return
        val seed = Exception().stackTrace[2].fileName
        val e = System.nanoTime()
        val s = if (SEED_S[seed] == null) 0L else SEED_S[seed]
        e(String.format(Locale.getDefault(), "%,25d", e - s))
        SEED_S[seed] = e
    }

    @JvmStatic
    fun tic(args: Array<String>) {
        if (!LOG)
            return
        val seed = Exception().stackTrace[2].fileName
        val e = System.nanoTime()
        val s = if (SEED_S[seed] == null) 0L else SEED_S[seed]
        e(String.format(Locale.getDefault(), "%,25d", e - s), _INTERNAL_MESSAGE(args))
        SEED_S[seed] = e
    }

    fun tic_s(seed: Any) {
        if (!LOG)
            return
        val e = System.nanoTime()
        SEED_S[seed] = e
        e("--start--$seed-------------")
    }

    fun tic(seed: Any, vararg args: Any) {
        if (!LOG)
            return
        val e = System.nanoTime()
        val s = if (SEED_S[seed] == null) 0L else SEED_S[seed]
        e(String.format(Locale.getDefault(), "%,25d", e - s), seed, _INTERNAL_MESSAGE(args))
        SEED_S[seed] = e
    }

    fun _loger(): Loger {
        return _LOGER
    }

    class Loger {
        private var sb: StringBuilder? = StringBuilder()
        private var start: Long = 0
        private var end: Long = 0

        fun log(vararg args: Any) {
            if (!LOG)
                return

            val now = System.nanoTime()

            if (sb == null) {
                sb = StringBuilder()
                start = now
                sb!!.append("start").append(",")
                sb!!.append(_DUMP_milliseconds(now)).append(",")
                sb!!.append("\n")
            }

            sb!!.append(_DUMP_milliseconds(now)).append(",")
            sb!!.append(System.nanoTime() - end).append(",")
            sb!!.append(_MESSAGE(*args)).append(",")
            sb!!.append(getLocator(Exception().stackTrace[1])).append("\n")
            end = now
        }

        fun print() {
            if (!LOG)
                return

            val now = System.nanoTime()
            sb!!.append("end").append(",")
            sb!!.append(_DUMP_milliseconds(now)).append(",")
            sb!!.append("length : " + (now - start)).append("\n")
            e(sb!!.toString())
            sb = null
        }
    }

    //flog
    fun flog(context: Context, vararg args: Any) {
        //		final StackTraceElement info = getStack();
        //		final String msg = _MESSAGE(args);
        flog(context.packageName, *args)
    }

    fun flog(file_prefix: String, vararg args: Any) {
        val info = stack
        val msg = _MESSAGE(*args)

        val dirPath = File(Environment.getExternalStorageDirectory(), LOG_ROOTPATH)
        val yyyymmdd = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
        val logfile = File(dirPath, file_prefix + "_" + yyyymmdd + ".log")

        flog(logfile, info, msg)
    }

    //xml
    private object PrettyXml {
        private val formatter = XmlFormatter(2, 80)

        fun format(s: String): String {
            return formatter.format(String(s), 0)
        }

        private class XmlFormatter(private val indentNumChars: Int, private val lineLength: Int) {
            private var singleLine: Boolean = false

            @Synchronized
            fun format(s: String, initialIndent: Int): String {
                var indent = initialIndent
                val sb = StringBuilder()
                var i = 0
                while (i < s.length) {
                    val currentChar = s[i]
                    if (currentChar == '<') {
                        val nextChar = s[i + 1]
                        if (nextChar == '/')
                            indent -= indentNumChars
                        if (!singleLine)
                        // Don't indent before closing element if we're creating opening and closing elements on a single line.
                            sb.append(buildWhitespace(indent))
                        if (nextChar != '?' && nextChar != '!' && nextChar != '/')
                            indent += indentNumChars
                        singleLine = false // Reset flag.
                    }
                    sb.append(currentChar)
                    if (currentChar == '>') {
                        if (s[i - 1] == '/') {
                            indent -= indentNumChars
                            sb.append("\n")
                        } else {
                            val nextStartElementPos = s.indexOf('<', i)
                            if (nextStartElementPos > i + 1) {
                                val textBetweenElements = s.substring(i + 1, nextStartElementPos)

                                // If the space between elements is solely newlines, let them through to preserve additional newlines in source document.
                                if (textBetweenElements.replace("\n".toRegex(), "").length == 0) {
                                    sb.append(textBetweenElements + "\n")
                                } else if (textBetweenElements.length <= lineLength * 0.5) {
                                    sb.append(textBetweenElements)
                                    singleLine = true
                                } else {
                                    sb.append("\n" + lineWrap(textBetweenElements, lineLength, indent, null) + "\n")
                                }// For larger amounts of text, wrap lines to a maximum line length.
                                // Put tags and text on a single line if the text is short.
                                i = nextStartElementPos - 1
                            } else {
                                sb.append("\n")
                            }
                        }
                    }
                    i++
                }
                return sb.toString()
            }
        }

        private fun buildWhitespace(numChars: Int): String {
            val sb = StringBuilder()
            for (i in 0 until numChars)
                sb.append(" ")
            return sb.toString()
        }

        private fun lineWrap(s: String?, lineLength: Int, indent: Int?, linePrefix: String?): String? {
            if (s == null)
                return null

            val sb = StringBuilder()
            var lineStartPos = 0
            var lineEndPos: Int
            var firstLine = true
            while (lineStartPos < s.length) {
                if (!firstLine)
                    sb.append("\n")
                else
                    firstLine = false

                if (lineStartPos + lineLength > s.length)
                    lineEndPos = s.length - 1
                else {
                    lineEndPos = lineStartPos + lineLength - 1
                    while (lineEndPos > lineStartPos && s[lineEndPos] != ' ' && s[lineEndPos] != '\t')
                        lineEndPos--
                }
                sb.append(buildWhitespace(indent!!))
                if (linePrefix != null)
                    sb.append(linePrefix)

                sb.append(s.substring(lineStartPos, lineEndPos + 1))
                lineStartPos = lineEndPos + 1
            }
            return sb.toString()
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //image save
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    fun compress(name: String, data: ByteArray) {
        try {
            val d = File(Environment.getExternalStorageDirectory(), LOG_ROOTPATH)
            val b = d.mkdirs()
            val now = Date(System.currentTimeMillis())
            val sdf = SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.ENGLISH)
            val f = File(d, sdf.format(now) + "_" + name + ".jpg")
            Log.e(f)
            val fos = FileOutputStream(f)
            BitmapFactory.decodeByteArray(data, 0, data.size).compress(CompressFormat.JPEG, 100, fos)
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun compress(name: String, bmp: Bitmap) {
        try {
            val d = File(Environment.getExternalStorageDirectory(), LOG_ROOTPATH)
            val b = d.mkdirs()
            val now = Date(System.currentTimeMillis())
            val sdf = SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.ENGLISH)
            val f = File(d, sdf.format(now) + "_" + name + ".jpg")
            Log.e(f)
            val fos = FileOutputStream(f)
            bmp.compress(CompressFormat.JPEG, 100, fos)
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //life tools
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //상속트리마지막 위치
    fun po(priority: Int, methodNameKey: String, vararg args: Any): Int {
        if (!LOG)
            return -1

        val info = getStack(methodNameKey)
        val tag = getTag(info)
        val locator = getLocator(info)
        val msg = _MESSAGE(*args)
        return println(priority, tag, locator, msg)
    }

    fun sendBroadcast(clz: Class<*>, intent: Intent) {
        if (!LOG)
            return
        try {
            val target = if (intent.component != null) intent.component!!.shortClassName else intent.toUri(0)
            Log.pc(Log.ERROR, "sendBroadcast", "▶▶", clz, target, intent)
        } catch (e: Exception) {

        }

    }

    fun startService(clz: Class<*>, intent: Intent) {
        if (!LOG)
            return
        try {
            val target = if (intent.component != null) intent.component!!.shortClassName else intent.toUri(0)
            Log.pc(Log.ERROR, "sendBroadcast", "▶▶", clz, target, intent)
        } catch (e: Exception) {

        }

    }

    fun onResume(clz: Class<*>) {
        //		Log.po(Log.ERROR, "onResume", clz);
    }

    fun onPause(clz: Class<*>) {
        //		Log.po(Log.WARN, "onPause", clz);
    }

    fun onDetach(clz: Class<*>) {
        Log.po(Log.WARN, "onDetach", clz)
    }

    fun onDestroyView(clz: Class<*>) {
        Log.po(Log.WARN, "onDestroyView", clz)
    }

    fun onCreate(clz: Class<*>, savedInstanceState: Bundle) {
        Log.po(Log.ERROR, "onCreate", clz)
    }

    fun onAttach(clz: Class<*>, context: Context) {
        Log.po(Log.ERROR, "onAttach", clz)
    }

    fun onCreate(clz: Class<*>) {
        Log.po(Log.ERROR, "onCreate", clz)
    }

    fun onNewIntent(clz: Class<*>) {
        Log.po(Log.ERROR, "onNewIntent", clz)
    }

    fun onDestroy(clz: Class<*>) {
        Log.po(Log.WARN, "onDestroy", clz)
    }

    fun onStart(clz: Class<*>) {
        Log.po(Log.ERROR, "onStart", clz)
    }

    fun onStop(clz: Class<*>) {
        Log.po(Log.WARN, "onStop", clz)
    }

    fun onRestart(clz: Class<*>) {
        Log.po(Log.INFO, "onRestart", clz)
    }

    fun startActivity(clz: Class<*>, intent: Intent) {
        startActivityForResult(clz, intent, -1)
    }

    fun startActivity(clz: Class<*>, intent: Intent, options: Bundle?) {
        startActivityForResult(clz, intent, -1, options)
    }

    fun onActivityCreated(clz: Class<*>, savedInstanceState: Bundle) {
        Log.po(Log.ERROR, "onActivityCreated", clz)
    }

    fun onActivityResult(clz: Class<*>, requestCode: Int, resultCode: Int, data: Intent?) {
        if (!LOG)
            return
        val level = if (resultCode == Activity.RESULT_OK) Log.INFO else Log.ERROR
        Log.po(
            level,
            "onActivityResult",
            "◀◀",
            clz,
            String.format("requestCode=0x%08x", requestCode)//
            ,
            (if (resultCode == Activity.RESULT_OK) "Activity.RESULT_OK" else "") + if (resultCode == Activity.RESULT_CANCELED) "Activity.RESULT_CANCELED" else ""
        )
        if (data != null && data.extras != null)
            Log.p(level, data.extras)
    }

    fun pc(priority: Int, methodNameKey: String, vararg args: Any): Int {
        if (!LOG)
            return -1

        val info = getStackC(methodNameKey)
        val tag = getTag(info)
        val locator = getLocator(info)
        val msg = _MESSAGE(*args)
        return println(priority, tag, locator, msg)
    }

    fun startActivities(clz: Class<*>, intents: Array<Intent>) {
        if (!LOG)
            return

        for (intent in intents) {
            try {
                val target = if (intent.component != null) intent.component!!.shortClassName else intent.toUri(0)
                Log.pc(Log.ERROR, "startActivities", "▶▶", clz, target, intent)
                //		printStackTrace();
            } catch (e: Exception) {
            }

        }
    }

    fun startActivityForResult(clz: Class<*>, intent: Intent, requestCode: Int) {
        if (!LOG)
            return
        try {
            val target = if (intent.component != null) intent.component!!.shortClassName else intent.toUri(0)
            Log.pc(
                Log.ERROR,
                if (requestCode == -1) "startActivity" else "startActivityForResult",
                "▶▶",
                clz,
                target,
                intent,
                String.format("0x%08X", requestCode)
            )
            //		printStackTrace();
        } catch (e: Exception) {
        }

    }

    fun startActivityForResult(clz: Class<*>, intent: Intent, requestCode: Int, options: Bundle?) {
        if (!LOG)
            return
        try {
            val target = if (intent.component != null) intent.component!!.shortClassName else intent.toUri(0)
            Log.pc(
                Log.ERROR,
                if (requestCode == -1) "startActivity" else "startActivityForResult",
                "▶▶",
                clz,
                target,
                intent,
                options,
                String.format("0x%08X", requestCode)
            )
            //		printStackTrace();
        } catch (e: Exception) {
        }

    }

    fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        if (!LOG)
            return
        Log.e(parent.javaClass, parent.getItemAtPosition(position), view, position, id)
    }

    fun measure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (!LOG)
            return
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        Log.d(String.format("0x%08x,0x%08x", widthMode, heightMode))
        Log.d(String.format("%10d,%10d", widthSize, heightSize))
    }

    //tools
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    internal class TraceLog : Throwable() {
        companion object {
            private val serialVersionUID = -8900034648685639609L
        }
    }

    fun printStackTrace() {
        if (!LOG)
            return
        TraceLog().printStackTrace()
    }

    fun printStackTrace(e: Exception) {
        if (!LOG)
            return
        e.printStackTrace()
    }

    fun onTouchEvent(event: MotionEvent) {
        if (!LOG)
            return
        try {
            val action = event.action and MotionEvent.ACTION_MASK
            if (action == MotionEvent.ACTION_MOVE) {
                val nanoTime = System.nanoTime()
                if (nanoTime - LAST_ACTION_MOVE < 1000000)
                    return
                LAST_ACTION_MOVE = nanoTime
            }
            Log.e(event)
        } catch (e: Exception) {
        }

    }

    fun showTable(db: SQLiteDatabase) {
        val c = db.rawQuery("SELECT * FROM sqlite_master WHERE type='table';", null)
        e(c)
        c.close()
    }

    //	public static void onCreate(Class<? extends android.support.v4.app.Fragment> clz, FragmentActivity activity) {
    //		if (!LOG)
    //			return;
    //		if (CLZS.contains(clz))
    //			return;
    //		Log.po(Log.ERROR, "onCreate", clz, activity.getClass());
    //	}
    //	public static void onDestroy(Class<? extends android.support.v4.app.Fragment> clz, FragmentActivity activity) {
    //		if (!LOG)
    //			return;
    //		if (CLZS.contains(clz))
    //			return;
    //		Log.po(Log.ERROR, "onDestroy", clz, activity.getClass());
    //	}
    //호환 팩
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
