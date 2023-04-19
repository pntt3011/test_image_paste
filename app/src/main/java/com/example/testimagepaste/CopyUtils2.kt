package com.example.testimagepaste

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.util.Log
import java.lang.reflect.Field
import java.util.concurrent.Executors


object CopyUtils2 {
    private const val REQUEST_ADD_CLIP = 4096 // Source: Reverse ClipboardSaveService.APK
    private const val IMAGE_CLIP_DATA_TYPE = 2
    private val executor = Executors.newSingleThreadExecutor()

    fun addClip(appContext: Context, path: String?): Boolean {
        if (path == null) return false
        return ClipboardService(appContext, path).start()
    }

    class ClipboardService(
        val appContext: Context,
        val path: String
    ) {
        private val connection = object: ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val messenger = Messenger(service)
                executor.submit {
                    sendClipboardBundle(messenger)
                    unbindService()
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                // Not implement
            }
        }

        fun start(): Boolean {
            return try {
                val intent = Intent("com.sec.android.clipboard.REQUEST_REMOTE_CONTROL")
                intent.setPackage("com.samsung.clipboardsaveservice")
                appContext.bindService(intent, connection, Context.BIND_AUTO_CREATE)
                true
            } catch (e: Exception) {
                Log.e("ClipboardService", "start > bind failed.", e)
                false
            }
        }

        fun sendClipboardBundle(messenger: Messenger) {
            try {
                val obtain = Message.obtain(null, REQUEST_ADD_CLIP)
                obtain.data = createClipboardIntent(appContext, path)?.extras
                messenger.send(obtain)
                Log.d("ClipboardService", "sendClipboardBundle success")
            } catch (e: Exception) {
                Log.e("ClipboardService", "sendClipboardBundle failed", e)
            }
        }

        fun unbindService() {
            try {
                appContext.unbindService(connection)
            } catch (e: Exception) {
                Log.e("ClipboardService", "unbindService failed", e);
            }
        }
    }

    fun createClipboardIntent(context: Context, path: String): Intent? {
        try {
            val semVersion = getOneUiVersion(context)
            if (semVersion >= 120500) {
                return sem110ClipboardIntent(path)
            } else if (semVersion >= 120100) {
                return sem110ClipboardIntent(path)
            } else if (semVersion >= 120000) {
                return sem110ClipboardIntent(path)
            } else if (semVersion >= 110500) {
                return sem110ClipboardIntent(path)
            } else if (semVersion >= 110100) {
                return sem110ClipboardIntent(path)
            } else if (semVersion >= 110000) {
                return sem110ClipboardIntent(path)
            } else if (semVersion >= 100000) {
                return sem100ClipboardIntent(path)
            } else if (semVersion >= 90500) {
                return defaultClipboardIntent(path)
            } else if (semVersion >= 90000) {
                return defaultClipboardIntent(path)
            } else if (semVersion >= 80500) {
                return defaultClipboardIntent(path)
            } else if (semVersion >= 80100) {
                return defaultClipboardIntent(path)
            } else if (semVersion >= 80000) {
                return defaultClipboardIntent(path)
            } else {
                return null
            }
        } catch (e: Exception) {
            return null
        }
    }

    private fun getOneUiVersion(context: Context): Int {
        try {
            if (!isSemAvailable(context)) {
                return -1
            }
            val semPlatformIntField: Field = Build.VERSION::class.java.getDeclaredField("SEM_PLATFORM_INT")
            return semPlatformIntField.getInt(null)
        } catch (e: Exception) {
            return -1
        }
    }

    private fun isSemAvailable(context: Context): Boolean {
        return context.packageManager.hasSystemFeature("com.samsung.feature.samsung_experience_mobile") ||
                context.packageManager.hasSystemFeature("com.samsung.feature.samsung_experience_mobile_lite")
    }

    private fun defaultClipboardIntent(path: String): Intent = Intent()

    private fun sem100ClipboardIntent(path: String): Intent {
        val intent = Intent("com.samsung.android.content.clipboard.action.ADD_CLIP")
        try {
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
            intent.putExtra("type", IMAGE_CLIP_DATA_TYPE)
            intent.putExtra("path", path)
            intent.putExtra("darkTheme", true)
        } catch (e: Exception) {
            Log.e("sem100ClipboardIntent", "createClipboardIntent failed e=" + e.message)
        }
        return intent
    }

    private fun sem110ClipboardIntent(path: String): Intent {
        val intent = Intent()
        try {
            intent.putExtra("type", IMAGE_CLIP_DATA_TYPE)
            intent.putExtra("path", path)
        } catch (e: java.lang.Exception) {
            Log.e("sem110ClipboardIntent", "createClipboardIntent failed e=" + e.message)
        }
        return intent
    }
}