package com.namastey.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.text.InputType
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonObject
import com.gowtham.library.utils.TrimVideo
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.AlbumDetailAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityAlbumDetailBinding
import com.namastey.listeners.OnItemClick
import com.namastey.model.AlbumBean
import com.namastey.model.VideoBean
import com.namastey.uiView.CreateAlbumView
import com.namastey.utils.Constants
import com.namastey.utils.GridSpacingItemDecoration
import com.namastey.utils.SessionManager
import com.namastey.utils.Utils
import com.namastey.viewModel.CreateAlbumViewModel
import com.video.trimmer.interfaces.OnTrimVideoListener
import kotlinx.android.synthetic.main.activity_album_detail.*
import kotlinx.android.synthetic.main.dialog_bottom_pick.*
import org.buffer.android.thumby.ThumbyActivity
import org.buffer.android.thumby.util.ThumbyUtils
import java.io.File
import javax.inject.Inject

class AlbumDetailActivity : BaseActivity<ActivityAlbumDetailBinding>(), CreateAlbumView,
    OnItemClick, OnTrimVideoListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var activityAlbumDetailBinding: ActivityAlbumDetailBinding
    private lateinit var albumViewModel: CreateAlbumViewModel
    private lateinit var albumDetailAdapter: AlbumDetailAdapter
    private var isEditAlbum = false
    private var postList = ArrayList<VideoBean>()
    private var albumId = 0L
    private var position = -1
    private var videoFile: File? = null
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private var albumBean = AlbumBean()
    private var fromEdit = false
    private var isSavedAlbum = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        albumViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(CreateAlbumViewModel::class.java)
        activityAlbumDetailBinding = bindViewData()
        activityAlbumDetailBinding.viewModel = albumViewModel

        initData()

    }

    private fun initData() {
        rvAlbumDetail.addItemDecoration(GridSpacingItemDecoration(2, 20, false))

        fromEdit = intent.getBooleanExtra(Constants.FROM_EDIT, false)
        if (intent.hasExtra("albumId")) {
            albumId = intent.getLongExtra("albumId", 0)

            albumViewModel.getAlbumDetail(albumId)
            edtAlbumName.visibility = View.VISIBLE
            edtAlbumName.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_edit_gray,
                0
            )
            edtAlbumName.compoundDrawablePadding = 25
            edtAlbumName.inputType = InputType.TYPE_NULL
            edtAlbumName.setOnTouchListener(object : View.OnTouchListener {
                @SuppressLint("ClickableViewAccessibility")
                override fun onTouch(v: View?, event: MotionEvent): Boolean {
                    val DRAWABLE_RIGHT = 2
                    if (event.action === MotionEvent.ACTION_UP) {
                        if (event.rawX >= edtAlbumName.right - (edtAlbumName.compoundDrawables[DRAWABLE_RIGHT].bounds.width() + 40)
                        ) {
                            if (isEditAlbum) {
                                isEditAlbum = false
                                edtAlbumName.inputType = InputType.TYPE_NULL
                                edtAlbumName.clearFocus()
                                editAlbumApiCall()
                                edtAlbumName.setCompoundDrawablesWithIntrinsicBounds(
                                    0,
                                    0,
                                    R.drawable.ic_edit_gray,
                                    0
                                )
                            } else {
                                isEditAlbum = true
                                edtAlbumName.requestFocus()
                                edtAlbumName.inputType = InputType.TYPE_CLASS_TEXT
                                edtAlbumName.setCompoundDrawablesWithIntrinsicBounds(
                                    0,
                                    0,
                                    R.drawable.ic_done_red,
                                    0
                                )
                            }

                            return true
                        }
                    }
                    return false
                }
            })

        } else {
            edtAlbumName.visibility = View.GONE
            val gender = intent.getStringExtra(Constants.GENDER)
            if (gender == Constants.Gender.female.name)
                imgTop.setImageResource(R.drawable.pink_bar)
            else
                imgTop.setImageResource(R.drawable.blue_bar)

            albumBean = intent.getParcelableExtra<AlbumBean>(Constants.ALBUM_BEAN) as AlbumBean
            tvAlbumTitle.text = albumBean.name
            albumDetailAdapter =
                AlbumDetailAdapter(
                    albumBean.post_video_list,
                    this@AlbumDetailActivity,
                    this,
                    fromEdit,
                    false, false
                )
            rvAlbumDetail.adapter = albumDetailAdapter
        }
    }

    private fun editAlbumApiCall() {
        Utils.hideKeyboard(this@AlbumDetailActivity)
        val jsonObject = JsonObject()
        jsonObject.addProperty(Constants.NAME, edtAlbumName.text.toString().trim())
        jsonObject.addProperty(Constants.DEVICE_TYPE, Constants.ANDROID)
        jsonObject.addProperty(Constants.ALBUM_ID, albumId)

        albumViewModel.addEditAlbum(jsonObject)
    }


    override fun onSuccessResponse(albumBean: AlbumBean) {
        isEditAlbum = false
    }

    override fun onSuccessAlbumDetails(arrayList: ArrayList<AlbumBean>) {
        if (arrayList.size > 0) {
            albumBean = arrayList[0]
            postList = arrayList[0].post_video_list
            edtAlbumName.setText(arrayList[0].name)
            if (arrayList[0].name == getString(R.string.saved)) {
                isSavedAlbum = true
                edtAlbumName.isEnabled = false
                edtAlbumName.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    0,
                    0
                )
            } else {
                isSavedAlbum = false
                postList.add(0, VideoBean())
            }

            albumDetailAdapter =
                AlbumDetailAdapter(
                    postList,
                    this@AlbumDetailActivity,
                    this,
                    fromEdit,
                    false,
                    isSavedAlbum
                )
            rvAlbumDetail.adapter = albumDetailAdapter
        }
    }

    override fun onSuccessDeletePost() {
        postList.removeAt(position)
        albumDetailAdapter.notifyItemRemoved(position)
        albumDetailAdapter.notifyItemRangeChanged(position, albumDetailAdapter.itemCount)
    }

    override fun getViewModel() = albumViewModel

    override fun getLayoutId() = R.layout.activity_album_detail

    override fun getBindingVariable() = BR.viewModel

    fun onClickAlbumDetailBack(view: View) {
        onBackPressed()
    }

    /**
     * gives option for select video or take video from camera
     */
    private fun selectVideo() {
        bottomSheetDialog = BottomSheetDialog(this@AlbumDetailActivity, R.style.dialogStyle)
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
            if (isPermissionGrantedForCamera())
                captureVideo()
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

    private fun captureVideo() {
        videoFile = File(
            Constants.FILE_PATH,
            System.currentTimeMillis().toString()
        )

        val cameraIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        startActivityForResult(cameraIntent, Constants.PERMISSION_CAMERA)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
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
                                val intent = Intent(
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
                            if (isPermissionGrantedForCamera())
                                captureVideo()
                        }
                    } else {
                        if (isPermissionGrantedForCamera())
                            captureVideo()
                    }
                }
            }
        }
    }


    override fun onBackPressed() {
        if (trimmerView.visibility == View.VISIBLE)
            trimmerView.visibility = View.GONE
        else
            finishActivity()
    }

    private fun openGalleryForVideo() {

        videoFile = File(
            Constants.FILE_PATH,
            System.currentTimeMillis().toString()
        )

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT;
        startActivityForResult(intent, Constants.REQUEST_VIDEO_SELECT)
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode === TrimVideo.VIDEO_TRIMMER_REQ_CODE && data != null) {
            val uri = Uri.parse(TrimVideo.getTrimmedVideoPath(data))
            Log.d("Trimmed video ", "Trimmed path:: $uri")

            videoFile = File(uri.path)
            val intent = Intent(this@AlbumDetailActivity, PostVideoActivity::class.java)
            intent.putExtra("videoFile", videoFile)
            intent.putExtra("albumId", albumBean.id)
//                intent.putExtra("thumbnailImage", pictureFile)
            intent.putExtra("albumBean", albumBean)
            openActivityForResult(intent, Constants.REQUEST_POST_VIDEO)
        }

        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_CODE_VIDEO_TRIM) {
            if (data != null) {
                val selectedVideo = data.getParcelableExtra<Uri>("videoPath") as Uri

//                videoFile = File(selectedVideo.toString())
                videoFile = File(selectedVideo.toString())

//                startActivityForResult(ThumbyActivity.getStartIntent(this,Uri.fromFile(videoFile)
//                ), RESULT_CODE_PICK_THUMBNAIL)

//                val pictureFile = Utils.bitmapToFile(this@CreateAlbumActivity,bitmap)

                val retriever = MediaMetadataRetriever()
//use one of overloaded setDataSource() functions to set your data source
                retriever.setDataSource(this@AlbumDetailActivity, Uri.fromFile(videoFile));
                val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                val timeInMillisec: Long = time?.toLong() ?: 0

                val second = timeInMillisec / 1000

                val file_size: Int = java.lang.String.valueOf(videoFile!!.length() / 1024).toInt()

                Log.d("Video time : ", "Video $time")
                Log.d("Video time : ", "Video $second")
                Log.d("Video time : ", "file_size  $file_size ")

                retriever.release()

//                        val intent = Intent(Intent.ACTION_VIEW, selectedVideo);
//        intent.setDataAndType(selectedVideo, "video/mp4");
//        startActivity(intent);

                val intent = Intent(this@AlbumDetailActivity, PostVideoActivity::class.java)
                intent.putExtra("videoFile", videoFile)
                intent.putExtra("albumId", albumId)
//                intent.putExtra("thumbnailImage", pictureFile)
                intent.putExtra("albumBean", albumBean)
                intent.putExtra("fromAlbumDetail", true)
                openActivityForResult(intent, Constants.REQUEST_POST_VIDEO)

            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_POST_VIDEO) {
            if (data != null) {      // Temp need to change
                albumViewModel.getAlbumDetail(albumId)
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_VIDEO_SELECT) {

            if (data != null) {
                val selectedVideo = data.data

                if (selectedVideo != null) {
//                    val videoPath = Utils.getPath(this, selectedVideo)
//                    Log.d("Path", videoPath.toString())
                    Log.d("Path", selectedVideo.toString())
//                    val photoUri = FileProvider.getUriForFile(
//                        this,
//                        applicationContext.packageName + ".provider",
//                        File(videoPath)
//                    )
//                    Log.d("Path", photoUri.toString())
//                    Log.d("Uri Path", Uri.parse(videoPath).toString())



//                    val retriever = MediaMetadataRetriever()
//use one of overloaded setDataSource() functions to set your data source
//                    retriever.setDataSource(this@AlbumDetailActivity, Uri.fromFile(File(videoPath)))
//                    val time =
//                        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
//                    val timeInMillisec: Long = time?.toLong() ?: 0
//
//                    val path = File(videoPath)
//                    val file_size: Int = java.lang.String.valueOf(path.length() / 1024).toInt()
//                    Log.d("Uri Path", Uri.fromFile(path).toString())
//
//                    val second = timeInMillisec / 1000
//                    Log.d("Video time : ", "Video $time")
//                    Log.d("Video time : ", "Video $second")
//                    Log.d("Video time : ", "file_size  $file_size ")
//                    videoFile = File(videoPath)




                    TrimVideo.activity(selectedVideo.toString())
//                        .setDestination("/storage/emulated/0/DCIM/namastey")  //default output path /storage/emulated/0/DOWNLOADS
                        .start(this)









//                    trimmerView.visibility = View.VISIBLE
//                    videoTrimmer.setOnTrimVideoListener(this)
//                        .setVideoURI(Uri.parse(videoPath))
//                        .setVideoInformationVisibility(true)
//                        .setMaxDuration(60)
//                        .setMinDuration(6)
//                        .setDestinationPath(
//                            Environment.getExternalStorageDirectory()
//                                .toString() + File.separator + "temp" + File.separator + "Videos" + File.separator
//                        )


                }
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == Constants.PERMISSION_CAMERA) {
            if (data != null) {
                val selectedVideo = data.data

                if (selectedVideo != null) {
                    val videoPath = Utils.getPath(this, selectedVideo)
                    Log.d("Path", videoPath.toString())
                    Log.d("Uri Path", Uri.parse(videoPath).toString())

//                    videoFile = File(videoPath)

                    trimmerView.visibility = View.VISIBLE
                    videoTrimmer.setOnTrimVideoListener(this)
                        .setVideoURI(Uri.parse(videoPath))
                        .setVideoInformationVisibility(true)
                        .setMaxDuration(60)
                        .setMinDuration(6)
                        .setDestinationPath(
                            Environment.getExternalStorageDirectory()
                                .toString() + File.separator + "temp" + File.separator + "Videos" + File.separator
                        )

//                    val intent =
//                        Intent(this, TrimmerActivity::class.java)
//                    intent.putExtra(
//                        Constants.EXTRA_VIDEO_PATH,
//                        videoPath
//                    )
//                    openActivityForResult(intent, Constants.REQUEST_CODE_VIDEO_TRIM)
                }
            }
        } else if (requestCode == Constants.RESULT_CODE_PICK_THUMBNAIL) {
            if (data != null) {
                val imageUri = data.getParcelableExtra<Uri>(ThumbyActivity.EXTRA_URI) as Uri
                val location = data.getLongExtra(ThumbyActivity.EXTRA_THUMBNAIL_POSITION, 0)
                val bitmap = ThumbyUtils.getBitmapAtFrame(this, imageUri, location, 250, 250)
                Log.d("Image ", "done")

                val pictureFile = Utils.bitmapToFile(this@AlbumDetailActivity, bitmap)

                val intent = Intent(this@AlbumDetailActivity, PostVideoActivity::class.java)
                intent.putExtra("videoFile", videoFile)
                intent.putExtra("albumId", albumId)
                intent.putExtra("thumbnailImage", pictureFile)
                intent.putExtra("albumBean", albumBean)
                openActivityForResult(intent, Constants.REQUEST_POST_VIDEO)
            }
        }
    }

    override fun onItemClick(postId: Long, position: Int) {
        if (position == 0 && !isSavedAlbum) {
            this.position = 0
            selectVideo()
        } else {
            this.position = position
            albumViewModel.removePostVideo(postId)
        }
    }

    override fun cancelAction() {
        TODO("Not yet implemented")
    }

    override fun getResult(uri: Uri) {
        trimmerView.visibility = View.GONE
        Log.d("TrimVideo: ", "getResult : " + uri.toString())

        videoFile = File(uri.path)
        val intent = Intent(this@AlbumDetailActivity, PostVideoActivity::class.java)
        intent.putExtra("videoFile", videoFile)
        intent.putExtra("albumId", albumBean.id)
//                intent.putExtra("thumbnailImage", pictureFile)
        intent.putExtra("albumBean", albumBean)
        openActivityForResult(intent, Constants.REQUEST_POST_VIDEO)

    }

    override fun onError(message: String) {
        trimmerView.visibility = View.GONE
        Log.d("TrimVideo: ", "onError" + message)
    }

    override fun onTrimStarted() {
        Log.d("TrimVideo: ", "onTrimStarted")
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::bottomSheetDialog.isInitialized)
            bottomSheetDialog.dismiss()
        albumViewModel.onDestroy()
    }

    fun onClickSave(view: View) {
        videoTrimmer.onSaveClicked()
    }

    fun onClickCancel(view: View) {
        trimmerView.visibility = View.GONE
    }
}