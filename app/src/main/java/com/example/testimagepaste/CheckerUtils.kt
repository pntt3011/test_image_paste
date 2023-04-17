package com.example.testimagepaste

import android.content.ClipboardManager
import android.content.Context

object CheckerUtils {
    fun runCheck(context: Context): Boolean {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.primaryClip?.let { clipData ->
            if (clipData.itemCount == 1 && clipData.description.hasMimeType("image/*")) {
                val uri = clipData.getItemAt(0).uri
                return true
            }
        }
        return false
    }
}