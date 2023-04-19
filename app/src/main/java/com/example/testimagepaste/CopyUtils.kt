package com.example.testimagepaste

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.StrictMode
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.security.Key

private const val TAG = "CopyUtils"
private const val SHARE_IMAGES_DIRECTORY_NAME = "screenshot"
private const val IMAGE_FILE_PATH = "images"
private const val JPEG_EXTENSION = ".jpg"
private const val API_AUTHORITY_SUFFIX = ".provider"

object CopyUtils {
    fun copyImageToClipboard(context: Context, resId: Int): Boolean {
        val data = retrieveImageBytes(context, resId)
        val (uri, path) = generateUriFromData(context, data)
        uri ?: return false
        path ?: return false


        if (isSamsungDevice() && KeyboardUtils.usingSamsungKeyboard(context)) {
            if (USE_UTILS_2) {
                return CopyUtils2.addClip(context.applicationContext, path)
            }
            return SemClipboardUtils.addImageClip(context, path)
        }

        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        context.grantUriPermission(
            ClipboardManager::class.java.canonicalName, uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
        val clip = ClipData.newUri(context.contentResolver, "image", uri)
        clipboard.setPrimaryClip(clip)
        return true
    }

    private fun isSamsungDevice(): Boolean = Build.MANUFACTURER.equals("samsung", ignoreCase = true)

    private fun retrieveImageBytes(context: Context, resId: Int): ByteArray {
        val d: Drawable = ContextCompat.getDrawable(context, resId)!!
        val bitmap = (d as BitmapDrawable).bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }

    private fun generateUriFromData(context: Context, data: ByteArray): Pair<Uri?, String?> {
        var fOut: FileOutputStream? = null
        try {
            val path = File(
                getDirectoryForImageCapture(context),
                SHARE_IMAGES_DIRECTORY_NAME
            )
            if (path.exists() || path.mkdir()) {
                val saveFile =
                    File.createTempFile(System.currentTimeMillis().toString(), JPEG_EXTENSION, path)
                fOut = FileOutputStream(saveFile)
                fOut.write(data)
                fOut.flush()
                return Pair(getContentUriFromFile(context, saveFile), saveFile.absolutePath)

            } else {
                Log.w(TAG, "Share failed -- Unable to create share image directory.")
            }
        } catch (ie: IOException) {
            // Ignore exception.
        } finally {
            fOut?.close()
        }

        return Pair(null, null)
    }

    private fun getDirectoryForImageCapture(context: Context): File {
        val oldPolicy = StrictMode.allowThreadDiskReads()
        return try {
            val path = File(context.filesDir, IMAGE_FILE_PATH)
            if (!path.exists() && !path.mkdir()) {
                throw IOException("Folder cannot be created.")
            }
            path
        } finally {
            StrictMode.setThreadPolicy(oldPolicy)
        }
    }

    private fun getContentUriFromFile(context: Context, file: File): Uri {
        val authority = context.applicationContext.packageName + API_AUTHORITY_SUFFIX // should be same as <provider android:authorities> in AndroidManifest.xml
        return FileProvider.getUriForFile(
            context, authority, file
        )
    }
}