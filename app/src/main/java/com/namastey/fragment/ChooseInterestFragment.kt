package com.namastey.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.DashboardActivity
import com.namastey.adapter.InterestAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentChooseInterestBinding
import com.namastey.listeners.OnImageItemClick
import com.namastey.model.InterestBean
import com.namastey.model.InterestSubCategoryBean
import com.namastey.roomDB.entity.User
import com.namastey.uiView.ChooseInterestView
import com.namastey.utils.*
import com.namastey.viewModel.ChooseInterestViewModel
import kotlinx.android.synthetic.main.fragment_choose_interest.*
import java.util.*
import javax.inject.Inject


class ChooseInterestFragment : BaseFragment<FragmentChooseInterestBinding>(), ChooseInterestView,
    OnImageItemClick {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var fragmentChooseInterestBinding: FragmentChooseInterestBinding
    private lateinit var chooseInterestViewModel: ChooseInterestViewModel
    private lateinit var layoutView: View
    private lateinit var interestAdapter: InterestAdapter
    private var selectInterestIdList: ArrayList<Int> = ArrayList()
    private var androidId = ""
    private var selectCategoryId: ArrayList<Int> = ArrayList()


    override fun getViewModel() = chooseInterestViewModel

    override fun getLayoutId() = R.layout.fragment_choose_interest

    override fun getBindingVariable() = BR.viewModel

    companion object {
        var noOfSelectedImage = 0

        fun getInstance(
            dob: String,
            selectVideoIdList: ArrayList<Int>
        ) =
            ChooseInterestFragment().apply {
                arguments = Bundle().apply {
                    putString(Constants.DATE_OF_BIRTH, dob)
                    putIntegerArrayList("selectVideoIdList", selectVideoIdList)
                }
                noOfSelectedImage = 0
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutView = view
        setupViewModel()
        initUI()
    }

    private fun initUI() {
        // chooseInterestViewModel.getChooseInterestList()
        chooseInterestViewModel.getAllSubcategoryList()

        androidId =
            Settings.Secure.getString(requireActivity().contentResolver, Settings.Secure.ANDROID_ID)
    }

    private fun setupViewModel() {
        chooseInterestViewModel = ViewModelProviders.of(this, viewModelFactory).get(
            ChooseInterestViewModel::class.java
        )
        chooseInterestViewModel.setViewInterface(this)

        fragmentChooseInterestBinding = getViewBinding()
        fragmentChooseInterestBinding.viewModel = chooseInterestViewModel
    }

    override fun onDestroy() {
        chooseInterestViewModel.onDestroy()
        super.onDestroy()
    }

    /**
     * click on any item then display top select count and store id list
     */
    override fun onImageItemClick(interestBeanInterest: InterestSubCategoryBean) {
        // tvSelectLabel.text = resources.getString(R.string.tv_select).plus(" ").plus(noOfSelectedImage)
        if (noOfSelectedImage > 9)
            tvSelectLabel.text =
                noOfSelectedImage.toString().plus(" ").plus(resources.getString(R.string.selected))
        else
            tvSelectLabel.text = resources.getString(R.string._0)
                .plus(noOfSelectedImage.toString())
                .plus(" ")
                .plus(resources.getString(R.string.selected))

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
        fragmentManager!!.popBackStack()
    }

    override fun onNext() {

        if (noOfSelectedImage >= Constants.MIN_CHOOSE_INTEREST) {

            val jsonObject = JsonObject()
            jsonObject.addProperty(Constants.GENDER, sessionManager.getUserGender())
            if (arguments!!.containsKey(Constants.DATE_OF_BIRTH)) {
                jsonObject.addProperty(
                    Constants.DATE_OF_BIRTH,
                    Utils.convertDateToAPIFormate(
                        arguments!!.getString(Constants.DATE_OF_BIRTH).toString()
                    )
                )
            }

            if (arguments!!.containsKey("selectVideoIdList")) {
                val jsonArray = JsonArray()
                for (selectVideoIdList in arguments!!.getIntegerArrayList("selectVideoIdList")!!) {
                    jsonArray.add(JsonPrimitive(selectVideoIdList))
                }
                jsonObject.add(Constants.LANGUAGE, jsonArray)
            }

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
            chooseInterestViewModel.updateOrCreateUser(jsonObject)

        } else {
            object : CustomAlertDialog(
                activity!!,
                resources.getString(R.string.msg_min_choose_interest), getString(R.string.ok), ""
            ) {
                override fun onBtnClick(id: Int) {
                    dismiss()
                }
            }.show()
        }

    }

    override fun onSuccessCreateOrUpdate(user: User) {
        ivSplashBackground.visibility = View.VISIBLE
        if (user.token.isNotEmpty())
            sessionManager.setAccessToken(user.token)
        sessionManager.setUserEmail(user.email)
        sessionManager.setUserPhone(user.mobile)
        sessionManager.setuserUniqueId(user.user_uniqueId)


        Handler().postDelayed({
            startActivity(Intent(activity, DashboardActivity::class.java))
            activity!!.finish()
        }, 1000)
    }

    override fun onSuccess(interestList: ArrayList<InterestBean>) {
        Log.e("ChooseInterestFragment", "interestList")
    }

    override fun onSuccessAllCategoryList(interestList: ArrayList<InterestSubCategoryBean>) {
        rvChooseInterest.addItemDecoration(GridSpacingItemDecoration(3, 10, false))
        interestAdapter = InterestAdapter(interestList, activity!!, this, true)
        rvChooseInterest.adapter = interestAdapter
    }

}