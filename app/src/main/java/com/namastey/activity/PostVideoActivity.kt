package com.namastey.activity

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.View.OnTouchListener
import android.widget.CompoundButton
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityPostVideoBinding
import com.namastey.model.VideoBean
import com.namastey.uiView.PostVideoView
import com.namastey.utils.Constants
import com.namastey.utils.GlideLib
import com.namastey.viewModel.PostVideoViewModel
import kotlinx.android.synthetic.main.activity_post_video.*
import kotlinx.android.synthetic.main.dialog_bottom_pick.*
import java.io.File
import javax.inject.Inject


class PostVideoActivity : BaseActivity<ActivityPostVideoBinding>(), PostVideoView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var activityPostVideoBinding: ActivityPostVideoBinding
    private lateinit var postVideoViewModel: PostVideoViewModel
    private var videoFile: File? = null
    private var pictuareFile: File? = null
    private var albumId = 0L
    private val REQUEST_CODE = 101
    private val REQUEST_CODE_CAMERA = 102
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private var shareWith = 1
    private var commentOff = 0
    var isTouched = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        postVideoViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(PostVideoViewModel::class.java)
        activityPostVideoBinding = bindViewData()
        activityPostVideoBinding.viewModel = postVideoViewModel

        initData()
    }

    private fun initData() {

        if (intent.hasExtra("videoFile")) {
            videoFile = intent.getSerializableExtra("videoFile") as File?
            albumId = intent.getLongExtra("albumId", 0L)
            Log.d("TAG", videoFile!!.name.toString())
        }

        switchCommentOff.setOnTouchListener(OnTouchListener { view, motionEvent ->
            isTouched = true
            false
        })

        switchCommentOff.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isTouched) {
                isTouched = false
                commentOff = if (isChecked)
                    1
                else
                    0

            }
        })
    }
    override fun getViewModel() = postVideoViewModel

    override fun getLayoutId() = R.layout.activity_post_video

    override fun getBindingVariable() = BR.viewModel
    /**
     * success of post video description using this post_video_id call add media api
     */
    override fun onSuccessPostVideoDesc(videoBean: VideoBean) {
        Log.d("PostVideoActivity",videoBean.toString())
        postVideoViewModel.addMedia(videoBean.id,videoFile)
    }

    override fun onSuccessPostVideo(videoBean: VideoBean) {
        Log.d("PostVideoActivity",videoBean.toString())
        postVideoViewModel.addMediaCoverImage(videoBean.id,pictuareFile)
    }

    override fun onSuccessPostCoverImage(videoBean: VideoBean) {
        Log.d("PostVideoActivity",videoBean.toString())
        val intent = Intent()
        intent.putExtra("videoBean",videoBean)
        setResult(Activity.RESULT_OK,intent)
        super.onBackPressed()
    }


    override fun onBackPressed() {
        finishActivity()
    }

    fun onClickPostVideoBack(view: View) {
        onBackPressed()
    }

    fun onClickPostVideo(view: View) {
        when {
            TextUtils.isEmpty(edtVideoDesc.text.toString()) -> showMsg(getString(R.string.msg_empty_video_desc))
            pictuareFile == null -> showMsg(getString(R.string.msg_empty_cover_image))
            else -> postVideoViewModel.postVideoDesc(
                edtVideoDesc.text.toString().trim(),
                albumId,
                shareWith,
                commentOff
            )
        }
    }

    fun onClickShareWith(view: View) {
        val items =
            arrayOf<CharSequence>(
                getString(R.string.everyone),
                getString(R.string.friends),
                getString(R.string.no_one),
                getString(R.string.cancel)
            )

        val builder: AlertDialog.Builder = AlertDialog.Builder(this@PostVideoActivity)
        builder.setTitle(getString(R.string.shared_with))
        builder.setItems(items) { dialog, item ->
            when {
                items[item] == getString(R.string.everyone) -> {
                    tvShare.text = getString(R.string.everyone)
                    shareWith = 1
                }
                items[item] == getString(R.string.friends) -> {
                    tvShare.text = getString(R.string.friends)
                    shareWith = 2
                }
                items[item] == getString(R.string.no_one) -> {
                    tvShare.text = getString(R.string.no_one)
                    shareWith = 3
                }
                items[item] == getString(R.string.cancel) -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    private fun openGalleryForImage() {
        pictuareFile = File(
            Constants.FILE_PATH,
            System.currentTimeMillis().toString() + ".jpeg"
        )

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                try {
                    val selectedImage = data!!.data
                    val filePathColumn =
                        arrayOf(MediaStore.Images.Media.DATA)
                    val cursor: Cursor? = this@PostVideoActivity.contentResolver.query(
                        selectedImage!!,
                        filePathColumn, null, null, null
                    )
                    cursor!!.moveToFirst()

                    val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
                    val picturePath: String = cursor.getString(columnIndex)
                    cursor.close()

                    GlideLib.loadImage(this, ivSelectCover, picturePath)
                    Log.d("Image Path", "Image Path  is $picturePath")
                    pictuareFile = File(picturePath)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun selectImage() {
        bottomSheetDialog = BottomSheetDialog(this@PostVideoActivity, R.style.choose_photo)
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

        bottomSheetDialog.tvPhotoTake.setOnClickListener {
            bottomSheetDialog.dismiss()

        }
        bottomSheetDialog.tvPhotoChoose.setOnClickListener {
            bottomSheetDialog.dismiss()
            openGalleryForImage()
        }
        bottomSheetDialog.tvPhotoCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }


    override fun onDestroy() {
        super.onDestroy()
        if (::bottomSheetDialog.isInitialized)
            bottomSheetDialog.dismiss()
        postVideoViewModel.onDestroy()
    }

    fun onClickSelectImage(view: View) {
        selectImage()
    }


}
