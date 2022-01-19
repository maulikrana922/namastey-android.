package com.namastey.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
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
import kotlinx.android.synthetic.main.activity_name.*
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

    }

    override fun onFailedUniqueName(error: ErrorBean?) {
        Log.e(TAG, "onFailedUniqueName: Error: \t ${error!!.user_name}")
        edtProfileUserName.requestFocus()
        edtProfileUserName.inputType = InputType.TYPE_CLASS_TEXT
        edtProfileUserName.setCompoundDrawablesWithIntrinsicBounds(
            0,
            0,
            0,
            0
        )
        tvUserNameError.visibility = View.VISIBLE
        tvUserNameError.text = error.user_name
    }

    override fun onSuccessUniqueName(msg: String) {
        tvUserNameError.visibility = View.GONE
        if (edtProfileUserName.text.toString().length >= 5) {
            edtProfileUserName.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_tick_square,
                0
            )
        }
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
//        edtProfileUserName.setCompoundDrawablesWithIntrinsicBounds(
//            0,
//            0,
//            R.drawable.ic_edit_gray,
//            0
//        )
/*        edtProfileCasualName.setCompoundDrawablesWithIntrinsicBounds(
            0,
            0,
            R.drawable.ic_edit_gray,
            0
        )
        edtProfileTagline.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_edit_gray, 0)
*/
        finishActivity()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(TAG, "onCreate: ")
        getActivityComponent().inject(this)

        setupViewModel()
        initData()
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


    }

    private fun initData() {


        edtProfileTagline.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (edtProfileTagline.hasFocus()) {
                    v.parent.requestDisallowInterceptTouchEvent(true)
                    when (event.action and MotionEvent.ACTION_MASK) {
                        MotionEvent.ACTION_SCROLL -> {
                            v.parent.requestDisallowInterceptTouchEvent(false)
                            return true
                        }
                    }
                }
                return false
            }
        })

        Log.e("EditProfileFragment", "FromAddSocialLink: \t $fromAddSocialLink")

//        edtProfileUserName.inputType = InputType.TYPE_NULL
//        edtProfileCasualName.inputType = InputType.TYPE_NULL
//        edtProfileTagline.inputType = InputType.TYPE_NULL
        edtProfileTagline.minLines = 5
        edtProfileTagline.maxLines = 5

        edtProfileUserName.setText(sessionManager.getStringValue(Constants.KEY_MAIN_USER_NAME))
        edtProfileCasualName.setText(sessionManager.getStringValue(Constants.KEY_CASUAL_NAME))
//        edtProfileCasualName.setSelection(edtProfileCasualName.text?.length ?: 0)
        edtProfileTagline.setText(sessionManager.getStringValue(Constants.KEY_TAGLINE))
        edtProfileTagline.setSelection(edtProfileTagline.text?.length ?: 0)


        edtEducation.setText(sessionManager.getStringValue(Constants.KEY_EDUCATION))
        edtOccupation.setText(sessionManager.getStringValue(Constants.KEY_JOB))

        edtProfileUserName.compoundDrawablePadding = 25

        edtProfileUserName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length >= 5) {
                    Log.d("Username: ", "username :" + s.toString())
                    if (sessionManager.getStringValue(Constants.KEY_MAIN_USER_NAME).lowercase() != s.toString().lowercase())
                        profileBasicViewModel.checkUniqueUsername(s.toString().lowercase())
                   else tvUserNameError.visibility = View.GONE
               } else {
                    tvUserNameError.visibility = View.GONE
                    edtProfileUserName.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        0,
                        0
                    )
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
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

        //  if (profileBean.education.size > 0) {
        sessionManager.setStringValue(profileBean.education, Constants.KEY_EDUCATION)
        edtEducation.setText(profileBean.education)
        // }
        // if (profileBean.jobs.size > 0) {
        //sessionManager.setJobBean(profileBean.jobs)
        sessionManager.setStringValue(profileBean.jobs, Constants.KEY_JOB)
        edtOccupation.setText(profileBean.jobs)
        // }


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
//        jsonObject.addProperty(Constants.GENDER, sessionManager.getInterestIn())


/*

        jsonObject.addProperty(
            Constants.TAGS,
            subCategoryIdList.joinToString()
        )
*/

/*
        val categoryIdList: ArrayList<Int> = ArrayList()
        for (category in sessionManager.getCategoryList()) {
            categoryIdList.add(category.id)
        }
        jsonObject.addProperty(
            Constants.CATEGORY_ID,
            categoryIdList.joinToString()
        )
*/

/*
        val socialAccountId = ArrayList<Long>()
        for (data in socialAccountList) {
            socialAccountId.add(data.id)
        }
        jsonObject.addProperty(Constants.SOCIAL_ACCOUNTS, socialAccountId.joinToString())
 */
        jsonObject.addProperty(
            Constants.EDUCATION,
            edtEducation.text.toString()
        )
        jsonObject.addProperty(
            Constants.JOBS,
            edtOccupation.text.toString()
        )
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
        when (view) {

            llInterest -> {
                openActivityWithResultCode(
                    this@EditActivity,
                    EditInterestActivity(),
                    Constants.REQUEST_CODE
                )
            }

            llSocialLink -> {
                val intent = Intent(this, SocialLinkActivity::class.java)
                intent.putExtra(Constants.ACTIVITY_EDIT, "EditActivity")
                intent.putExtra(Constants.KEY_IS_LOGIN, true)
                openActivity(intent)
            }
        }
    }

    private fun openActivityWithResultCode(
        activity: Activity,
        destinationActivity: Activity,
        result_code: Int
    ) {
        startActivityForResult(Intent(activity, destinationActivity::class.java), result_code)
        activity.overridePendingTransition(R.anim.enter, R.anim.exit)
    }

    fun onEditBackClick(view: View) {
        onBackPressed()
    }

    override fun onBackPressed() {
        finishActivity()
    }

    fun onEditSaveClick(view: View) {
        if (edtProfileUserName.text.toString().trim().length < 5) {
            showMsg(getString(R.string.empty_username_minimum))
        } else {
            editProfileApiCall()
        }
    }

}