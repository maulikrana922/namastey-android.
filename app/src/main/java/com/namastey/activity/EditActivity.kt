package com.namastey.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.InputType
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.google.gson.JsonObject
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityEditBinding
import com.namastey.model.ErrorBean
import com.namastey.model.ProfileBean
import com.namastey.model.SocialAccountBean
import com.namastey.uiView.ProfileBasicView
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import com.namastey.utils.Utils
import com.namastey.viewModel.ProfileBasicViewModel
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.view_profile_select_interest.*
import java.util.*
import javax.inject.Inject

class EditActivity : BaseActivity<ActivityEditBinding>(), ProfileBasicView {

    private val TAG: String = EditActivity::class.java.simpleName

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var profileBasicViewModel: ProfileBasicViewModel

    private lateinit var activityEditBinding: ActivityEditBinding
    private var isEditUsername = false
    private var isEditTagLine = false
    private var isEditCasualName = false
    private var socialAccountList: ArrayList<SocialAccountBean> = ArrayList()
    private var subCategoryIdList: ArrayList<Int> = ArrayList()
    private var fromAddSocialLink = false

    override fun onSuccessProfileDetails(profileBean: ProfileBean) {
        fillValue(profileBean)
        if (fromAddSocialLink) {

        }
    }

    override fun onSuccessSocialAccount(data: ArrayList<SocialAccountBean>) {
        socialAccountList = data
        socialAccountUI(socialAccountList)

        editProfileApiCall()
    }

    private fun socialAccountUI(data: ArrayList<SocialAccountBean>) {
//        TODO: Code comment Started
/*
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
*/
//        TODO: Code comment End


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

//        TODO: Code Commented Started
/*
        if (data.any { socialAccountBean -> socialAccountBean.name == getString(R.string.twitter) }) {
            mainTwitter.visibility = View.VISIBLE
            tvTwitterLink.text = data.single { s -> s.name == getString(R.string.twitter) }
                .link
        } else {
            mainTwitter.visibility = View.GONE
        }
*/
/*
        if (data.any { socialAccountBean -> socialAccountBean.name == getString(R.string.spotify) }) {
            mainSpotify.visibility = View.VISIBLE
            tvSpotifyLink.text = data.single { s -> s.name == getString(R.string.spotify) }
                .link
        } else {
            mainSpotify.visibility = View.GONE
        }
*/
//        TODO: Code Commented End

//        if (data.any { socialAccountBean -> socialAccountBean.name == getString(R.string.linkedin) }) {
//            mainLinkedin.visibility = View.VISIBLE
//            tvLinkedinLink.text = data.single { s -> s.name == getString(R.string.linkedin) }
//                .link
//        } else {
//            mainLinkedin.visibility = View.GONE
//        }
    }

    override fun onFailedUniqueName(error: ErrorBean?) {
        Log.e(TAG, "onFailedUniqueName: Error: \t ${error!!.user_name}")
        isEditUsername = true
        edtProfileUserName.requestFocus()
        edtProfileUserName.inputType = InputType.TYPE_CLASS_TEXT
        edtProfileUserName.setCompoundDrawablesWithIntrinsicBounds(
            0,
            0,
            R.drawable.ic_tick_square,
            0
        )
    }

    override fun onSuccessUniqueName(msg: String) {
        //super.onSuccess(msg)
        editProfileApiCall()
        Log.e(TAG, "onSuccess: Error: \t $msg")
    }

    override fun onSuccess(msg: String) {
        Utils.hideKeyboard(this)
        Log.d("Success : ", msg)
        isEditTagLine = false
        isEditUsername = false
        isEditCasualName = false
        sessionManager.setStringValue(
            edtProfileUserName.text.toString().trim(),
            Constants.KEY_MAIN_USER_NAME
        )
        sessionManager.setStringValue(
            edtProfileCasualName.text.toString().trim(),

            Constants.KEY_CASUAL_NAME
        )
        sessionManager.setStringValue(
            edtProfileTagline.text.toString().trim(),
            Constants.KEY_TAGLINE
        )
        edtProfileUserName.setCompoundDrawablesWithIntrinsicBounds(
            0,
            0,
            R.drawable.ic_edit_gray,
            0
        )
/*        edtProfileCasualName.setCompoundDrawablesWithIntrinsicBounds(
            0,
            0,
            R.drawable.ic_edit_gray,
            0
        )
        edtProfileTagline.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_edit_gray, 0)
*/

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(TAG, "onCreate: ")
        getActivityComponent().inject(this)

        setupViewModel()
    }

    override fun getViewModel() = profileBasicViewModel

