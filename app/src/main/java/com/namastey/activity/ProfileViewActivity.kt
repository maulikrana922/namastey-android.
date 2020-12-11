package com.namastey.activity

import android.content.ComponentName
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.AlbumListProfileAdapter
import com.namastey.dagger.module.GlideApp
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityProfileViewBinding
import com.namastey.fragment.ShareAppFragment
import com.namastey.listeners.OnItemClick
import com.namastey.listeners.OnViewAlbumClick
import com.namastey.model.*
import com.namastey.uiView.ProfileView
import com.namastey.utils.*
import com.namastey.viewModel.ProfileViewModel
import kotlinx.android.synthetic.main.activity_profile_view.*
import kotlinx.android.synthetic.main.dialog_alert.*
import kotlinx.android.synthetic.main.dialog_bottom_pick.*
import kotlinx.android.synthetic.main.dialog_bottom_share_feed.*
import kotlinx.android.synthetic.main.dialog_common_alert.*
import kotlinx.android.synthetic.main.view_private_account.*
import java.util.*
import javax.inject.Inject

class ProfileViewActivity : BaseActivity<ActivityProfileViewBinding>(),
    ProfileView,
    OnViewAlbumClick,
    OnItemClick {
    //OnFeedItemClick {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var activityProfileViewBinding: ActivityProfileViewBinding
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var albumListProfileAdapter: AlbumListProfileAdapter
    private var profileBean = ProfileBean()
    private var isMyProfile = false
    private lateinit var bottomSheetDialogShare: BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        profileViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel::class.java)
        activityProfileViewBinding = bindViewData()
        activityProfileViewBinding.viewModel = profileViewModel

        initData()
    }

    override fun onSuccessResponse(profileBean: ProfileBean) {
        this.profileBean = profileBean
        if (profileBean.user_id == sessionManager.getUserId()) {
            isMyProfile = true
            groupButtons.visibility = View.VISIBLE
            groupButtonsLike.visibility = View.GONE
        } else {
            isMyProfile = false
            groupButtons.visibility = View.INVISIBLE
            groupButtonsLike.visibility = View.VISIBLE

            when (profileBean.is_follow) {
                1 -> btnProfileFollow.text = getString(R.string.following)
                2 -> btnProfileFollow.text = getString(R.string.pending)
                else -> btnProfileFollow.text = getString(R.string.follow)
            }

            when (profileBean.is_like) {
                1 -> btnProfileLike.text = getString(R.string.liked)
                else -> btnProfileLike.text = getString(R.string.like)
            }
        }

        // Need to change
        if (profileBean.gender == Constants.Gender.female.name) {
            ivProfileTop.background = resources.getDrawable(R.drawable.female_bg)
        } else {
            ivProfileTop.background = resources.getDrawable(R.drawable.male_bg)
        }
        fillValue(profileBean)
    }

    override fun onSuccess(msg: String) {
//        super.onSuccess(msg)
        if (profileBean.is_follow == 1) {
            profileBean.is_follow = 0
            profileBean.followers -= 1
            btnProfileFollow.text = resources.getString(R.string.follow)
        } else {
            profileBean.is_follow = 1
            profileBean.followers += 1
            btnProfileFollow.text = resources.getString(R.string.following)
        }
        tvFollowersCount.text = profileBean.followers.toString()
    }

    private fun fillValue(profileBean: ProfileBean) {
        tvProfileUsername.text = profileBean.username
        if (profileBean.about_me.isNotEmpty() && profileBean.about_me != null)
            tvAbouteDesc.text = profileBean.about_me
        else
            tvAbouteDesc.text = getString(R.string.about_me_empty)

        tvFollowersCount.text = profileBean.followers.toString()
        tvFollowingCount.text = profileBean.following.toString()
        tvViewsCount.text = profileBean.viewers.toString()
        GlideLib.loadImage(this@ProfileViewActivity, ivProfileUser, profileBean.profileUrl)

        if (profileBean.education.size > 0) {
            tvEducation.text = profileBean.education[0].course
        }
        if (profileBean.jobs.size > 0) {
            tvJob.text = profileBean.jobs[0].title
        }

        //generateChooseInterestUI(profileBean.interest)
        if (profileBean.sub_cat_tag != null && profileBean.sub_cat_tag.size > 0) {
            generateChooseInterestUI(profileBean.sub_cat_tag)
        }
        socialAccountUI(profileBean.social_accounts)

        albumListProfileAdapter =
            AlbumListProfileAdapter(
                profileBean.albums,
                this@ProfileViewActivity,
                this,
                this,
                profileBean.gender
            )
        rvAlbumList.adapter = albumListProfileAdapter

        if ((profileBean.is_follow == 0 || profileBean.is_follow == 2) && profileBean.user_id != sessionManager.getUserId()) {
            if (profileBean.user_profile_type == 0) {
                layoutPrivateAccount.visibility = View.GONE
                rvAlbumList.visibility = View.VISIBLE
            } else if (profileBean.user_profile_type == 1) {
                layoutPrivateAccount.visibility = View.VISIBLE
                rvAlbumList.visibility = View.GONE
            }
        } else {
            rvAlbumList.visibility = View.VISIBLE
            layoutPrivateAccount.visibility = View.GONE
        }

    }

    /**
     * Generate dynamic choose interest view
     */
    private fun generateChooseInterestUITemp(interestList: ArrayList<InterestBean>) {
//        for (interestBean in interestList) {
//            val tvInterest = TextView(this@ProfileViewActivity)
//            tvInterest.layoutParams = LinearLayout.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT
//            )
//            tvInterest.text = interestBean.interest_name
//            tvInterest.setPadding(40, 18, 40, 18)
//            tvInterest.setTextColor(Color.WHITE)
//            tvInterest.setBackgroundResource(R.drawable.rounded_white_border_transparent_solid)
//
//            chipProfileInterest.addView(tvInterest)
//        }

        chipProfileInterest.removeAllViews()
        val tvInterest = TextView(this@ProfileViewActivity)
        tvInterest.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        tvInterest.text = interestList[0].interest_name
        tvInterest.setPadding(40, 18, 40, 18)
        tvInterest.setTextColor(Color.WHITE)
        tvInterest.setBackgroundResource(R.drawable.rounded_white_border_transparent_solid)

        chipProfileInterest.addView(tvInterest)

        if (interestList.size >= 2) {
            val tvInterestSecond = TextView(this@ProfileViewActivity)
            tvInterestSecond.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            tvInterestSecond.text = interestList[1].interest_name
            tvInterestSecond.setPadding(40, 18, 40, 18)
            tvInterestSecond.setTextColor(Color.WHITE)
            tvInterestSecond.setBackgroundResource(R.drawable.rounded_white_border_transparent_solid)

            chipProfileInterest.addView(tvInterestSecond)
        }

    }

    private fun generateChooseInterestUI(interestSubCategoryList: ArrayList<InterestSubCategoryBean>) {
        chipProfileInterest.removeAllViews()
        val tvInterest = TextView(this@ProfileViewActivity)
        tvInterest.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        tvInterest.text = interestSubCategoryList[0].name
        tvInterest.setPadding(40, 18, 40, 18)
        tvInterest.setTextColor(Color.WHITE)
        tvInterest.setBackgroundResource(R.drawable.rounded_white_border_transparent_solid)

        chipProfileInterest.addView(tvInterest)

        if (interestSubCategoryList.size >= 2) {
            val tvInterestSecond = TextView(this@ProfileViewActivity)
            tvInterestSecond.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            tvInterestSecond.text = interestSubCategoryList[1].name
            tvInterestSecond.setPadding(40, 18, 40, 18)
            tvInterestSecond.setTextColor(Color.WHITE)
            tvInterestSecond.setBackgroundResource(R.drawable.rounded_white_border_transparent_solid)

            chipProfileInterest.addView(tvInterestSecond)
        }

    }

    /**
     * Add social account icons
     */
    private fun socialAccountUI(data: ArrayList<SocialAccountBean>) {

        chipProfileSocial.removeAllViews()
        for (socialBean in data) {
            val ivSocialIcon = ImageView(this@ProfileViewActivity)
            ivSocialIcon.layoutParams = LinearLayout.LayoutParams(
                60,
                60
            )
//            ivSocialIcon.background = getDrawable(R.drawable.circle_white_solid)
            when (socialBean.name) {
                getString(R.string.facebook) -> {
                    ivSocialIcon.setImageResource(R.drawable.ic_facebook)
                    ivSocialIcon.setOnClickListener {
                        val intent =
                            packageManager.getLaunchIntentForPackage("com.facebook.katana")
                        if (intent != null) {
                            startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("fb://profile/".plus(socialBean.link))
//                                            Uri.parse("fb://facewebmodal/f?href=".plus(socialBean.link))
                                )
                            )
                        }
//                        else{
//                            intent = Intent(Intent.ACTION_VIEW)
//                            intent.data = Uri.parse("market://details?id=com.spotify.music")
//                            startActivity(intent)
//                        }
                    }
                }
                getString(R.string.instagram) -> {
                    ivSocialIcon.setImageResource(R.drawable.profile_link_instagram)
                }
                getString(R.string.snapchat) -> {
                    ivSocialIcon.setImageResource(R.drawable.ic_snapchat)

                }
                getString(R.string.tiktok) -> {
                    ivSocialIcon.setImageResource(R.drawable.ic_tiktok)
                }
                getString(R.string.spotify) -> {
                    ivSocialIcon.setImageResource(R.drawable.profile_link_spotify)
                    ivSocialIcon.setOnClickListener {

                        var intent =
                            packageManager.getLaunchIntentForPackage("com.spotify.music")
                        if (intent != null) {
                            startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(socialBean.link)
                                )
                            )
                        } else {
                            intent = Intent(Intent.ACTION_VIEW)
                            intent.data = Uri.parse("market://details?id=com.spotify.music")
                            startActivity(intent)
                        }
                    }
                }
                getString(R.string.linkedin) -> {
                    ivSocialIcon.setImageResource(R.drawable.profile_link_linkedin)
                }
                getString(R.string.twitter) -> {
                    ivSocialIcon.setImageResource(R.drawable.ic_share_twitter)
                    ivSocialIcon.setOnClickListener {
                        try {
                            startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(socialBean.link)
                                )
                            )
                        } catch (e: java.lang.Exception) {
                            val index = socialBean.link.indexOf('=')
                            val name: String? = if (index == -1) "" else socialBean.link
                                .substring(index + 1)
                            startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://twitter.com/".plus(name))
                                )
                            )
                        }
                    }
                }
            }
            chipProfileSocial.addView(ivSocialIcon)
        }
    }

    override fun getViewModel() = profileViewModel

    override fun getLayoutId() = R.layout.activity_profile_view

    override fun getBindingVariable() = BR.viewModel

    private fun initData() {

        if (intent.hasExtra("profileBean")) {
            profileBean = intent.getParcelableExtra<ProfileBean>("profileBean") as ProfileBean

            if (sessionManager.getUserGender() == Constants.Gender.female.name) {
                ivProfileTop.background = getDrawable(R.drawable.female_bg)
                GlideApp.with(this).load(R.drawable.ic_female)
                    .apply(RequestOptions.circleCropTransform()).placeholder(R.drawable.ic_female)
                    .fitCenter().into(ivProfileUser)
            } else {
                ivProfileTop.background = getDrawable(R.drawable.male_bg)
                GlideApp.with(this).load(R.drawable.ic_male)
                    .apply(RequestOptions.circleCropTransform()).placeholder(R.drawable.ic_male)
                    .fitCenter().into(ivProfileUser)
            }

            if (profileBean.profileUrl.isNotBlank()) {
                GlideLib.loadImage(this@ProfileViewActivity, ivProfileUser, profileBean.profileUrl)
            }
            tvProfileUsername.text = profileBean.username
            if (profileBean.about_me.isNotEmpty())
                tvAbouteDesc.text = profileBean.about_me
            else
                tvAbouteDesc.text = getString(R.string.about_me_empty)
            tvFollowersCount.text = profileBean.followers.toString()
            tvFollowingCount.text = profileBean.following.toString()
            tvViewsCount.text = profileBean.viewers.toString()

            if (sessionManager.getEducationBean().course.isNotEmpty()) {
                tvEducation.text = sessionManager.getEducationBean().course
            }
            if (sessionManager.getJobBean().title.isNotEmpty()) {
                tvJob.text = sessionManager.getJobBean().title
            }
//            profileViewModel.getUserFullProfile(sessionManager.getUserId())

            /*btnProfileLike.setOnClickListener {
                if (profileBean.is_like == 1)
                    profileViewModel.likeUserProfile(likedUserId, isLike)
                else
                profileViewModel.likeUserProfile(likedUserId, isLike)

            }*/
        }
//        else {
//            profileViewModel.getUserFullProfile(intent.getLongExtra(Constants.USER_ID, 0))
//        }

    }

    override fun onResume() {
        super.onResume()

        if (intent.hasExtra("profileBean")) {
            profileViewModel.getUserFullProfile(sessionManager.getUserId())
        } else {
            profileViewModel.getUserFullProfile(intent.getLongExtra(Constants.USER_ID, 0))
        }
        if (profileBean.user_id == sessionManager.getUserId()) {
            if (sessionManager.getStringValue(Constants.KEY_CASUAL_NAME).isNotEmpty()) {
                tvProfileUsername.text = sessionManager.getStringValue(Constants.KEY_CASUAL_NAME)
                if (sessionManager.getStringValue(Constants.KEY_TAGLINE).isNotEmpty())
                    tvAbouteDesc.text = sessionManager.getStringValue(Constants.KEY_TAGLINE)
                else
                    tvAbouteDesc.text = getString(R.string.about_me_empty)
                tvJob.text = sessionManager.getJobBean().title
                tvEducation.text = sessionManager.getEducationBean().course
            }
        }
    }

    fun onClickProfileBack(view: View) {
        onBackPressed()
    }

    fun onClickProfileImage(view: View) {}

    /**
     * Open following/followers screen click on counter
     */
    fun onClickFollow(view: View) {

        if (!sessionManager.getBooleanValue(Constants.KEY_IS_COMPLETE_PROFILE)) {
            completeSignUpDialog()
        }
        if (!sessionManager.isGuestUser()) {
            val intent = Intent(this@ProfileViewActivity, FollowingFollowersActivity::class.java)
            intent.putExtra(Constants.PROFILE_BEAN, profileBean)
            intent.putExtra("isMyProfile", isMyProfile)
            openActivity(intent)
        }
    }

    fun onClickFollowRequest(view: View) {
        if (!sessionManager.getBooleanValue(Constants.KEY_IS_COMPLETE_PROFILE)) {
            completeSignUpDialog()
        }

        if (!sessionManager.isGuestUser()) {
            if (profileBean.is_follow == 1) {
                profileViewModel.followUser(profileBean.user_id, 0)
            } else if (profileBean.is_follow == 0) {
                profileViewModel.followUser(profileBean.user_id, 1)
            }
        }
    }

    fun onClickEditProfile(view: View) {
        openActivity(this@ProfileViewActivity, EditProfileActivity())
    }

    override fun onBackPressed() {
        finishActivity()
    }

    override fun onDestroy() {
        profileViewModel.onDestroy()
        super.onDestroy()
    }

    override fun onItemClick(value: Long, position: Int) {
    }

    override fun onItemFollowingClick(dashboardBean: DashboardBean) {

    }

    override fun onViewAlbumItemClick(value: Long, position: Int) {
    }

    fun onClickMatches(view: View) {
        val intent = Intent(this@ProfileViewActivity, MatchesActivity::class.java)
        intent.putExtra("onClickMatches", true)
        openActivity(intent)
    }

    fun onClickProfileMore(view: View) {
        if (sessionManager.getUserId() == profileBean.user_id)
            openActivity(this@ProfileViewActivity, SettingsActivity())
        else if (profileBean.username.isNotEmpty())
            openShareOptionDialog(profileBean)
    }

    fun onClickProfileLike(view: View) {
        if (!sessionManager.getBooleanValue(Constants.KEY_IS_COMPLETE_PROFILE)) {
            completeSignUpDialog()
        }

        if (!sessionManager.isGuestUser()) {
            if (profileBean.is_like == 1) {
                profileViewModel.likeUserProfile(profileBean.user_id, 0)
            } else {
                profileViewModel.likeUserProfile(profileBean.user_id, 1)
            }
        }
    }

    override fun onSuccessProfileLike(dashboardBean: DashboardBean) {
        Log.e("ProfileViewActivity", "onSuccessProfileLike: data: \t ${dashboardBean.is_like}")
//        isLike = dashboardBean.is_like

        profileBean.is_like = dashboardBean.is_like
        if (dashboardBean.is_like == 1) {
            btnProfileLike.text = resources.getString(R.string.liked)
        } else {
            btnProfileLike.text = resources.getString(R.string.like)
        }

        if (dashboardBean.is_match == 1 && dashboardBean.is_like == 1) {
            val intent = Intent(this@ProfileViewActivity, MatchesScreenActivity::class.java)
            intent.putExtra("username", profileBean.username)
            intent.putExtra("profile_url", profileBean.profileUrl)
            openActivity(intent)
        }
    }

    override fun onSuccessFollow(profile: ProfileBean) {
        profileBean.is_follow = profile.is_follow

        if (profileBean.is_follow == 1) {
            profileBean.followers += 1
            btnProfileFollow.text = resources.getString(R.string.following)
        } else if (profileBean.is_follow == 0) {
            profileBean.followers -= 1
            btnProfileFollow.text = resources.getString(R.string.follow)
        } else if (profileBean.is_follow == 2) {
            btnProfileFollow.text = resources.getString(R.string.pending)
        }
        tvFollowersCount.text = profileBean.followers.toString()
    }

    /**
     * Display share option if user login
     */
    private fun openShareOptionDialog(profileBean: ProfileBean) {
        bottomSheetDialogShare = BottomSheetDialog(this@ProfileViewActivity, R.style.dialogStyle)
        bottomSheetDialogShare.setContentView(
            layoutInflater.inflate(
                R.layout.dialog_bottom_share_feed,
                null
            )
        )
        bottomSheetDialogShare.window?.setBackgroundDrawableResource(android.R.color.transparent)
        bottomSheetDialogShare.window?.attributes?.windowAnimations = R.style.DialogAnimation
        bottomSheetDialogShare.setCancelable(true)

        bottomSheetDialogShare.tvShareBlock.text = getString(R.string.privacy)

        bottomSheetDialogShare.ivShareSave.setImageResource(R.drawable.ic_share_report)
        bottomSheetDialogShare.tvShareSave.text = getString(R.string.report)

        bottomSheetDialogShare.ivShareReport.setImageResource(R.drawable.ic_send_message)
        bottomSheetDialogShare.tvShareReport.text = getString(R.string.send_message)

        bottomSheetDialogShare.tvShareCancel.setOnClickListener {
            bottomSheetDialogShare.dismiss()
        }

        val shareMessage = String.format(
            getString(R.string.profile_link_msg),
            profileBean.username,profileBean.about_me
        )
        // Share on Twitter app if install otherwise web link
        bottomSheetDialogShare.ivShareTwitter.setOnClickListener {
            val tweetUrl =
                StringBuilder("https://twitter.com/intent/tweet?text=".plus(shareMessage))
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

        bottomSheetDialogShare.ivShareApp.setOnClickListener {
            bottomSheetDialogShare.dismiss()
            addFragment(
                ShareAppFragment.getInstance(
                    sessionManager.getUserId(),
                    profileBean.profileUrl
                ), //Todo:Change resposne
                Constants.SHARE_APP_FRAGMENT
            )
        }

        bottomSheetDialogShare.ivShareFacebook.setOnClickListener {
            var facebookAppFound = false
            var shareIntent =
                Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)

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
                    "https://www.facebook.com/sharer/sharer.php?u=${shareMessage}"
                shareIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(sharerUrl)
                )
            }
            startActivity(shareIntent)
        }

        bottomSheetDialogShare.ivShareInstagram.setOnClickListener {

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
                    Uri.parse(shareMessage)
                )
                startActivity(sendIntent)
            } catch (e: PackageManager.NameNotFoundException) {
                Toast.makeText(
                    this@ProfileViewActivity,
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
                Intent.EXTRA_TEXT,shareMessage)
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        }

        //Privacy Click
        bottomSheetDialogShare.tvShareBlock.setOnClickListener {
            displayBlockUserDialog(profileBean)
        }
        bottomSheetDialogShare.ivShareBlock.setOnClickListener {
            displayBlockUserDialog(profileBean)
        }

        //Report click
        bottomSheetDialogShare.ivShareSave.setOnClickListener {
            displayReportUserDialog(profileBean)
        }
        bottomSheetDialogShare.tvShareSave.setOnClickListener {
            // bottomSheetDialogShare.dismiss()
            displayReportUserDialog(profileBean)
        }

        //Send Message
        bottomSheetDialogShare.ivShareReport.setOnClickListener {
            //bottomSheetDialogShare.dismiss()
            clickSendMessage(profileBean)
        }
        bottomSheetDialogShare.tvShareReport.setOnClickListener {
            bottomSheetDialogShare.dismiss()
        }

        bottomSheetDialogShare.show()
    }

    private fun clickSendMessage(profileBean: ProfileBean) {
        val matchesListBean = MatchesListBean()
        matchesListBean.id = profileBean.user_id
        matchesListBean.username = profileBean.username
        matchesListBean.profile_pic = profileBean.profileUrl
        matchesListBean.is_match = profileBean.is_match
        matchesListBean.is_block = profileBean.is_block
        matchesListBean.is_read = 1 //Todo: Change value from profileBean

        if (profileBean.user_id != sessionManager.getUserId()) {
            //user_profile_type = 0 = Public
            //user_profile_type = 1 = private
//            if (profileBean.user_profile_type == 0) {
//            if (profileBean.safetyBean.who_can_send_message == 0) {
                val intent = Intent(this, ChatActivity::class.java)
                intent.putExtra("matchesListBean", matchesListBean)
                intent.putExtra("isFromProfile", true)
                intent.putExtra("whoCanSendMessage", profileBean.safetyBean.who_can_send_message)
                openActivity(intent)
//            } else if (profileBean.user_profile_type == 1) {
                //set Dialog
//                dialogPrivateUser()
//            }
        }
    }

