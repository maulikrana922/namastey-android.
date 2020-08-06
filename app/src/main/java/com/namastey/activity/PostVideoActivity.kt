package com.namastey.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.View.OnTouchListener
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityPostVideoBinding
import com.namastey.model.AlbumBean
import com.namastey.model.VideoBean
import com.namastey.uiView.PostVideoView
import com.namastey.utils.Constants
import com.namastey.utils.GlideLib
import com.namastey.utils.Utils
import com.namastey.viewModel.PostVideoViewModel
import kotlinx.android.synthetic.main.activity_post_video.*
import kotlinx.android.synthetic.main.dialog_bottom_pick.*
import org.buffer.android.thumby.ThumbyActivity
import org.buffer.android.thumby.util.ThumbyUtils
import java.io.File
import javax.inject.Inject


class PostVideoActivity : BaseActivity<ActivityPostVideoBinding>(), PostVideoView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var activityPostVideoBinding: ActivityPostVideoBinding
    private lateinit var postVideoViewModel: PostVideoViewModel
    private var videoFile: File? = null
    private var pictureFile: File? = null
    private var albumBean = AlbumBean()
    private var albumList: ArrayList<AlbumBean> = ArrayList()
    private var items: Array<CharSequence> = arrayOf()
    private val REQUEST_CODE_IMAGE = 101

    //    private val REQUEST_CODE_CAMERA = 102
    private val RESULT_CODE_PICK_THUMBNAIL = 104
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private var shareWith = 1
    private var commentOff = 0
    private var isTouched = false

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
            albumBean = intent.getParcelableExtra<AlbumBean>("albumBean") as AlbumBean
            videoFile = intent.getSerializableExtra("videoFile") as File?
            pictureFile = intent.getSerializableExtra("thumbnailImage") as File?
            Log.d("TAG", videoFile!!.name.toString())

//            GlideLib.loadThumbnailImage(this@PostVideoActivity,ivSelectCover,Uri.fromFile(videoFile))
//            val thumb: Bitmap =
//                ThumbnailUtils.createVideoThumbnail(videoFile!!.path, MediaStore.Video.Thumbnails.MINI_KIND)

//            GlideLib.loadImageBitmap(this@PostVideoActivity,ivSelectCover,thumb)
//            val drawable: BitmapDrawable = ivSelectCover.getDrawable() as BitmapDrawable
//            val bitmap: Bitmap = drawable.bitmap
//
//            val tempUri = getImageUri(applicationContext, thumb)
//            pictureFile = File(getRealPathFromURI(tempUri))

            val thumb: Bitmap =
                BitmapFactory.decodeFile(pictureFile!!.absolutePath)
            GlideLib.loadImageBitmap(this@PostVideoActivity, ivSelectCover, thumb)
            tvAlbumName.text = albumBean.name
            postVideoViewModel.getAlbumList()
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
        Log.d("PostVideoActivity", videoBean.toString())
        postVideoViewModel.addMedia(videoBean.id, videoFile)
    }

    override fun onSuccessPostVideo(videoBean: VideoBean) {
        Log.d("PostVideoActivity", videoBean.toString())
        postVideoViewModel.addMediaCoverImage(videoBean.id, pictureFile)
    }

    override fun onSuccessPostCoverImage(videoBean: VideoBean) {
        Log.d("PostVideoActivity", videoBean.toString())
        val intent = Intent()
        intent.putExtra("videoBean", videoBean)
        setResult(Activity.RESULT_OK, intent)
        super.onBackPressed()
    }


    /**
     * successfully got album list
     */
    override fun onSuccessAlbumList(arrayList: ArrayList<AlbumBean>) {
        albumList = arrayList
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
            pictureFile == null -> showMsg(getString(R.string.msg_empty_cover_image))
            else -> postVideoViewModel.postVideoDesc(
                edtVideoDesc.text.toString().trim(),
                albumBean.id,
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
        pictureFile = File(
            Constants.FILE_PATH,
            System.currentTimeMillis().toString() + ".jpeg"
        )

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_IMAGE)
    }

    private fun capturePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Create the File where the photo should go
            try {

                val photoUri: Uri = FileProvider.getUriForFile(
                    this,
                    applicationContext.packageName + ".provider",
                    Utils.getCameraFile(this@PostVideoActivity)
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(takePictureIntent, Constants.PERMISSION_CAMERA)

                // Continue only if the File was successfully created
//                if (pictureFile != null) {

//                    var photoURI = FileProvider.getUriForFile(
//                        this@PostVideoActivity,
//                        BuildConfig.APPLICATION_ID + ".provider",
//                        pictureFile!!
//                    )
//
//                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//                    startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA)
//                }
            } catch (ex: Exception) {
                // Error occurred while creating the File
                showMsg(ex.localizedMessage)
            }
        }
