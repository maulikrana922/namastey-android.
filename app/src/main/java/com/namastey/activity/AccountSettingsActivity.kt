package com.namastey.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityAccountSettingsBinding
import com.namastey.fragment.AccountSettingsFragment
import com.namastey.uiView.AccountSettingsView
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import com.namastey.viewModel.AccountSettingsViewModel
import kotlinx.android.synthetic.main.activity_account_settings.*
import javax.inject.Inject

class AccountSettingsActivity : BaseActivity<ActivityAccountSettingsBinding>(),
    AccountSettingsView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var activityAccountSettingsBinding: ActivityAccountSettingsBinding
    private lateinit var accountSettingsViewModel: AccountSettingsViewModel


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
        if (sessionManager.getUserGender() == Constants.Gender.male.name)
            llSettingBackground.background = getDrawable(R.drawable.blue_bar)
        else
            llSettingBackground.background = getDrawable(R.drawable.pink_bar)

        addFragment(
            AccountSettingsFragment(),
            Constants.ACCOUNT_SETTINGS_FRAGMENT
        )
    }

    fun changeHeaderText(headerText: String) {
        tvHeader.text = headerText
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

    override fun onDestroy() {
        accountSettingsViewModel.onDestroy()
        super.onDestroy()
    }
}