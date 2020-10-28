package com.namastey.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.AccountSettingsActivity
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentSafetyBinding
import com.namastey.uiView.SafetyView
import com.namastey.viewModel.SafetyViewModel
import javax.inject.Inject

class SafetyFragment : BaseFragment<FragmentSafetyBinding>(), SafetyView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var fragmentContentLanguageBinding: FragmentSafetyBinding
    private lateinit var safetyViewModel: SafetyViewModel
    private lateinit var layoutView: View


    override fun getViewModel() = safetyViewModel

    override fun getLayoutId() = R.layout.fragment_safety

    override fun getBindingVariable() = BR.viewModel


    companion object {
        fun getInstance() =
            SafetyFragment().apply {
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
        safetyViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(SafetyViewModel::class.java)
        safetyViewModel.setViewInterface(this)
        fragmentContentLanguageBinding = getViewBinding()
        fragmentContentLanguageBinding.viewModel = safetyViewModel
    }

    override fun onResume() {
        super.onResume()
        (activity as AccountSettingsActivity).changeHeaderText(getString(R.string.safety))
    }

    override fun onDestroy() {
        safetyViewModel.onDestroy()
        super.onDestroy()
    }
}