    override fun getLayoutId() = R.layout.activity_edit

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance() =
            EditActivity().apply {
                /*arguments = Bundle().apply {
                    putBoolean("fromAddSocialLink", fromAddSocialLink)
                }*/
            }
    }

    private fun setupViewModel() {
        profileBasicViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ProfileBasicViewModel::class.java)
        profileBasicViewModel.setViewInterface(this)

        activityEditBinding = bindViewData()
        activityEditBinding.viewModel = profileBasicViewModel

        initData()
    }

    private fun initData() {

        Log.e("EditProfileFragment", "FromAddSocialLink: \t $fromAddSocialLink")

//        edtProfileUserName.inputType = InputType.TYPE_NULL
//        edtProfileCasualName.inputType = InputType.TYPE_NULL
//        edtProfileTagline.inputType = InputType.TYPE_NULL
        edtProfileTagline.minLines = 5
        edtProfileTagline.maxLines = 5

        profileBasicViewModel.getUserFullProfile(sessionManager.getUserId().toString(), "")
//        generateProfileTagUI()
        edtProfileUserName.setCompoundDrawablesWithIntrinsicBounds(
            0,
            0,
            R.drawable.ic_edit_gray,
            0
        )
        edtProfileUserName.compoundDrawablePadding = 25
/*
        edtProfileCasualName.setCompoundDrawablesWithIntrinsicBounds(
            0,
            0,
            R.drawable.ic_edit_gray,
            0
        )
        edtProfileCasualName.compoundDrawablePadding = 25

        edtProfileTagline.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_edit_gray, 0)
        edtProfileTagline.compoundDrawablePadding = 25
*/

        edtProfileUserName.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                val DRAWABLE_RIGHT = 2
                if (event.action === MotionEvent.ACTION_UP) {
                    if (event.rawX >= edtProfileUserName.right - (edtProfileUserName.compoundDrawables[DRAWABLE_RIGHT].bounds.width() + 50)
                    ) {
                        if (isEditUsername) {
                            isEditUsername = false
                            edtProfileUserName.inputType = InputType.TYPE_NULL
                            edtProfileUserName.clearFocus()
                            if (edtProfileUserName.text.toString()
                                    .trim() != sessionManager.getStringValue(Constants.KEY_MAIN_USER_NAME)
                            ) {
                                //editProfileApiCall()
                                profileBasicViewModel.checkUniqueUsername(
                                    edtProfileUserName.text.toString().trim()
                                )
                            }
                            edtProfileUserName.setCompoundDrawablesWithIntrinsicBounds(
                                0,
                                0,
                                R.drawable.ic_edit_gray,
                                0
                            )
                        } else {
                            isEditUsername = true
                            edtProfileUserName.requestFocus()
                            edtProfileUserName.inputType = InputType.TYPE_CLASS_TEXT
                            edtProfileUserName.setCompoundDrawablesWithIntrinsicBounds(
                                0,
                                0,
                                R.drawable.ic_tick_square,
                                0
                            )
                        }

                        return true
                    }
                }
                return false
            }
        })

/*
        edtProfileCasualName.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                val DRAWABLE_RIGHT = 2
                if (event.action === MotionEvent.ACTION_UP) {
                    if (event.rawX >= edtProfileCasualName.right - (edtProfileCasualName.compoundDrawables[DRAWABLE_RIGHT].bounds.width() + 50)
                    ) {
                        if (isEditCasualName) {
                            isEditCasualName = false
*/
/*
//                            edtProfileCasualName.inputType = InputType.TYPE_NULL
                            edtProfileCasualName.clearFocus()
                            editProfileApiCall()
                            edtProfileCasualName.setCompoundDrawablesWithIntrinsicBounds(
                                0,
                                0,
                                R.drawable.ic_edit_gray,
                                0
                            )
*//*

                        } else {
                            isEditCasualName = true
*/
/*
                            edtProfileCasualName.inputType = InputType.TYPE_CLASS_TEXT
                            edtProfileCasualName.requestFocus()
                            edtProfileCasualName.setCompoundDrawablesWithIntrinsicBounds(
                                0,
                                0,
                                R.drawable.ic_tick_square,
                                0
                            )
*//*

                        }

                        return true
                    }
                }
                return false
            }
        })
*/

