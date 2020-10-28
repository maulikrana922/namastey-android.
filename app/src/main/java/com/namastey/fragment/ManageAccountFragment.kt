package com.namastey.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.AccountSettingsActivity
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentManageAccountBinding
import com.namastey.uiView.ManageAccountView
import com.namastey.viewModel.ManageAccountViewModel
import javax.inject.Inject

class ManageAccountFragment : BaseFragment<FragmentManageAccountBinding>(), ManageAccountView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
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
    }

    private fun setupViewModel() {
        manageAccountViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ManageAccountViewModel::class.java)
        fragmentBlockListBinding = getViewBinding()
        fragmentBlockListBinding.viewModel = manageAccountViewModel
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