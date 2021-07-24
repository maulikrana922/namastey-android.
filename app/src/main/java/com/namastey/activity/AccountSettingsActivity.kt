package com.namastey.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.google.gson.JsonObject
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityAccountSettingsBinding
import com.namastey.fragment.AccountSettingsFragment
import com.namastey.model.VideoLanguageBean
import com.namastey.uiView.AccountSettingsView
import com.namastey.utils.Constants
import com.namastey.utils.CustomAlertDialog
import com.namastey.utils.SessionManager
import com.namastey.viewModel.AccountSettingsViewModel
import kotlinx.android.synthetic.main.activity_account_settings.*
import java.util.ArrayList
import javax.inject.Inject

class AccountSettingsActivity : BaseActivity<ActivityAccountSettingsBinding>(),
    AccountSettingsView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var activityAccountSettingsBinding: ActivityAccountSettingsBinding
    private lateinit var accountSettingsViewModel: AccountSettingsViewModel
    private var selectLanguageIdList: ArrayList<Int> = ArrayList()
    private var selectedLanguageList: ArrayList<VideoLanguageBean> = ArrayList()

    override fun getViewModel() = accountSettingsViewModel

    override fun getLayoutId() = R.layout.activity_account_settings

    override fun getBindingVariable() = BR.viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        accountSettingsViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(AccountSettingsViewModel::class.java)
        accountSettingsViewModel.setViewInterface(this)
        activityAccountSettingsBinding = bindViewData()
        activityAccountSettingsBinding.viewModel = accountSettingsViewModel

        initData()
    }

    private fun initData() {

        addFragment(
            AccountSettingsFragment(),
            Constants.ACCOUNT_SETTINGS_FRAGMENT
        )
    }

    fun changeHeaderText(headerText: String) {
        tvHeader.text = headerText
    }

    fun setVisibilitySave(isVisible: Boolean){
        when {
            isVisible -> tvSaveLanguage.visibility = View.VISIBLE
            else -> tvSaveLanguage.visibility = View.GONE
        }
    }

    fun setSelectedLanguage(selectLanguageIdList: ArrayList<Int>, selectedLanguageList: ArrayList<VideoLanguageBean>) {
        this.selectLanguageIdList = selectLanguageIdList
        this.selectedLanguageList = selectedLanguageList
    }

    fun onClickAccountSettingsBack(view: View) {
        onBackPressed()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        } else {
            finishActivity()
        }
    }

    override fun onSuccess(msg: String) {
        sessionManager.setLanguageList(selectedLanguageList)
        onBackPressed()
    }
    override fun onDestroy() {
        accountSettingsViewModel.onDestroy()
        super.onDestroy()
    }

    fun onClickSaveLanguage(view: View) {
        val jsonObject = JsonObject()

        if (selectLanguageIdList.size >= 1) {
            jsonObject.addProperty(Constants.LANGUAGE, selectLanguageIdList.joinToString())
            Log.d("Profile Request:", jsonObject.toString())

            accountSettingsViewModel.editProfile(jsonObject)
        }else{
            object : CustomAlertDialog(
                    this@AccountSettingsActivity,
                    getString(R.string.msg_min_language), getString(R.string.ok), ""
            ) {
                override fun onBtnClick(id: Int) {
                    dismiss()
                }
            }.show()

        }
    }

    fun onClickInviteFriend(view: View) {
        if (getOnInteractionWithFragment() != null) {
            getOnInteractionWithFragment()!!.onClickOfFragmentView(view)
        }
    }
}