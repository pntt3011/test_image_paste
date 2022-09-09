package com.example.testimagepaste

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var editText: CustomEditText
    private lateinit var image: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        image = findViewById(R.id.image)
        editText = findViewById(R.id.edit)
        editText.onPasteImage = {uri, mime ->
            try {
                contentResolver.openInputStream(uri)?.let {
                    image.setImageURI(uri)
                    it.close()
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }
}