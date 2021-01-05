package com.namastey.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
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
import com.namastey.activity.EducationListActivity
import com.namastey.activity.JobListingActivity
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentEditProfileBinding
import com.namastey.listeners.OnInteractionWithFragment
import com.namastey.model.ErrorBean
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
    private val TAG: String = EditProfileFragment::class.java.simpleName

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var fragmentEditProfileBinding: FragmentEditProfileBinding
    private lateinit var layoutView: View
    private lateinit var profileBasicViewModel: ProfileBasicViewModel
    private var isEditUsername = false
    private var isEditTagLine = false
    private var socialAccountList: ArrayList<SocialAccountBean> = ArrayList()
    private var subCategoryIdList: ArrayList<Int> = ArrayList()

    override fun onSuccessProfileDetails(profileBean: ProfileBean) {
        fillValue(profileBean)
    }

    override fun onSuccessSocialAccount(data: ArrayList<SocialAccountBean>) {
        socialAccountList = data
        socialAccountUI(socialAccountList)

        editProfileApiCall()
    }

    override fun onFailedUniqueName(error: ErrorBean?) {
        Log.e(TAG, "onFailedUniqueName: Error: \t ${error!!.user_name}")
        tvUniqueNameError.visibility = View.VISIBLE
        tvUniqueNameError.text = error!!.user_name
    }

    override fun onSuccessUniqueName(msg: String) {
        super.onSuccess(msg)
        tvUniqueNameError.visibility = View.GONE
        Log.e(TAG, "onSuccess: Error: \t $msg")
    }

    override fun onSuccess(msg: String) {
        Utils.hideKeyboard(requireActivity())
        Log.d("Success : ", msg)
        isEditTagLine = false
        isEditUsername = false
        sessionManager.setStringValue(edtProfileCasualName.text.toString().trim(),Constants.KEY_CASUAL_NAME)
        sessionManager.setStringValue(edtProfileTagline.text.toString().trim(),Constants.KEY_TAGLINE)
        edtProfileCasualName.setCompoundDrawablesWithIntrinsicBounds(
            0,
            0,
            R.drawable.ic_edit_gray,
            0
        );
        edtProfileTagline.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_edit_gray, 0);

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

        rangeProfileAge.setOnRangeSeekbarFinalValueListener { minValue, maxValue ->
            Log.d("min max:", "$minValue $maxValue")
            sessionManager.setStringValue(maxValue.toString(), Constants.KEY_AGE_MAX)
            sessionManager.setStringValue(minValue.toString(), Constants.KEY_AGE_MIN)
            editProfileApiCall()
        }
        edtProfileCasualName.inputType = InputType.TYPE_NULL
        edtProfileTagline.inputType = InputType.TYPE_NULL
        edtProfileTagline.minLines = 5
        edtProfileTagline.maxLines = 5

        profileBasicViewModel.getUserFullProfile(sessionManager.getUserId())
