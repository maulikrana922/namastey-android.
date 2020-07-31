package com.namastey.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener
import com.google.gson.JsonObject
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.EditProfileActivity
import com.namastey.activity.ProfileInterestActivity
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentEditProfileBinding
import com.namastey.listeners.OnInteractionWithFragment
import com.namastey.model.JobBean
import com.namastey.model.ProfileBean
import com.namastey.model.SocialAccountBean
import com.namastey.uiView.ProfileBasicView
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import com.namastey.utils.Utils
import com.namastey.viewModel.ProfileBasicViewModel
import kotlinx.android.synthetic.main.view_profile_basic_info.*
import kotlinx.android.synthetic.main.view_profile_select_interest.*
import kotlinx.android.synthetic.main.view_profile_tag.view.*
import javax.inject.Inject

class EditProfileFragment : BaseFragment<FragmentEditProfileBinding>(), ProfileBasicView,
    OnInteractionWithFragment {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var fragmentEditProfileBinding: FragmentEditProfileBinding
    private lateinit var layoutView: View
    private lateinit var profileBasicViewModel: ProfileBasicViewModel
    private var isEditUsername = false
    private var isEditTagLine = false

    override fun onSuccessProfileDetails(profileBean: ProfileBean) {
        fillValue(profileBean)
    }

    override fun getViewModel() = profileBasicViewModel

    override fun getLayoutId() = R.layout.fragment_edit_profile

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance() =
            EditProfileFragment().apply {

            }
    }

    private fun setupViewModel() {
        profileBasicViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ProfileBasicViewModel::class.java)
        profileBasicViewModel.setViewInterface(this)

        fragmentEditProfileBinding = getViewBinding()
        fragmentEditProfileBinding.viewModel = profileBasicViewModel

        initData()
    }

    private fun initData() {

        (activity as EditProfileActivity).setListenerOfInteractionWithFragment(this)

//        rangeProfileAge.apply()
        rangeProfileAge.setOnRangeSeekbarChangeListener(OnRangeSeekbarChangeListener { minValue, maxValue ->
            tvProfileAgeValue.text = "$minValue and $maxValue"
        })

        profileBasicViewModel.getUserFullProfile()
//        generateProfileTagUI()
        edtProfileCasualName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_edit_gray, 0);
        edtProfileCasualName.compoundDrawablePadding = 25

        edtProfileTagline.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_edit_gray, 0);
        edtProfileTagline.compoundDrawablePadding = 25

        edtProfileCasualName.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                val DRAWABLE_RIGHT = 2
                if (event.getAction() === MotionEvent.ACTION_UP) {
                    if (event.rawX >= edtProfileCasualName.right - (edtProfileCasualName.compoundDrawables[DRAWABLE_RIGHT].bounds.width() + 50)
                    ) {
                        if (isEditUsername){
                            isEditUsername = false
                            edtProfileCasualName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_edit_gray, 0);
                        }else{
                            isEditUsername = true
                            edtProfileCasualName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_red, 0);
                        }

                        return true
                    }
                }
                return false
            }
        })
        edtProfileTagline.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                val DRAWABLE_RIGHT = 2
                if (event.getAction() === MotionEvent.ACTION_UP) {
                    if (event.rawX >= edtProfileCasualName.right - (edtProfileCasualName.compoundDrawables[DRAWABLE_RIGHT].bounds.width() + 50)
                    ) {
                        if (isEditTagLine){
                            isEditTagLine = false
                            edtProfileTagline.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_edit_gray, 0);
                        }else{
                            isEditTagLine = true
                            edtProfileTagline.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_red, 0);
                        }

                        return true
                    }
                }
                return false
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutView = view
        setupViewModel()