//    private fun dialogPrivateUser() {
//        object : CustomAlertDialog(
//            this,
//            getString(R.string.private_account_message),
//            getString(R.string.ok),
//            getString(R.string.cancel)
//        ) {
//            override fun onBtnClick(id: Int) {
//                when (id) {
//                    btnPos.id -> {
//                        dismiss()
//                    }
//                }
//            }
//        }.show()
//    }

    /**
     * Display dialog of report user
     */
    private fun displayReportUserDialog(dashboardBean: ProfileBean) {
        object : CustomCommonAlertDialog(
            this@ProfileViewActivity,
            dashboardBean.username,
            getString(R.string.msg_report_user),
            dashboardBean.profileUrl,
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

    private fun displayBlockUserDialog(dashboardBean: ProfileBean) {
        object : CustomCommonAlertDialog(
            this@ProfileViewActivity,
            dashboardBean.username,
            getString(R.string.msg_block_user),
            dashboardBean.profileUrl,
            getString(R.string.block_user),
            resources.getString(R.string.no_thanks)
        ) {
            override fun onBtnClick(id: Int) {
                when (id) {
                    btnAlertOk.id -> {
                        bottomSheetDialogShare.dismiss()
                        profileViewModel.blockUser(dashboardBean.user_id, 1)
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
            BottomSheetDialog(this@ProfileViewActivity, R.style.dialogStyle)
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
            profileViewModel.reportUser(reportUserId, getString(R.string.its_spam))
        }
        bottomSheetReport.tvPhotoChoose.setOnClickListener {
            bottomSheetReport.dismiss()
            profileViewModel.reportUser(reportUserId, getString(R.string.its_inappropriate))
        }
        bottomSheetReport.tvPhotoCancel.setOnClickListener {
            bottomSheetReport.dismiss()
        }
        bottomSheetReport.show()
    }

    override fun onSuccessReport(msg: String) {
        object : CustomAlertDialog(
            this@ProfileViewActivity,
            msg, getString(R.string.ok), ""
        ) {
            override fun onBtnClick(id: Int) {
                dismiss()
            }
        }.show()
    }

    override fun onSuccessBlockUser(msg: String) {
        object : CustomAlertDialog(
            this@ProfileViewActivity,
            msg, getString(R.string.ok), ""
        ) {
            override fun onBtnClick(id: Int) {
                //dismiss()
                val intent = Intent(this@ProfileViewActivity, DashboardActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
        }.show()
    }

    override fun onSuccessSavePost(msg: String) {
    }
}