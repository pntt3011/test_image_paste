package com.example.testimagepaste

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

private const val TAG = "MainActivity"
private const val SHARE_IMAGES_DIRECTORY_NAME = "screenshot"
private const val IMAGE_FILE_PATH = "images"
private const val JPEG_EXTENSION = ".jpg"
private const val API_AUTHORITY_SUFFIX = ".provider"

class MainActivity : AppCompatActivity() {
    private lateinit var editText: CustomEditText
    private lateinit var image: ImageView
    private lateinit var copy: Button
    private var resourceId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        image = findViewById(R.id.image)
        editText = findViewById(R.id.edit)
        editText.onPasteImage = {uri, _ ->
            try {
                contentResolver.openInputStream(uri)?.let {
                    image.setImageURI(uri)
                    it.close()
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }

        resourceId = R.drawable.a
        copy = findViewById(R.id.copy)

        copy.setOnClickListener {
            copyImageToClipboard()
            Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun copyImageToClipboard() {
        val data = retrieveImageBytes()
        val uri = generateUriFromData(data)

        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        grantUriPermission(
            ClipboardManager::class.java.canonicalName, uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
        val clip = ClipData.newUri(contentResolver, "image", uri)
        clipboard.setPrimaryClip(clip)

        // TODO: Check if needed
//        applicationContext.contentResolver.delete(uri!!, null, null)
    }

    private fun retrieveImageBytes(): ByteArray {
        val d: Drawable = ContextCompat.getDrawable(this, resourceId)!!
        val bitmap = (d as BitmapDrawable).bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }

    private fun generateUriFromData(data: ByteArray): Uri? {
        var fOut: FileOutputStream? = null
        try {
            val path = File(
                getDirectoryForImageCapture(this),
                SHARE_IMAGES_DIRECTORY_NAME
            )
            if (path.exists() || path.mkdir()) {
                val saveFile =
                    File.createTempFile(System.currentTimeMillis().toString(), JPEG_EXTENSION, path)
                fOut = FileOutputStream(saveFile)
                fOut.write(data)
                fOut.flush()
                return getContentUriFromFile(saveFile)

            } else {
                Log.w(TAG, "Share failed -- Unable to create share image directory.")
            }
        } catch (ie: IOException) {
            // Ignore exception.
        } finally {
            fOut?.close()
        }

        return null
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

    private fun getContentUriFromFile(file: File): Uri {
        return FileProvider.getUriForFile(
            this, packageName + API_AUTHORITY_SUFFIX, file
        )
    }
}