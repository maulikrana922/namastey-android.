package com.namastey.utils

import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.*
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


object Utils {

    fun hideKeyboard(context: Activity) {
        // Check if no view has focus
        val view = context.currentFocus
        if (view != null) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun convertDateToAPIFormate(date: String): String {
        val parser = SimpleDateFormat(Constants.DATE_FORMATE_API, Locale.getDefault())
        val formatter = SimpleDateFormat(Constants.DATE_FORMATE_DISPLAY, Locale.getDefault())
        if (!TextUtils.isEmpty(date))
            return parser.format(formatter.parse(date))
        else
            return date
    }

    fun convertTimestampToChatFormat(timestamp: Long): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat(Constants.DATE_FORMATE_CHAT, Locale.getDefault())
        return format.format(date)
    }

    fun rectangleShapeBorder(v: View, borderColor: Int) {
        val shape = GradientDrawable()
        shape.shape = GradientDrawable.RECTANGLE
        shape.cornerRadii = floatArrayOf(0f, 0f, 54f, 54f, 0f, 0f, 54f, 54f)
//        shape.setColor(backgroundColor)
        shape.setStroke(3, borderColor)
        v.background = shape
    }

    fun rectangleShapeGradient(v: View, intArrayOf: IntArray) {
        val gd = GradientDrawable(
            GradientDrawable.Orientation.TR_BL,
            intArrayOf
        )

        gd.shape = GradientDrawable.RECTANGLE
        gd.cornerRadii = floatArrayOf(0f, 0f, 54f, 54f, 0f, 0f, 54f, 54f)
        v.background = gd
    }

    fun imageOverlayGradient(v: View, startColor: String, endColor: String) {
        val gd = GradientDrawable(
            GradientDrawable.Orientation.TR_BL,

            intArrayOf(
                Color.parseColor("#50".plus(startColor.substring(1))),
                Color.parseColor("#50".plus(endColor.substring(1)))
            )
        )

        gd.shape = GradientDrawable.RECTANGLE
        v.foreground = gd
    }

    fun saveBitmapToFile(file: File): File? {
        return try {
            // BitmapFactory options to downsize the image
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            o.inSampleSize = 6
            // factor of downsizing the image
            var inputStream = FileInputStream(file)
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o)
            inputStream.close()
            // The new size we want to scale to
            val REQUIRED_SIZE = 75
            // Find the correct scale value. It should be the power of 2.
            var scale = 1
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                o.outHeight / scale / 2 >= REQUIRED_SIZE
            ) {
                scale *= 2
            }
            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale
            inputStream = FileInputStream(file)
            val selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2)
            inputStream.close()
            // here i override the original image file
            file.createNewFile()
            val outputStream = FileOutputStream(file)
            selectedBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            file
        } catch (e: Exception) {
            null
        }
    }

    fun getPath(context: Context, uri: Uri): String? {
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }

            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri: Uri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                )
                return getDataColumn(context, contentUri, null, emptyArray())
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                when (type) {
                    "image" -> {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }
                    "video" -> {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    }
                    "audio" -> {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                    split[1]
                )
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(context, uri, null, emptyArray())
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    private fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            cursor = uri?.let {
                context.contentResolver.query(
                    it, projection, selection, selectionArgs,
                    null
                )
            }
            if (cursor != null && cursor.moveToFirst()) {
                val column_index: Int = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    fun getRealPathFromURI(context: Context, uri: Uri?): String? {
        var path = ""
        if (context.contentResolver != null) {
            val cursor =
                uri?.let { context.contentResolver.query(it, null, null, null, null) }
            if (cursor != null) {
                cursor.moveToFirst()
                val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                path = cursor.getString(idx)
                cursor.close()
            }
        }
        return path
    }

    /**
     * Convert bitmap image to file
     */
    fun bitmapToFile(context: Context, bitmap: Bitmap): File {
        val bitmapFile = File(context.cacheDir, "Title.jpg")
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

    fun getCameraFile(context: Context): File {
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File(dir, Constants.FILE_NAME)
    }

    fun scaleBitmapDown(
        bitmap: Bitmap,
        maxDimension: Int
    ): Bitmap? {
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height
        var resizedWidth = maxDimension
        var resizedHeight = maxDimension
        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension
            resizedWidth =
                (resizedHeight * originalWidth.toFloat() / originalHeight.toFloat()).toInt()
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension
            resizedHeight =
                (resizedWidth * originalHeight.toFloat() / originalWidth.toFloat()).toInt()
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension
            resizedWidth = maxDimension
        }
        return Bitmap.createScaledBitmap(
            bitmap,
            resizedWidth,
            resizedHeight,
            false
        )
    }

    /**
     * Get duration of audio from firebase url
     */
    fun getMediaDuration(voiceUrl: String): String {
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(voiceUrl)
            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            retriever.release()
            String.format("%02d:%02d ",
                duration?.toLong()?.let { TimeUnit.MILLISECONDS.toMinutes(it) },
                duration?.toLong()?.let { TimeUnit.MILLISECONDS.toSeconds(it) }
                    ?.minus(TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration.toLong())))
            )
        } catch (e: Exception) {
            e.printStackTrace()
            "00:00"
        }
