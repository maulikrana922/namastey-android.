package com.namastey.fragment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.AccountSettingsActivity
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentAccountSettingsBinding
import com.namastey.uiView.AccountSettingsView
import com.namastey.utils.Constants
import com.namastey.viewModel.AccountSettingsViewModel
import kotlinx.android.synthetic.main.fragment_account_settings.*
import javax.inject.Inject

class AccountSettingsFragment : BaseFragment<FragmentAccountSettingsBinding>(),
    AccountSettingsView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var fragmentAccountBinding: FragmentAccountSettingsBinding
    private lateinit var accountSettingsViewModel: AccountSettingsViewModel
    private lateinit var layoutView: View


    override fun getViewModel() = accountSettingsViewModel

    override fun getLayoutId() = R.layout.fragment_account_settings

    override fun getBindingVariable() = BR.viewModel


    companion object {
        fun getInstance() =
            AccountSettingsFragment().apply {
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

        initData()
    }

    private fun setupViewModel() {
        accountSettingsViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(AccountSettingsViewModel::class.java)
        accountSettingsViewModel.setViewInterface(this)
        fragmentAccountBinding = getViewBinding()
        fragmentAccountBinding.viewModel = accountSettingsViewModel
    }

    private fun initData() {

        tvNotifications.setOnClickListener {
            (activity as AccountSettingsActivity).changeHeaderText(getString(R.string.notifications))

            (activity as AccountSettingsActivity).addFragment(
                AccountSettingsNotificationFragment.getInstance(),
                Constants.ACCOUNT_SETTINGS_NOTIFICATIONS_FRAGMENT
            )
        }

        tvBlockedList.setOnClickListener {
            (activity as AccountSettingsActivity).changeHeaderText(getString(R.string.block_list))
            (activity as AccountSettingsActivity).addFragment(
                BlockListFragment.getInstance(),
                Constants.BLOCK_LIST_FRAGMENT
            )
        }

        tvManageAccount.setOnClickListener {
            (activity as AccountSettingsActivity).changeHeaderText(getString(R.string.manage_account))
            (activity as AccountSettingsActivity).addFragment(
                ManageAccountFragment.getInstance(),
                Constants.MANAGE_ACCOUNT_FRAGMENT
            )
        }

        tvContentLanguages.setOnClickListener {
            (activity as AccountSettingsActivity).changeHeaderText(getString(R.string.content_languages))
            (activity as AccountSettingsActivity).addFragment(
                ContentLanguagesFragment.getInstance(),
                Constants.CONTENT_LANGUAGES_FRAGMENT
            )
        }

        tvSafety.setOnClickListener {
            (activity as AccountSettingsActivity).changeHeaderText(getString(R.string.safety))
            (activity as AccountSettingsActivity).addFragment(
                SafetyFragment.getInstance(),
                Constants.SAFETY_FRAGMENT
            )
        }
        tvInviteFriends.setOnClickListener {
            addFragmentFindFriend(
                FindFriendFragment.getInstance(true
                ),
                Constants.FIND_FRIEND_FRAGMENT
            )
        }
        tvContactUs.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_SEND)
            intent.type = "plain/text"
            intent.putExtra(
                Intent.EXTRA_EMAIL,
                arrayOf(getString(R.string.contact_us_email))
            )
            intent.putExtra(Intent.EXTRA_SUBJECT, "Namastey Contact us")
            intent.putExtra(Intent.EXTRA_TEXT, "Hello Namastey Team")
            startActivity(Intent.createChooser(intent, ""))
        }

        tvLeaveFeedback.setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context!!.packageName}")))
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${context!!.packageName}")))
            }
        }
        tvPersonalizeData.setOnClickListener {
            (activity as AccountSettingsActivity).changeHeaderText(getString(R.string.personalize_data))
            (activity as AccountSettingsActivity).addFragment(
                PersonalizeDataFragment.getInstance(),
                Constants.PERSONALIZE_DATA_FRAGMENT
            )
        }

        tvTermsConditions.setOnClickListener {
            openUrlInBrowser(getString(R.string.tv_term_link))
        }

        tvPrivacyPolicy.setOnClickListener {
            openUrlInBrowser(getString(R.string.tv_policy_link))
        }

        tvBillingTerms.setOnClickListener {
            openUrlInBrowser(getString(R.string.tv_billing_link))
        }
    }

    private fun openUrlInBrowser(link: String) {
        try {
            val myIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            startActivity(myIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                requireContext(),
                "No application can handle this request. Please install a web browser or check your URL.",
                Toast.LENGTH_LONG
            ).show()
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AccountSettingsActivity).changeHeaderText(getString(R.string.account_settings))
    }

    override fun onDestroy() {
        accountSettingsViewModel.onDestroy()
        super.onDestroy()
    }
}