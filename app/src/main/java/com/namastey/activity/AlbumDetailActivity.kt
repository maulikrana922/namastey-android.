package com.namastey.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.InputType
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonObject
import com.gowtham.library.utils.CompressOption
import com.gowtham.library.utils.TrimType
import com.gowtham.library.utils.TrimVideo
import com.gowtham.library.utils.TrimmerUtils
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.AlbumDetailAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityAlbumDetailBinding
import com.namastey.listeners.OnItemClick
import com.namastey.listeners.OnPostImageClick
import com.namastey.listeners.OnSelectUserItemClick
import com.namastey.model.AlbumBean
import com.namastey.model.DashboardBean
import com.namastey.model.VideoBean
import com.namastey.uiView.CreateAlbumView
import com.namastey.utils.*
import com.namastey.viewModel.CreateAlbumViewModel
import kotlinx.android.synthetic.main.activity_album_detail.*
import kotlinx.android.synthetic.main.dialog_alert.*
import kotlinx.android.synthetic.main.dialog_bottom_pick.*
import java.io.File
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.*
import javax.inject.Inject

class AlbumDetailActivity : BaseActivity<ActivityAlbumDetailBinding>(), CreateAlbumView,
    OnItemClick, OnPostImageClick, OnSelectUserItemClick {
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
    private var isShowMenu = false
    private var isHide = 0
    private var textHideShow = ""

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

        if (intent.hasExtra("isShowMenu")) {
            isShowMenu = intent.getBooleanExtra("isShowMenu", false)
        }
        Log.e("AlbumDetailActivity ", "isShowMenu:: $isShowMenu")

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
                    this,
                    this,
                    fromEdit,
                    false,
                    false
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
//        if (trimmerView.visibility == View.VISIBLE)
//            trimmerView.visibility = View.GONE
//        else
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

    fun onClickAlbumDetailMore(view: View) {
        if (isShowMenu) {
            createPopUpMenu()
        }
    }

    private fun createPopUpMenu() {
        val popupMenu = PopupMenu(this, ivMore)
        popupMenu.menuInflater.inflate(R.menu.menu_album_detail, popupMenu.menu)
        val menuShowHide = popupMenu.menu.findItem(R.id.action_show_hide)
        val menuDelete = popupMenu.menu.findItem(R.id.action_delete_order).setVisible(true)
        if (isHide == 1) {
            menuShowHide.title = resources.getString(R.string.action_show_album)
            textHideShow = getString(R.string.action_show_album)
        } else {
            menuShowHide.title = resources.getString(R.string.action_hide_album)
            textHideShow = getString(R.string.action_hide_album)
        }


        try {
            val fields: Array<Field> = PopupMenu::class.java.declaredFields
            for (field in fields) {
                if ("mPopup" == field.name) {
                    field.isAccessible = true
                    val menuPopupHelper: Any = field.get(popupMenu)
                    val classPopupHelper = Class.forName(menuPopupHelper.javaClass.name)
                    val setForceIcons: Method = classPopupHelper.getMethod(
                        "setForceShowIcon",
                        Boolean::class.javaPrimitiveType
                    )
                    setForceIcons.invoke(menuPopupHelper, true)
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_delete_order -> {
                    dialogDeleteAlbum()
                }
                R.id.action_show_hide -> {
                    dialogHideAlbum(textHideShow)
                    menuShowHide.title = textHideShow

                }
            }
            true
        }
        popupMenu.show()
    }

    private fun dialogDeleteAlbum() {
        object : CustomAlertDialog(
            this,
            resources.getString(R.string.msg_delete_album),
            getString(R.string.yes),
            getString(R.string.cancel)
        ) {
            override fun onBtnClick(id: Int) {
                when (id) {
                    btnPos.id -> {
                        albumViewModel.deleteAlbum(albumId)

                    }
                    btnNeg.id -> {
                        dismiss()
                    }
                }
            }
        }.show()
    }

    private fun dialogHideAlbum(textHideShow1: String) {
        object : CustomAlertDialog(
            this,
            //resources.getString(R.string.msg_hide_album),
            String.format(getString(R.string.msg_hide_album), textHideShow1),
            getString(R.string.yes),
            getString(R.string.cancel)
        ) {
            override fun onBtnClick(id: Int) {
                when (id) {
                    btnPos.id -> {
                        if (isHide == 1)
                            albumViewModel.hideAlbum(albumId, 0)
                        else if (isHide == 0)
                            albumViewModel.hideAlbum(albumId, 1)
                    }
                    btnNeg.id -> {
                        dismiss()
                    }
                }
            }
        }.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode === TrimVideo.VIDEO_TRIMMER_REQ_CODE && data != null) {
            val uri = Uri.parse(TrimVideo.getTrimmedVideoPath(data))
            Log.e("Trimmed video ", "Trimmed path:: $uri")

            videoFile = File(uri.path)
            val intent = Intent(this@AlbumDetailActivity, PostVideoActivity::class.java)
            intent.putExtra("videoFile", videoFile)
            intent.putExtra("albumId", albumBean.id)
//                intent.putExtra("thumbnailImage", pictureFile)
            intent.putExtra("albumBean", albumBean)
            openActivityForResult(intent, Constants.REQUEST_POST_VIDEO)
        }

//        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_CODE_VIDEO_TRIM) {
//            if (data != null) {
//                val selectedVideo = data.getParcelableExtra<Uri>("videoPath") as Uri
//
//                videoFile = File(selectedVideo.toString())
//
//                val intent = Intent(this@AlbumDetailActivity, PostVideoActivity::class.java)
//                intent.putExtra("videoFile", videoFile)
//                intent.putExtra("albumId", albumId)
////                intent.putExtra("thumbnailImage", pictureFile)
//                intent.putExtra("albumBean", albumBean)
//                intent.putExtra("fromAlbumDetail", true)
//                openActivityForResult(intent, Constants.REQUEST_POST_VIDEO)
//
//            }
//        } else
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_POST_VIDEO) {
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



                    TrimVideo.activity(selectedVideo.toString())
//                        .setCompressOption(CompressOption(30,"1M",460,320))
                        .setTrimType(TrimType.MIN_MAX_DURATION)
                        .setMinToMax(10,30)
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
//                    val videoPath = Utils.getPath(this, selectedVideo)
                    Log.d("Path", selectedVideo.toString())
//                    Log.d("Uri Path", Uri.parse(videoPath).toString())

//                    videoFile = File(videoPath)

//                    val wAndh = TrimmerUtils.getVideoWidthHeight(this,Uri.parse(selectedVideo.toString()))
//                    var width = wAndh[0]
//                    val height = wAndh[1]
//                    if(wAndh[0]>800){
//                        width/=2
//                        width/=2
//                        TrimVideo.activity(selectedVideo.toString())
//                            .setCompressOption(CompressOption(30,"1M",width,height))
//                            .setTrimType(TrimType.MIN_MAX_DURATION)
//                            .setMinToMax(10,30)
////                        .setDestination("/storage/emulated/0/DCIM/namastey")  //default output path /storage/emulated/0/DOWNLOADS
//                            .start(this)
//
//                    }else{

                        TrimVideo.activity(selectedVideo.toString())
//                            .setCompressOption(CompressOption(30,"400k",width,height))
                            .setTrimType(TrimType.MIN_MAX_DURATION)
                            .setMinToMax(10,30)
//                        .setDestination("/storage/emulated/0/DCIM/namastey")  //default output path /storage/emulated/0/DOWNLOADS
                            .start(this)
//                    }

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
        }
        // Need to use in future
//        else if (requestCode == Constants.RESULT_CODE_PICK_THUMBNAIL) {
//            if (data != null) {
//                val imageUri = data.getParcelableExtra<Uri>(ThumbyActivity.EXTRA_URI) as Uri
//                val location = data.getLongExtra(ThumbyActivity.EXTRA_THUMBNAIL_POSITION, 0)
//                val bitmap = ThumbyUtils.getBitmapAtFrame(this, imageUri, location, 250, 250)
//                Log.d("Image ", "done")
//
//                val pictureFile = Utils.bitmapToFile(this@AlbumDetailActivity, bitmap)
//
//                val intent = Intent(this@AlbumDetailActivity, PostVideoActivity::class.java)
//                intent.putExtra("videoFile", videoFile)
//                intent.putExtra("albumId", albumId)
//                intent.putExtra("thumbnailImage", pictureFile)
//                intent.putExtra("albumBean", albumBean)
//                openActivityForResult(intent, Constants.REQUEST_POST_VIDEO)
//            }
//        }
    }

    override fun onItemPostImageClick(position: Int, videoList: ArrayList<VideoBean>) {
        this.position = position
        val intent = Intent(this@AlbumDetailActivity, AlbumVideoActivity::class.java)
        intent.putExtra(Constants.VIDEO_LIST, videoList)
        intent.putExtra("position", position)
        openActivity(intent)
    }

    override fun onItemClick(userId: Long, position: Int) {
        if (position == 0 && !isSavedAlbum) {
            this.position = 0
            selectVideo()
        } else {
            this.position = position
            if (isSavedAlbum)
                albumViewModel.removePostVideo(userId,1)
            else
                albumViewModel.removePostVideo(userId,0)
        }
    }

    override fun onItemFollowingClick(dashboardBean: DashboardBean) {

    }

    override fun onSuccessDeletePost() {
        postList.removeAt(position)
        albumDetailAdapter.notifyItemRemoved(position)
        albumDetailAdapter.notifyItemRangeChanged(position, albumDetailAdapter.itemCount)
    }

    override fun onSuccessAlbumDelete(msg: String) {
        onBackPressed()
    }

    override fun onSuccessAlbumHide(msg: String) {
        Log.e("AlbumDetailActivity ", "onSuccessAlbumHide:: $msg")
        albumViewModel.getAlbumDetail(albumId)
    }

    override fun onSuccessResponse(albumBean: AlbumBean) {
        isEditAlbum = false
    }

    override fun onSuccessAlbumDetails(arrayList: ArrayList<AlbumBean>) {
        if (isShowMenu) {
            ivMore.visibility = View.VISIBLE
        }
        if (arrayList.size > 0) {
            albumBean = arrayList[0]
            postList = arrayList[0].post_video_list
            edtAlbumName.setText(arrayList[0].name)
            this.isHide = arrayList[0].is_hide
            if (arrayList[0].name == getString(R.string.saved)) {
                ivMore.visibility = View.GONE
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
                    this,
                    this,
                    fromEdit,
                    false,
                    isSavedAlbum
                )
            rvAlbumDetail.adapter = albumDetailAdapter
        }
    }