//        return duration?.toLongOrNull() ?: 0
    }

    fun setHtmlText(textView: TextView, text: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.text = Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
        } else {
            textView.text = Html.fromHtml(text)
        }
    }

    fun covertTimeToText(dataDate: String?): String? {
        var convTime: String? = null
        val prefix = ""
        val suffix = ""
        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
            val pasTime = dateFormat.parse(dataDate)
            val nowTime = Date()
            val dateDiff = nowTime.time - pasTime.time
            val second: Long = TimeUnit.MILLISECONDS.toSeconds(dateDiff)
            val minute: Long = TimeUnit.MILLISECONDS.toMinutes(dateDiff)
            val hour: Long = TimeUnit.MILLISECONDS.toHours(dateDiff)
            val day: Long = TimeUnit.MILLISECONDS.toDays(dateDiff)
            when {
                second < 60 -> {
                    convTime = "$second sec $suffix"
                }
                minute < 60 -> {
                    convTime = "$minute min $suffix"
                }
                hour < 24 -> {
                    convTime = "$hour hr $suffix"
                }
                day >= 7 -> {
                    convTime = when {
                        day > 360 -> {
                            (day / 360).toString() + " y " + suffix
                        }
                        day > 30 -> {
                            (day / 30).toString() + " m " + suffix
                        }
                        else -> {
                            (day / 7).toString() + " w " + suffix
                        }
                    }
                }
                day < 7 -> {
                    convTime = "$day d $suffix"
                }
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            Log.e("Utils", "Exception: \t " + e.message!!)
        }
        return convTime
    }

    fun getCurrentAndDifferenceTime() {
        val c = Calendar.getInstance()
        c.add(Calendar.DAY_OF_MONTH, 1)
        c[Calendar.HOUR_OF_DAY] = 0
        c[Calendar.MINUTE] = 0
        c[Calendar.SECOND] = 0
        c[Calendar.MILLISECOND] = 0
        val howMany = c.timeInMillis - System.currentTimeMillis()
        val interval = 1000L

        Log.e("Utils", "howMany: \t $howMany")
        Log.e("Utils", "timeInMillis: \t ${c.timeInMillis}")
        Log.e("Utils", "System: \t ${System.currentTimeMillis()}")

        convertMilliSecondToHours(howMany, interval)
    }

    private fun convertMilliSecondToHours(millis: Long, interval: Long) {
        val t: CountDownTimer
        t = object : CountDownTimer(millis, interval) {
            override fun onTick(millisUntilFinished: Long) {
                val timer = String.format(
                    "%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                        TimeUnit.MILLISECONDS.toHours(
                            millisUntilFinished
                        )
                    ), // The change is in this line
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(
                            millisUntilFinished
                        )
                    )
                )

                Log.e("Utils", "timer: \t $timer")
            }

            override fun onFinish() {
                Log.e("Utils", "Finish: \t 00:00:00")
                cancel()
            }
        }.start()
    }

    private var mStartTime = 0L
    private val mHandler = Handler()
    private lateinit var mUpdateTimeTask: Runnable

    fun startAppCountTimer(context: Context) {
        mUpdateTimeTask = object : Runnable {
            override fun run() {
                val start = mStartTime
                val millis = SystemClock.uptimeMillis() - start
                var seconds = (millis / 1000).toInt()
                val minutes = seconds / 60
                seconds %= 60

                val timeInFormat = String.format(
                    "%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
                )

                //Log.e("Utils", "timeInFormat:  \t $timeInFormat")

                mHandler.postDelayed(this, 1000)
                SessionManager(context).setStringValue(timeInFormat, Constants.KEY_SPEND_APP_TIME)
                //mHandler.postDelayed(this, 60000)
            }
        }

        if (mStartTime == 0L) {
            mStartTime = SystemClock.uptimeMillis()
            mHandler.removeCallbacks(mUpdateTimeTask)
            mHandler.postDelayed(mUpdateTimeTask, 1000)
        }
    }

    fun stopAppCountTimer(context: Context){
        mHandler.removeCallbacksAndMessages(null);
        mHandler.removeCallbacks(mUpdateTimeTask)
        SessionManager(context).setStringValue("", Constants.KEY_SPEND_APP_TIME)
    }
}
