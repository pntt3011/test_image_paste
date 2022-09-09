package com.example.testimagepaste

import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.inputmethod.EditorInfoCompat
import androidx.core.view.inputmethod.InputConnectionCompat
import androidx.core.view.inputmethod.InputConnectionCompat.OnCommitContentListener
import androidx.core.view.inputmethod.InputContentInfoCompat

private const val TAG = "CustomEditText"

class CustomEditText(context: Context, attributeSet: AttributeSet): AppCompatEditText(context, attributeSet) {
    var onPasteImage: (Uri, String) -> Unit = { _, _ -> }

    override fun onTextContextMenuItem(id: Int): Boolean {
        if (id == android.R.id.paste) {
            pasteClipboard()
        }
        return super.onTextContextMenuItem(id)
    }

    private fun pasteClipboard() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.primaryClip?.let { clipData ->
            if (clipData.itemCount == 1 && clipData.description.hasMimeType("image/*")) {
                onPasteImage(clipData.getItemAt(0).uri, clipData.description.getMimeType(0))
            }
        }
    }

    override fun onCreateInputConnection(editorInfo: EditorInfo): InputConnection? {
        val ic = super.onCreateInputConnection(editorInfo) ?: return null
        try {
            setContentMimeTypes(editorInfo)
            val callback = createCommitContentListener()
            return InputConnectionCompat.createWrapper(ic, editorInfo, callback)

        } catch (e: Throwable) {
            Log.e(TAG, "onCreateInputConnection: ${e.message}", )
        }
        return ic
    }

    private fun setContentMimeTypes(editorInfo: EditorInfo) {
        EditorInfoCompat.setContentMimeTypes(
            editorInfo,
            arrayOf("image/gif", "image/*", "image/jpg", "image/png", "image/webp")
        )
    }

    private fun createCommitContentListener(): OnCommitContentListener {
        return OnCommitContentListener { inputContentInfo: InputContentInfoCompat, flags: Int, _: Bundle? ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1 &&
                flags and InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION != 0
            ) {
                try {
                    inputContentInfo.requestPermission()
                } catch (e: Exception) {
                    return@OnCommitContentListener false
                }
            }
            onPasteImage(
                inputContentInfo.contentUri,
                inputContentInfo.description.getMimeType(0)
            )
            true
        }
    }
}