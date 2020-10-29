package com.namastey.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.AccountSettingsActivity
import com.namastey.activity.SignUpActivity
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentManageAccountBinding
import com.namastey.uiView.ManageAccountView
import com.namastey.utils.Constants
import com.namastey.utils.CustomAlertDialog
import com.namastey.utils.SessionManager
import com.namastey.viewModel.ManageAccountViewModel
import kotlinx.android.synthetic.main.dialog_alert.*
import kotlinx.android.synthetic.main.fragment_manage_account.*
import javax.inject.Inject

class ManageAccountFragment : BaseFragment<FragmentManageAccountBinding>(), ManageAccountView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var fragmentBlockListBinding: FragmentManageAccountBinding
    private lateinit var manageAccountViewModel: ManageAccountViewModel
    private lateinit var layoutView: View

    override fun getViewModel() = manageAccountViewModel

    override fun getLayoutId() = R.layout.fragment_manage_account

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance() =
            ManageAccountFragment().apply {
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

    private fun setupViewModel() {
        manageAccountViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ManageAccountViewModel::class.java)
        manageAccountViewModel.setViewInterface(this)
        fragmentBlockListBinding = getViewBinding()
        fragmentBlockListBinding.viewModel = manageAccountViewModel
    }

    private fun initUI() {

        tvUsername.text = sessionManager.getStringValue(Constants.KEY_CASUAL_NAME)
        tvAge.text = sessionManager.getIntegerValue(Constants.KEY_AGE).toString()

        tvLogout.setOnClickListener {
            onLogoutClick()
        }
    }

    private fun onLogoutClick() {
        object : CustomAlertDialog(
            requireActivity(),
            resources.getString(R.string.msg_logout),
            getString(R.string.logout),
            getString(R.string.cancel)
        ) {
            override fun onBtnClick(id: Int) {
                when (id) {
                    btnPos.id -> {
                        sessionManager.logout()
                        val intent = Intent(requireActivity(), SignUpActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        openActivity(intent)
                    }
                    btnNeg.id -> {
                        dismiss()
                    }
                }
            }
        }.show()
    }

    override fun onResume() {
        super.onResume()
        (activity as AccountSettingsActivity).changeHeaderText(getString(R.string.manage_account))
    }

    override fun onDestroy() {
        manageAccountViewModel.onDestroy()
        super.onDestroy()
    }

}