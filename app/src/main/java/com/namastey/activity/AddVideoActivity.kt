package com.namastey.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
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
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.gowtham.library.utils.TrimType
import com.gowtham.library.utils.TrimVideo
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityAddVideoBinding
import com.namastey.roomDB.entity.User
import com.namastey.uiView.AddVideoView
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import com.namastey.utils.Utils
import com.namastey.viewModel.AddVideoViewModel
import kotlinx.android.synthetic.main.activity_add_video.*
import kotlinx.android.synthetic.main.activity_profile_view.*
import kotlinx.android.synthetic.main.dialog_bottom_pick.*
import java.io.File
import java.util.*
import javax.inject.Inject

class AddVideoActivity : BaseActivity<ActivityAddVideoBinding>(), AddVideoView {

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var binding: ActivityAddVideoBinding
    private lateinit var addVideoViewModel: AddVideoViewModel
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private var videoFile: File? = null
    private var isVideoUpload = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        setupViewModel()

        initData()
    }

    private fun setupViewModel() {
        addVideoViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(AddVideoViewModel::class.java)
        addVideoViewModel.setViewInterface(this)
        binding = bindViewData()
        binding.viewModel = addVideoViewModel
    }

    override fun onSuccessCreateOrUpdate(user: User) {
        if (user.token.isNotEmpty())
            sessionManager.setAccessToken(user.token)
//        sessionManager.setUserPhone(user.mobile)

        if (sessionManager.getIntegerValue(Constants.KEY_IS_INVITED) == 1) {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            openActivity(intent)
        } else {
            openActivity(this, NotInvitedActivity())
        }

    }

    override fun getViewModel() = addVideoViewModel

    override fun getLayoutId() = R.layout.activity_add_video

    override fun getBindingVariable() = BR.viewModel

    private fun initData() {
    }

    private fun editProfileApiCall() {

        val jsonObject = JsonObject()

        jsonObject.addProperty(
            Constants.USERNAME,
            sessionManager.getStringValue(Constants.KEY_MAIN_USER_NAME)
        )

        jsonObject.addProperty(
            Constants.CASUAL_NAME,
            sessionManager.getStringValue(Constants.KEY_CASUAL_NAME)
        )

        jsonObject.addProperty(
            Constants.DATE_OF_BIRTH,
            Utils.convertDateToAPIFormate(
                sessionManager.getStringValue(Constants.KEY_BIRTH_DAY)
            )
        )

        jsonObject.addProperty(Constants.GENDER, sessionManager.getUserGender())

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
        jsonObject.addProperty(Constants.INTERESTED_IN_GENDER, sessionManager.getInterestIn())

        val jsonArrayInterest = JsonArray()
        for (selectInterest in sessionManager.getChooseInterestList()) {
            jsonArrayInterest.add(JsonPrimitive(selectInterest))
        }
        jsonObject.add(
            Constants.TAGS,
            jsonArrayInterest
        )

        val jsonArray = JsonArray()
        for (selectVideoIdList in sessionManager.getVideoLanguageList()) {
            jsonArray.add(JsonPrimitive(selectVideoIdList))
        }
        jsonObject.add(
            Constants.LANGUAGE,
            jsonArray
        )


//        val categoryIdList: ArrayList<Int> = ArrayList()
//        for (category in sessionManager.getCategoryList()) {
//            categoryIdList.add(category.id)
//        }
//        jsonObject.addProperty(
//            Constants.CATEGORY_ID,
//            categoryIdList.joinToString()
//        )
//        val socialAccountId = ArrayList<Long>()
//        for (data in socialAccountList) {
//            socialAccountId.add(data.id)
//        }
//        jsonObject.addProperty(Constants.SOCIAL_ACCOUNTS, socialAccountId.joinToString())
        // if (sessionManager.getEducationBean().course.isNotEmpty()) {
        jsonObject.addProperty(
            Constants.EDUCATION,
            sessionManager.getStringValue(Constants.KEY_EDUCATION)
        )
        // }

//        if (sessionManager.getJobBean().title.isNotEmpty()) {
        jsonObject.addProperty(Constants.JOBS, sessionManager.getStringValue(Constants.KEY_JOB))
//        }
        val androidID = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)

        jsonObject.addProperty(Constants.DEVICE_ID, androidID)
        jsonObject.addProperty(Constants.DEVICE_TYPE, Constants.ANDROID)

        Log.e("ProfileRequest", "CreateProfile Request:\t $jsonObject")

        addVideoViewModel.createUser(jsonObject)
    }


    override fun onBackPressed() {
        finishActivity()
    }

    fun onClickBack(view: View) {
        onBackPressed()
    }

    fun onClickContinue(view: View) {
        if (isVideoUpload) {
            editProfileApiCall()
        } else {
            showMsg(R.string.msg_add_video)
        }
    }

    fun onClickAddVideo(view: View) {
        //openActivity(this@AddVideoActivity, PostVideoActivity())
        //selectVideo()
        isReadWritePermissionGrantedVide()
    }

    fun onClickSkip(view: View) {
        editProfileApiCall()
    }

    private fun selectVideo() {
        bottomSheetDialog = BottomSheetDialog(this@AddVideoActivity, R.style.dialogStyle)
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
            isReadWritePermissionGrantedVide()
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

    private fun isReadWritePermissionGrantedVide() {
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
        startActivityForResult(intent, Constants.REQUEST_VIDEO_SELECT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("Request Code : ", requestCode.toString())

        if (requestCode === TrimVideo.VIDEO_TRIMMER_REQ_CODE && data != null) {
            val uri = Uri.parse(TrimVideo.getTrimmedVideoPath(data))
            Log.e("Trimmed video ", "Trimmed path:: $uri")

            videoFile = File(uri.path)
            val intent = Intent(this@AddVideoActivity, PostVideoActivity::class.java)
            intent.putExtra("videoFile", videoFile)
            openActivityForResult(intent, Constants.REQUEST_POST_VIDEO)

        } else if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_POST_VIDEO) {
            if (data != null) {
                videoFile = data.getSerializableExtra("videoFile") as File?
                ivAddVideo.visibility = View.INVISIBLE
                videoView.visibility = View.VISIBLE

                videoView.setVideoURI(Uri.parse(videoFile!!.path))
                videoView.start()
                isVideoUpload = true
                videoView.setOnPreparedListener { mp -> mp.isLooping = true }
            }
        } else if (resultCode == Activity.RESULT_OK && (requestCode == Constants.REQUEST_VIDEO_SELECT || requestCode == Constants.PERMISSION_CAMERA)) {

            if (data != null) {
                val selectedVideo = data.data

                if (selectedVideo != null) {
                    Log.d("Path", selectedVideo.toString())
                    TrimVideo.activity(selectedVideo.toString())
                        .setTrimType(TrimType.MIN_MAX_DURATION)
                        .setMinToMax(10, 30)
                        .start(this)
                }
            }
        }
    }

}