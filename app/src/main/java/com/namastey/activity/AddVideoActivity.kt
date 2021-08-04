package com.namastey.activity

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityAddVideoBinding
import com.namastey.roomDB.entity.User
import com.namastey.uiView.AddVideoView
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import com.namastey.utils.Utils
import com.namastey.viewModel.AddVideoViewModel
import java.util.*
import javax.inject.Inject

class AddVideoActivity : BaseActivity<ActivityAddVideoBinding>(), AddVideoView {

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var binding: ActivityAddVideoBinding
    private lateinit var addVideoViewModel: AddVideoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        setupViewModel()

        initData()
    }

    private fun setupViewModel() {
        addVideoViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(AddVideoViewModel::class.java)
        addVideoViewModel.setViewInterface(this)
        binding = bindViewData()
        binding.viewModel = addVideoViewModel
    }

    override fun onSuccessCreateOrUpdate(user: User) {
        if (user.token.isNotEmpty())
            sessionManager.setAccessToken(user.token)
//        sessionManager.setUserPhone(user.mobile)

        if (sessionManager.getIntegerValue(Constants.KEY_IS_INVITED) == 1) {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            openActivity(intent)
        } else {
            openActivity(this, NotInvitedActivity())
        }

    }

    override fun getViewModel() = addVideoViewModel

    override fun getLayoutId() = R.layout.activity_add_video

    override fun getBindingVariable() = BR.viewModel

    private fun initData() {

    }

    private fun editProfileApiCall() {

        val jsonObject = JsonObject()

        jsonObject.addProperty(
            Constants.USERNAME,
            sessionManager.getStringValue(Constants.KEY_MAIN_USER_NAME)
        )

        jsonObject.addProperty(
            Constants.CASUAL_NAME,
            sessionManager.getStringValue(Constants.KEY_CASUAL_NAME)
        )

        jsonObject.addProperty(
            Constants.DATE_OF_BIRTH,
            Utils.convertDateToAPIFormate(
                sessionManager.getStringValue(Constants.KEY_BIRTH_DAY)
            )
        )

        jsonObject.addProperty(Constants.GENDER, sessionManager.getUserGender())

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
            sessionManager.getStringValue(Constants.KEY_TAGLINE)
        )
        jsonObject.addProperty(Constants.INTERESTED_IN_GENDER, sessionManager.getInterestIn())

        val jsonArrayInterest = JsonArray()
        for (selectInterest in sessionManager.getChooseInterestList()) {
            jsonArrayInterest.add(JsonPrimitive(selectInterest))
        }
        jsonObject.add(
            Constants.TAGS,
            jsonArrayInterest
        )

        val jsonArray = JsonArray()
        for (selectVideoIdList in sessionManager.getVideoLanguageList()) {
            jsonArray.add(JsonPrimitive(selectVideoIdList))
        }
        jsonObject.add(
            Constants.LANGUAGE,
            jsonArray
        )


//        val categoryIdList: ArrayList<Int> = ArrayList()
//        for (category in sessionManager.getCategoryList()) {
//            categoryIdList.add(category.id)
//        }
//        jsonObject.addProperty(
//            Constants.CATEGORY_ID,
//            categoryIdList.joinToString()
//        )
//        val socialAccountId = ArrayList<Long>()
//        for (data in socialAccountList) {
//            socialAccountId.add(data.id)
//        }
//        jsonObject.addProperty(Constants.SOCIAL_ACCOUNTS, socialAccountId.joinToString())
       // if (sessionManager.getEducationBean().course.isNotEmpty()) {
            jsonObject.addProperty(
                Constants.EDUCATION,
                sessionManager.getStringValue(Constants.KEY_EDUCATION)
            )
       // }

//        if (sessionManager.getJobBean().title.isNotEmpty()) {
            jsonObject.addProperty(Constants.JOBS, sessionManager.getStringValue(Constants.KEY_JOB))
//        }
        val androidID = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)

        jsonObject.addProperty(Constants.DEVICE_ID, androidID)
        jsonObject.addProperty(Constants.DEVICE_TYPE, Constants.ANDROID)

        Log.e("ProfileRequest", "CreateProfile Request:\t $jsonObject")

        addVideoViewModel.createUser(jsonObject)
    }


    override fun onBackPressed() {
        finishActivity()
    }

    fun onClickBack(view: View) {
        onBackPressed()
    }

    fun onClickContinue(view: View) {
//        openActivity(this@AddVideoActivity, NotInvitedActivity())
    }

    fun onClickAddVideo(view: View){
        openActivity(this@AddVideoActivity, PostVideoActivity())
    }
    fun onClickSkip(view: View) {
        editProfileApiCall()
    }
}