package com.namastey.activity

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
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
import com.namastey.listeners.OnItemClick
import com.namastey.model.AlbumBean
import com.namastey.model.ProfileBean
import com.namastey.uiView.CreateAlbumView
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import com.namastey.utils.Utils
import com.namastey.viewModel.CreateAlbumViewModel
import kotlinx.android.synthetic.main.activity_create_album.*
import kotlinx.android.synthetic.main.dialog_bottom_pick.*
import org.buffer.android.thumby.ThumbyActivity
import org.buffer.android.thumby.ThumbyActivity.Companion.EXTRA_THUMBNAIL_POSITION
import org.buffer.android.thumby.ThumbyActivity.Companion.EXTRA_URI
import org.buffer.android.thumby.util.ThumbyUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class CreateAlbumActivity : BaseActivity<ActivityCreateAlbumBinding>(), CreateAlbumView,
    OnCreateAlbumItemClick,OnItemClick {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var activityCreateAlbumBinding: ActivityCreateAlbumBinding
    private lateinit var createAlbumViewModel: CreateAlbumViewModel
    private var albumList: ArrayList<AlbumBean> = ArrayList()
    private lateinit var albumAdapter: AlbumCreateListAdapter
    private var adapterPosition: Int = 0
    private val REQUEST_VIDEO_SELECT = 101
    private val REQUEST_CODE_CAMERA = 102
    private val REQUEST_POST_VIDEO = 103
    private val RESULT_CODE_PICK_THUMBNAIL = 104
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private var videoFile: File? = null
    private var albumBean = AlbumBean()
    private var fromAlbumList = false

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

        if (intent.hasExtra("fromAlbumList")){
            fromAlbumList = intent.getBooleanExtra("fromAlbumList",false)
            createAlbumViewModel.getAlbumList()
        }else{
//        Default one album create uploads
            var albumBean = AlbumBean()
            albumBean.name = getString(R.string.uploads)
            createAlbumApi(albumBean, true)
        }

//        albumList.add(albumBean)
//        createAlbumViewModel.getAlbumList()
//        albumAdapter = AlbumCreateListAdapter(albumList, this@CreateAlbumActivity, this)
//        rvAlbumList.adapter = albumAdapter
    }

    /**
     * album created successfully
     */
    override fun onSuccessResponse(albumBean: AlbumBean) {
        albumBean.is_created = 0    // 0 = display edit icons and disable edittext
        // 1 = display done icons and enable edittext

        if (albumBean.name.equals(getString(R.string.uploads))){
            createAlbumViewModel.getAlbumList()
        }else{
            albumList[adapterPosition] = albumBean
            albumAdapter.notifyItemChanged(adapterPosition)
        }
    }

    override fun onSuccessCreateProfile(profileBean: ProfileBean) {
        Log.d("Success : ", profileBean.username)

//        openActivity(this@CreateAlbumActivity, ProfileActivity())
    }

    override fun onSuccess(msg: String) {
        Log.d("Success : ", msg)

        openActivity(this@CreateAlbumActivity, ProfileActivity())
    }
    /**
     * Get album list with video details
     */
    override fun onSuccessAlbumDetails(albumBeanList: ArrayList<AlbumBean>) {
        albumList.clear()
        albumList = albumBeanList
        albumAdapter = AlbumCreateListAdapter(albumList, this@CreateAlbumActivity, this,this)
        rvAlbumList.adapter = albumAdapter
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
        var albumBean = AlbumBean()
        albumBean.is_created = 1
        albumList.add(albumBean)
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

        createAlbumViewModel.addAlbum(jsonObject)
    }

    /**
     * click on finish button call api for sign up complete
     */
    fun onClickFinish(view: View) {

        if (!fromAlbumList) {
            val jsonObject = JsonObject()

            jsonObject.addProperty(
                Constants.USERNAME,
                sessionManager.getStringValue(Constants.KEY_CASUAL_NAME)
            )
            jsonObject.addProperty(
                Constants.MIN_AGE,
                sessionManager.getStringValue(Constants.KEY_AGE_MIN)
            )
            jsonObject.addProperty(
                Constants.MAX_AGE,
                sessionManager.getStringValue(Constants.KEY_AGE_MAX)
            )
            jsonObject.addProperty(
                Constants.TAG_LINE,
                sessionManager.getStringValue(Constants.KEY_TAGLINE)
            )
            jsonObject.addProperty(Constants.GENDER, sessionManager.getInterestIn())
            jsonObject.addProperty(
                Constants.TAGS,
                ProfileInterestActivity.categoryIdList.joinToString()
            )

            var categoryIdList: ArrayList<Int> = ArrayList()
            for (category in sessionManager.getCategoryList()) {
                categoryIdList.add(category.id)
            }
            jsonObject.addProperty(
                Constants.CATEGORY_ID,
                categoryIdList.joinToString()
            )
            var socialAccountId = ArrayList<Long>()
            for (data in ProfileInterestActivity.socialAccountList) {
                socialAccountId.add(data.id)
            }
            jsonObject.addProperty(Constants.SOCIAL_ACCOUNTS, socialAccountId.joinToString())
            jsonObject.addProperty(
                Constants.EDUCATION,
                sessionManager.getEducationBean().user_education_Id
            )
            jsonObject.addProperty(
                Constants.EDUCATION,
                sessionManager.getEducationBean().user_education_Id
            )
            jsonObject.addProperty(Constants.JOBS, sessionManager.getJobBean().id)
            var albumId = ArrayList<Long>()
            for (data in albumList) {
                albumId.add(data.id)
            }
            jsonObject.addProperty(Constants.ALBUMS, albumId.joinToString())

            jsonObject.addProperty(Constants.DEVICE_ID, "23456789")    // Need to change
            jsonObject.addProperty(Constants.DEVICE_TYPE, Constants.ANDROID)

            Log.d("CreateProfile Request:", jsonObject.toString())

            createAlbumViewModel.createProfile(jsonObject)
        }else{
            openActivity(this@CreateAlbumActivity, ProfileActivity())
        }
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
            isCameraPermissionGranted()
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
                captureVideo()
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
            captureVideo()
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
        startActivityForResult(intent, REQUEST_VIDEO_SELECT)
    }

    private fun captureVideo() {
        videoFile = File(
            Constants.FILE_PATH,
            System.currentTimeMillis().toString()
        )

        val cameraIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_POST_VIDEO) {
            if (data != null) {      // Temp need to change
//                var videoBean: VideoBean = data.getParcelableExtra("videoBean")
//
//                albumBean.post_video_list.add(videoBean)
//                albumList[adapterPosition] = albumBean
//                albumAdapter.notifyItemChanged(adapterPosition)
                createAlbumViewModel.getAlbumList()
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_VIDEO_SELECT) {

            if (data != null) {
                val selectedVideo = data.data

                if (selectedVideo != null) {
                    var videoPath = Utils.getPath(this, selectedVideo)
                    Log.d("Path", videoPath)
//
//                    val filePathColumn =
//                        arrayOf(MediaStore.Video.Media.DATA)
//                    val cursor: Cursor? = this@CreateAlbumActivity.contentResolver.query(
//                        selectedVideo!!,
//                        filePathColumn, null, null, null
//                    )
//                    cursor!!.moveToFirst()
//
//                    val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
//                    val picturePath: String = cursor.getString(columnIndex)
//                    cursor.close()

                    videoFile = File(videoPath)

//                    loadThumbnails(videoPath!!)
                    startActivityForResult(ThumbyActivity.getStartIntent(this, selectedVideo), RESULT_CODE_PICK_THUMBNAIL)

//                    var intent = Intent(this@CreateAlbumActivity, PostVideoActivity::class.java)
//                    intent.putExtra("videoFile", videoFile)
//                    intent.putExtra("albumId", albumBean.id)
//                    intent.putExtra("albumBean", albumBean)
//                    openActivityForResult(intent, REQUEST_POST_VIDEO)
                }
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_CAMERA) {
            if (data != null) {
                val selectedVideo = data.data

                if (selectedVideo != null) {
                    var videoPath = Utils.getPath(this, selectedVideo)
                    Log.d("Path", videoPath)

                    videoFile = File(videoPath)

                    startActivityForResult(ThumbyActivity.getStartIntent(this, selectedVideo), RESULT_CODE_PICK_THUMBNAIL)

//                    var intent = Intent(this@CreateAlbumActivity, PostVideoActivity::class.java)
//                    intent.putExtra("videoFile", videoFile)
//                    intent.putExtra("albumId", albumBean.id)
//                    intent.putExtra("albumBean", albumBean)
//                    openActivityForResult(intent, REQUEST_POST_VIDEO)
                }
            }
        } else if (requestCode == RESULT_CODE_PICK_THUMBNAIL) {
            if (data != null) {
                val imageUri = data?.getParcelableExtra(EXTRA_URI) as Uri
                val location = data.getLongExtra(EXTRA_THUMBNAIL_POSITION, 0)
                val bitmap = ThumbyUtils.getBitmapAtFrame(this, imageUri, location, 250, 250)
                Log.d("Image ", "done")

                var pictureFile = Utils.bitmapToFile(this@CreateAlbumActivity,bitmap)

//                val tempUri = Utils.getImageUri(applicationContext, bitmap)
//                var pictuareFile = File(Utils.getRealPathFromURI(this@CreateAlbumActivity,tempUri))

                var intent = Intent(this@CreateAlbumActivity, PostVideoActivity::class.java)
                intent.putExtra("videoFile", videoFile)
                intent.putExtra("albumId", albumBean.id)
                intent.putExtra("thumbnailImage", pictureFile)
                intent.putExtra("albumBean", albumBean)
                openActivityForResult(intent, REQUEST_POST_VIDEO)
            }
        }
    }
//    private fun loadThumbnails(uri: String) {
//        val metaDataSource = MediaMetadataRetriever()
////        if (Build.VERSION.SDK_INT >= 14)
////            metaDataSource.setDataSource(uri.path, HashMap<String, String>())
////        else
//            metaDataSource.setDataSource(uri)
//
//        val videoLength = (metaDataSource.extractMetadata(
//            MediaMetadataRetriever.METADATA_KEY_DURATION).toInt() * 1000).toLong()
//
//        val thumbnailCount = 7
//
//        val interval = videoLength / thumbnailCount
//
//        for (i in 0 until thumbnailCount - 1) {
//            val frameTime = i * interval
//
////            var bitmap = metaDataSource.getFrameAtTime(5000000)
//            var bitmap = metaDataSource.getFrameAtTime(frameTime, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
//            try {
//                val targetWidth: Int
//                val targetHeight: Int
////                if (bitmap.height > bitmap.width) {
////                    targetHeight = frameDimension
////                    val percentage = frameDimension.toFloat() / bitmap.height
////                    targetWidth = (bitmap.width * percentage).toInt()
////                } else {
////                    targetWidth = frameDimension
////                    val percentage = frameDimension.toFloat() / bitmap.width
////                    targetHeight = (bitmap.height * percentage).toInt()
////                }
////                bitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, false)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//
////            container_thumbnails.addView(ThumbnailView(context).apply { setImageBitmap(bitmap) })
//        }
//        metaDataSource.release()
//    }

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
                                val builder = AlertDialog.Builder(this)
                                builder.setMessage(getString(R.string.permission_denied_storage_message))
                                    .setTitle(getString(R.string.permission_required))

                                builder.setPositiveButton(
                                    getString(R.string.go_to_settings)
                                ) { dialog, id ->
                                    var intent = Intent(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.fromParts("package", packageName, null)
                                    )
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                    finish()
                                }

                                val dialog = builder.create()
                                dialog.setCanceledOnTouchOutside(false)
                                dialog.show()
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
                captureVideo()
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
                            val builder = AlertDialog.Builder(this)
                            if (grantResults[0] == PackageManager.PERMISSION_DENIED)
                                builder.setMessage(getString(R.string.permission_denied_camera_message))

                            if (grantResults[1] == PackageManager.PERMISSION_DENIED)
                                builder.setMessage(getString(R.string.permission_denied_storage_message))
                                    .setTitle(getString(R.string.permission_required))

                            builder.setPositiveButton(
                                getString(R.string.go_to_settings)
                            ) { dialog, id ->
                                var intent = Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", packageName, null)
                                )
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                finish()
                            }

                            val dialog = builder.create()
                            dialog.setCanceledOnTouchOutside(false)
                            dialog.show()
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

    /**
     * Click on post video delete button remove video from album
     */
    override fun onItemClick(value: Long) {
        createAlbumViewModel.removePostVideo(value)
    }

    override fun onSuccessDeletePost() {
        createAlbumViewModel.getAlbumList()
    }
}