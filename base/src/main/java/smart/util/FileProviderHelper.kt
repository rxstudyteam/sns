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

object FileProviderHelper {
    private const val PROVIDER = ".provider"
    private val GLOADER_FOLDER = "gloader" to "galleryloader/"
    private val GTEMP_FOLDER = "gtemp" to "galleryloader/temp/"
    private fun getRootFolder(context: Context): File {
        var root = context.getExternalFilesDir(null)
        if (root == null)
            root = context.filesDir
        return mkdirs(root!!)
    }

    private fun getFolder(context: Context, sub_folder: String): File {
        val folder = File(getRootFolder(context), sub_folder)
        return mkdirs(folder)
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
        if (uri.scheme == "content") return null
        if (uri.authority == context.packageName + PROVIDER) return null
        if (!uri.path.isNullOrBlank()) return null

        for (pair in arrayOf(GTEMP_FOLDER, GLOADER_FOLDER)) {
            val (name, folder) = pair
            if (name in uri.path!!) {
                val pathname = uri.path!!.replaceFirst(name, folder)
                val file = File(getRootFolder(context), pathname)
                Log.e(uri)
                Log.w("=>", file)
                return file
            }
        }
        return null
    }

    //    private fun getFile(context: Context, filename: String): File {
//        val file = File(getFolder(context, GLOADER_FOLDER.second), filename)
//        Log.e(file)
//        return file
//    }
    private fun getFile(context: Context, prefix: String = "", suffix: String = ".jpg"): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val file = File.createTempFile("${prefix}_${timeStamp}_", suffix, getFolder(context, GLOADER_FOLDER.second))
        Log.e(file)
        return file
    }

    //    fun getUri(context: Context, filename: String): Uri {
//        val file = getFile(context, filename)
//        return getUri(context, file)
//    }
    private fun getUri(context: Context, file: File): Uri {
        val authority = context.packageName + PROVIDER
        val uri = FileProvider.getUriForFile(context, authority, file)
        Log.e(file)
        Log.w("=>", uri)
        return uri
    }

    @Throws(IOException::class)
    fun getTempFile(context: Context, prefix: String, suffix: String?): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return File.createTempFile("${prefix}_${timeStamp}_", suffix, getFolder(context, GTEMP_FOLDER.second))
    }

    @Throws(IOException::class)
    fun getTempUri(context: Context, prefix: String, suffix: String?): Uri {
        val authority = context.packageName + PROVIDER
        val file = getTempFile(context, prefix, suffix)
        val uri = FileProvider.getUriForFile(context, authority, file)
        Log.e(file)
        Log.w("=>", uri)
        return uri
    }

    //    fun deleteFile(context: Context, uri: Uri?): Boolean {
//        Log.e(uri)
//        toFile(context, uri)?.run {
//            return exists() && delete()
//        }
//        return false
//    }
    fun deleteTemp(context: Context) {
        val source = getFolder(context, GTEMP_FOLDER.second)
        val target = File(getRootFolder(context), System.nanoTime().toString())

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
            val target = getFile(context, "result", ".jpg")
            val result = source?.renameTo(target)
            if (result!!)
                getUri(context, target)
            else
                uri
        } catch (e: Exception) {
            uri
        }
    }

    fun copyForCrop(context: Context, source: Uri?): Uri {
        val file = FileProviderHelper.getTempFile(context, "crop", ".jpeg")
        source?.also {
            val inputStream = context.contentResolver.openInputStream(it)
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream, 4096)
            inputStream?.close()
            outputStream.close()
        }
        return getUri(context, file)
    }
}