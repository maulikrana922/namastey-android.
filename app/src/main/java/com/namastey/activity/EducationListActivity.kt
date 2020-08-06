package com.namastey.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.EducationAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityEducationListBinding
import com.namastey.fragment.EducationFragment
import com.namastey.listeners.OnEducationItemClick
import com.namastey.model.EducationBean
import com.namastey.uiView.EducationView
import com.namastey.utils.Constants
import com.namastey.utils.GlideLib
import com.namastey.utils.SessionManager
import com.namastey.viewModel.EducationViewModel
import kotlinx.android.synthetic.main.activity_education_list.*
import javax.inject.Inject

class EducationListActivity : BaseActivity<ActivityEducationListBinding>(), EducationView,
    OnEducationItemClick {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var activityEducationListBinding: ActivityEducationListBinding
    private lateinit var educationViewModel: EducationViewModel
    private lateinit var educationAdapter: EducationAdapter
    private var educationList = ArrayList<EducationBean>()
    private var position: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        educationViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(EducationViewModel::class.java)
        activityEducationListBinding = bindViewData()
        activityEducationListBinding.viewModel = educationViewModel

        initData()

    }

    override fun onSuccessResponse(educationBean: EducationBean) {
        TODO("Not yet implemented")
    }

    override fun onSuccessEducationList(educationList: ArrayList<EducationBean>) {
        this.educationList = educationList
        educationAdapter =
            EducationAdapter(this.educationList, this@EducationListActivity, sessionManager, this)
        rvEducationList.adapter = educationAdapter
    }

    override fun getViewModel() = educationViewModel

    override fun getLayoutId() = R.layout.activity_education_list

    override fun getBindingVariable() = BR.viewModel

    private fun initData() {
        if (sessionManager.getStringValue(Constants.KEY_PROFILE_URL).isNotEmpty()) {
            GlideLib.loadImage(
                this@EducationListActivity,
                ivProfileImage,
                sessionManager.getStringValue(Constants.KEY_PROFILE_URL)
            )
        }
        educationViewModel.getEducationList()
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
        if (resultCode == Constants.REQUEST_CODE_EDUCATION) {
            if (data != null) {
                val educationBean = data.getParcelableExtra<EducationBean>("educationBean")

                if (position != 0) {
                    educationBean?.let {
                        educationList[position] = it
                        educationAdapter.notifyItemChanged(position)
                    }
                } else {
                    educationBean?.let {
                        educationList.add(it)
                        educationAdapter.notifyItemInserted(educationList.size)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        educationViewModel.onDestroy()
        super.onDestroy()
    }

    fun onClickEducationBack(view: View) {
        onBackPressed()
    }

    fun onClickAddEducation(view: View) {
        addFragment(
            EducationFragment.getInstance(
                true, EducationBean()
            ),
            Constants.EDUCATION_FRAGMENT
        )
    }

    override fun onEducationItemClick(educationBean: EducationBean, position: Int) {
        this.position = position
        addFragment(
            EducationFragment.getInstance(
                true, educationBean
            ),
            Constants.EDUCATION_FRAGMENT
        )
    }
}