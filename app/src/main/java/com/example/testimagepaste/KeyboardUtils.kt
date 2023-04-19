package com.example.testimagepaste

import android.content.Context
import android.provider.Settings

object KeyboardUtils {
    fun usingSamsungKeyboard(context: Context): Boolean {
        return getCurrentKeyboard(context).contains("samsung")
    }

    private fun getCurrentKeyboard(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD)
    }
}