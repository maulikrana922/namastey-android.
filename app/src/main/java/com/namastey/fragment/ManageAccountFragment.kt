package com.namastey.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.AccountSettingsActivity
import com.namastey.activity.SignUpActivity
import com.namastey.customViews.CustomTextView
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

        tvUsername.text = sessionManager.getStringValue(Constants.KEY_MAIN_USER_NAME)
        tvAge.text = sessionManager.getIntegerValue(Constants.KEY_AGE).toString()

        if (sessionManager.getUserGender() != "") {
            llGender.visibility = View.VISIBLE
            tvGender.text = sessionManager.getUserGender()
        } else {
            llGender.visibility = View.GONE
        }

        if (sessionManager.getUserEmail() != "") {
            llEmail.visibility = View.VISIBLE
            tvEmail.text = sessionManager.getUserEmail()
        } else {
            llEmail.visibility = View.GONE
        }

        if (sessionManager.getUserPhone() != "") {
            llMobile.visibility = View.VISIBLE
            tvMobile.text = sessionManager.getUserPhone()
        } else {
            llMobile.visibility = View.GONE
        }

        tvLogout.setOnClickListener {
            onLogoutClick()
        }
        tvDeleteAccount.setOnClickListener {
            onDeleteAccountClick()
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
                        manageAccountViewModel.logOut()
                    }
                    btnNeg.id -> {
                        dismiss()
                    }
                }
            }
        }.show()
    }

    private fun onDeleteAccountClick() {

        val deleteDialog = Dialog(requireActivity(), R.style.MyDialogTheme)
        deleteDialog.setContentView(R.layout.dialog_delete)
        deleteDialog.setCancelable(false)
        deleteDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        deleteDialog.findViewById<CustomTextView>(R.id.btnConfirm).setOnClickListener {
            manageAccountViewModel.removeAccount()
            deleteDialog.dismiss()
        }
        deleteDialog.findViewById<CustomTextView>(R.id.btnDeleteCancel).setOnClickListener {
            deleteDialog.dismiss()
        }
        deleteDialog.show()

/*
        object : CustomAlertDialog(
            requireActivity(),
            resources.getString(R.string.msg_delete),
            getString(R.string.yes),
            getString(R.string.cancel)
        ) {
            override fun onBtnClick(id: Int) {
                when (id) {
                    btnPos.id -> {
                        manageAccountViewModel.removeAccount()
                    }
                    btnNeg.id -> {
                        dismiss()
                    }
                }
            }
        }.show()
*/
    }

    override fun onLogoutSuccess(msg: String) {
        sessionManager.logout()
        val intent = Intent(requireActivity(), SignUpActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        openActivity(intent)
    }

    override fun onLogoutFailed(msg: String, error: Int) {
    }

    override fun onSuccess(msg: String) {
        Log.e("msg", msg)
        sessionManager.logout()
        val intent = Intent(requireActivity(), SignUpActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        openActivity(intent)
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