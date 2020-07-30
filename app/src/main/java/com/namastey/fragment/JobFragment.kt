package com.namastey.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.google.gson.JsonObject
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentJobBinding
import com.namastey.model.JobBean
import com.namastey.uiView.JobView
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import com.namastey.utils.Utils
import com.namastey.viewModel.JobViewModel
import kotlinx.android.synthetic.main.fragment_job.*
import javax.inject.Inject

class JobFragment : BaseFragment<FragmentJobBinding>(), JobView,
    View.OnClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    @Inject
    lateinit var sessionManager: SessionManager
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

        var jobBean = sessionManager.getJobBean()
        edtJobTitle.setText(jobBean.title)
        edtJobCompany.setText(jobBean.company_name)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)
    }

    override fun onSuccessResponse(jobBean: JobBean) {
//        jobBean.company_name = edtJobCompany.text.toString().trim()
//        jobBean.title = edtJobTitle.text.toString().trim()
        sessionManager.setJobBean(jobBean)
        activity!!.onBackPressed()
    }

    override fun getViewModel() = jobViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutView = view
        setupViewModel()

    }

    override fun onClick(p0: View?) {
        Utils.hideKeyboard(requireActivity())
        when (p0) {
            ivCloseJob -> {
                fragmentManager!!.popBackStack()
            }
            btnJobDone -> {
                val jsonObject = JsonObject()
                when {
                    TextUtils.isEmpty(edtJobTitle.text.toString()) -> showMsg(getString(R.string.msg_empty_job_title))
                    TextUtils.isEmpty(edtJobCompany.text.toString()) -> showMsg(getString(R.string.msg_empty_job_company))
                    else -> {
                        jsonObject.addProperty(Constants.TITLE, edtJobTitle.text.toString().trim())
                        jsonObject.addProperty(
                            Constants.COMPANY_NAME,
                            edtJobCompany.text.toString().trim()
                        )
                        jsonObject.addProperty(Constants.DEVICE_ID, "23456789")    // Need to change
                        jsonObject.addProperty(Constants.DEVICE_TYPE, Constants.ANDROID)

                        if (sessionManager.getJobBean().company_name.isNotEmpty())
                            jsonObject.addProperty(Constants.JOB_ID, sessionManager.getJobBean().id)

                        jobViewModel.addJob(jsonObject)
                    }
                }
//                fragmentManager!!.popBackStack()
            }
        }
    }

    override fun onDestroy() {
        jobViewModel.onDestroy()
        super.onDestroy()
    }
}