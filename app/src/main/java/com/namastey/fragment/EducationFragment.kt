package com.namastey.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentEducationBinding
import com.namastey.uiView.EducationView
import com.namastey.utils.Utils
import com.namastey.viewModel.EducationViewModel
import kotlinx.android.synthetic.main.fragment_education.*
import kotlinx.android.synthetic.main.fragment_interest_in.*
import javax.inject.Inject

class EducationFragment : BaseFragment<FragmentEducationBinding>(), EducationView,
    View.OnClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var fragmentEducationBinding: FragmentEducationBinding
    private lateinit var layoutView: View
    private lateinit var educationViewModel: EducationViewModel

    override fun getLayoutId() = R.layout.fragment_education

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance() =
            EducationFragment().apply {

            }
    }

    private fun setupViewModel() {
        educationViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(EducationViewModel::class.java)
        educationViewModel.setViewInterface(this)

        fragmentEducationBinding = getViewBinding()
        fragmentEducationBinding.viewModel = educationViewModel

        initData()
    }

    private fun initData() {
        ivCloseEducation.setOnClickListener(this)
        btnEducationDone.setOnClickListener(this)

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)
    }

    override fun getViewModel() = educationViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutView = view
        setupViewModel()

    }

    override fun onClick(p0: View?) {
        when (p0) {
            ivCloseEducation -> {
                Utils.hideKeyboard(requireActivity())
                fragmentManager!!.popBackStack()
            }
            btnEducationDone -> {
                Utils.hideKeyboard(requireActivity())
                fragmentManager!!.popBackStack()
            }
        }
    }

//    override fun onDestroy() {
//        educationViewModel.onDestroy()
//        super.onDestroy()
//    }
}