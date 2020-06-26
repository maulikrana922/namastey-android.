package com.namastey.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.SelectCategoryAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentSelectCategoryBinding
import com.namastey.listeners.OnCategoryItemClick
import com.namastey.model.CategoryBean
import com.namastey.uiView.ProfileSelectCategoryView
import com.namastey.utils.SessionManager
import com.namastey.viewModel.SelectCategoryViewModel
import kotlinx.android.synthetic.main.fragment_select_category.*
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

    override fun onSuccessCategory(categoryBeanList: ArrayList<CategoryBean>) {
        var selectCategoryAdapter =
            SelectCategoryAdapter(categoryBeanList, requireActivity(), this, sessionManager)
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

        selectCategoryViewModel.getCategoryList()
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
                sessionManager.setCategoryList(ArrayList())
                sessionManager.setCategoryList(selectedCategoryList)
                fragmentManager!!.popBackStack()
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
    override fun onCategoryItemClick(categoryBean: CategoryBean) {
        if (selectedCategoryList.any { it.id == categoryBean.id }) {
            selectedCategoryList.remove(categoryBean)
        } else {
            selectedCategoryList.add(categoryBean)
        }
    }
}