//    override fun cancelAction() {
//    }
//
//    override fun getResult(uri: Uri) {
//        trimmerView.visibility = View.GONE
//        Log.d("TrimVideo: ", "getResult : " + uri.toString())
//
//        videoFile = File(uri.path)
//        val intent = Intent(this@AlbumDetailActivity, PostVideoActivity::class.java)
//        intent.putExtra("videoFile", videoFile)
//        intent.putExtra("albumId", albumBean.id)
////                intent.putExtra("thumbnailImage", pictureFile)
//        intent.putExtra("albumBean", albumBean)
//        openActivityForResult(intent, Constants.REQUEST_POST_VIDEO)
//
//    }
//
//    override fun onError(message: String) {
//        trimmerView.visibility = View.GONE
//        Log.d("TrimVideo: ", "onError" + message)
//    }
//
//    override fun onTrimStarted() {
//        Log.d("TrimVideo: ", "onTrimStarted")
//    }

    override fun onDestroy() {
        super.onDestroy()
        if (::bottomSheetDialog.isInitialized)
            bottomSheetDialog.dismiss()
        albumViewModel.onDestroy()
    }

    override fun onSelectItemClick(userId: Long, position: Int) {
        val intent = Intent(this, ProfileViewActivity::class.java)
        intent.putExtra(Constants.USER_ID, userId)
        openActivity(intent)
    }

    override fun onSelectItemClick(userId: Long, position: Int, userProfileType: String) {
        TODO("Not yet implemented")
    }


//    fun onClickSave(view: View) {
//        videoTrimmer.onSaveClicked()
//    }
//
//    fun onClickCancel(view: View) {
//        trimmerView.visibility = View.GONE
//    }
}