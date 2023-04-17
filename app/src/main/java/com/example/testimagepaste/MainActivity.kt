package com.example.testimagepaste

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private var image: ImageView? = null
    private var resourceId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        image = findViewById(R.id.image)
        val editText: CustomEditText = findViewById(R.id.edit)
        editText.onPasteImage = this::onPasteImage

        resourceId = R.drawable.a
        val copy: Button = findViewById(R.id.copy)
        copy.setOnClickListener {
            CopyUtils.copyImageToClipboard(this, resourceId)
            Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show()
        }
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