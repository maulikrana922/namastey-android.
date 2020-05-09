package com.namastey.activity

import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivitySignUpBinding
import com.namastey.uiView.SignUpView
import com.namastey.viewModel.BaseViewModel
import com.namastey.viewModel.SignUpViewModel
import javax.inject.Inject

class SignUpActivity : BaseActivity<ActivitySignUpBinding>(), SignUpView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var activitySignUpBinding: ActivitySignUpBinding
    private lateinit var signUpViewModel: SignUpViewModel


    override fun getViewModel() = signUpViewModel

    override fun getLayoutId() = R.layout.activity_sign_up

    override fun getBindingVariable() = BR.viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        signUpViewModel = ViewModelProviders.of(this,viewModelFactory).get(SignUpViewModel::class.java)
        activitySignUpBinding = bindViewData()
        activitySignUpBinding.viewModel = signUpViewModel
    }

    override fun openSignUpWithPhone() {
    }

}