//        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_IMAGE) {
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
                    pictureFile = Utils.saveBitmapToFile(File(picturePath))

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if (requestCode == Constants.PERMISSION_CAMERA) {
                if (data != null) {
                    val photoUri = FileProvider.getUriForFile(
                        this,
                        applicationContext.packageName + ".provider",
                        Utils.getCameraFile(this@PostVideoActivity)
                    )
                    val bitmap: Bitmap = Utils.scaleBitmapDown(
                        MediaStore.Images.Media.getBitmap(contentResolver, photoUri),
                        1200
                    )!!
                    GlideLib.loadImageBitmap(this, ivSelectCover, bitmap)
//                    GlideLib.loadImageBitmap(this, ivSelectCover, data.extras.get("data") as Bitmap)
//                    val photo = data.extras["data"] as Bitmap
//                    val tempUri = Utils.getImageUri(applicationContext, photo)
//                    pictureFile = File(Utils.getRealPathFromURI(this@PostVideoActivity,tempUri))
                    pictureFile = Utils.getCameraFile(this@PostVideoActivity)

//                    pictureFile = Utils.saveBitmapToFile(pictureFile!!)
                }
            } else if (requestCode == RESULT_CODE_PICK_THUMBNAIL) {
                if (data != null) {
                    val imageUri = data?.getParcelableExtra<Uri>(ThumbyActivity.EXTRA_URI) as Uri
                    val location = data.getLongExtra(ThumbyActivity.EXTRA_THUMBNAIL_POSITION, 0)
                    val bitmap = ThumbyUtils.getBitmapAtFrame(this, imageUri, location, 250, 250)
                    Log.d("Image ", "done")
                    GlideLib.loadImageBitmap(this@PostVideoActivity, ivSelectCover, bitmap)
//                    val tempUri = Utils.getImageUri(applicationContext, bitmap)
//                    pictureFile = File(Utils.getRealPathFromURI(this@PostVideoActivity,tempUri))
                    pictureFile = Utils.bitmapToFile(this@PostVideoActivity, bitmap)
                }
            }
        }
    }

//    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
//        val bytes = ByteArrayOutputStream()
//        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
//        val path =
//            Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
//        return Uri.parse(path)
//    }
//    private fun getRealPathFromURI(uri: Uri?): String? {
//        var path = ""
//        if (contentResolver != null) {
//            val cursor =
//                contentResolver.query(uri, null, null, null, null)
//            if (cursor != null) {
//                cursor.moveToFirst()
//                val idx = cursor.getColumnIndex(Images.ImageColumns.DATA)
//                path = cursor.getString(idx)
//                cursor.close()
//            }
//        }
//        return path
//    }

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
        bottomSheetDialog.tvFromVideo.visibility = View.VISIBLE

        bottomSheetDialog.tvPhotoTake.setOnClickListener {
            bottomSheetDialog.dismiss()
            if (isPermissionGrantedForCamera())
                capturePhoto()
        }
        bottomSheetDialog.tvPhotoChoose.setOnClickListener {
            bottomSheetDialog.dismiss()
            openGalleryForImage()
        }
        bottomSheetDialog.tvFromVideo.setOnClickListener {
            bottomSheetDialog.dismiss()
            var videoUri = Uri.fromFile(videoFile)
            startActivityForResult(
                ThumbyActivity.getStartIntent(this, videoUri),
                RESULT_CODE_PICK_THUMBNAIL
            )
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

    /**
     *  click on album name open list of album. select album to add video
     */
    fun onClickSelectAlbum(view: View) {
        val albumBuilder =
            AlertDialog.Builder(this@PostVideoActivity)
        albumBuilder.setTitle(getString(R.string.select_album))

        val arrayAdapter = ArrayAdapter<String>(
            this@PostVideoActivity,
            android.R.layout.simple_list_item_1
        )

        for (album in albumList) {
            arrayAdapter.add(album.name)
        }

        albumBuilder.setNegativeButton(
            getString(R.string.cancel)
        ) { dialog, which -> dialog.dismiss() }

        albumBuilder.setAdapter(
            arrayAdapter
        ) { dialog, which ->
            val strName = arrayAdapter.getItem(which)
            tvAlbumName.text = strName
            var position = arrayAdapter.getPosition(strName)
            albumBean = albumList[position]
            Log.d("albumBean", albumBean.name + " " + albumBean.id)

        }
        albumBuilder.show()
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
                            if (isPermissionGrantedForCamera())
                                capturePhoto()
                        }
                    } else {
                        if (isPermissionGrantedForCamera())
                            capturePhoto()
                    }
                }
            }
        }
    }
}

