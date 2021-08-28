package com.namastey.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.request.RequestOptions
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.EditProfileActivity
import com.namastey.adapter.SelectCategoryAdapter
import com.namastey.dagger.module.GlideApp
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentSelectCategoryBinding
import com.namastey.listeners.OnCategoryItemClick
import com.namastey.model.CategoryBean
import com.namastey.uiView.ProfileSelectCategoryView
import com.namastey.utils.Constants
import com.namastey.utils.CustomAlertDialog
import com.namastey.utils.GlideLib
import com.namastey.utils.SessionManager
import com.namastey.viewModel.SelectCategoryViewModel
import kotlinx.android.synthetic.main.fragment_select_category.*
import java.util.*
import javax.inject.Inject

class SelectCategoryFragment : BaseFragment<FragmentSelectCategoryBinding>(),
    ProfileSelectCategoryView, View.OnClickListener, OnCategoryItemClick {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var fragmentSelectCategoryBinding: FragmentSelectCategoryBinding
    private lateinit var selectCategoryViewModel: SelectCategoryViewModel
    private lateinit var layoutView: View
    private var selectedCategoryList: ArrayList<CategoryBean> = ArrayList()
    private var selectCategoryId: ArrayList<Int> = ArrayList()
    private var categoryBean: CategoryBean = CategoryBean()

    override fun onSuccessCategory(categoryBeanList: ArrayList<CategoryBean>) {

//        Log.e("SelectCategoryFragment", "selectedCategoryList: \t $selectedCategoryList")

        if (sessionManager.getCategoryList().size == 0) {
            for (category in categoryBeanList) {
                categoryBean = category
                if (selectCategoryId.contains(categoryBean.id)) {
                    selectedCategoryList.add(categoryBean)
                }
            }
            sessionManager.setCategoryList(selectedCategoryList)
        }
        val selectCategoryAdapter =
            SelectCategoryAdapter(
                categoryBeanList,
                selectCategoryId,
                requireActivity(),
                this,
                sessionManager
            )
        rvSelectCategory.adapter = selectCategoryAdapter
    }

    override fun getViewModel() = selectCategoryViewModel

    override fun getLayoutId() = R.layout.fragment_select_category

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance() =
            SelectCategoryFragment().apply {
            }
    }

    private fun setupViewModel() {
        selectCategoryViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(SelectCategoryViewModel::class.java)
        selectCategoryViewModel.setViewInterface(this)

        fragmentSelectCategoryBinding = getViewBinding()
        fragmentSelectCategoryBinding.viewModel = selectCategoryViewModel

        initData()
    }

    private fun initData() {
        ivCloseCategory.setOnClickListener(this)
        btnSelectCategoryDone.setOnClickListener(this)
        if (sessionManager.getUserGender() == Constants.Gender.female.name) {
            GlideApp.with(this).load(R.drawable.ic_female)
                .apply(RequestOptions.circleCropTransform()).placeholder(R.drawable.ic_female)
                .fitCenter().into(ivProfileImage)
        } else {
            GlideApp.with(this).load(R.drawable.ic_male)
                .apply(RequestOptions.circleCropTransform()).placeholder(R.drawable.ic_male)
                .fitCenter().into(ivProfileImage)
        }
        if (sessionManager.getStringValue(Constants.KEY_PROFILE_URL).isNotEmpty()) {
            GlideLib.loadImage(
                requireActivity(),
                ivProfileImage,
                sessionManager.getStringValue(Constants.KEY_PROFILE_URL)
            )
        }
        selectedCategoryList = sessionManager.getCategoryList()
        if (selectedCategoryList.size == 0)
            selectCategoryId = sessionManager.getChooseInterestList()

        selectCategoryViewModel.getCategoryList(sessionManager.getUserId())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutView = view
        setupViewModel()
    }

    override fun onClick(p0: View?) {
        when (p0) {
            ivCloseCategory -> {
                fragmentManager!!.popBackStack()
            }
            btnSelectCategoryDone -> {
                if (selectedCategoryList.size >= 3) {
                    sessionManager.setCategoryList(ArrayList())
                    sessionManager.setCategoryList(selectedCategoryList)
                    sessionManager.setChooseInterestList(ArrayList())
                    sessionManager.setChooseInterestList(selectCategoryId)
                    if (activity is EditProfileActivity) {
                        targetFragment!!.onActivityResult(
                            Constants.REQUEST_CODE,
                            Activity.RESULT_OK,
                            Intent().putExtra("fromSelectCategory", true)
                        )
                    }
                    activity!!.onBackPressed()
                } else {
                    object : CustomAlertDialog(
                        activity!!,
                        resources.getString(R.string.choose_minimum_category),
                        getString(R.string.ok),
                        ""
                    ) {
                        override fun onBtnClick(id: Int) {
                            dismiss()
                        }
                    }.show()
                }
            }
        }
    }

    override fun onDestroy() {
        selectCategoryViewModel.onDestroy()
        super.onDestroy()
    }

    /**
     * click on adapter category items
     */
    @SuppressLint("NewApi")
    override fun onCategoryItemClick(categoryBean: CategoryBean) {
        if (selectedCategoryList.any { it.id == categoryBean.id }) {
            selectedCategoryList.removeIf { bean -> bean.id == categoryBean.id }
        } else {
            selectedCategoryList.add(categoryBean)
        }
    }

    override fun onSubCategoryItemClick(position: Int) {
    }
}