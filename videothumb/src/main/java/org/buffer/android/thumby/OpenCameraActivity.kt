package org.buffer.android.thumby

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.wonderkiln.camerakit.CameraKitEventCallback
import com.wonderkiln.camerakit.CameraKitImage
import kotlinx.android.synthetic.main.activity_open_camera.*
import org.buffer.android.thumby.util.Constants
import org.buffer.android.thumby.util.ThumbyUtils
import java.io.File


class OpenCameraActivity : AppCompatActivity() {
    private var pictureFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_camera)
        btn_gallery.setOnClickListener {
            openGalleryForImage()
        }
        btn_capture.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            camera_kit_view.captureImage(CameraKitEventCallback<CameraKitImage?> { event ->
                val jpeg: ByteArray = event.jpeg
                val bitmap: Bitmap = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.size)
                val filePath = ThumbyUtils.bitmapToFile(this@OpenCameraActivity, bitmap)
                Log.e("File Path:", filePath.toString())
                val intent = Intent()
                intent.putExtra(Constants.IMAGE_PATH, filePath.toString())
                setResult(Activity.RESULT_OK, intent)
                finish()

            })
        }

        btn_switch_camera.setOnClickListener {
            if (camera_kit_view.isFacingFront)
                camera_kit_view.isFacingBack
            else camera_kit_view.isFacingBack
        }

    }

    override fun onStart() {
        super.onStart()
        camera_kit_view.start()
    }

    override fun onResume() {
        super.onResume()
        camera_kit_view.start()
    }

    override fun onPause() {
        camera_kit_view.stop()
        super.onPause()
    }

    override fun onStop() {
        camera_kit_view.stop()
        super.onStop()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_CODE_IMAGE) {
            try {
                val selectedImage = data!!.data
                val filePathColumn =
                    arrayOf(MediaStore.Images.Media.DATA)
                val cursor: Cursor? = this@OpenCameraActivity.contentResolver.query(
                    selectedImage!!,
                    filePathColumn, null, null, null
                )
                cursor!!.moveToFirst()

                val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
                val picturePath: String = cursor.getString(columnIndex)
                cursor.close()

                Log.d("Image Path", "Image Path  is $picturePath")
                val intent = Intent()
                intent.putExtra(Constants.IMAGE_PATH, picturePath)
                setResult(Activity.RESULT_OK, intent)
                finish()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun openGalleryForImage() {
        pictureFile = File(
            Constants.FILE_PATH,
            System.currentTimeMillis().toString() + ".jpeg"
        )
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, Constants.REQUEST_CODE_IMAGE)
    }
}