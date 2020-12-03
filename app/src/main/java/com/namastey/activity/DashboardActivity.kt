package com.namastey.activity

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hendraanggrian.appcompat.widget.Mention
import com.hendraanggrian.appcompat.widget.MentionArrayAdapter
import com.namastey.BR
import com.namastey.BuildConfig
import com.namastey.R
import com.namastey.adapter.CategoryAdapter
import com.namastey.adapter.CommentAdapter
import com.namastey.adapter.FeedAdapter
import com.namastey.adapter.MentionListAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityDashboardBinding
import com.namastey.fragment.SignUpFragment
import com.namastey.listeners.OnFeedItemClick
import com.namastey.listeners.OnMentionUserItemClick
import com.namastey.listeners.OnSelectUserItemClick
import com.namastey.model.CategoryBean
import com.namastey.model.CommentBean
import com.namastey.model.DashboardBean
import com.namastey.model.MentionListBean
import com.namastey.uiView.DashboardView
import com.namastey.utils.*
import com.namastey.viewModel.DashboardViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.dialog_bottom_pick.*
import kotlinx.android.synthetic.main.dialog_bottom_post_comment.*
import kotlinx.android.synthetic.main.dialog_bottom_share_feed.*
import kotlinx.android.synthetic.main.dialog_common_alert.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


