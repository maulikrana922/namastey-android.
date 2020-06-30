package com.namastey.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentEducationBinding
import com.namastey.model.EducationBean
import com.namastey.uiView.EducationView
import com.namastey.utils.SessionManager
import com.namastey.utils.Utils
import com.namastey.viewModel.EducationViewModel
import kotlinx.android.synthetic.main.fragment_education.*
import javax.inject.Inject

class EducationFragment : BaseFragment<FragmentEducationBinding>(), EducationView,
    View.OnClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    @Inject
    lateinit var sessionManager: SessionManager
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

        initListener()
        initData()
    }

    private fun initListener() {
        ivCloseEducation.setOnClickListener(this)
        btnEducationDone.setOnClickListener(this)
    }

    private fun initData() {
        var educationBean = sessionManager.getEducationBean()
        edtCollegeName.setText(educationBean.collegeName)
        edtEducationYear.setText(educationBean.year)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)
    }

    override fun onSuccessResponse(educationBean: EducationBean) {
        educationBean.collegeName = edtCollegeName.text.toString()
        educationBean.year = edtEducationYear.text.toString()
        sessionManager.setEducationBean(educationBean)
        fragmentManager!!.popBackStack()
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
                when {
                    TextUtils.isEmpty(edtCollegeName.text.toString()) -> {
                        showMsg(getString(R.string.msg_empty_college_name))
                    }
                    TextUtils.isEmpty(edtEducationYear.text.toString()) -> {
                        showMsg(getString(R.string.msg_empty_year))
                    }
                    else -> {
                        if (sessionManager.getEducationBean().collegeName.isEmpty())
                            educationViewModel.addEducation(edtCollegeName.text.toString().trim(),edtEducationYear.text.toString().trim())
                        else
                            educationViewModel.updateEducation(sessionManager.getEducationBean().user_education_Id,edtCollegeName.text.toString().trim(),edtEducationYear.text.toString().trim())
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        educationViewModel.onDestroy()
        super.onDestroy()
    }
}