/*
        edtProfileTagline.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                val DRAWABLE_RIGHT = 2
                if (event.action === MotionEvent.ACTION_UP) {
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
                            )
                        } else {
                            isEditTagLine = true
                            edtProfileTagline.inputType = InputType.TYPE_CLASS_TEXT
                            edtProfileTagline.requestFocus()

                            edtProfileTagline.setCompoundDrawablesWithIntrinsicBounds(
                                0,
                                0,
                                R.drawable.ic_tick_square,
                                0
                            )
                        }

                        return true
                    }
                }
                return false
            }
        })
*/
    }

    private fun fillValue(profileBean: ProfileBean) {
        sessionManager.setStringValue(profileBean.max_age.toString(), Constants.KEY_AGE_MAX)
        sessionManager.setStringValue(profileBean.min_age.toString(), Constants.KEY_AGE_MIN)
        sessionManager.setStringValue(profileBean.about_me, Constants.KEY_TAGLINE)
        sessionManager.setStringValue(profileBean.profileUrl, Constants.KEY_PROFILE_URL)
        sessionManager.setStringValue(profileBean.username, Constants.KEY_MAIN_USER_NAME)
        sessionManager.setStringValue(profileBean.casual_name, Constants.KEY_CASUAL_NAME)
        sessionManager.setIntegerValue(profileBean.age, Constants.KEY_AGE)

        edtProfileUserName.setText(profileBean.username)
        edtProfileCasualName.setText(profileBean.casual_name)
        edtProfileCasualName.setSelection(edtProfileCasualName.text?.length ?: 0)
        edtProfileTagline.setText(profileBean.about_me)
        edtProfileTagline.setSelection(edtProfileTagline.text?.length ?: 0)
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


    }

    private fun generateProfileTagUI() {
/*
        var profileTagCount = 0
        if (sessionManager.getCategoryList().size > 0) {
            for (categoryBean in sessionManager.getCategoryList()) {
                val layoutInflater = LayoutInflater.from(this@EditActivity)
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
                    val tvSubCategory = TextView(this@EditActivity)
                    tvSubCategory.layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )

                    tvSubCategory.text = subCategoryBean.name.toString()
                    tvSubCategory.setPadding(40, 20, 40, 20)
                    tvSubCategory.setTextColor(Color.BLACK)
                    tvSubCategory.setBackgroundResource(R.drawable.rounded_white_solid_white_corner)

                    view.chipProfileTag.addView(tvSubCategory)

                    if (subCategoryBean.is_selected == 1) {
                        subCategoryIdList.add(subCategoryBean.id)
                        ++profileTagCount
                        tvSubCategory.setTextColor(Color.WHITE)
                        tvSubCategory.setBackgroundResource(R.drawable.rounded_green_solid_all_corner)

                    }
                    tvSubCategory.setOnClickListener {

                        if (tvSubCategory.background.constantState == ContextCompat.getDrawable(
                                this@EditActivity,
                                R.drawable.rounded_white_solid_white_corner
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
                            tvSubCategory.setBackgroundResource(R.drawable.rounded_white_solid_white_corner)

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
                        val count = llProfileTag.childCount - 1
                        for (index in 0..count) {
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
*/
    }

    private fun editProfileApiCall() {

        val jsonObject = JsonObject()

        jsonObject.addProperty(
            Constants.USERNAME,
            edtProfileUserName.text.toString().trim()
        )

        jsonObject.addProperty(
            Constants.CASUAL_NAME,
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
        val androidID =
            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        jsonObject.addProperty(Constants.DEVICE_ID, androidID)
        jsonObject.addProperty(Constants.DEVICE_TYPE, Constants.ANDROID)

        Log.e("EditProfileFragment", "CreateProfile Request:\t $jsonObject")

        profileBasicViewModel.editProfile(jsonObject)
    }

    override fun onDestroy() {
        profileBasicViewModel.onDestroy()
        super.onDestroy()
    }

    fun onClickBasicInfo(view: View) {
        onClick(view)
    }

    private fun onClick(view: View) {
        when (view) {
            llEducation -> {
                openActivityWithResultCode(
                    this@EditActivity,
                    EducationListActivity(),
                    Constants.REQUEST_CODE_EDUCATION
                )
            }

            llJob -> {
                openActivityWithResultCode(
                    this@EditActivity,
                    JobListingActivity(),
                    Constants.REQUEST_CODE_JOB
                )
            }

            llInterest -> {

            }

            llSocialLink -> {
                openActivityWithResultCode(
                    this@EditActivity,
                    SocialLinkActivity(),
                    Constants.REQUEST_CODE
                )
            }
        }
    }

    private fun openActivityWithResultCode(
        activity: Activity,
        destinationActivity: Activity,
        result_code: Int
    ) {
        startActivityForResult(Intent(activity, destinationActivity::class.java), result_code)
        activity.overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    fun onEditBackClick(view: View) {
        onBackPressed()
    }

    fun onEditSaveClick(view: View) {
        editProfileApiCall()
    }

}