//        initData()

    }

    private fun generateProfileTagUI() {
        var profileTagCount = 0
        if (sessionManager.getCategoryList().size > 0) {
            for (categoryBean in sessionManager.getCategoryList()) {
                var layoutInflater = LayoutInflater.from(requireActivity())
                var view = layoutInflater.inflate(R.layout.view_profile_tag, llProfileTag, false)
                view.tvCategory.text = categoryBean.name.toString()

                for (subCategoryBean in categoryBean.sub_category) {
                    val tvCategory = TextView(requireActivity())
                    tvCategory.layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )

                    tvCategory.text = subCategoryBean.name.toString()
                    tvCategory.setPadding(40, 20, 40, 20)
                    tvCategory.setTextColor(Color.WHITE)
                    tvCategory.setBackgroundResource(R.drawable.rounded_gray_solid)

                    view.chipProfileTag.addView(tvCategory)

                    if (subCategoryBean.is_selected == 1){
                        ++profileTagCount
                        Utils.rectangleShapeGradient(
                            tvCategory, resources.getColor(R.color.gradient_six_start),
                            resources.getColor(R.color.gradient_six_end)
                        )
                    }
                    tvCategory.setOnClickListener {

                        if (tvCategory.background.constantState == ContextCompat.getDrawable(
                                requireActivity(),
                                R.drawable.rounded_gray_solid
                            )?.constantState
                        ) {
                            ++profileTagCount
                            Utils.rectangleShapeGradient(
                                tvCategory, resources.getColor(R.color.gradient_six_start),
                                resources.getColor(R.color.gradient_six_end)
                            )
                        } else {
                            --profileTagCount
                            tvCategory.setBackgroundResource(R.drawable.rounded_gray_solid)
                        }
                        tvCountProfileTag.text = profileTagCount.toString()
                    }
                }

                llProfileTag.addView(view)

                view.tvCategory.setOnClickListener {
                    //                    if (categoryBean.sub_category.size > 0) {
                    if (view.chipProfileTag.visibility == View.VISIBLE) {
                        view.chipProfileTag.visibility = View.GONE
                        view.tvCategory.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_arrow_down_gray,
                            0
                        )
                    } else {
                        view.chipProfileTag.visibility = View.VISIBLE
                        view.tvCategory.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_arrow_up_gray,
                            0
                        )
                    }
                }
            }
        }
        tvCountProfileTag.text = profileTagCount.toString()
    }

    /**
     * Generate social account UI for added on profile
     */
    private fun socialAccountUI(data: ArrayList<SocialAccountBean>) {
        if (data.any{ socialAccountBean -> socialAccountBean.name == getString(R.string.facebook) }){
            mainFacebook.visibility = View.VISIBLE
            tvFacebookLink.text = data.single { s -> s.name == getString(R.string.facebook) }
                .link
        }else{
            mainFacebook.visibility = View.GONE
        }

        if (data.any{ socialAccountBean -> socialAccountBean.name == getString(R.string.instagram) }){
            mainInstagram.visibility = View.VISIBLE
            tvInstagramLink.text = data.single { s -> s.name == getString(R.string.instagram) }
                .link
        }else{
            mainInstagram.visibility = View.GONE
        }

        if (data.any{ socialAccountBean -> socialAccountBean.name == getString(R.string.snapchat) }){
            mainSnapchat.visibility = View.VISIBLE
            tvSnapchatLink.text = data.single { s -> s.name == getString(R.string.snapchat) }
                .link
        }else{
            mainSnapchat.visibility = View.GONE
        }

        if (data.any{ socialAccountBean -> socialAccountBean.name == getString(R.string.tiktok) }){
            mainTikTok.visibility = View.VISIBLE
            tvTiktokLink.text = data.single { s -> s.name == getString(R.string.tiktok) }
                .link
        }else{
            mainTikTok.visibility = View.GONE
        }
        if (data.any{ socialAccountBean -> socialAccountBean.name == getString(R.string.spotify) }){
            mainSpotify.visibility = View.VISIBLE
            tvSpotifyLink.text = data.single { s -> s.name == getString(R.string.spotify) }
                .link
        }else{
            mainSpotify.visibility = View.GONE
        }
        if (data.any{ socialAccountBean -> socialAccountBean.name == getString(R.string.linkedin) }){
            mainLinkedin.visibility = View.VISIBLE
            tvLinkedinLink.text = data.single { s -> s.name == getString(R.string.linkedin) }
                .link
        }else{
            mainLinkedin.visibility = View.GONE
        }
    }


    override fun onClickOfFragmentView(view: View) {
        onClick(view)
    }

    private fun onClick(view: View) {
        when (view) {
            llCategory -> {
                (activity as EditProfileActivity).addFragment(
                    SelectCategoryFragment.getInstance(
                    ),
                    Constants.SELECT_CATEGORY_FRAGMENT
                )
            }
            llInterestIn -> {
                (activity as EditProfileActivity).addFragment(
                    InterestInFragment.getInstance(
                    ),
                    Constants.INTEREST_IN_FRAGMENT
                )
            }
            llEducation -> {
                (activity as EditProfileActivity).addFragment(
                    EducationFragment.getInstance(
                    ),
                    Constants.EDUCATION_FRAGMENT
                )
            }
            llJob -> {
                (activity as EditProfileActivity).addFragment(
                    JobFragment.getInstance(
                    ),
                    Constants.JOB_FRAGMENT
                )
            }
            // click on add link button open new fragment social link
            ivAddLink -> {
                (activity as EditProfileActivity).addFragment(
                    AddLinksFragment.getInstance(true, ArrayList<SocialAccountBean>()),
                    Constants.ADD_LINKS_FRAGMENT
                )
            }
        }

    }

    private fun fillValue(profileBean: ProfileBean) {

        sessionManager.setStringValue(profileBean.max_age.toString(),Constants.KEY_AGE_MAX)
        sessionManager.setStringValue(profileBean.min_age.toString(),Constants.KEY_AGE_MIN)
        sessionManager.setStringValue(profileBean.about_me,Constants.KEY_TAGLINE)

        edtProfileCasualName.setText(profileBean.username)
        edtProfileTagline.setText(profileBean.about_me)
        sessionManager.setCategoryList(profileBean.category)
        generateProfileTagUI()

        if (profileBean.education.size > 0) {
            sessionManager.setEducationBean(profileBean.education[0])
            tvProfileEducation.text = sessionManager.getEducationBean().course
        }
        if (profileBean.jobs.size > 0) {
            sessionManager.setJobBean(profileBean.jobs[0])
            tvProfileJobs.text = sessionManager.getJobBean().title
        }
        rangeProfileAge.setMaxStartValue(
            sessionManager.getStringValue(Constants.KEY_AGE_MAX).toFloat()
        )
        rangeProfileAge.setMinStartValue(
            sessionManager.getStringValue(Constants.KEY_AGE_MIN).toFloat()
        )
        rangeProfileAge.apply()
        sessionManager.setInterestIn(profileBean.interest_in_gender)
        if (profileBean.interest_in_gender != 0) {
            when (sessionManager.getInterestIn()) {
                1 -> tvProfileInterestIn.text = getString(R.string.men)
                2 -> tvProfileInterestIn.text = getString(R.string.women)
                3 -> tvProfileInterestIn.text = getString(R.string.everyone)
            }
//            llInterestIn.setBackgroundResource(R.drawable.rounded_white_solid)
        }
        var socialAccountList = profileBean.social_accounts
        socialAccountUI(socialAccountList)

        if (sessionManager.getCategoryList().size >= 3) {
            tvProfileSelectCategory.text = sessionManager.getCategoryList().get(0).name
            llCategory.setBackgroundResource(R.drawable.rounded_white_solid)
        }


//        llEducation.setBackgroundResource(R.drawable.rounded_white_solid)

//        if (sessionManager.getJobBean().title.isNotEmpty()) {
//            tvProfileJobs.text = sessionManager.getJobBean().title
//            llJob.setBackgroundResource(R.drawable.rounded_white_solid)
//        }
    }

    fun editProfileApiCall() {

        val jsonObject = JsonObject()

        jsonObject.addProperty(
            Constants.USERNAME,
            edtProfileCasualName.text.toString().trim()
        )

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
            edtProfileTagline.text.toString().trim()
        )
        jsonObject.addProperty(Constants.GENDER, sessionManager.getInterestIn())
        jsonObject.addProperty(
            Constants.TAGS,
            ProfileInterestActivity.categoryIdList.joinToString()
        )

        var categoryIdList: ArrayList<Int> = ArrayList()
        for (category in  sessionManager.getCategoryList()){
            categoryIdList.add(category.id)
        }
        jsonObject.addProperty(
            Constants.CATEGORY_ID,
            categoryIdList.joinToString()
        )
        var socialAccountId = ArrayList<Long>()
        for (data in ProfileInterestActivity.socialAccountList) {
            socialAccountId.add(data.id)
        }
        jsonObject.addProperty(Constants.SOCIAL_ACCOUNTS, socialAccountId.joinToString())
        jsonObject.addProperty(
            Constants.EDUCATION,
            sessionManager.getEducationBean().user_education_Id
        )
        jsonObject.addProperty(
            Constants.EDUCATION,
            sessionManager.getEducationBean().user_education_Id
        )
        jsonObject.addProperty(Constants.JOBS, sessionManager.getJobBean().id)
        var albumId = ArrayList<Long>()
//        for (data in albumList) {
//            albumId.add(data.id)
//        }
//        jsonObject.addProperty(Constants.ALBUMS, albumId.joinToString())

        jsonObject.addProperty(Constants.DEVICE_ID, "23456789")    // Need to change
        jsonObject.addProperty(Constants.DEVICE_TYPE, Constants.ANDROID)

        Log.d("CreateProfile Request:",jsonObject.toString())

//        profileBasicViewModel.getUserFullProfile(jsonObject)
    }

    override fun onDestroy() {
        profileBasicViewModel.onDestroy()
        super.onDestroy()
    }
}