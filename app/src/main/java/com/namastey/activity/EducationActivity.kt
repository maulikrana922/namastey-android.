package com.namastey.activity

import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.google.gson.JsonObject
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityEducationBinding
import com.namastey.model.EducationBean
import com.namastey.model.JobBean
import com.namastey.uiView.EducationView
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import com.namastey.viewModel.BaseViewModel
import com.namastey.viewModel.EducationViewModel
import kotlinx.android.synthetic.main.activity_education.*
import kotlinx.android.synthetic.main.fragment_education.*
import kotlinx.android.synthetic.main.fragment_job.*
import java.util.ArrayList
import javax.inject.Inject

class EducationActivity : BaseActivity<ActivityEducationBinding>(), EducationView {

    private lateinit var educationViewModel: EducationViewModel
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var binding: ActivityEducationBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        setupViewModel()
    }

    private fun setupViewModel() {
        educationViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(EducationViewModel::class.java)
        educationViewModel.setViewInterface(this)

        binding = bindViewData()
        binding.viewModel = educationViewModel

        initData()
    }

    private fun initData(){

    }

    override fun onSuccessResponse(educationBean: EducationBean) {
      // sessionManager.setEducationBean(educationBean)

        if (TextUtils.isEmpty(edtOccupation.text.toString().trim())){
            openActivity(this@EducationActivity, ProfilePicActivity())
        }else{
            addJob()
        }
    }

    override fun onSuccessResponseJob(jobBean: JobBean) {
//        sessionManager.setJobBean(jobBean)

        openActivity(this@EducationActivity, ProfilePicActivity())

    }
    override fun onSuccessEducationList(educationList: ArrayList<EducationBean>) {
        TODO("Not yet implemented")
    }

    override fun getViewModel() = educationViewModel

    override fun getLayoutId() = R.layout.activity_education

    override fun getBindingVariable() = BR.viewModel

    override fun onBackPressed() {
        finishActivity()
    }

    fun onClickContinue(view: View) {
        if (!TextUtils.isEmpty(edtEducation.text.toString().trim())){
            educationViewModel.addEducation(
                edtEducation.text.toString().trim()
            )
        }else if (!TextUtils.isEmpty(edtOccupation.text.toString().trim())){
            addJob()
        }
    }

    private fun addJob(){
        val jsonObject = JsonObject()
        jsonObject.addProperty(Constants.TITLE, edtOccupation.text.toString().trim())
        val androidID = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
        jsonObject.addProperty(Constants.DEVICE_ID, androidID)
        jsonObject.addProperty(Constants.DEVICE_TYPE, Constants.ANDROID)

        educationViewModel.addJob(jsonObject)
    }
    fun onClickEducatoinBack(view: View) {
        onBackPressed()
    }

    fun onClickSkip(view: View) {
        openActivity(this@EducationActivity, ProfilePicActivity())
    }
}