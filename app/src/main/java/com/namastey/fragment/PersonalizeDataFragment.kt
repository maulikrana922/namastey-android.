package com.namastey.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.AccountSettingsActivity
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentPersonalizeDataBinding
import com.namastey.model.SafetyBean
import com.namastey.uiView.PersonalizeDataView
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import com.namastey.viewModel.PersonalizeDataViewModel
import kotlinx.android.synthetic.main.fragment_personalize_data.*
import javax.inject.Inject


class PersonalizeDataFragment : BaseFragment<FragmentPersonalizeDataBinding>(),
    PersonalizeDataView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var fragmentPersonalizeDataBinding: FragmentPersonalizeDataBinding
    private lateinit var personalizeDataViewModel: PersonalizeDataViewModel
    private lateinit var layoutView: View

    override fun getViewModel() = personalizeDataViewModel

    override fun getLayoutId() = R.layout.fragment_personalize_data

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance() =
            PersonalizeDataFragment().apply {
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

        setFromSessionManager()
    }

    private fun setupViewModel() {
        personalizeDataViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(PersonalizeDataViewModel::class.java)
        personalizeDataViewModel.setViewInterface(this)
        fragmentPersonalizeDataBinding = getViewBinding()
        fragmentPersonalizeDataBinding.viewModel = personalizeDataViewModel
    }

    private fun initData() {
        switchSuggestYourAccountToOthers.setOnCheckedChangeListener { buttonView, isChecked ->
            when {
                isChecked -> {
                    personalizeDataViewModel.suggestYourAccountOnOff(1)
                }
                else -> {
                    personalizeDataViewModel.suggestYourAccountOnOff(0)
                }
            }
        }
    }

    private fun setFromSessionManager() {
        switchSuggestYourAccountToOthers.isChecked =
            sessionManager.getIntegerValue(Constants.KEY_SUGGEST_YOUR_ACCOUNT_TO_OTHERS) == 1
    }

    override fun onResume() {
        super.onResume()
        (activity as AccountSettingsActivity).changeHeaderText(getString(R.string.personalize_data))
    }


    override fun onSuccessResponse(safetyBean: SafetyBean) {
        Log.e("SafetySubFragment", "onSuccessResponse  safetyBean: \t ${safetyBean.is_suggest}")
        sessionManager.setIntegerValue(
            safetyBean.is_suggest, Constants.KEY_SUGGEST_YOUR_ACCOUNT_TO_OTHERS
        )
    }

    override fun onDestroy() {
        personalizeDataViewModel.onDestroy()
        super.onDestroy()
    }
}