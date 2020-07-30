package org.buffer.android.thumby.util

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build

object ThumbyUtils {

    fun getBitmapAtFrame(
        context: Context,
        uri: Uri,
        frameTime: Long,
        width: Int,
        height: Int
    ): Bitmap {
        val mediaMetadataRetriever = MediaMetadataRetriever()
//        if (ApiLevelHelper.isAtLeast(Build.VERSION_CODES.Q)) {
//            mediaMetadataRetriever.setDataSource(uri.path)
//        } else {
//        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.Q)
            mediaMetadataRetriever.setDataSource(context, uri)
//        }
        var bitmap = mediaMetadataRetriever.getFrameAtTime(frameTime,
            MediaMetadataRetriever.OPTION_CLOSEST)
        try {
            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bitmap
    }
}