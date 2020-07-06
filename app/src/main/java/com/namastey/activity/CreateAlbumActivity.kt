package com.namastey.activity

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonObject
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.AlbumCreateListAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityCreateAlbumBinding
import com.namastey.listeners.OnCreateAlbumItemClick
import com.namastey.model.AlbumBean
import com.namastey.model.VideoBean
import com.namastey.uiView.CreateAlbumView
import com.namastey.utils.Constants
import com.namastey.viewModel.CreateAlbumViewModel
import kotlinx.android.synthetic.main.activity_create_album.*
import kotlinx.android.synthetic.main.dialog_bottom_pick.*
import java.io.*
import javax.inject.Inject

class CreateAlbumActivity : BaseActivity<ActivityCreateAlbumBinding>(), CreateAlbumView,
    OnCreateAlbumItemClick {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var activityCreateAlbumBinding: ActivityCreateAlbumBinding
    private lateinit var createAlbumViewModel: CreateAlbumViewModel
    private var albumList: ArrayList<AlbumBean> = ArrayList()
    private lateinit var albumAdapter: AlbumCreateListAdapter
    private var adapterPosition: Int = 0
    private val REQUEST_CODE = 101
    private val REQUEST_CODE_CAMERA = 102
    private val REQUEST_POST_VIDEO = 103
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private var videoFile: File? = null
    private var albumBean = AlbumBean()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        createAlbumViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(CreateAlbumViewModel::class.java)
        activityCreateAlbumBinding = bindViewData()
        activityCreateAlbumBinding.viewModel = createAlbumViewModel

        initData()

    }

    private fun initData() {
//        addNewAlbum()

//        Default one album create uploads
        var albumBean = AlbumBean()
        albumBean.name = getString(R.string.uploads)

        createAlbumApi(albumBean, true)
        albumList.add(albumBean)
        albumAdapter = AlbumCreateListAdapter(albumList, this@CreateAlbumActivity, this)
        rvAlbumList.adapter = albumAdapter
    }

    /**
     * album created successfully
     */
    override fun onSuccessResponse(albumBean: AlbumBean) {
        albumBean.is_created = 0    // 0 = display edit icons and disable edittext
        // 1 = display done icons and enable edittext
        albumList[adapterPosition] = albumBean
        albumAdapter.notifyItemChanged(adapterPosition)
    }

    override fun getViewModel() = createAlbumViewModel

    override fun getLayoutId() = R.layout.activity_create_album

    override fun getBindingVariable() = BR.viewModel

    override fun onBackPressed() {
        finishActivity()
    }

    fun onClickCreateAlbumBack(view: View) {
        onBackPressed()
    }

    /**
     * click on add album generate new layout for add album
     */
    fun onClickAddAlbum(view: View) {
        albumList.add(AlbumBean())
        albumAdapter.notifyItemInserted(albumList.size)
//        addNewAlbum()
    }

    /**
     * create programaticaaly album view
     * now not used
     */
