package com.namastey.activity

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.request.RequestOptions
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.AlbumListProfileAdapter
import com.namastey.dagger.module.GlideApp
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityProfileViewBinding
import com.namastey.listeners.OnItemClick
import com.namastey.listeners.OnViewAlbumClick
import com.namastey.model.*
import com.namastey.uiView.ProfileView
import com.namastey.utils.Constants
import com.namastey.utils.GlideLib
import com.namastey.utils.SessionManager
import com.namastey.viewModel.ProfileViewModel
import kotlinx.android.synthetic.main.activity_profile_view.*
import kotlinx.android.synthetic.main.view_private_account.*
import javax.inject.Inject

class ProfileViewActivity : BaseActivity<ActivityProfileViewBinding>(),
    ProfileView,
    OnViewAlbumClick,
    OnItemClick {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var activityProfileViewBinding: ActivityProfileViewBinding
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var albumListProfileAdapter: AlbumListProfileAdapter
    private var profileBean = ProfileBean()
    private var isMyProfile = false
    private var completeSignUp = 0

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
        tvAbouteDesc.text = profileBean.about_me
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
                viewPrivateAccount.visibility = View.VISIBLE
            } else if (profileBean.user_profile_type == 1) {
                layoutPrivateAccount.visibility = View.VISIBLE
                rvAlbumList.visibility = View.GONE
                viewPrivateAccount.visibility = View.GONE
            }
        } else {
            rvAlbumList.visibility = View.VISIBLE
            layoutPrivateAccount.visibility = View.GONE
            viewPrivateAccount.visibility = View.GONE
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
        completeSignUp = sessionManager.getCompleteSignUp()

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
            tvAbouteDesc.text = profileBean.about_me
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
                tvAbouteDesc.text = sessionManager.getStringValue(Constants.KEY_TAGLINE)
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

        if (completeSignUp == 0) {
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
        if (completeSignUp == 0) {
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
    }

    fun onClickProfileLike(view: View) {
        if (!sessionManager.isGuestUser()) {
            if (profileBean.is_like == 1) {
                profileViewModel.likeUserProfile(profileBean.user_id, 0)
            } else {
                if (completeSignUp == 0) {
                    completeSignUpDialog()
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
}