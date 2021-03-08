package com.namastey.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.JobListingAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityJobListingBinding
import com.namastey.fragment.JobFragment
import com.namastey.listeners.OnJobItemClick
import com.namastey.model.JobBean
import com.namastey.uiView.JobView
import com.namastey.utils.Constants
import com.namastey.utils.GlideLib
import com.namastey.utils.SessionManager
import com.namastey.viewModel.JobViewModel
import kotlinx.android.synthetic.main.activity_job_listing.*
import java.util.*
import javax.inject.Inject

class JobListingActivity : BaseActivity<ActivityJobListingBinding>(), JobView, OnJobItemClick {


    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var activityJobListingBinding: ActivityJobListingBinding
    private lateinit var jobViewModel: JobViewModel
    private var jobList = ArrayList<JobBean>()
    private lateinit var jobAdapter: JobListingAdapter
    private var position: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        jobViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(JobViewModel::class.java)
        activityJobListingBinding = bindViewData()
        activityJobListingBinding.viewModel = jobViewModel

        initData()

    }

    override fun onSuccessResponse(jobBean: JobBean) {
    }

    override fun onSuccessJobList(jobList: ArrayList<JobBean>) {
        this.jobList = jobList
        jobAdapter =
            JobListingAdapter(this.jobList, this@JobListingActivity, sessionManager, this)
        rvJobList.adapter = jobAdapter
    }

    override fun getViewModel() = jobViewModel

    override fun getLayoutId() = R.layout.activity_job_listing

    override fun getBindingVariable() = BR.viewModel

    private fun initData() {
        if (sessionManager.getStringValue(Constants.KEY_PROFILE_URL).isNotEmpty()) {
            GlideLib.loadImage(
                this@JobListingActivity,
                ivProfileImage,
                sessionManager.getStringValue(Constants.KEY_PROFILE_URL)
            )
        }
        jobViewModel.getJobList()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            finishActivity()
        }
    }

    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        super.onActivityReenter(resultCode, data)
        if (resultCode == Constants.REQUEST_CODE_JOB && data != null) {
            if (data.hasExtra("removeJob")) {
                jobList.removeAt(position)
                jobAdapter.notifyItemRemoved(position)
                jobAdapter.notifyItemRangeChanged(position, jobAdapter.itemCount)
            } else {
                val jobBean = data.getParcelableExtra<JobBean>("jobBean")
                if (position != -1) {
                    jobBean?.let {
                        jobList[position] = it
                        jobAdapter.notifyItemChanged(position)

                    }
                }else {
                    jobBean?.let {
                        jobList.add(it)
                        jobAdapter.notifyItemInserted(jobList.size)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        jobViewModel.onDestroy()
        super.onDestroy()
    }

    fun onClickJobBack(view: View) {
        onBackPressed()
    }

    fun onClickAddJob(view: View) {
        addFragment(
            JobFragment.getInstance(
                true, JobBean()
            ),
            Constants.JOB_FRAGMENT
        )
    }

    override fun onJobItemClick(jobBean: JobBean, position: Int) {
        this.position = position
        addFragment(
            JobFragment.getInstance(
                true, jobBean
            ),
            Constants.JOB_FRAGMENT
        )
    }

}