//    private fun addNewAlbum(){
//        var layoutInflater = LayoutInflater.from(this@CreateAlbumActivity)
//
//        var view = layoutInflater.inflate(R.layout.view_create_album, llAlbumList, false)
//
//        view.ivEditAlbum.setOnClickListener{
//            createAlbumApi()
//        }
//        llAlbumList.addView(view)
//    }

    private fun createAlbumApi(albumBean: AlbumBean, isCreate: Boolean) {
        var jsonObject = JsonObject()
        jsonObject.addProperty(Constants.NAME, albumBean.name)
        jsonObject.addProperty(Constants.DEVICE_TYPE, Constants.ANDROID)

        if (!isCreate)
            jsonObject.addProperty(Constants.ALBUM_ID, albumBean.id)

        createAlbumViewModel.addJob(jsonObject)
    }

    /**
     * click on finish button call api for sign up complete
     */
    fun onClickFinish(view: View) {

    }

    override fun onDestroy() {
        super.onDestroy()
        if (::bottomSheetDialog.isInitialized)
            bottomSheetDialog.dismiss()
        createAlbumViewModel.onDestroy()
    }

    /**
     * when user click on add album done button then call api for add album
     */
    override fun onCreateAlbumItemClick(albumBean: AlbumBean, position: Int, isCreate: Boolean) {
        adapterPosition = position
        createAlbumApi(albumBean, isCreate)
    }

    /**
     * click on album add video to particular album
     */
    override fun onClickAddVideo(albumBean: AlbumBean) {
        this.albumBean = albumBean
        if (albumBean.is_created == 1)
            showMsg(getString(R.string.msg_save_album))
        else
            selectVideo()

//        openActivity(this@CreateAlbumActivity,PostVideoActivity())
    }

    /**
     * gives option for select video or take video from camera
     */
    private fun selectVideo() {
        bottomSheetDialog = BottomSheetDialog(this@CreateAlbumActivity, R.style.choose_photo)
        bottomSheetDialog.setContentView(
            layoutInflater.inflate(
                R.layout.dialog_bottom_pick,
                null
            )
        )
        bottomSheetDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        bottomSheetDialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.tvPhotoTake.setOnClickListener {
            bottomSheetDialog.dismiss()

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

    private fun isCameraPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
//                capturePhoto()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    Constants.PERMISSION_CAMERA
                )
            }
        } else {
//            capturePhoto()
        }
    }

    private fun isReadWritePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                openGalleryForVideo()
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
            openGalleryForVideo()
        }
    }

    private fun openGalleryForVideo() {

        videoFile = File(
            Constants.FILE_PATH,
            System.currentTimeMillis().toString()
        )

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT;
        startActivityForResult(intent, REQUEST_CODE)
    }

    private fun capturePhoto() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_POST_VIDEO){
            if (data != null){      // Temp need to change
                var videoBean:VideoBean = data.getParcelableExtra("videoBean")

                albumBean.post_video_list.add(videoBean)
                albumList[adapterPosition] = albumBean
                albumAdapter.notifyItemChanged(adapterPosition)
            }
        }else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {

            if (data != null) {
                val selectedImage = data.data

                if (selectedImage != null) {
                    var videoPath = selectedImage.path
                    Log.d("Path", videoPath)

                    val filePathColumn =
                        arrayOf(MediaStore.Images.Media.DATA)
                    val cursor: Cursor? = this@CreateAlbumActivity.contentResolver.query(
                        selectedImage!!,
                        filePathColumn, null, null, null
                    )
                    cursor!!.moveToFirst()

                    val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
                    val picturePath: String = cursor.getString(columnIndex)
                    cursor.close()

                    videoFile = File(picturePath)

                    var intent = Intent(this@CreateAlbumActivity,PostVideoActivity::class.java)
                    intent.putExtra("videoFile",videoFile)
                    intent.putExtra("albumId",albumBean.id)
                    openActivityForResult(intent,REQUEST_POST_VIDEO)
                }
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_CAMERA) {
//            if (data != null)
//                ivProfileUser.setImageBitmap(data.extras.get("data") as Bitmap)
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            Constants.PERMISSION_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
                    openGalleryForVideo()
                } else {
                    if (grantResults.isNotEmpty()) {
                        if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED) {
                            // user rejected the permission
                            val permission: String =
                                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                                    permissions[0]
                                } else {
                                    permissions[1]
                                }
                            val showRationale = shouldShowRequestPermissionRationale(permission)
                            if (!showRationale) {
                                val intent = Intent()
                                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                val uri = Uri.fromParts("package", packageName, null)
                                intent.data = uri
                                startActivity(intent)
                            } else {
                                isReadWritePermissionGranted()
                            }
                        } else {
                            isReadWritePermissionGranted()
                        }
                    }
                }
            }
            Constants.PERMISSION_CAMERA -> if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                capturePhoto()
            } else {
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED) {
                        // user rejected the permission
                        val permission: String =
                            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                                permissions[0]
                            } else {
                                permissions[1]
                            }
                        val showRationale = shouldShowRequestPermissionRationale(permission)
                        if (!showRationale) {
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            startActivity(intent)
                        } else {
                            isCameraPermissionGranted()
                        }
                    } else {
                        isCameraPermissionGranted()
                    }
                }
            }
        }
    }


}
