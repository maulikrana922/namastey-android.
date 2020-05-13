package com.namastey.fragment

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import android.view.View
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentOtpBinding
import com.namastey.uiView.OTPView
import com.namastey.viewModel.OTPViewModel
import javax.inject.Inject

class OTPFragment : BaseFragment<FragmentOtpBinding>(), OTPView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var fragmentOtpBinding: FragmentOtpBinding
    private lateinit var otpViewModel: OTPViewModel
    private lateinit var layoutView: View

    override fun onCloseOtp() {
        activity!!.onBackPressed()
    }

    override fun onConfirm() {
        Log.d("onconfirm", "onconfirm")
    }

    override fun getViewModel() = otpViewModel

    override fun getLayoutId() = R.layout.fragment_otp

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance(title: String) =
            OTPFragment().apply {
                arguments = Bundle().apply {
                    putString("title", title)
                }
            }
    }

    private fun setupViewModel() {
        otpViewModel = ViewModelProviders.of(this, viewModelFactory).get(OTPViewModel::class.java)
        otpViewModel.setViewInterface(this)

        fragmentOtpBinding = getViewBinding()
        fragmentOtpBinding.viewModel = otpViewModel

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

    }
}