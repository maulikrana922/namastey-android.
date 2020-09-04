package com.namastey.activity

import android.graphics.Color
import android.os.Bundle
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
import com.namastey.model.InterestBean
import com.namastey.model.ProfileBean
import com.namastey.model.SocialAccountBean
import com.namastey.uiView.ProfileView
import com.namastey.utils.Constants
import com.namastey.utils.GlideLib
import com.namastey.utils.SessionManager
import com.namastey.viewModel.ProfileViewModel
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile_view.*
import kotlinx.android.synthetic.main.activity_profile_view.groupButtons
import kotlinx.android.synthetic.main.activity_profile_view.ivProfileUser
import kotlinx.android.synthetic.main.activity_profile_view.tvAbouteDesc
import kotlinx.android.synthetic.main.activity_profile_view.tvFollowersCount
import kotlinx.android.synthetic.main.activity_profile_view.tvFollowingCount
import kotlinx.android.synthetic.main.activity_profile_view.tvProfileUsername
import kotlinx.android.synthetic.main.activity_profile_view.tvViewsCount
import java.io.File
import javax.inject.Inject

class ProfileViewActivity : BaseActivity<ActivityProfileViewBinding>(), ProfileView,
    OnViewAlbumClick, OnItemClick {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var activityProfileViewBinding: ActivityProfileViewBinding
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var albumListProfileAdapter: AlbumListProfileAdapter
    private var profileBean = ProfileBean()

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
        if (profileBean.user_id == sessionManager.getUserId()){
            groupButtons.visibility = View.VISIBLE
            groupButtonsLike.visibility = View.GONE
        }else{
            groupButtons.visibility = View.INVISIBLE
            groupButtonsLike.visibility = View.VISIBLE
        }

        // Need to change
        if (profileBean.gender == Constants.Gender.female.name) {
            ivProfileTop.background = resources.getDrawable(R.drawable.female_bg)
        } else {
            ivProfileTop.background = resources.getDrawable(R.drawable.male_bg)
        }
        fillValue(profileBean)
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

        generateChooseInterestUI(profileBean.interest)
        socialAccountUI(profileBean.social_accounts)

        albumListProfileAdapter =
            AlbumListProfileAdapter(profileBean.albums, this@ProfileViewActivity, this, this)
        rvAlbumList.adapter = albumListProfileAdapter
    }

    /**
     * Generate dynamic choose interest view
     */
    private fun generateChooseInterestUI(interestList: ArrayList<InterestBean>) {
        for (interestBean in interestList) {
            val tvInterest = TextView(this@ProfileViewActivity)
            tvInterest.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            tvInterest.text = interestBean.interest_name
            tvInterest.setPadding(40, 18, 40, 18)
            tvInterest.setTextColor(Color.WHITE)
            tvInterest.setBackgroundResource(R.drawable.rounded_white_border_transparent_solid)

            chipProfileInterest.addView(tvInterest)
        }
    }

    /**
     * Add social account icons
     */
    private fun socialAccountUI(data: ArrayList<SocialAccountBean>) {

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
                }
                getString(R.string.linkedin) -> {
                    ivSocialIcon.setImageResource(R.drawable.profile_link_linkedin)
                }
            }
            chipProfileSocial.addView(ivSocialIcon)
        }
    }

    override fun getViewModel() = profileViewModel

    override fun getLayoutId() = R.layout.activity_profile_view

    override fun getBindingVariable() = BR.viewModel

    private fun initData() {

//        If profile image not set then display default image

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
            profileViewModel.getUserFullProfile(sessionManager.getUserId())
        }else{
            profileViewModel.getUserFullProfile(intent.getLongExtra(Constants.USER_ID,0))
        }

    }

    override fun onResume() {
        super.onResume()
        if (profileBean.user_id == sessionManager.getUserId()){
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

    fun onClickFollow(view: View) {
//        openActivity(this@ProfileViewActivity, FollowingFollowersActivity())
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
        TODO("Not yet implemented")
    }

    override fun onViewAlbumItemClick(value: Long, position: Int) {
        TODO("Not yet implemented")
    }
}