//        generateProfileTagUI()
        edtProfileCasualName.setCompoundDrawablesWithIntrinsicBounds(
            0,
            0,
            R.drawable.ic_edit_gray,
            0
        );
        edtProfileCasualName.compoundDrawablePadding = 25

        edtProfileTagline.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_edit_gray, 0);
        edtProfileTagline.compoundDrawablePadding = 25

        edtProfileCasualName.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                val DRAWABLE_RIGHT = 2
                if (event.getAction() === MotionEvent.ACTION_UP) {
                    if (event.rawX >= edtProfileCasualName.right - (edtProfileCasualName.compoundDrawables[DRAWABLE_RIGHT].bounds.width() + 50)
                    ) {
                        if (isEditUsername) {
                            isEditUsername = false
                            edtProfileCasualName.inputType = InputType.TYPE_NULL
                            edtProfileCasualName.clearFocus()
                            editProfileApiCall()
                            edtProfileCasualName.setCompoundDrawablesWithIntrinsicBounds(
                                0,
                                0,
                                R.drawable.ic_edit_gray,
                                0
                            );
                        } else {
                            isEditUsername = true
                            edtProfileCasualName.requestFocus()
                            edtProfileCasualName.inputType = InputType.TYPE_CLASS_TEXT
                            edtProfileCasualName.setCompoundDrawablesWithIntrinsicBounds(
                                0,
                                0,
                                R.drawable.ic_done_red,
                                0
                            );
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
                    if (event.rawX >= edtProfileTagline.right - (edtProfileTagline.compoundDrawables[DRAWABLE_RIGHT].bounds.width() + 50)
                    ) {
                        if (isEditTagLine) {
                            isEditTagLine = false
                            edtProfileTagline.inputType = InputType.TYPE_NULL
                            edtProfileTagline.clearFocus()
                            editProfileApiCall()
                            edtProfileTagline.setCompoundDrawablesWithIntrinsicBounds(
                                0,
                                0,
                                R.drawable.ic_edit_gray,
                                0
                            );
                        } else {
                            isEditTagLine = true
                            edtProfileTagline.inputType = InputType.TYPE_CLASS_TEXT
                            edtProfileTagline.requestFocus()
                            edtProfileTagline.setCompoundDrawablesWithIntrinsicBounds(
                                0,
                                0,
                                R.drawable.ic_done_red,
                                0
                            );
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
                val layoutInflater = LayoutInflater.from(requireActivity())
                val view = layoutInflater.inflate(R.layout.view_profile_tag, llProfileTag, false)
                view.tvCategory.text = categoryBean.name.toString()

                //                    if (categoryBean.sub_category.size > 0) {
                when {
                    categoryBean.startColor.isEmpty() -> {
                        categoryBean.startColor = "#B2BAF2"     // Default set if colours not found
                        categoryBean.endColor = "#28BAD3"
                    }
                }

                Utils.rectangleShapeGradient(
                    view, intArrayOf(
                        Color.parseColor(categoryBean.startColor),
                        Color.parseColor(categoryBean.endColor)
                    )
                )

                for (subCategoryBean in categoryBean.sub_category) {
                    val tvSubCategory = TextView(requireActivity())
                    tvSubCategory.layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )

                    tvSubCategory.text = subCategoryBean.name.toString()
                    tvSubCategory.setPadding(40, 20, 40, 20)
                    tvSubCategory.setTextColor(Color.BLACK)
                    tvSubCategory.setBackgroundResource(R.drawable.rounded_white_solid_all_corner)

                    view.chipProfileTag.addView(tvSubCategory)

                    if (subCategoryBean.is_selected == 1) {
                        subCategoryIdList.add(subCategoryBean.id)
                        ++profileTagCount
                        tvSubCategory.setTextColor(Color.WHITE)
                        tvSubCategory.setBackgroundResource(R.drawable.rounded_green_solid_all_corner)

                    }
                    tvSubCategory.setOnClickListener {

                        if (tvSubCategory.background.constantState == ContextCompat.getDrawable(
                                requireActivity(),
                                R.drawable.rounded_white_solid_all_corner
                            )?.constantState
                        ) {
                            subCategoryIdList.add(subCategoryBean.id)
                            ++profileTagCount
                            tvSubCategory.setTextColor(Color.WHITE)
                            tvSubCategory.setBackgroundResource(R.drawable.rounded_green_solid_all_corner)

                            editProfileApiCall()

                        } else {
                            subCategoryIdList.remove(subCategoryBean.id)
                            --profileTagCount
                            tvSubCategory.setTextColor(Color.BLACK)
                            tvSubCategory.setBackgroundResource(R.drawable.rounded_white_solid_all_corner)

                            editProfileApiCall()
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
                        val count = llProfileTag.childCount -1
                        for (index in 0..count){
                            val viewCategory = llProfileTag.getChildAt(index)
                            viewCategory.chipProfileTag.visibility = View.GONE
                            viewCategory.tvCategory.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                0,
                                0,
                                R.drawable.ic_arrow_down_gray,
                                0
                            )
                        }
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
        if (data.any { socialAccountBean -> socialAccountBean.name == getString(R.string.facebook) }) {
            mainFacebook.visibility = View.VISIBLE
            tvFacebookLink.text = data.single { s -> s.name == getString(R.string.facebook) }
                .link
        } else {
            mainFacebook.visibility = View.GONE
        }

        if (data.any { socialAccountBean -> socialAccountBean.name == getString(R.string.instagram) }) {
            mainInstagram.visibility = View.VISIBLE
            tvInstagramLink.text = data.single { s -> s.name == getString(R.string.instagram) }
                .link
        } else {
            mainInstagram.visibility = View.GONE
        }

//        if (data.any { socialAccountBean -> socialAccountBean.name == getString(R.string.snapchat) }) {
//            mainSnapchat.visibility = View.VISIBLE
//            tvSnapchatLink.text = data.single { s -> s.name == getString(R.string.snapchat) }
//                .link
//        } else {
//            mainSnapchat.visibility = View.GONE
//        }
//
//        if (data.any { socialAccountBean -> socialAccountBean.name == getString(R.string.tiktok) }) {
//            mainTikTok.visibility = View.VISIBLE
//            tvTiktokLink.text = data.single { s -> s.name == getString(R.string.tiktok) }
//                .link
//        } else {
//            mainTikTok.visibility = View.GONE
//        }
        if (data.any { socialAccountBean -> socialAccountBean.name == getString(R.string.twitter) }) {
            mainTwitter.visibility = View.VISIBLE
            tvTwitterLink.text = data.single { s -> s.name == getString(R.string.twitter) }
                .link
        } else {
            mainTwitter.visibility = View.GONE
        }
        if (data.any { socialAccountBean -> socialAccountBean.name == getString(R.string.spotify) }) {
            mainSpotify.visibility = View.VISIBLE
            tvSpotifyLink.text = data.single { s -> s.name == getString(R.string.spotify) }
                .link
        } else {
            mainSpotify.visibility = View.GONE
        }
//        if (data.any { socialAccountBean -> socialAccountBean.name == getString(R.string.linkedin) }) {
//            mainLinkedin.visibility = View.VISIBLE
//            tvLinkedinLink.text = data.single { s -> s.name == getString(R.string.linkedin) }
//                .link
//        } else {
//            mainLinkedin.visibility = View.GONE
//        }
    }

    override fun onClickOfFragmentView(view: View) {
        onClick(view)
    }

    private fun onClick(view: View) {
        when (view) {
            llCategory -> {
                val categoryFragment = SelectCategoryFragment.getInstance()
                categoryFragment.setTargetFragment(this, Constants.REQUEST_CODE)
                (activity as EditProfileActivity).addFragment(
                    categoryFragment,
                    Constants.SELECT_CATEGORY_FRAGMENT
                )
            }
            llInterestIn -> {
                val interestInFragment = InterestInFragment.getInstance()
                interestInFragment.setTargetFragment(this, Constants.REQUEST_CODE)
                (activity as EditProfileActivity).addFragment(
                    interestInFragment,
                    Constants.INTEREST_IN_FRAGMENT
                )
            }
            llEducation -> {
                openActivityWithResultCode(
                    requireActivity(),
                    EducationListActivity(),
                    Constants.REQUEST_CODE_EDUCATION
                )
            }
            llJob -> {
                openActivityWithResultCode(
                    requireActivity(),
                    JobListingActivity(),
                    Constants.REQUEST_CODE_JOB
                )
            }
            // click on add link button open new fragment social link
            ivAddLink -> {
                val addLinksFragment = AddLinksFragment.getInstance(true, socialAccountList)
                addLinksFragment.setTargetFragment(this, Constants.REQUEST_CODE)
                (activity as EditProfileActivity).addFragment(
                    addLinksFragment,
                    Constants.ADD_LINKS_FRAGMENT
                )
            }
        }

    }

    private fun fillValue(profileBean: ProfileBean) {

        sessionManager.setStringValue(profileBean.max_age.toString(), Constants.KEY_AGE_MAX)
        sessionManager.setStringValue(profileBean.min_age.toString(), Constants.KEY_AGE_MIN)
        sessionManager.setStringValue(profileBean.about_me, Constants.KEY_TAGLINE)
        sessionManager.setStringValue(profileBean.profileUrl, Constants.KEY_PROFILE_URL)
        sessionManager.setStringValue(profileBean.username, Constants.KEY_CASUAL_NAME)
        sessionManager.setIntegerValue(profileBean.age, Constants.KEY_AGE)

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
        }
        socialAccountList = profileBean.social_accounts
        socialAccountUI(socialAccountList)

        if (sessionManager.getCategoryList().size >= 3) {
            tvProfileSelectCategory.text = sessionManager.getCategoryList().get(0).name
            llCategory.setBackgroundResource(R.drawable.rounded_white_solid)
        }
    }

    private fun editProfileApiCall() {

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
            subCategoryIdList.joinToString()
        )

        val categoryIdList: ArrayList<Int> = ArrayList()
        for (category in sessionManager.getCategoryList()) {
            categoryIdList.add(category.id)
        }
        jsonObject.addProperty(
            Constants.CATEGORY_ID,
            categoryIdList.joinToString()
        )
        val socialAccountId = ArrayList<Long>()
        for (data in socialAccountList) {
            socialAccountId.add(data.id)
        }
        jsonObject.addProperty(Constants.SOCIAL_ACCOUNTS, socialAccountId.joinToString())
        jsonObject.addProperty(
            Constants.EDUCATION,
            sessionManager.getEducationBean().id
        )

        jsonObject.addProperty(Constants.JOBS, sessionManager.getJobBean().id)

        jsonObject.addProperty(Constants.DEVICE_ID, "23456789")    // Need to change
        jsonObject.addProperty(Constants.DEVICE_TYPE, Constants.ANDROID)

        Log.d("CreateProfile Request:", jsonObject.toString())

        profileBasicViewModel.editProfile(jsonObject)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("onActivityResult", "onActivityResult")
        if (requestCode == Constants.REQUEST_CODE_EDUCATION || requestCode == Constants.REQUEST_CODE_JOB){
            tvProfileJobs.text = sessionManager.getJobBean().title
            tvProfileEducation.text = sessionManager.getEducationBean().course
            editProfileApiCall()
        }else if (data != null) {
            when {
                data.hasExtra("fromInterestIn") -> {
                    when (sessionManager.getInterestIn()) {
                        1 -> tvProfileInterestIn.text = getString(R.string.men)
                        2 -> tvProfileInterestIn.text = getString(R.string.women)
                        3 -> tvProfileInterestIn.text = getString(R.string.everyone)
                    }
                    editProfileApiCall()
                }
                data.hasExtra("fromSelectCategory") -> {
                    tvProfileSelectCategory.text = sessionManager.getCategoryList()[0].name
                    llProfileTag.removeAllViews()
                    subCategoryIdList.clear()
                    generateProfileTagUI()

                    editProfileApiCall()
                }
                data.hasExtra("fromAddLink") -> {
                    profileBasicViewModel.getSocialLink()
                }

                //        Log.d("onActivityResult","onActivityResult")
            }

//        Log.d("onActivityResult","onActivityResult")
        }
    }

    override fun onDestroy() {
        profileBasicViewModel.onDestroy()
        super.onDestroy()
    }
}