package com.namastey.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.InterestAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityInterestBinding
import com.namastey.fragment.ChooseInterestFragment
import com.namastey.listeners.OnImageItemClick
import com.namastey.model.InterestBean
import com.namastey.model.InterestSubCategoryBean
import com.namastey.roomDB.entity.User
import com.namastey.uiView.ChooseInterestView
import com.namastey.utils.*
import com.namastey.viewModel.ChooseInterestViewModel
import kotlinx.android.synthetic.main.activity_interest.*
import java.util.*
import javax.inject.Inject

class InterestActivity : BaseActivity<ActivityInterestBinding>(), ChooseInterestView,
    OnImageItemClick {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var chooseInterestViewModel: ChooseInterestViewModel

    private lateinit var activityInterestBinding: ActivityInterestBinding
    private lateinit var interestAdapter: InterestAdapter
    private var selectInterestIdList: ArrayList<Int> = ArrayList()
    private var androidId = ""
    private var selectCategoryId: ArrayList<Int> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        setupViewModel()

        initData()
    }

    private fun initData() {
        // chooseInterestViewModel.getChooseInterestList()
        chooseInterestViewModel.getAllSubcategoryList()

        androidId =
            Settings.Secure.getString(
                this@InterestActivity.contentResolver,
                Settings.Secure.ANDROID_ID
            )
    }

    /**
     * click on any item then display top select count and store id list
     */
    override fun onImageItemClick(interestBeanInterest: InterestSubCategoryBean) {

        if (selectInterestIdList.contains(interestBeanInterest.id)) {
            selectCategoryId.remove(interestBeanInterest.category_id)
            selectInterestIdList.remove(interestBeanInterest.id)
        } else {
            selectCategoryId.add(interestBeanInterest.category_id)
            selectInterestIdList.add(interestBeanInterest.id)
        }

        sessionManager.setChooseInterestList(selectCategoryId)
    }

    override fun onClose() {
    }

    override fun onNext() {

        if (selectInterestIdList.size >= Constants.MIN_CHOOSE_INTEREST) {

            val jsonObject = JsonObject()
            jsonObject.addProperty(Constants.GENDER, sessionManager.getUserGender())


            val jsonArrayInterest = JsonArray()
            for (selectInterest in selectInterestIdList) {
                jsonArrayInterest.add(JsonPrimitive(selectInterest))
            }
            // jsonObject.add(Constants.INTEREST, jsonArrayInterest)
            jsonObject.add(Constants.TAGS, jsonArrayInterest)

            jsonObject.addProperty(Constants.DEVICE_ID, androidId)    // Need to change
            jsonObject.addProperty(Constants.DEVICE_TYPE, Constants.ANDROID)
            if (!sessionManager.isGuestUser()) {
                jsonObject.addProperty(Constants.USER_UNIQUEID, sessionManager.getUserUniqueId())
            } else {
                sessionManager.setGuestUser(true)
            }
//            chooseInterestViewModel.updateOrCreateUser(jsonObject)

        } else {
            object : CustomAlertDialog(
                this@InterestActivity,
                resources.getString(R.string.msg_min_choose_interest), getString(R.string.ok), ""
            ) {
                override fun onBtnClick(id: Int) {
                    dismiss()
                }
            }.show()
        }

    }

    override fun onSuccessCreateOrUpdate(user: User) {
        if (user.token.isNotEmpty())
            sessionManager.setAccessToken(user.token)
        sessionManager.setUserEmail(user.email)
        sessionManager.setUserPhone(user.mobile)
        sessionManager.setuserUniqueId(user.user_uniqueId)


        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this@InterestActivity, DashboardActivity::class.java))
            this@InterestActivity.finish()
        }, 1000)
    }

    override fun onSuccess(interestList: ArrayList<InterestBean>) {
        Log.e("ChooseInterestFragment", "interestList")
    }

    override fun onSuccessAllCategoryList(interestList: ArrayList<InterestSubCategoryBean>) {
        rvChooseInterest.addItemDecoration(GridSpacingItemDecoration(3, 10, false))
        interestAdapter = InterestAdapter(interestList, this@InterestActivity, this, true)
        rvChooseInterest.adapter = interestAdapter
    }

    override fun getViewModel() = chooseInterestViewModel


    override fun getLayoutId() = R.layout.activity_interest

    override fun getBindingVariable() = BR.viewModel

    private fun setupViewModel() {
        chooseInterestViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ChooseInterestViewModel::class.java)
        chooseInterestViewModel.setViewInterface(this)
        activityInterestBinding = bindViewData()
        activityInterestBinding.viewModel = chooseInterestViewModel
    }

    fun onClickContinue(view: View){
        if (selectInterestIdList.size >= Constants.MIN_CHOOSE_INTEREST) {

            sessionManager.setChooseInterestList(selectInterestIdList)
            
//            val jsonArrayInterest = JsonArray()
//            for (selectInterest in selectInterestIdList) {
//                jsonArrayInterest.add(JsonPrimitive(selectInterest))
//            }
//            jsonObject.add(Constants.TAGS, jsonArrayInterest)

            openActivity(this@InterestActivity, AboutActivity())

        } else {
            object : CustomAlertDialog(
                this@InterestActivity,
                resources.getString(R.string.msg_min_choose_interest), getString(R.string.ok), ""
            ) {
                override fun onBtnClick(id: Int) {
                    dismiss()
                }
            }.show()
        }
    }
    override fun onBackPressed() {
        finishActivity()
    }

    fun onClickBack(view: View) {
        onBackPressed()
    }

    override fun onDestroy() {
        chooseInterestViewModel.onDestroy()
        super.onDestroy()
    }
}