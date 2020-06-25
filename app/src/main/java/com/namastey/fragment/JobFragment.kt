package com.namastey.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentEducationBinding
import com.namastey.databinding.FragmentJobBinding
import com.namastey.uiView.EducationView
import com.namastey.uiView.JobView
import com.namastey.utils.Utils
import com.namastey.viewModel.EducationViewModel
import com.namastey.viewModel.JobViewModel
import kotlinx.android.synthetic.main.fragment_education.*
import kotlinx.android.synthetic.main.fragment_interest_in.*
import kotlinx.android.synthetic.main.fragment_job.*
import javax.inject.Inject

class JobFragment : BaseFragment<FragmentJobBinding>(), JobView,
    View.OnClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var fragmentJobBinding: FragmentJobBinding
    private lateinit var layoutView: View
    private lateinit var jobViewModel: JobViewModel

    override fun getLayoutId() = R.layout.fragment_job

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance() =
            JobFragment().apply {

            }
    }

    private fun setupViewModel() {
        jobViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(JobViewModel::class.java)
        jobViewModel.setViewInterface(this)

        fragmentJobBinding = getViewBinding()
        fragmentJobBinding.viewModel = jobViewModel

        initData()
    }

    private fun initData() {
        ivCloseJob.setOnClickListener(this)
        btnJobDone.setOnClickListener(this)

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)
    }

    override fun getViewModel() = jobViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutView = view
        setupViewModel()

    }

    override fun onClick(p0: View?) {
        when (p0) {
            ivCloseJob -> {
                Utils.hideKeyboard(requireActivity())
                fragmentManager!!.popBackStack()
            }
            btnJobDone -> {
                Utils.hideKeyboard(requireActivity())
                fragmentManager!!.popBackStack()
            }
        }
    }

//    override fun onDestroy() {
//        jobViewModel.onDestroy()
//        super.onDestroy()
//    }
}