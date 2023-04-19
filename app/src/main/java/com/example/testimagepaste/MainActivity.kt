package com.example.testimagepaste

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

const val USE_UTILS_2 = false

class MainActivity : AppCompatActivity() {
    private var image: ImageView? = null
    private var resourceId: Int = -1

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            123 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    copy()
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        image = findViewById(R.id.image)
        val editText: CustomEditText = findViewById(R.id.edit)
        editText.onPasteImage = this::onPasteImage

        resourceId = R.drawable.a
        val copy: Button = findViewById(R.id.copy)
        copy.setOnClickListener {
            if (USE_UTILS_2 && ActivityCompat.checkSelfPermission(this, "com.samsung.clipboardsaveservice.CLIPBOARDSAVESERVICE_PERMISSION") != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, listOf("com.samsung.clipboardsaveservice.CLIPBOARDSAVESERVICE_PERMISSION").toTypedArray(), 123)
            } else {
                copy()
            }
        }

        val check: Button = findViewById(R.id.checker)
        check.setOnClickListener {
            CopyUtils.copyImageToClipboard(this, resourceId)
            if (CheckerUtils.runCheck(this)) {
                Toast.makeText(this, "Image", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Not image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun copy() {
        CopyUtils.copyImageToClipboard(this, resourceId)
//        Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show()
    }

    private fun onPasteImage(uri: Uri, mimeType: String) {
        try {
            contentResolver.openInputStream(uri)?.let {
                image?.setImageURI(uri)
                it.close()
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}