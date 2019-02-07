package smart.util

import android.content.Context
import android.log.Log
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * ```
 *     <uses-permission
 *         android:name="android.permission.WRITE_EXTERNAL_STORAGE"
 *         android:maxSdkVersion="18" />
 *     <application>
 *         <provider
 *             android:name="androidx.core.content.FileProvider"
 *             android:authorities="${applicationId}.provider"
 *             android:enabled="true"
 *             android:grantUriPermissions="true"
 *             android:exported="false">
 *             <meta-data
 *                 android:name="android.support.FILE_PROVIDER_PATHS"
 *                 android:resource="@xml/provider_paths" />
 *         </provider>
 *
 *         <activity
 *             android:name="smart.util.GalleryLoader"
 *             android:theme="@style/GalleryLoaderTheme" />
 *
 *     </application>
 * ```
 */

object FileProviderHelper {
    private const val PROVIDER = ".provider"
    private val GLOADER_FOLDER = "gloader" to "galleryloader/"
    private val GTEMP_FOLDER = "gtemp" to "galleryloader/temp/"
    private fun getRootFolder(context: Context): File {
        return mkdirs(context.getExternalFilesDir(null)!!)
    }

    private fun createFolder(context: Context, sub_folder: String): File {
        return mkdirs(File(getRootFolder(context), sub_folder))
    }

    private fun mkdirs(dir: File): File {
        if (!dir.exists()) {
            val result = dir.mkdirs()
            if (!result)
                throw NullPointerException("!dir folder create fail")
            Log.e("mkdirs", result, dir)
        }
        return dir
    }

    private fun toFile(context: Context, uri: Uri?): File? {
        uri ?: return null
        if (uri.scheme != "content") return null
        if (uri.authority != context.packageName + PROVIDER) return null
        if (uri.path.isNullOrBlank()) return null

        for (pair in arrayOf(GTEMP_FOLDER, GLOADER_FOLDER)) {
            val (name, folder) = pair
            if (name in uri.path!!) {
                val pathname = uri.path!!.replaceFirst(name, folder)
                val file = File(getRootFolder(context), pathname)
                Log.e(file to uri)
                return file
            }
        }
        return null
    }

    private fun toUri(context: Context, file: File): Uri {
        val authority = context.packageName + PROVIDER
        val uri = FileProvider.getUriForFile(context, authority, file)
        Log.e(uri to file)
        return uri
    }

    @Throws(IOException::class)
    private fun createTempFile(context: Context, prefix: String = "", suffix: String? = ".jpg"): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return File.createTempFile("${prefix}_${timeStamp}_", suffix, createFolder(context, GLOADER_FOLDER.second))
    }

    @Throws(IOException::class)
    fun createTempUri(context: Context, prefix: String, suffix: String?): Uri {
        val authority = context.packageName + PROVIDER
        val file = createTempFile(context, prefix, suffix)
        val uri = FileProvider.getUriForFile(context, authority, file)
        Log.e(uri to file)
        return uri
    }

    fun deleteTempFolder(context: Context) {
        val source = File(getRootFolder(context), GTEMP_FOLDER.second)
        if (!source.exists()) return
//        val source = createFolder(context, GTEMP_FOLDER.second)
        val target = File.createTempFile("delete_", "", getRootFolder(context))
//        createTempFile(getRootFolder(context), System.nanoTime().toString())

        if (source.renameTo(target)) {
            deleteRecursive(target)
        } else {
            deleteRecursive(source)
        }
    }

    private fun deleteRecursive(maybeFolder: File) {
        if (maybeFolder.isDirectory)
            for (child in maybeFolder.listFiles()!!)
                deleteRecursive(child)
        maybeFolder.delete()
    }

    fun moveForResult(context: Context, uri: Uri?): Uri? {
        return try {
            val source = toFile(context, uri)
            val target = createTempFile(context, "result", ".jpg")
            val result = source?.renameTo(target)
            if (result!!)
                toUri(context, target)
            else
                uri
        } catch (e: Exception) {
            uri
        }
    }

    fun copyForCrop(context: Context, source: Uri?): Uri {
        val file = FileProviderHelper.createTempFile(context, "crop", ".jpeg")
        source?.also {
            val inputStream = context.contentResolver.openInputStream(it)
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream, 4096)
            inputStream?.close()
            outputStream.close()
        }
        return toUri(context, file)
    }
}