class DashboardActivity : BaseActivity<ActivityDashboardBinding>(), DashboardView, OnFeedItemClick,
    OnSelectUserItemClick, OnMentionUserItemClick {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var activityDashboardBinding: ActivityDashboardBinding
    private lateinit var dashboardViewModel: DashboardViewModel
    private var feedList: ArrayList<DashboardBean> = ArrayList()
    private var categoryBeanList: ArrayList<CategoryBean> = ArrayList()
    private lateinit var feedAdapter: FeedAdapter
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var mentionListAdapter: MentionListAdapter
    private lateinit var mentionArrayAdapter: ArrayAdapter<Mention>
    private val PERMISSION_REQUEST_CODE = 101
    private lateinit var bottomSheetDialogShare: BottomSheetDialog
    private lateinit var bottomSheetDialogComment: BottomSheetDialog
    private var colorDrawableBackground = ColorDrawable(Color.RED)
    private lateinit var deleteIcon: Drawable
    private var position = -1
    private var commentCount = -1
    private var isUpdateComment = false
    private var fileUrl = ""
    private var postId = 0L

    override fun getViewModel() = dashboardViewModel

    override fun getLayoutId() = R.layout.activity_dashboard

    override fun getBindingVariable() = BR.viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        dashboardViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(DashboardViewModel::class.java)
        activityDashboardBinding = bindViewData()
        activityDashboardBinding.viewModel = dashboardViewModel

        initData()
    }

    private fun initData() {
        sessionManager.setLoginUser(true)

        Utils.rectangleShapeGradient(
            tvDiscover, intArrayOf(
                ContextCompat.getColor(this, R.color.color_spotify),
                ContextCompat.getColor(this, R.color.color_instagram)
            )
        )
        if (sessionManager.getUserGender() == Constants.Gender.female.name)
            ivUser.setImageResource(R.drawable.ic_female_user)
        else
            ivUser.setImageResource(R.drawable.ic_top_profile)

        dashboardViewModel.getCategoryList()
//        setDashboardList()

        setupPermissions()

        dashboardViewModel.getFeedList(0)

        mentionArrayAdapter = MentionArrayAdapter(this)

    }

    private fun setupPermissions() {
        val locationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )


        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(getString(R.string.location_permission_message))
                    .setTitle(getString(R.string.permission_required))

                builder.setPositiveButton(
                    getString(R.string.ok)
                ) { dialog, id ->
                    makeRequest()
                }

                val dialog = builder.create()
                dialog.show()
            } else
                makeRequest()
        }

    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_REQUEST_CODE
        )
    }

    /**
     * Display share option if user login
     */
    private fun openShareOptionDialog(dashboardBean: DashboardBean) {
        bottomSheetDialogShare = BottomSheetDialog(this@DashboardActivity, R.style.dialogStyle)
        bottomSheetDialogShare.setContentView(
            layoutInflater.inflate(
                R.layout.dialog_bottom_share_feed,
                null
            )
        )
        bottomSheetDialogShare.window?.setBackgroundDrawableResource(android.R.color.transparent)
        bottomSheetDialogShare.window?.attributes?.windowAnimations = R.style.DialogAnimation
        bottomSheetDialogShare.setCancelable(true)

        bottomSheetDialogShare.tvShareCancel.setOnClickListener {
            bottomSheetDialogShare.dismiss()
        }

        // Share on Twitter app if install otherwise web link
        bottomSheetDialogShare.ivShareTwitter.setOnClickListener {
            val tweetUrl =
                StringBuilder("https://twitter.com/intent/tweet?text=")
            tweetUrl.append(dashboardBean.video_url)
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(tweetUrl.toString())
            )
            val matches: List<ResolveInfo> =
                packageManager.queryIntentActivities(intent, 0)
            for (info in matches) {
                if (info.activityInfo.packageName.toLowerCase(Locale.ROOT)
                        .startsWith("com.twitter")
                ) {
                    intent.setPackage(info.activityInfo.packageName)
                }
            }
            startActivity(intent)
        }

        bottomSheetDialogShare.ivShareFacebook.setOnClickListener {
            var facebookAppFound = false
            var shareIntent =
                Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, dashboardBean.video_url)
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(dashboardBean.video_url))

            val pm: PackageManager = packageManager
            val activityList: List<ResolveInfo> = pm.queryIntentActivities(shareIntent, 0)
            for (app in activityList) {
                if (app.activityInfo.packageName.contains("com.facebook.katana")) {
                    val activityInfo: ActivityInfo = app.activityInfo
                    val name =
                        ComponentName(activityInfo.applicationInfo.packageName, activityInfo.name)
                    shareIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                    shareIntent.component = name
                    facebookAppFound = true
                    break
                }
            }
            if (!facebookAppFound) {
                val sharerUrl =
                    "https://www.facebook.com/sharer/sharer.php?u=${dashboardBean.video_url}"
                shareIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(sharerUrl)
                )
            }
            startActivity(shareIntent)
        }

        bottomSheetDialogShare.ivShareInstagram.setOnClickListener {
            var intent =
                packageManager.getLaunchIntentForPackage("com.instagram.android")
            if (intent != null) {
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.setPackage("com.instagram.android")
                try {

                    val folder = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                    val fileName = "share_video.mp4"
                    val file = File(folder, fileName)
                    val uri = let {
                        FileProvider.getUriForFile(
                            it,
                            "${BuildConfig.APPLICATION_ID}.provider",
                            file
                        )
                    }

                    fileUrl = dashboardBean.video_url

                    downloadFile(this@DashboardActivity, fileUrl, uri)

                } catch (e: Exception) {
                    Log.e("ERROR", e.printStackTrace().toString())
                }

            } else {
                intent = Intent(Intent.ACTION_VIEW)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.data = Uri.parse("market://details?id=" + "com.instagram.android")
                startActivity(intent)
            }
        }
        bottomSheetDialogShare.ivShareWhatssapp.setOnClickListener {
            try {
                val pm: PackageManager = packageManager
                pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                sendIntent.type = "*/*"
                sendIntent.type = "text/plain"
                sendIntent.setPackage("com.whatsapp")

//                sendIntent.putExtra(
//                    Intent.EXTRA_STREAM,
//                    Uri.parse(dashboardBean.video_url)

                sendIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    Uri.parse(dashboardBean.video_url)
                )
                startActivity(sendIntent)
            } catch (e: PackageManager.NameNotFoundException) {
                Toast.makeText(
                    this@DashboardActivity,
                    getString(R.string.whatsapp_not_install_message),
                    Toast.LENGTH_SHORT
                ).show()
                e.printStackTrace()
            }
        }

        bottomSheetDialogShare.ivShareOther.setOnClickListener {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            sendIntent.putExtra(
                Intent.EXTRA_TEXT, dashboardBean.video_url
            )
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        }
        bottomSheetDialogShare.ivShareSave.setOnClickListener {
            bottomSheetDialogShare.dismiss()
            dashboardViewModel.savePost(dashboardBean.id)
        }
        bottomSheetDialogShare.ivShareReport.setOnClickListener {
            displayReportUserDialog(dashboardBean)
        }
        bottomSheetDialogShare.tvShareReport.setOnClickListener {
            displayReportUserDialog(dashboardBean)
        }

        bottomSheetDialogShare.tvShareBlock.setOnClickListener {
            displayBlockUserDialog(dashboardBean)
        }
        bottomSheetDialogShare.ivShareBlock.setOnClickListener {
            displayBlockUserDialog(dashboardBean)
        }

        bottomSheetDialogShare.show()
    }

    /* fun download(link: String, path: String) {
         URL(link).openStream().use { input ->
             FileOutputStream(File(path)).use { output ->
                 input.copyTo(output)
             }
         }
     }

     fun downloadFile(uRl: String) {
         val direct = File(getExternalFilesDir(null), "/namastey")

         if (!direct.exists()) {
             direct.mkdirs()
         }

         val mgr = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

         val downloadUri = Uri.parse(uRl)
         val request = DownloadManager.Request(
             downloadUri
         )
 //    Environment.getExternalStorageDirectory()
 //        .toString() + File.separator + "temp" + File.separator + "Videos" + File.separator
         request.setAllowedNetworkTypes(
             DownloadManager.Request.NETWORK_WIFI or
                     DownloadManager.Request.NETWORK_MOBILE
         )
             .setAllowedOverRoaming(false).setTitle("namastey") //Download Manager Title
             .setDescription("Downloading...") //Download Manager description
 //            .setDestinationUri(Uri.parse("file://" + Environment.DIRECTORY_PICTURES + "/myfile.mp4"));

             .setDestinationInExternalPublicDir(
                 Constants.FILE_PATH,
                 "temp5.mp4"
             )

         mgr.enqueue(request)

     }*/

    /**
     * Display dialog of report user
     */
    private fun displayReportUserDialog(dashboardBean: DashboardBean) {
        object : CustomCommonAlertDialog(
            this@DashboardActivity,
            dashboardBean.username,
            getString(R.string.msg_report_user),
            dashboardBean.profile_url,
            getString(R.string.report_user),
            resources.getString(R.string.no_thanks)
        ) {
            override fun onBtnClick(id: Int) {
                when (id) {
                    btnAlertOk.id -> {
                        bottomSheetDialogShare.dismiss()
                        showReportTypeDialog(dashboardBean.user_id)
                    }
                }
            }
        }.show()

    }

    /**
     * Display dialog with option (Reason)
     */
    private fun showReportTypeDialog(reportUserId: Long) {
        val bottomSheetReport: BottomSheetDialog =
            BottomSheetDialog(this@DashboardActivity, R.style.dialogStyle)
        bottomSheetReport.setContentView(
            layoutInflater.inflate(
                R.layout.dialog_bottom_pick,
                null
            )
        )
        bottomSheetReport.window?.setBackgroundDrawableResource(android.R.color.transparent)
        bottomSheetReport.window?.attributes?.windowAnimations = R.style.DialogAnimation
        bottomSheetReport.setCancelable(false)

        bottomSheetReport.tvPhotoTake.text = getString(R.string.its_spam)
        bottomSheetReport.tvPhotoTake.setTextColor(Color.RED)
        bottomSheetReport.tvPhotoChoose.text = getString(R.string.its_inappropriate)
        bottomSheetReport.tvPhotoChoose.setTextColor(Color.RED)
        bottomSheetReport.tvPhotoCancel.setTextColor(Color.BLUE)

        bottomSheetReport.tvPhotoTake.setOnClickListener {
            bottomSheetReport.dismiss()
            dashboardViewModel.reportUser(reportUserId, getString(R.string.its_spam))
        }
        bottomSheetReport.tvPhotoChoose.setOnClickListener {
            bottomSheetReport.dismiss()
            dashboardViewModel.reportUser(reportUserId, getString(R.string.its_inappropriate))
        }
        bottomSheetReport.tvPhotoCancel.setOnClickListener {
            bottomSheetReport.dismiss()
        }
        bottomSheetReport.show()
    }

    private fun displayBlockUserDialog(dashboardBean: DashboardBean) {
        object : CustomCommonAlertDialog(
            this@DashboardActivity,
            dashboardBean.username,
            getString(R.string.msg_block_user),
            dashboardBean.profile_url,
            getString(R.string.block_user),
            resources.getString(R.string.no_thanks)
        ) {
            override fun onBtnClick(id: Int) {
                when (id) {
                    btnAlertOk.id -> {
                        bottomSheetDialogShare.dismiss()
                        dashboardViewModel.blockUser(dashboardBean.user_id, 1)
                    }
                }
            }
        }.show()

    }

    /**
     * Success of get category list
     */
    override fun onSuccessCategory(categoryBeanList: ArrayList<CategoryBean>) {
        this.categoryBeanList = categoryBeanList
        tvDiscover.visibility = View.VISIBLE
        val categoryAdapter = CategoryAdapter(this.categoryBeanList, this)
        val horizontalLayout = LinearLayoutManager(
            this@DashboardActivity,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        rvCategory.layoutManager = horizontalLayout
        rvCategory.adapter = categoryAdapter

//        setDashboardList()

    }

    override fun onSuccessFeed(dashboardList: ArrayList<DashboardBean>) {
        feedList = dashboardList
        feedAdapter = FeedAdapter(feedList, this@DashboardActivity, this, sessionManager)
        viewpagerFeed.adapter = feedAdapter
    }


    // Temp open this activity
    fun onClickUser(view: View) {
        openActivity(this, ProfileActivity())
    }

    fun onClickInbox(view: View) {
        if (sessionManager.isGuestUser()) {
            addFragment(
                SignUpFragment.getInstance(
                    true
                ),
                Constants.SIGNUP_FRAGMENT
            )
        } else {
            val intent = Intent(this@DashboardActivity, MatchesActivity::class.java)
            intent.putExtra("onClickMatches", true)
            openActivity(intent)
        }
    }

    override fun onDestroy() {
        dashboardViewModel.onDestroy()
        if (::bottomSheetDialogShare.isInitialized)
            bottomSheetDialogShare.dismiss()

        if (::bottomSheetDialogComment.isInitialized)
            bottomSheetDialogComment.dismiss()

        super.onDestroy()
    }

    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        super.onActivityReenter(resultCode, data)
        if (resultCode == Constants.REQUEST_CODE && data != null) {
            when {
                data.hasExtra("fromSubCategory") -> {
                    with(dashboardViewModel) { getFeedList(data.getIntExtra("subCategoryId", 0)) }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.FILTER_OK) {
            if (data != null && data.hasExtra("fromSubCategory")) {
                supportFragmentManager.popBackStack()
                with(dashboardViewModel) { getFeedList(data.getIntExtra("subCategoryId", 0)) }
            } else {
                supportFragmentManager.popBackStack()
            }
        }

    }

    private fun downloadFile(context: Context, url: String, file: Uri) {
        val ktor = HttpClient(Android)
        dashboardViewModel.setDownloading(true)
        bottomSheetDialogShare.progress_bar.visibility = View.VISIBLE
        bottomSheetDialogShare.ivShareInstagram.visibility = View.INVISIBLE
        context.contentResolver.openOutputStream(file)?.let { outputStream ->
            CoroutineScope(Dispatchers.IO).launch {
                ktor.downloadFile(outputStream, url).collect {
                    withContext(Dispatchers.Main) {
                        when (it) {
                            is DownloadResult.Success -> {
                                dashboardViewModel.setDownloading(false)
                                bottomSheetDialogShare.progress_bar.visibility = View.GONE
                                bottomSheetDialogShare.ivShareInstagram.visibility = View.VISIBLE
                                if (::bottomSheetDialogShare.isInitialized)
                                    bottomSheetDialogShare.dismiss()
                                bottomSheetDialogShare.progress_bar.progress = 0
                                openInstagram(file)
                            }
                            is DownloadResult.Error -> {
                                bottomSheetDialogShare.progress_bar.visibility = View.GONE
                                bottomSheetDialogShare.ivShareInstagram.visibility = View.VISIBLE
                                dashboardViewModel.setDownloading(false)
                                Toast.makeText(
                                    context,
                                    "Error while downloading file",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            is DownloadResult.Progress -> {
                                bottomSheetDialogShare.progress_bar.progress = it.progress
                            }
                        }
                    }
                }
            }
        }
    }

    private fun openInstagram(uri: Uri) {
        let { context ->
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.setPackage("com.instagram.android")
            shareIntent.type = "video/*"

            shareIntent.putExtra(
                Intent.EXTRA_STREAM,
                uri
            )
            startActivity(shareIntent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {
                        val builder = AlertDialog.Builder(this)
                        builder.setMessage(getString(R.string.location_permission_message))
                            .setTitle(getString(R.string.permission_required))

                        builder.setPositiveButton(
                            getString(R.string.ok)
                        ) { dialog, id ->
                            ActivityCompat.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                PERMISSION_REQUEST_CODE
                            )
                        }

                        val dialog = builder.create()
                        dialog.show()
                    } else {
                        val builder = AlertDialog.Builder(this)
                        builder.setMessage(getString(R.string.permission_denied_message))
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
                    }

                }
            }
        }
    }


    /**
     * user_profile_type = 1 means private
     * and user_profile_type = 0 means public
     */
    override fun onClickFollow(position: Int, dashboardBean: DashboardBean, isFollow: Int) {
        if (sessionManager.isGuestUser() && dashboardBean.user_profile_type == 1) {
            addFragment(
                SignUpFragment.getInstance(
                    true
                ),
                Constants.SIGNUP_FRAGMENT
            )
        } else {
            this.position = position
            dashboardViewModel.followUser(dashboardBean.user_id, isFollow)
        }
    }

    override fun onItemClick(dashboardBean: DashboardBean) {
        if (sessionManager.isGuestUser() && dashboardBean.user_profile_type == 1) {
            addFragment(
                SignUpFragment.getInstance(
                    true
                ),
                Constants.SIGNUP_FRAGMENT
            )
        } else {
            openShareOptionDialog(dashboardBean)
        }
    }

    override fun onProfileLikeClick(position: Int, dashboardBean: DashboardBean, isLike: Int) {
        if (sessionManager.isGuestUser()) {
            addFragment(
                SignUpFragment.getInstance(
                    true
                ),
                Constants.SIGNUP_FRAGMENT
            )
        } else {
            this.position = position
            dashboardViewModel.likeUserProfile(dashboardBean.user_id, isLike)
        }
    }

    /**
     * Click on commnet count display list of comment and add comment dialog
     */
    override fun onCommentClick(position: Int, postId: Long) {
        this.position = position
        this.postId = postId
        bottomSheetDialogComment = BottomSheetDialog(this@DashboardActivity, R.style.dialogStyle)
        bottomSheetDialogComment.setContentView(
            layoutInflater.inflate(
                R.layout.dialog_bottom_post_comment,
                null
            )
        )
        bottomSheetDialogComment.window?.setBackgroundDrawableResource(android.R.color.transparent)
        bottomSheetDialogComment.window?.attributes?.windowAnimations = R.style.DialogAnimation
        bottomSheetDialogComment.setCancelable(true)
        deleteIcon = ContextCompat.getDrawable(this, R.drawable.ic_delete_white)!!

        dashboardViewModel.getCommentList(postId)

        bottomSheetDialogComment.ivCommentAdd.setOnClickListener {
            if (sessionManager.isGuestUser()) {
                bottomSheetDialogComment.dismiss()
                addFragment(
                    SignUpFragment.getInstance(
                        true
                    ),
                    Constants.SIGNUP_FRAGMENT
                )
            } else {
                if (bottomSheetDialogComment.edtComment.text.toString().isNotBlank()) {
                    dashboardViewModel.addComment(
                        postId,
                        bottomSheetDialogComment.edtComment.text.toString()
                    )
                }
            }
        }

        bottomSheetDialogComment.edtComment.setOnClickListener {
            if (sessionManager.isGuestUser()) {
                bottomSheetDialogComment.dismiss()
                addFragment(
                    SignUpFragment.getInstance(
                        true
                    ),
                    Constants.SIGNUP_FRAGMENT
                )
            }
        }

        addCommentsTextChangeListener()

        bottomSheetDialogComment.ivCloseComment.setOnClickListener {
            bottomSheetDialogComment.dismiss()
        }

        bottomSheetDialogComment.setOnDismissListener {
            if (isUpdateComment) {
                isUpdateComment = false
                val dashboardBean = feedList[position]
                dashboardBean.comments = commentCount
                feedAdapter.notifyItemChanged(position)
            }
        }

        bottomSheetDialogComment.show()
    }

    private fun addCommentsTextChangeListener() {

        /* bottomSheetDialogComment.edtComment.addTextChangedListener(object : TextWatcher {
             override fun beforeTextChanged(
                 s: CharSequence?,
                 start: Int,
                 count: Int,
                 after: Int
             ) {
             }

             override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                 *//* if (s.length > 2) {
                     dashboardViewModel.getMentionList(s.toString())
                 }

                 if (s.length == 0) {
                     bottomSheetDialogComment.rvPostComment.visibility = View.VISIBLE
                 }*//*
            }

            *//* override fun afterTextChanged(editable: Editable?) {
             }*//*

            override fun afterTextChanged(editable: Editable) {
                if (sessionManager.isGuestUser()) {
                    bottomSheetDialogComment.dismiss()
                    addFragment(
                        SignUpFragment.getInstance(
                            true
                        ),
                        Constants.SIGNUP_FRAGMENT
                    )
                } else {
                    val text = editable.toString()
                    val p: Pattern = Pattern.compile("[@][a-zA-Z0-9-.]+")
                    val m: Matcher = p.matcher(text)
                    val cursorPosition: Int = bottomSheetDialogComment.edtComment.selectionStart
                    while (m.find()) {
                        if (cursorPosition >= m.start() && cursorPosition <= m.end()) {
                            val s = m.start() + 1
                            val e = m.end()
                            dashboardViewModel.getMentionList(text.substring(s, e))
                        }
                    }
                }
            }
        })

*/
        mentionArrayAdapter.clear()
        dashboardViewModel.getMentionList("")
        var strMention = ""
        bottomSheetDialogComment.edtComment.showSoftInputOnFocus
        bottomSheetDialogComment.edtComment.mentionColor =
            ContextCompat.getColor(this, R.color.colorBlack)
        bottomSheetDialogComment.edtComment.mentionAdapter = mentionArrayAdapter
        bottomSheetDialogComment.edtComment.setMentionTextChangedListener { view, text ->
            Log.e("mention", text.toString())
            mentionArrayAdapter.notifyDataSetChanged()
            strMention = text.toString()
            if (text.length==1) {
                bottomSheetDialogComment.lvMentionList.visibility = View.GONE
            } else bottomSheetDialogComment.lvMentionList.visibility = View.VISIBLE

        }

        bottomSheetDialogComment.lvMentionList.adapter = mentionArrayAdapter


        bottomSheetDialogComment.lvMentionList.setOnItemClickListener { _, _, i, l ->
            val strName = bottomSheetDialogComment.edtComment.text.toString().replace(
                strMention, "${
                    mentionArrayAdapter.getItem(
                        i
                    ).toString()
                }"
            )
            bottomSheetDialogComment.edtComment.setText(strName)
            bottomSheetDialogComment.edtComment.setSelection(bottomSheetDialogComment.edtComment.text!!.length)
            bottomSheetDialogComment.lvMentionList.visibility = View.GONE
        }

    }

    override fun onUserProfileClick(dashboardBean: DashboardBean) {
        if (sessionManager.isGuestUser()) {
            addFragment(
                SignUpFragment.getInstance(
                    true
                ),
                Constants.SIGNUP_FRAGMENT
            )
        } else {
            val intent = Intent(this@DashboardActivity, ProfileViewActivity::class.java)
            intent.putExtra(Constants.USER_ID, dashboardBean.user_id)
            openActivity(intent)
        }
    }

    override fun onFeedBoost(userId: Long) {
        if (sessionManager.isGuestUser()) {
            addFragment(
                SignUpFragment.getInstance(
                    true
                ),
                Constants.SIGNUP_FRAGMENT
            )
        }
    }

    override fun onSelectItemClick(userId: Long, position: Int) {
    }

    override fun onSelectItemClick(
        userId: Long,
        position: Int,
        userProfileType: String
    ) {
        Log.e(
            "DashboardActivity",
            "onSelectItemClick: \t userProfileType: $userProfileType"
        )
        if (userProfileType == "1" && sessionManager.isGuestUser()) {
            bottomSheetDialogComment.dismiss()
            addFragment(
                SignUpFragment.getInstance(
                    true
                ),
                Constants.SIGNUP_FRAGMENT
            )
        } else {
            val intent = Intent(this@DashboardActivity, ProfileViewActivity::class.java)
            intent.putExtra(Constants.USER_ID, userId)
            openActivity(intent)
        }
    }

    override fun onPostViewer(postId: Long) {
        dashboardViewModel.postView(postId)
    }

    override fun onSuccessGetComment(data: ArrayList<CommentBean>) {
        bottomSheetDialogComment.rvMentionList.visibility = View.GONE
        bottomSheetDialogComment.tvTotalComment.text =
            data.size.toString().plus(" ").plus(getString(R.string.comments))

        bottomSheetDialogComment.rvPostComment.addItemDecoration(
            DividerItemDecoration(
                this@DashboardActivity,
                LinearLayoutManager.VERTICAL
            )
        )

        commentAdapter = CommentAdapter(data, this@DashboardActivity, this)
        bottomSheetDialogComment.rvPostComment.adapter = commentAdapter

        val params: ViewGroup.LayoutParams =
            bottomSheetDialogComment.rvPostComment.layoutParams


        if (data.size > 6) {
            params.height = 1000
            bottomSheetDialogComment.rvPostComment.layoutParams = params
        } else {
            params.height = ViewGroup.LayoutParams.MATCH_PARENT
            bottomSheetDialogComment.rvPostComment.layoutParams = params
        }

        val itemTouchHelperCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    viewHolder2: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun getSwipeDirs(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ): Int {
                    if (data[viewHolder.adapterPosition].user_id != sessionManager.getUserId()) return 0
                    return super.getSwipeDirs(recyclerView, viewHolder)
                }

                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    swipeDirection: Int
                ) {

                    if (sessionManager.getUserId() == data[viewHolder.adapterPosition].user_id) {
                        dashboardViewModel.deleteComment(data[viewHolder.adapterPosition].id)
                        data.removeAt(viewHolder.adapterPosition)
                        commentAdapter.notifyItemRemoved(viewHolder.adapterPosition)
                        commentAdapter.notifyItemRangeChanged(
                            viewHolder.adapterPosition,
                            commentAdapter.itemCount
                        )
                    }
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    val itemView = viewHolder.itemView
                    val iconMarginVertical =
                        (viewHolder.itemView.height - deleteIcon.intrinsicHeight) / 2

                    if (dX > 0) {
                        colorDrawableBackground.setBounds(
                            itemView.left,
                            itemView.top,
                            dX.toInt(),
                            itemView.bottom
                        )
                        deleteIcon.setBounds(
                            itemView.left + iconMarginVertical,
                            itemView.top + iconMarginVertical,
                            itemView.left + iconMarginVertical + deleteIcon.intrinsicWidth,
                            itemView.bottom - iconMarginVertical
                        )
                    } else {
                        colorDrawableBackground.setBounds(
                            itemView.right + dX.toInt(),
                            itemView.top,
                            itemView.right,
                            itemView.bottom
                        )
                        deleteIcon.setBounds(
                            itemView.right - iconMarginVertical - deleteIcon.intrinsicWidth,
                            itemView.top + iconMarginVertical,
                            itemView.right - iconMarginVertical,
                            itemView.bottom - iconMarginVertical
                        )
                        deleteIcon.level = 0
                    }

                    colorDrawableBackground.draw(c)

                    c.save()

                    if (dX > 0)
                        c.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                    else
                        c.clipRect(
                            itemView.right + dX.toInt(),
                            itemView.top,
                            itemView.right,
                            itemView.bottom
                        )

                    deleteIcon.draw(c)

                    c.restore()

                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(bottomSheetDialogComment.rvPostComment)

    }

    override fun onSuccess(msg: String) {
        bottomSheetDialogComment.tvTotalComment.text =
            commentAdapter.itemCount.toString().plus(" ").plus(getString(R.string.comments))

        isUpdateComment = true
        commentCount = commentAdapter.itemCount
    }

    override fun onSuccessAddComment(commentBean: CommentBean) {
        bottomSheetDialogComment.edtComment.setText("")
        commentAdapter.addCommentLastPosition(commentBean)

        bottomSheetDialogComment.tvTotalComment.text =
            commentAdapter.itemCount.toString().plus(" ").plus(getString(R.string.comments))
        commentCount = commentAdapter.itemCount
        isUpdateComment = true
    }

    override fun onSuccessProfileLike(data: DashboardBean) {
        val dashboardBean = feedList[position]
        dashboardBean.is_match = data.is_match
        dashboardBean.is_like = data.is_like

        if (dashboardBean.is_match == 1 && dashboardBean.is_like == 1) {
            Log.e("DashboardActivity", "userName: \t ${dashboardBean.username}")
            Log.e("DashboardActivity", "userName:   \t ${dashboardBean.profile_url}")
            Log.e(
                "DashboardActivity",
                "userName: \t ${sessionManager.getStringValue(Constants.KEY_PROFILE_URL)}"
            )
            Log.e(
                "DashboardActivity",
                "userName: \t ${sessionManager.getStringValue(Constants.KEY_CASUAL_NAME)}"
            )
            val intent = Intent(this@DashboardActivity, MatchesScreenActivity::class.java)
            intent.putExtra("username", dashboardBean.username);
            intent.putExtra("profile_url", dashboardBean.profile_url)
            openActivity(intent)
        }

        feedList[position] = dashboardBean
        feedAdapter.notifyItemChanged(position)
    }

    override fun onSuccessFollow(msg: String) {
        val dashboardBean = feedList[position]
        if (dashboardBean.is_follow == 1)
            dashboardBean.is_follow = 0
        else
            dashboardBean.is_follow = 1

        feedList[position] = dashboardBean
        feedAdapter.notifyItemChanged(position)
    }

    override fun onSuccessReport(msg: String) {
        object : CustomAlertDialog(
            this@DashboardActivity,
            msg, getString(R.string.ok), ""
        ) {
            override fun onBtnClick(id: Int) {
                dismiss()
            }
        }.show()
    }

    override fun onSuccessSavePost(msg: String) {
        object : CustomAlertDialog(
            this@DashboardActivity,
            msg, getString(R.string.ok), ""
        ) {
            override fun onBtnClick(id: Int) {
                dismiss()
            }
        }.show()
    }

    override fun onSuccessMention(mentionList: ArrayList<MentionListBean>) {
        if (mentionList.size > 0) {
            bottomSheetDialogComment.rvMentionList.addItemDecoration(
                DividerItemDecoration(
                    this@DashboardActivity,
                    LinearLayoutManager.VERTICAL
                )
            )
            bottomSheetDialogComment.rvMentionList.visibility = View.GONE

            /* mentionListAdapter = MentionListAdapter(mentionList, this@DashboardActivity, this)
             bottomSheetDialogComment.rvMentionList.adapter = mentionListAdapter*/
            for (i in mentionList.indices) {
                mentionArrayAdapter.addAll(
                    if (mentionList[i].profile_url != "") Mention(mentionList[i].username, "", mentionList[i].profile_url)
                    else Mention(mentionList[i].username)
                )
            }

        } else {
            bottomSheetDialogComment.rvMentionList.visibility = View.GONE
        }
    }

    override fun onSuccessBlockUser(msg: String) {
        object : CustomAlertDialog(
            this@DashboardActivity,
            msg, getString(R.string.ok), ""
        ) {
            override fun onBtnClick(id: Int) {
                dismiss()
                feedList.clear()
                dashboardViewModel.getFeedList(0)
            }
        }.show()
    }

    fun onClickDiscover(view: View) {
        dashboardViewModel.getFeedList(0)
        val intent = Intent(this@DashboardActivity, FilterActivity::class.java)
        intent.putExtra("categoryList", categoryBeanList)
        openActivityForResult(intent, Constants.FILTER_OK)
    }

    override fun onMentionItemClick(userId: Long, position: Int, username: String) {
        bottomSheetDialogComment.edtComment.setText(username)
        bottomSheetDialogComment.rvMentionList.visibility = View.GONE
    }
}
