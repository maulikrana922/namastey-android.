package com.namastey.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.SubCategoryAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentSelectFilterBinding
import com.namastey.model.CategoryBean
import com.namastey.uiView.SelectFilterView
import com.namastey.utils.GridSpacingItemDecoration
import com.namastey.viewModel.SelectFilterViewModel
import kotlinx.android.synthetic.main.fragment_select_filter.*
import javax.inject.Inject

class SelectFilterFragment : BaseFragment<FragmentSelectFilterBinding>(), SelectFilterView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var fragmentSelectFilterBinding: FragmentSelectFilterBinding
    private lateinit var selectFilterViewModel: SelectFilterViewModel
    private lateinit var layoutView: View
    private var subCategoryList: ArrayList<CategoryBean> = ArrayList()

    companion object {
        fun getInstance(subCategoryList: ArrayList<CategoryBean>) =
            SelectFilterFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("subCategoryList", subCategoryList)
                }
            }
    }

    override fun getViewModel() = selectFilterViewModel

    override fun getLayoutId() = R.layout.fragment_select_filter

    override fun getBindingVariable() = BR.viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutView = view
        setupViewModel()
        initUI()
    }

    private fun initUI() {

        if (arguments!!.containsKey("subCategoryList")) {
            subCategoryList =
                arguments!!.getSerializable("subCategoryList") as ArrayList<CategoryBean>

            rvSelectFilter.addItemDecoration(GridSpacingItemDecoration(2, 10, false))
            var subCategoryAdapter = SubCategoryAdapter(subCategoryList, activity!!)
            rvSelectFilter.adapter = subCategoryAdapter
        }

    }


    private fun setupViewModel() {
        selectFilterViewModel = ViewModelProviders.of(this, viewModelFactory).get(
            SelectFilterViewModel::class.java
        )
        selectFilterViewModel.setViewInterface(this)

        fragmentSelectFilterBinding = getViewBinding()
        fragmentSelectFilterBinding.viewModel = selectFilterViewModel
    }

    override fun onDestroy() {
        super.onDestroy()
    }


}