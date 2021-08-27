package org.buffer.android.thumby.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.media.MediaMetadataRetriever
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

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
        var bitmap = mediaMetadataRetriever.getFrameAtTime(
            frameTime,
            MediaMetadataRetriever.OPTION_CLOSEST
        )
        try {
            //bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false)
            bitmap = getResizedBitmap(bitmap, width, height)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bitmap
    }

    private fun getResizedBitmap(bitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap? {
        val resizedBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)
        val scaleX = newWidth / bitmap.width.toFloat()
        val scaleY = newHeight / bitmap.height.toFloat()
        val pivotX = 0f
        val pivotY = 0f
        val scaleMatrix = Matrix()
        scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY)
        val canvas = Canvas(resizedBitmap)
        canvas.setMatrix(scaleMatrix)
        canvas.drawBitmap(bitmap, 0F, 0F, Paint(Paint.FILTER_BITMAP_FLAG))
        return resizedBitmap
    }

    /**
     * Convert bitmap image to file
     */
    fun bitmapToFile(context: Context, bitmap: Bitmap): File {
        val bitmapFile = File(context.cacheDir, "CoverImage.jpg")
        bitmapFile.createNewFile()

//Convert bitmap to byte array
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos)
        val bitmapData = bos.toByteArray()

//write the bytes in file
        val fos = FileOutputStream(bitmapFile)
        fos.write(bitmapData)
        fos.flush()
        fos.close()

        return bitmapFile
    }
}