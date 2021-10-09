package org.buffer.android.thumby.util

import android.os.Environment
import java.io.File

object Constants {
    const val PERMISSION_GALLERY = 102
    const val REQUEST_POST_VIDEO = 103
    const val REQUEST_CODE_IMAGE = 104
    const val IMAGE_PATH = "image"
    const val IMAGE_TYPE = "image/*"
    private val ROOT = File.separator + "NAMASTEY"
    private val SD_CARD_PATH = Environment.getExternalStorageDirectory().path
    val FILE_PATH = SD_CARD_PATH.plus(ROOT)
}
