package com.example.testimagepaste

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import java.lang.reflect.Proxy

object SemClipboardUtils {
    private const val TAG = "SemClipboardUtils"
    private const val REASON_DUPLICATED = 2
    private const val REASON_EMPTY_DATA = 3
    private const val REASON_NOT_ALLOWED_TO_USE = 4
    private const val REASON_UNKNOWN = 1

    @SuppressLint("WrongConstant")
    fun addImageClip(context: Context, path: String): Boolean {
        return try {
            val clipboardManagerCls = Class.forName("com.samsung.android.content.clipboard.SemClipboardManager")
//            context.grantUriPermission(
//                clipboardManagerCls.canonicalName, uri,
//                Intent.FLAG_GRANT_READ_URI_PERMISSION
//            )
            val listenerCls = Class.forName("com.samsung.android.content.clipboard.SemClipboardManager\$OnAddClipResultListener")
            val clipDataCls = Class.forName("com.samsung.android.content.clipboard.data.SemClipData")
            val imageClipDataCls = Class.forName("com.samsung.android.content.clipboard.data.SemImageClipData")

            val imageClipData = imageClipDataCls.newInstance()
            val setImagePathMethod = imageClipDataCls.getDeclaredMethod("setImagePath", String::class.java)
            setImagePathMethod.invoke(imageClipData, path)

            val clipboardManager = context.getSystemService("semclipboard")
            val addClipMethod = clipboardManagerCls.getDeclaredMethod("addClip", Context::class.java, clipDataCls, listenerCls)
            val listener = createAddClipListener(listenerCls)
            addClipMethod.invoke(clipboardManager, context, imageClipData, listener)

            true
        } catch (e: java.lang.Exception) {
            Log.e(TAG, "addImageClip: $e")
            false
        }
    }

    private fun createAddClipListener(listenerCls: Class<*>): Any? {
        return try {
            Proxy.newProxyInstance(listenerCls.classLoader, arrayOf(listenerCls)) { _, method, args ->
                if (method.name.equals("onSuccess")) {
                    Log.d(TAG, "createAddClipListener: success")
                } else if (method.name.equals("onFailure")) {
                    Log.d(TAG, "createAddClipListener: failed with code " + args[0])
                } else {
                    Log.d(TAG, "Unknown")
                }
            }
        } catch (e: Exception) {
            null
        }
    }
}