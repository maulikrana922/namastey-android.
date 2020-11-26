package com.namastey.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonObject
import com.hendraanggrian.appcompat.widget.Mention
import com.hendraanggrian.appcompat.widget.MentionArrayAdapter
import com.hendraanggrian.appcompat.widget.SocialArrayAdapter
import com.namastey.BR
import com.namastey.BuildConfig
import com.namastey.R
import com.namastey.adapter.MentionListAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityPostVideoBinding
import com.namastey.listeners.OnMentionUserItemClick
import com.namastey.model.AlbumBean
import com.namastey.model.MentionListBean
import com.namastey.model.VideoBean
import com.namastey.uiView.PostVideoView
import com.namastey.utils.*
import com.namastey.viewModel.PostVideoViewModel
import io.ktor.client.*
import io.ktor.client.engine.android.*
import kotlinx.android.synthetic.main.activity_post_video.*
import kotlinx.android.synthetic.main.dialog_bottom_pick.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.buffer.android.thumby.ThumbyActivity
import org.buffer.android.thumby.util.ThumbyUtils
import java.io.File
import javax.inject.Inject


class PostVideoActivity : BaseActivity<ActivityPostVideoBinding>(), PostVideoView,
    OnMentionUserItemClick {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var activityPostVideoBinding: ActivityPostVideoBinding
    private lateinit var postVideoViewModel: PostVideoViewModel
    private lateinit var mentionListAdapter: MentionListAdapter
    private lateinit var defaultMentionAdapter: ArrayAdapter<Mention>
    private var videoFile: File? = null
    private var pictureFile: File? = null
    private var albumBean = AlbumBean()
    private var videoBean = VideoBean()
    private var albumList: ArrayList<AlbumBean> = ArrayList()
    private var isFromEditPost = false

    //    private val RESULT_CODE_PICK_THUMBNAIL = 104
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private var shareWith = 1
    private var commentOff = 0
    private var isTouched = false
    private var items = arrayOf<CharSequence>()
    private var lengthCount = 0

    override fun getViewModel() = postVideoViewModel

    override fun getLayoutId() = R.layout.activity_post_video

    override fun getBindingVariable() = BR.viewModel

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

        items = arrayOf<CharSequence>(
            getString(R.string.everyone),
            getString(R.string.friends),
            getString(R.string.no_one),
            getString(R.string.cancel)
        )
        if (intent.hasExtra("editPost")) {
            isFromEditPost = true
            videoBean = intent.getParcelableExtra<VideoBean>("videoBean") as VideoBean
            albumBean.name = videoBean.album_name
            albumBean.id = videoBean.album_id
            tvTitlePostVideo.text = getString(R.string.edit_post)
            edtVideoDesc.setText(videoBean.description)
            btnPostVideo.text = getString(R.string.update)
//            videoFile = File(Uri.parse(videoBean.video_url).path)
            GlideLib.loadImage(this, ivSelectCover, videoBean.cover_image_url)
            tvAlbumName.isEnabled = false

            val folder = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            pictureFile = File(folder, Constants.FILE_NAME)
            videoFile = File(folder, Constants.FILE_NAME_VIDEO)
            val uri = let {
                FileProvider.getUriForFile(
                    it,
                    "${BuildConfig.APPLICATION_ID}.provider",
                    pictureFile!!
                )
            }
            val uriVideo = let {
                FileProvider.getUriForFile(
                    it,
                    "${BuildConfig.APPLICATION_ID}.provider",
                    videoFile!!
                )
            }
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            postVideoViewModel.setIsLoading(true)
            downloadFile(this@PostVideoActivity, videoBean.cover_image_url, uri, false)
            downloadFile(this@PostVideoActivity, videoBean.video_url, uriVideo, true)

            switchCommentOff.isChecked = videoBean.is_comment == 1
            val share = videoBean.share_with
            val item = share - 1
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
            }
        } else {
            isFromEditPost = false
            if (intent.hasExtra("videoFile")) {
                albumBean = intent.getParcelableExtra<AlbumBean>("albumBean") as AlbumBean
                videoFile = intent.getSerializableExtra("videoFile") as File?
//            pictureFile = intent.getSerializableExtra("thumbnailImage") as File?
                Log.d("TAG", videoFile!!.name.toString())

//            val thumb: Bitmap =
//                BitmapFactory.decodeFile(pictureFile!!.absolutePath)
//            GlideLib.loadImageBitmap(this@PostVideoActivity, ivSelectCover, thumb)
                if (intent.hasExtra("fromAlbumDetail")) {
                    tvAlbumName.isEnabled = false
                } else {
                    postVideoViewModel.getAlbumList()
                }
            }
        }
        tvAlbumName.text = albumBean.name

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

        addCommentsTextChangeListener()
    }

    private fun downloadFile(context: Context, url: String, file: Uri, videoDownload: Boolean) {
        val ktor = HttpClient(Android)
        context.contentResolver.openOutputStream(file)?.let { outputStream ->
            CoroutineScope(Dispatchers.IO).launch {
                ktor.downloadFile(outputStream, url).collect {
                    withContext(Dispatchers.Main) {
                        when (it) {
                            is DownloadResult.Success -> {
                                if (videoDownload) {
                                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    postVideoViewModel.setIsLoading(false)
                                }
                            }
                            is DownloadResult.Error -> {
                                Toast.makeText(
                                    context,
                                    getString(R.string.slow_internet),
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            is DownloadResult.Progress -> {
                            }
                        }
                    }
                }
            }
        }
    }

    private fun addCommentsTextChangeListener() {

        defaultMentionAdapter = MentionArrayAdapter(this)
        /* postVideoViewModel.getMentionList(
             "Grishma"
         )*/

        defaultMentionAdapter.addAll(
            Mention("Mehul", "", "https://avatars1.githubusercontent.com/u/11507430?s=460&v=4"),
            Mention("Mona"),
            Mention("Chintu"),
            Mention("Pintu"),
            Mention("Dinesh")
        )
        edtVideoDesc.mentionColor = ContextCompat.getColor(this, R.color.colorBlack)
        edtVideoDesc.mentionAdapter = defaultMentionAdapter
        edtVideoDesc.setMentionTextChangedListener { _, text ->
            Log.e("mention", text.toString())
        }
    }

    /**
     * success of post video description using this post_video_id call add media api
     */
    override fun onSuccessPostVideoDesc(videoBean: VideoBean) {
        Log.d("PostVideoActivity", videoBean.toString())
        if (isFromEditPost) {
            this.videoBean.description = videoBean.description
            this.videoBean.share_with = videoBean.share_with
            this.videoBean.is_comment = videoBean.is_comment
            postVideoViewModel.addMediaCoverImage(videoBean.id, pictureFile)
        } else
            postVideoViewModel.addMedia(videoBean.id, videoFile)


    }

    override fun onSuccessPostVideo(videoBean: VideoBean) {
        Log.d("PostVideoActivity", videoBean.toString())
        postVideoViewModel.addMediaCoverImage(videoBean.id, pictureFile)
    }

    override fun onSuccessPostCoverImage(videoBean: VideoBean) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Log.d("PostVideoActivity", videoBean.toString())
        val intent = Intent()
        if (isFromEditPost) {
            this.videoBean.cover_image_url = videoBean.cover_image_url
            intent.putExtra("videoBean", this.videoBean)
        } else
            intent.putExtra("videoBean", videoBean)
        setResult(Activity.RESULT_OK, intent)
        super.onBackPressed()
    }

    /**
     * successfully got album list
     */
    override fun onSuccessAlbumList(arrayList: ArrayList<AlbumBean>) {
        albumList = arrayList
        if (albumList.any { albumBean -> albumBean.name == getString(R.string.saved) }) {
            albumList.remove(albumList.single { s -> s.name == getString(R.string.saved) })
        }
    }

    override fun onSuccessMention(mentionList: ArrayList<MentionListBean>) {
        if (mentionList.size > 0) {
            //rvMentionList.visibility = View.VISIBLE
            mentionListAdapter = MentionListAdapter(mentionList, this@PostVideoActivity, this)
            rvMentionList.adapter = mentionListAdapter
            Log.e("listSize", mentionList.size.toString())

            for (i in mentionList.indices) {
                defaultMentionAdapter.addAll(
                    Mention(
                        mentionList[i].username
                    )
                )
            }


        }

        // mentionList.clear()
    }

    override fun onBackPressed() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        finishActivity()
    }

    fun onClickPostVideoBack(view: View) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        onBackPressed()
    }

    fun onClickPostVideo(view: View) {
        when {
            TextUtils.isEmpty(edtVideoDesc.text.toString()) -> showMsg(getString(R.string.msg_empty_video_desc))
            pictureFile == null -> showMsg(getString(R.string.msg_empty_cover_image))
            else -> {
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )

                val jsonObject = JsonObject()
                jsonObject.addProperty(Constants.DESCRIPTION, edtVideoDesc.text.toString().trim())
                jsonObject.addProperty(Constants.ALBUM_ID, albumBean.id)
                jsonObject.addProperty(Constants.SHARE_WITH, shareWith)
                jsonObject.addProperty(Constants.IS_COMMENT, commentOff)

                if (isFromEditPost)
                    jsonObject.addProperty(Constants.VIDEO_POST_ID, videoBean.id)

                postVideoViewModel.postVideoDesc(jsonObject)
            }
        }
    }

    fun onClickShareWith(view: View) {
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
                    Utils.getCameraFile(this@PostVideoActivity)
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(takePictureIntent, Constants.REQUEST_CODE_CAMERA_IMAGE)

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
            if (requestCode == Constants.REQUEST_CODE_IMAGE) {
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
            } else if (requestCode == Constants.REQUEST_CODE_CAMERA_IMAGE) {
//                if (data != null) {
                val photoUri = FileProvider.getUriForFile(
                    this,
                    applicationContext.packageName + ".provider",
                    Utils.getCameraFile(this@PostVideoActivity)
                )

//                val bitmap = if(Build.VERSION.SDK_INT < 28) {
////                    MediaStore.Images.Media.getBitmap(
////                        this.contentResolver,
////                        photoUri
////                    )
//                    Utils.scaleBitmapDown(
//                        MediaStore.Images.Media.getBitmap(contentResolver, photoUri),
//                        1200
//                    )!!
//                } else {
//                    val source = ImageDecoder.createSource(this.contentResolver, photoUri)
//                    ImageDecoder.decodeBitmap(source)
//                }

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
//                }
            } else if (requestCode == Constants.RESULT_CODE_PICK_THUMBNAIL) {
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

    /* private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
         val bytes = ByteArrayOutputStream()
         inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
         val path =
             Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
         return Uri.parse(path)
     }
     private fun getRealPathFromURI(uri: Uri?): String? {
         var path = ""
         if (contentResolver != null) {
             val cursor =
                 contentResolver.query(uri, null, null, null, null)
             if (cursor != null) {
                 cursor.moveToFirst()
                 val idx = cursor.getColumnIndex(Images.ImageColumns.DATA)
                 path = cursor.getString(idx)
                 cursor.close()
             }
         }
         return path
     }*/

    private fun selectImage() {
        bottomSheetDialog = BottomSheetDialog(this@PostVideoActivity, R.style.dialogStyle)
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

            val videoUri = Uri.fromFile(videoFile)
            if (videoUri != null) {
                startActivityForResult(
                    ThumbyActivity.getStartIntent(this, videoUri),
                    Constants.RESULT_CODE_PICK_THUMBNAIL
                )
            }
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
            val position = arrayAdapter.getPosition(strName)
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

    override fun onMentionItemClick(userId: Long, position: Int, username: String) {
        lengthCount = 0

        val strDesc = StringBuilder().append(edtVideoDesc.text.toString() + username + " ")
        edtVideoDesc.setText(strDesc)
        edtVideoDesc.setSelection(edtVideoDesc.text!!.length);
        rvMentionList.visibility = View.GONE
    }

}

