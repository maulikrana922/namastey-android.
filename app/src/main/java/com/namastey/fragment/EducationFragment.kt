package com.namastey.fragment

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.request.RequestOptions
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.EducationListActivity
import com.namastey.dagger.module.GlideApp
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentEducationBinding
import com.namastey.model.EducationBean
import com.namastey.model.JobBean
import com.namastey.uiView.EducationView
import com.namastey.utils.*
import com.namastey.viewModel.EducationViewModel
import kotlinx.android.synthetic.main.activity_job_listing.*
import kotlinx.android.synthetic.main.dialog_alert.*
import kotlinx.android.synthetic.main.fragment_education.*
import kotlinx.android.synthetic.main.fragment_education.ivProfileImage
import java.util.*
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
    private var isFromListing = false
    private var educationBean = EducationBean()

    override fun getLayoutId() = R.layout.fragment_education

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance(isFromListing: Boolean, educationBean: EducationBean) =
            EducationFragment().apply {
                arguments = Bundle().apply {
                    putBoolean("isFromListing", isFromListing)
                    putParcelable("educationBean", educationBean)
                }
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
        btnEducationRemove.setOnClickListener(this)
    }

    private fun initData() {

        isFromListing = arguments!!.getBoolean("isFromListing", false)
        if (sessionManager.getUserGender() == Constants.Gender.female.name) {
            GlideApp.with(this).load(R.drawable.ic_female)
                .apply(RequestOptions.circleCropTransform()).placeholder(R.drawable.ic_female)
                .fitCenter().into(ivProfileImage)
        } else {
            GlideApp.with(this).load(R.drawable.ic_male)
                .apply(RequestOptions.circleCropTransform()).placeholder(R.drawable.ic_male)
                .fitCenter().into(ivProfileImage)
        }
        if (isFromListing) {
            if (sessionManager.getStringValue(Constants.KEY_PROFILE_URL).isNotEmpty()) {
                GlideLib.loadImage(
                    requireContext(), ivProfileImage, sessionManager.getStringValue(
                        Constants.KEY_PROFILE_URL
                    )
                )
            }
            educationBean = arguments!!.getParcelable<EducationBean>("educationBean")!!
            if (educationBean.college.isNotEmpty()) {
                tvAddEducation.text = getString(R.string.edit_education)
                btnEducationRemove.visibility = View.VISIBLE
                edtCollegeName.setText(educationBean.college)
                edtEducationCourse.setText(educationBean.course)
            }
        } else {
           // edtCollegeName.setText( sessionManager.getStringValue(Constants.KEY_EDUCATION))
            //edtEducationCourse.setText( sessionManager.getStringValue(Constants.KEY_EDUCATION))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)
    }

    override fun onSuccessResponse(educationBean: EducationBean) {
//        educationBean.college = edtCollegeName.text.toString()
//        educationBean.course = edtEducationCourse.text.toString()

        if (activity is EducationListActivity) {
            activity!!.onActivityReenter(
                Constants.REQUEST_CODE_EDUCATION,
                Intent().putExtra("educationBean", educationBean)
            )
        } else {
            //sessionManager.setEducationBean(educationBean)
        }
        activity!!.onBackPressed()
    }

    override fun onSuccess(msg: String) {
        if (activity is EducationListActivity) {
            activity!!.onActivityReenter(
                Constants.REQUEST_CODE_EDUCATION,
                Intent().putExtra("removeEducation", true)
            )
            requireActivity().onBackPressed()
        }
    }

    override fun onSuccessEducationList(educationList: ArrayList<EducationBean>) {
    }

    override fun onSuccessResponseJob(jobBean: JobBean) {
        TODO("Not yet implemented")
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
                    TextUtils.isEmpty(edtEducationCourse.text.toString()) -> {
                        showMsg(getString(R.string.msg_empty_occupation))
                    }
                    else -> {
                        if (isFromListing) {
                            if (educationBean.college.isNotEmpty()) {
                                educationViewModel.updateEducation(
                                    educationBean.id,
                                    edtCollegeName.text.toString().trim(),
                                    edtEducationCourse.text.toString().trim()
                                )
                            } else {
                                educationViewModel.addEducation(
                                    edtEducationCourse.text.toString().trim()
                                )
                            }
                        } else {
                            /*if (sessionManager.getEducationBean().college.isEmpty())
                                educationViewModel.addEducation(
                                    edtEducationCourse.text.toString().trim()
                                )
                            else
                                educationViewModel.updateEducation(
                                    sessionManager.getEducationBean().id,
                                    edtCollegeName.text.toString().trim(),
                                    edtEducationCourse.text.toString().trim()
                                )*/
                        }
                    }
                }
            }
         /*   btnEducationRemove -> {
                if (sessionManager.getEducationBean().id == educationBean.id) {
                    object : CustomAlertDialog(
                        requireActivity(),
                        resources.getString(R.string.msg_selected_education),
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
                                    educationViewModel.removeEducationAPI(educationBean.id)
                                }
                                btnNeg.id -> {
                                    dismiss()
                                }
                            }
                        }
                    }.show()
                }
            }*/
        }
    }

    override fun onDestroy() {
        educationViewModel.onDestroy()
        super.onDestroy()
    }
}