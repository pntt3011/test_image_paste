package com.example.testimagepaste

import android.content.res.AssetFileDescriptor
import android.net.Uri
import android.os.Bundle
import androidx.core.content.FileProvider

class MyProvider: FileProvider() {
    override fun openTypedAssetFile(uri: Uri, mimeTypeFilter: String, opts: Bundle?): AssetFileDescriptor? {
        return super.openTypedAssetFile(uri, mimeTypeFilter, opts)
    }
}