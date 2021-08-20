package com.namastey.activity

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gowtham.library.utils.TrimType
import com.gowtham.library.utils.TrimVideo
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
import kotlinx.android.synthetic.main.activity_profile_view.groupButtons
import kotlinx.android.synthetic.main.activity_profile_view.ivProfileCamera
import kotlinx.android.synthetic.main.activity_profile_view.ivProfileUser
import kotlinx.android.synthetic.main.activity_profile_view.tvAbouteDesc
import kotlinx.android.synthetic.main.activity_profile_view.tvFollowersCount
import kotlinx.android.synthetic.main.activity_profile_view.tvFollowingCount
import kotlinx.android.synthetic.main.activity_profile_view.tvProfileUsername
import kotlinx.android.synthetic.main.activity_profile_view.tvViewsCount
import kotlinx.android.synthetic.main.dialog_bottom_pick.*
import kotlinx.android.synthetic.main.dialog_bottom_share_feed_new.*
import kotlinx.android.synthetic.main.dialog_bottom_share_feed_new.ivShareBlock
import kotlinx.android.synthetic.main.dialog_bottom_share_feed_new.ivShareFacebook
import kotlinx.android.synthetic.main.dialog_bottom_share_feed_new.ivShareInstagram
import kotlinx.android.synthetic.main.dialog_bottom_share_feed_new.ivShareOther
import kotlinx.android.synthetic.main.dialog_bottom_share_feed_new.ivShareReport
import kotlinx.android.synthetic.main.dialog_bottom_share_feed_new.ivShareTwitter
import kotlinx.android.synthetic.main.dialog_bottom_share_feed_new.ivShareWhatssapp
import kotlinx.android.synthetic.main.dialog_bottom_share_feed_new.svShareOption
import kotlinx.android.synthetic.main.dialog_bottom_share_feed_new.tvShareBlock
import kotlinx.android.synthetic.main.dialog_bottom_share_feed_new.tvShareCancel
import kotlinx.android.synthetic.main.dialog_bottom_share_feed_new.tvShareReport
import kotlinx.android.synthetic.main.dialog_bottom_share_feed_new.tvShareSave
import kotlinx.android.synthetic.main.dialog_common_alert.*
import java.io.File
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
    private var username = ""
    private val REQUEST_CODE = 101
    private var profileFile: File? = null
    private var isCameraOpen = false
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private var videoFile: File? = null

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
            tvAboutLable.setText(R.string.about_me)
            groupButtons.visibility = View.VISIBLE
            groupButtonsLike.visibility = View.INVISIBLE
            ivProfileCamera.visibility = View.VISIBLE
            sessionManager.setStringValue(profileBean.profileUrl, Constants.KEY_PROFILE_URL)
            sessionManager.setStringValue(profileBean.username, Constants.KEY_MAIN_USER_NAME)
            if (profileBean.is_completly_signup == 1) {
                sessionManager.setBooleanValue(true, Constants.KEY_IS_COMPLETE_PROFILE)
            } else {
                sessionManager.setBooleanValue(false, Constants.KEY_IS_COMPLETE_PROFILE)
            }
            sessionManager.setStringValue(profileBean.about_me, Constants.KEY_TAGLINE)
            sessionManager.setStringValue(profileBean.casual_name, Constants.KEY_CASUAL_NAME)
            sessionManager.setStringValue(profileBean.distance, Constants.DISTANCE)
            sessionManager.setIntegerValue(profileBean.is_hide, Constants.IS_HIDE)
            sessionManager.setStringValue(profileBean.min_age.toString(), Constants.KEY_AGE_MIN)
            sessionManager.setStringValue(profileBean.max_age.toString(), Constants.KEY_AGE_MAX)
            sessionManager.setInterestIn(profileBean.interest_in_gender)
            sessionManager.setIntegerValue(profileBean.user_profile_type, Constants.PROFILE_TYPE)
            sessionManager.setIntegerValue(profileBean.is_global, Constants.KEY_GLOBAL)
            sessionManager.setIntegerValue(profileBean.age, Constants.KEY_AGE)
            sessionManager.setStringValue(profileBean.jobs, Constants.KEY_JOB)
            sessionManager.setStringValue(profileBean.education, Constants.KEY_EDUCATION)
        } else {
            isMyProfile = false
            groupButtons.visibility = View.INVISIBLE
            groupButtonsLike.visibility = View.VISIBLE
            tvAboutLable.setText(R.string.about)

            when (profileBean.is_follow) {
                1 -> btnProfileFollow.text = getString(R.string.following)
                2 -> btnProfileFollow.text = getString(R.string.pending)
                else -> btnProfileFollow.text = getString(R.string.follow)
            }

            when (profileBean.is_like) {
                1 -> {
                    btnProfileLike.text = getString(R.string.liked)
                    btnProfileLike.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.color_text_red
                        )
                    )
                    btnProfileLike.background =
                        ContextCompat.getDrawable(this, R.drawable.rounded_btn)
                    btnProfileLike.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.heart,
                        0,
                        0,
                        0
                    )
                }
                else -> {
                    btnProfileLike.text = getString(R.string.match)
                    btnProfileLike.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.white
                        )
                    )
                    btnProfileLike.background =
                        ContextCompat.getDrawable(this, R.drawable.rounded_btn_red)
                    btnProfileLike.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_like_dashboard,
                        0,
                        0,
                        0
                    )
                }
            }
        }

        // Need to change
        if (profileBean.gender == Constants.Gender.female.name) {
            // ivProfileTop.background = resources.getDrawable(R.drawable.female_bg)
            GlideApp.with(this).load(R.drawable.ic_female)
                .apply(RequestOptions.circleCropTransform()).placeholder(R.drawable.ic_female)
                .fitCenter().into(ivProfileUser)
        } else {
            // ivProfileTop.background = resources.getDrawable(R.drawable.male_bg)
            GlideApp.with(this).load(R.drawable.ic_male)
                .apply(RequestOptions.circleCropTransform()).placeholder(R.drawable.ic_male)
                .fitCenter().into(ivProfileUser)
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
        tvProfileUsername.text = profileBean.casual_name
        tvCasualName.text = profileBean.username

        if (profileBean.about_me.isNotEmpty() && profileBean.about_me != null)
            tvAbouteDesc.text = profileBean.about_me
        else
            tvAbouteDesc.text = getString(R.string.about_me_empty)



        ivProfileMore.visibility = View.VISIBLE
        tvFollowersCount.text = profileBean.followers.toString()
        tvFollowingCount.text = profileBean.following.toString()
        tvViewsCount.text = profileBean.viewers.toString()
        if (profileBean.profileUrl.isNotEmpty())
            GlideLib.loadImage(this@ProfileViewActivity, ivProfileUser, profileBean.profileUrl)

        //if (profileBean.education.size > 0) {
        tvEducation.text = profileBean.education
        // }
        //if (profileBean.jobs.size > 0) {
        tvJob.text = profileBean.jobs
        // }

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
                profileBean.gender,
                isMyProfile,
                profileBean

            )
        rvAlbumList.adapter = albumListProfileAdapter

        if ((profileBean.is_follow == 0 || profileBean.is_follow == 2) && profileBean.user_id != sessionManager.getUserId()) {
            if (profileBean.user_profile_type == 0) {
                layoutPrivateAccount.visibility = View.GONE
                rvAlbumList.visibility = View.VISIBLE
            } else if (profileBean.user_profile_type == 1) {
                layoutPrivateAccount.visibility = View.VISIBLE
                chipProfileInterest.visibility = View.GONE
                tvInterestTitel.visibility = View.GONE
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
    private fun generateChooseInterestUI(interestSubCategoryList: ArrayList<InterestSubCategoryBean>) {
        chipProfileInterest.removeAllViews()
        val tvInterest = TextView(this@ProfileViewActivity)
        tvInterest.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        tvInterest.text = interestSubCategoryList[0].name
        tvInterest.setPadding(40, 18, 40, 18)
        tvInterest.setTextColor(ContextCompat.getColor(this, R.color.color_text_red))
        tvInterest.setBackgroundResource(R.drawable.rounded_gray_border_transparent_solid)

        chipProfileInterest.addView(tvInterest)

        if (interestSubCategoryList.size >= 2) {
            val tvInterestSecond = TextView(this@ProfileViewActivity)
            tvInterestSecond.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            tvInterestSecond.text = interestSubCategoryList[1].name
            tvInterestSecond.setPadding(40, 18, 40, 18)
            tvInterestSecond.setTextColor(ContextCompat.getColor(this, R.color.color_text_red))
            tvInterestSecond.setBackgroundResource(R.drawable.rounded_gray_border_transparent_solid)
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
                    ivSocialIcon.setImageResource(R.drawable.ic_link_facebook)
                    ivSocialIcon.setOnClickListener {
                        if ((profileBean.is_follow == 0 || profileBean.is_follow == 2) && profileBean.user_id != sessionManager.getUserId()) {
                            if (profileBean.user_profile_type == 0) {
                                val intent =
                                    packageManager.getLaunchIntentForPackage("com.facebook.katana")
                                if (intent != null) {
//                                    startActivity(
//                                        Intent(
//                                            Intent.ACTION_VIEW,
//                                            Uri.parse("fb://profile/".plus(socialBean.link))
////                                            Uri.parse("fb://facewebmodal/f?href=".plus(socialBean.link))
//                                        )
//                                    )
                                    try {
                                        val versionCode = packageManager.getPackageInfo(
                                            "com.facebook.katana",
                                            0
                                        ).versionCode
                                        if (versionCode >= 3002850) {
                                            val uri =
                                                Uri.parse("fb://facewebmodal/f?href=${socialBean.link}")
                                            startActivity(
                                                Intent(
                                                    Intent.ACTION_VIEW,
                                                    uri
                                                )
                                            )
                                        } else {
                                            // open the Facebook app using the old method (fb://profile/id or fb://page/id)
                                            startActivity(
                                                Intent(
                                                    Intent.ACTION_VIEW,
                                                    Uri.parse("fb://page/${socialBean.link}")
                                                )
                                            )
                                        }
                                    } catch (e: PackageManager.NameNotFoundException) {
                                        // Facebook is not installed. Open the browser
                                        startActivity(
                                            Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse(socialBean.link)
                                            )
                                        )
                                    }
                                }
                            } else if (profileBean.user_profile_type == 1) {
                                //Do Nothing
                            }
                        } else {
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
                        }

//                        else{
//                            intent = Intent(Intent.ACTION_VIEW)
//                            intent.data = Uri.parse("market://details?id=com.spotify.music")
//                            startActivity(intent)
//                        }
                    }
                }
                getString(R.string.instagram) -> {
                    ivSocialIcon.setImageResource(R.drawable.ic_link_instagram)

                    ivSocialIcon.setOnClickListener {
                        Log.e("ProfileViewActivity", "Instagram Click: \t ${socialBean.link}")
                        if ((profileBean.is_follow == 0 || profileBean.is_follow == 2) && profileBean.user_id != sessionManager.getUserId()) {
                            if (profileBean.user_profile_type == 0) {
                                val uri = Uri.parse(socialBean.link)
                                /* try {
                                     startActivity(likeIng)
                                 } catch (e: ActivityNotFoundException) {
                                     startActivity(
                                         Intent(
                                             Intent.ACTION_VIEW,
                                             // Uri.parse("http://instagram.com/xxx")
                                             Uri.parse(socialBean.link)
                                         )
                                     )
                                 }*/

                                try { //first try to open in instagram app
                                    val appIntent =
                                        packageManager.getLaunchIntentForPackage("com.instagram.android")
                                    if (appIntent != null) {
                                        appIntent.action = Intent.ACTION_VIEW
                                        appIntent.data = uri
                                        startActivity(appIntent)
                                    }
                                } catch (e: ActivityNotFoundException) {
                                    val browserIntent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse(socialBean.link)
                                    )
                                    startActivity(browserIntent)
                                }

                            } else if (profileBean.user_profile_type == 1) {
                                //Do Nothing
                            }
                        } else {
                            val uri = Uri.parse(socialBean.link)

                            try { //first try to open in instagram app
                                val appIntent =
                                    packageManager.getLaunchIntentForPackage("com.instagram.android")
                                if (appIntent != null) {
                                    appIntent.action = Intent.ACTION_VIEW
                                    appIntent.data = uri
                                    startActivity(appIntent)
                                }
                            } catch (e: ActivityNotFoundException) {
                                val browserIntent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(socialBean.link)
                                )
                                startActivity(browserIntent)
                            }

                            /*try {
                                startActivity(likeIng)
                            } catch (e: ActivityNotFoundException) {
                                e.printStackTrace()
                                startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        // Uri.parse("http://instagram.com/xxx")
                                        Uri.parse(socialBean.link)
                                    )
                                )
                            }*/
                        }

//                        else{
//                            intent = Intent(Intent.ACTION_VIEW)
//                            intent.data = Uri.parse("market://details?id=com.spotify.music")
//                            startActivity(intent)
//                        }
                    }
                }
                getString(R.string.snapchat) -> {
                    ivSocialIcon.setImageResource(R.drawable.ic_link_snapchat)

                }
                getString(R.string.tiktok) -> {
                    ivSocialIcon.setImageResource(R.drawable.ic_tiktok)
                }
                getString(R.string.spotify) -> {
                    ivSocialIcon.setImageResource(R.drawable.ic_link_spotify)
                    ivSocialIcon.setOnClickListener {
                        if ((profileBean.is_follow == 0 || profileBean.is_follow == 2) && profileBean.user_id != sessionManager.getUserId()) {
                            if (profileBean.user_profile_type == 0) {
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
                            } else if (profileBean.user_profile_type == 1) {
                                //Do Nothing
                            }
                        } else {
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
                }
                getString(R.string.linkedin) -> {
                    ivSocialIcon.setImageResource(R.drawable.profile_link_linkedin)
                }
                getString(R.string.twitter) -> {
                    ivSocialIcon.setImageResource(R.drawable.ic_link_twitter)
                    ivSocialIcon.setOnClickListener {

                        if ((profileBean.is_follow == 0 || profileBean.is_follow == 2) && profileBean.user_id != sessionManager.getUserId()) {
                            if (profileBean.user_profile_type == 0) {
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

                            } else if (profileBean.user_profile_type == 1) {
                                //Do Nothing
                            }
                        } else {
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
            }
            chipProfileSocial.addView(ivSocialIcon)
        }
    }

    override fun getViewModel() = profileViewModel

    override fun getLayoutId() = R.layout.activity_profile_view

    override fun getBindingVariable() = BR.viewModel

    private fun initData() {
        if (intent.getStringExtra(Constants.USERNAME) != "" && intent.getStringExtra(Constants.USERNAME) != null) {
            username = intent.getStringExtra(Constants.USERNAME)!!
            Log.e("ProfileViewActivity", "username, $username")
        } else {
            username = ""
        }

        if (intent.hasExtra("ownProfile")) {
//            profileBean = intent.getParcelableExtra<ProfileBean>("profileBean") as ProfileBean

//            if (sessionManager.getUserGender() == Constants.Gender.female.name) {
//                //ivProfileTop.background = getDrawable(R.drawable.female_bg)
//                GlideApp.with(this).load(R.drawable.ic_female)
//                    .apply(RequestOptions.circleCropTransform()).placeholder(R.drawable.ic_female)
//                    .fitCenter().into(ivProfileUser)
//            } else {
//                //ivProfileTop.background = getDrawable(R.drawable.male_bg)
//                GlideApp.with(this).load(R.drawable.ic_male)
//                    .apply(RequestOptions.circleCropTransform()).placeholder(R.drawable.ic_male)
//                    .fitCenter().into(ivProfileUser)
//            }

            if (sessionManager.getStringValue(Constants.KEY_PROFILE_URL).isNotBlank()) {
                GlideLib.loadImage(this@ProfileViewActivity, ivProfileUser, sessionManager.getStringValue(Constants.KEY_PROFILE_URL))
            }
//            tvProfileUsername.text = profileBean.username
//            if (profileBean.about_me.isNotEmpty())
//                tvAbouteDesc.text = profileBean.about_me
//            else
//                tvAbouteDesc.text = getString(R.string.about_me_empty)
//            tvFollowersCount.text = profileBean.followers.toString()
//            tvFollowingCount.text = profileBean.following.toString()
//            tvViewsCount.text = profileBean.viewers.toString()
//
//            // if (sessionManager.getEducationBean().course.isNotEmpty()) {
//            tvEducation.text = sessionManager.getStringValue(Constants.KEY_EDUCATION)
//            //  }
//            //if (sessionManager.getJobBean().title.isNotEmpty()) {
//            tvJob.text = sessionManager.getStringValue(Constants.KEY_JOB)
            //}
//            profileViewModel.getUserFullProfile(sessionManager.getUserId())

            /*btnProfileLike.setOnClickListener {
                if (profileBean.is_like == 1)
                    profileViewModel.likeUserProfile(likedUserId, isLike)
                else
                profileViewModel.likeUserProfile(likedUserId, isLike)

            }*/
        }
        /*  else {
              profileViewModel.getUserFullProfile(sessionManager.getUserId().toString(),sessionManager.getStringValue(Constants.KEY_MAIN_USER_NAME))
          }*/

    }

    override fun onResume() {
        super.onResume()

        if (intent.hasExtra("ownProfile")) {
            Log.e("ProfileViewActivity", "profileBean, ")
            profileViewModel.getUserFullProfile("", "")
        } else {
            // profileViewModel.getUserFullProfile(intent.getLongExtra(Constants.USER_ID, 0), username)
            //profileViewModel.getUserFullProfile("", username)
            if (username != "") {
                Log.e("ProfileViewActivity", "username, ")
                profileViewModel.getUserFullProfile("", username)
            } else {
                Log.e("ProfileViewActivity", "USER_ID ")

                profileViewModel.getUserFullProfile(
                    intent.getLongExtra(Constants.USER_ID, 0).toString(), ""
                )

            }
        }
        if (profileBean.user_id == sessionManager.getUserId()) {
            if (sessionManager.getStringValue(Constants.KEY_MAIN_USER_NAME).isNotEmpty()) {
                tvProfileUsername.text = sessionManager.getStringValue(Constants.KEY_MAIN_USER_NAME)
                if (sessionManager.getStringValue(Constants.KEY_TAGLINE).isNotEmpty())
                    tvAbouteDesc.text = sessionManager.getStringValue(Constants.KEY_TAGLINE)
                else
                    tvAbouteDesc.text = getString(R.string.about_me_empty)
                tvJob.text = sessionManager.getStringValue(Constants.KEY_JOB)
                tvEducation.text = sessionManager.getStringValue(Constants.KEY_EDUCATION)
            }
        }
    }

    fun onClickProfileBack(view: View) {
        onBackPressed()
    }

    fun onClickProfileImage(view: View) {
        selectImage()
    }

    private fun selectImage() {

        val options = arrayOf<CharSequence>(
            getString(R.string.take_photo),
            getString(R.string.select_photo)
        )


        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle(
            getString(R.string.upload_profile_picture)
        )
        builder.setItems(options) { dialog, item ->
            when (item) {
                0 -> isCameraPermissionGranted()
                1 -> isReadWritePermissionGranted()
            }
        }
        builder.show()
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
                capturePhoto()
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
            capturePhoto()
        }
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
        startActivityForResult(intent, Constants.REQUEST_POST_VIDEO)
    }

    private fun isReadWritePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                openGalleryForImage()
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
            openGalleryForImage()
        }
    }


    private fun openGalleryForImage() {

        profileFile = File(
            Constants.FILE_PATH,
            System.currentTimeMillis().toString() + ".jpeg"
        )

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE)
    }

    private fun capturePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            try {
                val photoUri: Uri = FileProvider.getUriForFile(
                    this,
                    applicationContext.packageName + ".provider",
                    Utils.getCameraFile(this@ProfileViewActivity)
                )

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                startActivityForResult(takePictureIntent, Constants.REQUEST_CODE_CAMERA_IMAGE)
            } catch (ex: Exception) {
                showMsg(ex.localizedMessage)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("Request Code : ", requestCode.toString())
        if (requestCode === TrimVideo.VIDEO_TRIMMER_REQ_CODE && data != null) {
            val uri = Uri.parse(TrimVideo.getTrimmedVideoPath(data))
            Log.e("Trimmed video ", "Trimmed path:: $uri")

            videoFile = File(uri.path)
            val intent = Intent(this@ProfileViewActivity, PostVideoActivity::class.java)
            intent.putExtra("videoFile", videoFile)
           // intent.putExtra("albumId", albums.id)
//                intent.putExtra("thumbnailImage", pictureFile)
           //intent.putExtra("albumBean", albumBean)
            openActivityForResult(intent, Constants.REQUEST_POST_VIDEO)
        }
        else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
//            ivProfileUser.setImageURI(data?.data) // handle chosen image

            if (data != null) {
                val selectedImage = data.data

                if (selectedImage != null) {
//                    val inputStream: InputStream?
                    try {
//                        val selectedImage = data.data
                        val filePathColumn =
                            arrayOf(MediaStore.Images.Media.DATA)
                        val cursor: Cursor? = this@ProfileViewActivity.contentResolver.query(
                            selectedImage,
                            filePathColumn, null, null, null
                        )
                        cursor!!.moveToFirst()

                        val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
                        val picturePath: String = cursor.getString(columnIndex)
                        cursor.close()

                        GlideLib.loadImage(this, ivProfileUser, picturePath)
                        Log.d("Image Path", "Image Path  is $picturePath")
                        profileFile = Utils.saveBitmapToFile(File(picturePath))

                        if (profileFile != null && profileFile!!.exists()) {
                            isCameraOpen = true
                            profileViewModel.updateProfilePic(profileFile!!)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_CODE_CAMERA_IMAGE) {
//            if (data != null) {
            val imageFile = Utils.getCameraFile(this@ProfileViewActivity)
            val photoUri = FileProvider.getUriForFile(
                this,
                applicationContext.packageName + ".provider",
                imageFile
            )
            val bitmap: Bitmap = Utils.scaleBitmapDown(
                MediaStore.Images.Media.getBitmap(contentResolver, photoUri),
                1200
            )!!

            GlideLib.loadImageBitmap(this, ivProfileUser, bitmap)

            if (imageFile.exists()) {
                isCameraOpen = true
                profileViewModel.updateProfilePic(imageFile)
            }
//            }
        }else if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_POST_VIDEO) {

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
        }
    }

    /**
     * Open following/followers screen click on counter
     */
    fun onClickFollow(view: View) {

        val whoCanView = profileBean.safetyBean.is_followers
        if (!sessionManager.getBooleanValue(Constants.KEY_IS_COMPLETE_PROFILE)) {
            completeSignUpDialog()
        } else if (!sessionManager.isGuestUser()) {
            if (sessionManager.getUserId() == profileBean.user_id || whoCanView == 0) {
                openFollowersScreen()
            } else if (whoCanView == 1) {
                if (profileBean.is_follow == 1) {
                    openFollowersScreen()
                }
            }
        }
    }

    fun onClickFollowing(view: View){
        val whoCanView = profileBean.safetyBean.is_followers
        if (!sessionManager.getBooleanValue(Constants.KEY_IS_COMPLETE_PROFILE)) {
            completeSignUpDialog()
        } else if (!sessionManager.isGuestUser()) {
            if (sessionManager.getUserId() == profileBean.user_id || whoCanView == 0) {
                openFollowingsScreen()
            } else if (whoCanView == 1) {
                if (profileBean.is_follow == 1) {
                    openFollowingsScreen()
                }
            }
        }
    }
    fun openFollowersScreen() {
        val intent = Intent(this@ProfileViewActivity, FollowersActivity::class.java)
        intent.putExtra(Constants.PROFILE_BEAN, profileBean)
        if (sessionManager.getUserId() == profileBean.user_id)
            intent.putExtra("isMyProfile", true)
        else
            intent.putExtra("isMyProfile", false)
        intent.putExtra("title", "Follower")
        openActivity(intent)
    }

    fun openFollowingsScreen() {
        val intent = Intent(this@ProfileViewActivity, FollowingActivity::class.java)
        intent.putExtra(Constants.PROFILE_BEAN, profileBean)
        if (sessionManager.getUserId() == profileBean.user_id)
            intent.putExtra("isMyProfile", true)
        else
            intent.putExtra("isMyProfile", false)
        intent.putExtra("title", "Following")
        openActivity(intent)
    }

    fun onClickFollowRequest(view: View) {
        if (!sessionManager.getBooleanValue(Constants.KEY_IS_COMPLETE_PROFILE)) {
            completeSignUpDialog()
        }

        if (!sessionManager.isGuestUser() && sessionManager.getBooleanValue(Constants.KEY_IS_COMPLETE_PROFILE)) {
            if (profileBean.is_follow == 1) {
                profileViewModel.followUser(profileBean.user_id, 0)
            } else if (profileBean.is_follow == 0) {
                profileViewModel.followUser(profileBean.user_id, 1)
            }
        }
    }

    fun onClickEditProfile(view: View) {
//        openActivity(this@ProfileViewActivity, EditProfileActivity())
        openActivity(this@ProfileViewActivity, EditActivity())
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

    fun onClickAddVideo(view: View) {
        selectVideo()
    }

    fun onClickProfileMore(view: View) {
        if (sessionManager.getUserId() == profileBean.user_id)
            openActivity(this@ProfileViewActivity, SettingsActivity())
        else if (profileBean.username.isNotEmpty()) {
            if (!sessionManager.getBooleanValue(Constants.KEY_IS_COMPLETE_PROFILE)) {
                completeSignUpDialog()
            } else {
                openShareOptionDialog(profileBean)
            }
        }
    }

    fun onClickProfileLike(view: View) {
        if (!sessionManager.getBooleanValue(Constants.KEY_IS_COMPLETE_PROFILE)) {
            completeSignUpDialog()
        } else {
            if (!sessionManager.isGuestUser()) {
                if (profileBean.is_like == 1) {
                    profileViewModel.likeUserProfile(profileBean.user_id, 0)
                } else {
                    profileViewModel.likeUserProfile(profileBean.user_id, 1)
                }
            }
        }
    }

    override fun onSuccessProfileLike(dashboardBean: DashboardBean) {
        Log.e("ProfileViewActivity", "onSuccessProfileLike: data: \t ${dashboardBean.is_like}")
//        isLike = dashboardBean.is_like

        profileBean.is_like = dashboardBean.is_like
        if (dashboardBean.is_like == 1) {
            btnProfileLike.text = resources.getString(R.string.liked)
            btnProfileLike.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.color_text_red
                )
            )
            btnProfileLike.background =
                ContextCompat.getDrawable(this, R.drawable.rounded_btn)
            btnProfileLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart, 0, 0, 0)
        } else {
            btnProfileLike.text = resources.getString(R.string.match)
            btnProfileLike.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )
            btnProfileLike.background =
                ContextCompat.getDrawable(this, R.drawable.rounded_btn_red)
            btnProfileLike.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_like_dashboard,
                0,
                0,
                0
            )
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
                R.layout.dialog_bottom_share_feed_new,
                null
            )
        )
        if (profileBean.safetyBean.is_share == 0) {
            bottomSheetDialogShare.svShareOption.alpha = 0.3f
        }

        bottomSheetDialogShare.window?.setBackgroundDrawableResource(android.R.color.transparent)
        bottomSheetDialogShare.window?.attributes?.windowAnimations = R.style.DialogAnimation
        bottomSheetDialogShare.setCancelable(true)

        bottomSheetDialogShare.tv_user_name.text = profileBean.username
        bottomSheetDialogShare.tv_Job.text = profileBean.jobs
        if (profileBean.profileUrl.isNotBlank()) {
            GlideLib.loadImage(this@ProfileViewActivity,bottomSheetDialogShare.iv_user_profile, profileBean.profileUrl)
        }

        bottomSheetDialogShare.tvShareBlock.text = getString(R.string.block)

        bottomSheetDialogShare.ivShareReport.setImageResource(R.drawable.ic_report_new)
        bottomSheetDialogShare.tvShareReport.text = getString(R.string.report)

        bottomSheetDialogShare.ivShareMessage.setImageResource(R.drawable.ic_chat_new)
        if (profileBean.safetyBean.who_can_send_message == 2)
            bottomSheetDialogShare.tvShareMessage.text = getString(R.string.message_locked)
        else if (profileBean.safetyBean.who_can_send_message == 0) {
            bottomSheetDialogShare.tvShareMessage.text = getString(R.string.send_message)
        } else {
            if (profileBean.safetyBean.who_can_send_message == 1 && profileBean.is_follow_me == 1) {
                bottomSheetDialogShare.tvShareMessage.text = getString(R.string.send_message)
            } else {
                bottomSheetDialogShare.tvShareMessage.text = getString(R.string.message_locked)
            }
        }
        /*bottomSheetDialogShare.tvShareCancel.setOnClickListener {
            bottomSheetDialogShare.dismiss()
        }*/

        val shareMessage = String.format(
            getString(R.string.profile_link_msg),
            profileBean.username, profileBean.about_me
        )

        if (profileBean.safetyBean.is_share == 1) {
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
                val message = String.format(
                    getString(R.string.profile_link_msg),
                    profileBean.username, profileBean.about_me
                ).plus(" \n")
                    .plus(String.format(getString(R.string.profile_link), profileBean.username))
                addFragment(
                    ShareAppFragment.getInstance(
                        sessionManager.getUserId(),
                        "",
                        "",
                        message,
                        true
                    ),
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
                            ComponentName(
                                activityInfo.applicationInfo.packageName,
                                activityInfo.name
                            )
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
                    Intent.EXTRA_TEXT, shareMessage
                )
                sendIntent.type = "text/plain"
                startActivity(sendIntent)
            }
        }
        //Privacy Click
        bottomSheetDialogShare.tvShareBlock.setOnClickListener {
            displayBlockUserDialog(profileBean)
        }
        bottomSheetDialogShare.ivShareBlock.setOnClickListener {
            displayBlockUserDialog(profileBean)
        }

        //Report click
        bottomSheetDialogShare.ivShareReport.setOnClickListener {
            displayReportUserDialog(profileBean)
        }
        bottomSheetDialogShare.tvShareReport.setOnClickListener {
            // bottomSheetDialogShare.dismiss()
            displayReportUserDialog(profileBean)
        }

        //Send Message
        bottomSheetDialogShare.ivShareMessage.setOnClickListener {
            if (bottomSheetDialogShare.tvShareMessage.text == getString(R.string.send_message)) {
                clickSendMessage(profileBean)
            }
        }
        bottomSheetDialogShare.tvShareMessage.setOnClickListener {
            if (bottomSheetDialogShare.tvShareMessage.text == getString(R.string.send_message)) {
                clickSendMessage(profileBean)
            }
        }

        bottomSheetDialogShare.show()
    }

    private fun clickSendMessage(profileBean: ProfileBean) {
        val matchesListBean = MatchesListBean()
        matchesListBean.id = profileBean.user_id
        matchesListBean.username = profileBean.username
        matchesListBean.casual_name = profileBean.casual_name
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
            when (profileBean.is_follow_me) {
                1 -> intent.putExtra("isFollowMe", true)
                else -> intent.putExtra("isFollowMe", false)
            }
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
        object : CustomCommonNewAlertDialog(
            this@ProfileViewActivity,
            dashboardBean.casual_name,
            getString(R.string.msg_report_user),
            dashboardBean.profileUrl,
            getString(R.string.confirm),
            resources.getString(R.string.cancel)
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
        object : CustomCommonNewAlertDialog(
            this@ProfileViewActivity,
            dashboardBean.casual_name,
            getString(R.string.msg_block_user),
            dashboardBean.profileUrl,
            getString(R.string.confirm),
            resources.getString(R.string.cancel)
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

    override fun onSuccessBoostPriceList(boostPriceBean: ArrayList<BoostPriceBean>) {
    }

    override fun onLogoutSuccess(msg: String) {
    }

    override fun onLogoutFailed(msg: String, error: Int) {
    }

    /**
     * gives option for select video or take video from camera
     */
    private fun selectVideo() {
        bottomSheetDialog = BottomSheetDialog(this@ProfileViewActivity, R.style.dialogStyle)
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
        startActivityForResult(cameraIntent, Constants.REQUEST_POST_VIDEO)
    }
}