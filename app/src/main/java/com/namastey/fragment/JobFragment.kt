package com.namastey.fragment

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.request.RequestOptions
import com.google.gson.JsonObject
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.JobListingActivity
import com.namastey.dagger.module.GlideApp
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentJobBinding
import com.namastey.model.JobBean
import com.namastey.uiView.JobView
import com.namastey.utils.*
import com.namastey.viewModel.JobViewModel
import kotlinx.android.synthetic.main.dialog_alert.*
import kotlinx.android.synthetic.main.fragment_job.*
import java.util.*
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
    private var jobBean = JobBean()

    override fun getLayoutId() = R.layout.fragment_job

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance(isFromJobListing: Boolean, jobBean: JobBean) =
            JobFragment().apply {
                arguments = Bundle().apply {
                    putBoolean("isFromJobListing", isFromJobListing)
                    putParcelable("jobBean", jobBean)
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
        btnJobRemove.setOnClickListener(this)

        isFromJobListing = arguments!!.getBoolean("isFromJobListing", false)

        if (sessionManager.getUserGender() == Constants.Gender.female.name) {
            GlideApp.with(this).load(R.drawable.ic_female)
                .apply(RequestOptions.circleCropTransform()).placeholder(R.drawable.ic_female)
                .fitCenter().into(ivProfileImage)
        } else {
            GlideApp.with(this).load(R.drawable.ic_male)
                .apply(RequestOptions.circleCropTransform()).placeholder(R.drawable.ic_male)
                .fitCenter().into(ivProfileImage)
        }

        if (isFromJobListing) {
            if (sessionManager.getStringValue(Constants.KEY_PROFILE_URL).isNotEmpty()) {
                GlideLib.loadImage(
                    requireContext(),
                    ivProfileImage,
                    sessionManager.getStringValue(Constants.KEY_PROFILE_URL)
                )
            }
            jobBean = arguments!!.getParcelable<JobBean>("jobBean")!!
            if (jobBean.title.isNotEmpty()) {
                tvJob.text = getString(R.string.edit_job)
                btnJobRemove.visibility = View.VISIBLE
                edtJobTitle.setText(jobBean.title)
                edtJobCompany.setText(jobBean.company_name)
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

    override fun onSuccess(msg: String) {
        if (activity is JobListingActivity) {
            activity!!.onActivityReenter(
                Constants.REQUEST_CODE_JOB,
                Intent().putExtra("removeJob", true)
            )
            requireActivity().onBackPressed()
        }
    }

    override fun onSuccessJobList(jobList: ArrayList<JobBean>) {
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
                        val androidID = Settings.Secure.getString(requireActivity().contentResolver, Settings.Secure.ANDROID_ID)
                        jsonObject.addProperty(Constants.DEVICE_ID, androidID)
                        jsonObject.addProperty(Constants.DEVICE_TYPE, Constants.ANDROID)

                        if (isFromJobListing) {
                            if (jobBean.company_name.isNotEmpty()) {
                                jsonObject.addProperty(
                                    Constants.JOB_ID,
                                    jobBean.id
                                )
                            }
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
            btnJobRemove -> {
                if (sessionManager.getJobBean().id == jobBean.id) {
                    object : CustomAlertDialog(
                        requireActivity(),
                        resources.getString(R.string.msg_selected_job),
                        getString(R.string.ok),
                        ""
                    ) {
                        override fun onBtnClick(id: Int) {
                            dismiss()
                        }
                    }.show()
                } else {
                    object : CustomAlertDialog(
                        requireActivity(),
                        resources.getString(R.string.msg_remove_post),
                        getString(R.string.yes),
                        getString(R.string.cancel)
                    ) {
                        override fun onBtnClick(id: Int) {
                            when (id) {
                                btnPos.id -> {
                                    jobViewModel.removeJobAPI(jobBean.id)
                                }
                                btnNeg.id -> {
                                    dismiss()
                                }
                            }
                        }
                    }.show()
                }
            }
        }
    }

    override fun onDestroy() {
        jobViewModel.onDestroy()
        super.onDestroy()
    }
}