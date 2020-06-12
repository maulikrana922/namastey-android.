package com.namastey.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentSignUpBinding
import com.namastey.roomDB.entity.User
import com.namastey.uiView.SignUpView
import com.namastey.viewModel.SignUpViewModel
import kotlinx.android.synthetic.main.fragment_sign_up.*
import javax.inject.Inject

class SignUpFragment : BaseFragment<FragmentSignUpBinding>(), SignUpView,
    View.OnClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var fragmentSignUpBinding: FragmentSignUpBinding
    private lateinit var signUpViewModel: SignUpViewModel
    private lateinit var layoutView: View
    override fun skipLogin() {

    }

    override fun onSuccessResponse(user: User) {
    }


    override fun getViewModel() = signUpViewModel

    override fun getLayoutId() = R.layout.fragment_sign_up

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance() =
            SignUpFragment().apply {

            }
    }

    private fun setupViewModel() {
        signUpViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(SignUpViewModel::class.java)
        signUpViewModel.setViewInterface(this)

        fragmentSignUpBinding = getViewBinding()
        fragmentSignUpBinding.viewModel = signUpViewModel

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
        ivSignupClose.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v){
            ivSignupClose ->{
                fragmentManager!!.popBackStack()
            }
        }
    }
}