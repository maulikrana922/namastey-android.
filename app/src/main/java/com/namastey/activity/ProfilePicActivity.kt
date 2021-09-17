package com.namastey.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.animation.RotateAnimation
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityProfilePicBinding
import com.namastey.uiView.ProfilePicView
import com.namastey.utils.Constants
import com.namastey.utils.GlideLib
import com.namastey.utils.Utils
import com.namastey.viewModel.ProfilePicViewModel
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile_pic.*
import kotlinx.android.synthetic.main.dialog_bottom_pick.*
import java.io.File
import javax.inject.Inject

class ProfilePicActivity : BaseActivity<ActivityProfilePicBinding>(), ProfilePicView {

    private lateinit var bottomSheetDialog: BottomSheetDialog
    private var profileFile: File? = null

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var profilePicViewModel: ProfilePicViewModel
    private lateinit var binding: ActivityProfilePicBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)


        profilePicViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ProfilePicViewModel::class.java)
        binding = bindViewData()
        binding.viewModel = profilePicViewModel

    }

    override fun getViewModel() = profilePicViewModel

    override fun getLayoutId() = R.layout.activity_profile_pic

    override fun getBindingVariable() = BR.viewModel

    private fun selectImage() {
        bottomSheetDialog = BottomSheetDialog(this@ProfilePicActivity, R.style.dialogStyle)
        bottomSheetDialog.setContentView(
            layoutInflater.inflate(
                R.layout.dialog_bottom_pick,
                null
            )
        )
        bottomSheetDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        bottomSheetDialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.tvPhotoTake.text = getString(R.string.take_photo)
        bottomSheetDialog.tvPhotoChoose.text = getString(R.string.select_photo)
        bottomSheetDialog.tvFromVideo.visibility = View.GONE

        bottomSheetDialog.tvPhotoTake.setOnClickListener {
            bottomSheetDialog.dismiss()
            if (isPermissionGrantedForCamera())
                capturePhoto()
        }
        bottomSheetDialog.tvPhotoChoose.setOnClickListener {
            bottomSheetDialog.dismiss()
            isReadWritePermissionGranted()
        }

        bottomSheetDialog.tvPhotoCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    private fun openGalleryForImage() {
        profileFile = File(
            Constants.FILE_PATH,
            System.currentTimeMillis().toString() + ".jpeg"
        )

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, Constants.REQUEST_CODE_IMAGE)
    }

    private fun capturePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Create the File where the photo should go
            try {

                val photoUri: Uri = FileProvider.getUriForFile(
                    this,
                    applicationContext.packageName + ".provider",
                    Utils.getCameraFile(this@ProfilePicActivity)
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivityForResult(takePictureIntent, Constants.REQUEST_CODE_CAMERA_IMAGE)
            } catch (ex: Exception) {
                showMsg(ex.localizedMessage)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.REQUEST_CODE_CAMERA_IMAGE) {
                profileFile = Utils.getCameraFile(this@ProfilePicActivity)
                val photoUri = FileProvider.getUriForFile(
                    this,
                    applicationContext.packageName.plus(".provider"),
                    profileFile!!
                )

                val bitmap: Bitmap = Utils.scaleBitmapDown(
                    MediaStore.Images.Media.getBitmap(contentResolver, photoUri),
                    1200
                )!!
                GlideLib.loadImageBitmap(this, ivProfilePic, bitmap)

            } else if (requestCode == Constants.REQUEST_CODE_IMAGE) {

                if (data != null) {
                    val selectedImage = data.data

                    if (selectedImage != null) {
                        try {
                            val filePathColumn =
                                arrayOf(MediaStore.Images.Media.DATA)
                            val cursor: Cursor? = this@ProfilePicActivity.contentResolver.query(
                                selectedImage,
                                filePathColumn, null, null, null
                            )
                            cursor!!.moveToFirst()

                            val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
                            val picturePath: String = cursor.getString(columnIndex)
                            cursor.close()

                            Log.d("Image Path", "Image Path  is $picturePath")
                            //profileFile = Utils.saveBitmapToFile(File(picturePath))

                            val bitmap: Bitmap = BitmapFactory.decodeFile(picturePath)
                            profileFile=File(Utils.saveBitmapToExtFilesDir(bitmap,this))

                            val photoUri = FileProvider.getUriForFile(
                                this,
                                applicationContext.packageName.plus(".provider"),
                                profileFile!!
                            )
                            GlideLib.loadImage(this, ivProfilePic, photoUri.toString())

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        finishActivity()
    }

    fun onClickBack(view: View) {
        onBackPressed()
    }

    override fun onSuccess(msg: String) {
        openActivity(this@ProfilePicActivity, LanguageActivity())
    }

    fun onClickContinue(view: View) {
        if (profileFile != null && profileFile!!.exists()) {
            ivPlus.visibility = View.GONE
            profilePicViewModel.updateProfilePic(profileFile!!)
        } else {
            showMsg(R.string.select_profile_pic)
        }
    }

    fun onClickPlus(view: View) {
        selectImage()
    }
    private fun isReadWritePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                openGalleryForImage()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), Constants.PERMISSION_STORAGE
                )
            }
        } else {
            openGalleryForImage()
        }
    }

}