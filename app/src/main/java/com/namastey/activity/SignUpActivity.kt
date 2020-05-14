package com.namastey.activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.view.View
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivitySignUpBinding
import com.namastey.fragment.SelectGenderFragment
import com.namastey.fragment.SignupWithPhoneFragment
import com.namastey.uiView.SignUpView
import com.namastey.utils.Constants
import com.namastey.viewModel.SignUpViewModel
import kotlinx.android.synthetic.main.activity_sign_up.*
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

        signUpViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(SignUpViewModel::class.java)
        activitySignUpBinding = bindViewData()
        activitySignUpBinding.viewModel = signUpViewModel
    }

    override fun openSignUpWithPhone() {
        tvSkipSignUp.visibility = View.INVISIBLE
        addFragment(
            SignupWithPhoneFragment.getInstance(
                "signUp"
            ),
            Constants.SIGNUP_WITH_PHONE_FRAGMENT
        )
    }

    override fun onSignupWithFacebook() {
        signUpViewModel.signupWithFacebook("facebook")
    }

    override fun skipLogin() {
        tvSkipSignUp.visibility = View.INVISIBLE
        addFragment(SelectGenderFragment.getInstance("user"),
            Constants.SELECT_GENDER_FRAGMENT)
    }

    override fun onBackPressed() {
        val selectGenderFragment =
            supportFragmentManager.findFragmentByTag(Constants.SELECT_GENDER_FRAGMENT)
        val videoLanguageFrgment =
            supportFragmentManager.findFragmentByTag(Constants.VIDEO_LANGUAG_EFRAGMENT)
        val chooseInterestFragment =
            supportFragmentManager.findFragmentByTag(Constants.CHOOSE_INTEREST_FRAGMENT)
        tvSkipSignUp.visibility = View.VISIBLE

        if (videoLanguageFrgment != null || chooseInterestFragment != null){
            supportFragmentManager.popBackStack()
        }else if (selectGenderFragment != null){
            removeAllFragment()
        }else
            super.onBackPressed()
    }

    /**
     * Remove all fragment from stack
     */
    private fun removeAllFragment() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }
}
