package com.namastey.fragment

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.google.gson.JsonObject
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.JobListingActivity
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentJobBinding
import com.namastey.model.JobBean
import com.namastey.uiView.JobView
import com.namastey.utils.Constants
import com.namastey.utils.GlideLib
import com.namastey.utils.SessionManager
import com.namastey.utils.Utils
import com.namastey.viewModel.JobViewModel
import kotlinx.android.synthetic.main.activity_education_list.*
import kotlinx.android.synthetic.main.fragment_job.*
import kotlinx.android.synthetic.main.fragment_job.ivProfileImage
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
    private var isFromJobListing = false

    override fun getLayoutId() = R.layout.fragment_job

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance(isFromJobListing: Boolean) =
            JobFragment().apply {
                arguments = Bundle().apply {
                    putBoolean("isFromJobListing", isFromJobListing)
                }
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

        isFromJobListing = arguments!!.getBoolean("isFromJobListing", false)

        if (isFromJobListing) {
            if (sessionManager.getStringValue(Constants.KEY_PROFILE_URL).isNotEmpty()){
                GlideLib.loadImage(requireContext(),ivProfileImage,sessionManager.getStringValue(Constants.KEY_PROFILE_URL))
            }
        } else {
            val jobBean = sessionManager.getJobBean()
            edtJobTitle.setText(jobBean.title)
            edtJobCompany.setText(jobBean.company_name)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)
    }

    override fun onSuccessResponse(jobBean: JobBean) {
        if (activity is JobListingActivity) {
            activity!!.onActivityReenter(
                Constants.REQUEST_CODE_JOB,
                Intent().putExtra("jobBean", jobBean)
            )
        } else {
            sessionManager.setJobBean(jobBean)
        }
        activity!!.onBackPressed()
    }

    override fun onSuccessJobList(jobList: ArrayList<JobBean>) {
        TODO("Not yet implemented")
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

                        if (isFromJobListing) {

                        } else {
                            if (sessionManager.getJobBean().company_name.isNotEmpty())
                                jsonObject.addProperty(
                                    Constants.JOB_ID,
                                    sessionManager.getJobBean().id
                                )
                        }
                        jobViewModel.addJob(jsonObject)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        jobViewModel.onDestroy()
        super.onDestroy()
    }
}