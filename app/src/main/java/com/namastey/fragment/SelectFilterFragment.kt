package com.namastey.fragment

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.FilterActivity
import com.namastey.adapter.SubCategoryAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentSelectFilterBinding
import com.namastey.model.CategoryBean
import com.namastey.uiView.SelectFilterView
import com.namastey.utils.Constants
import com.namastey.utils.GridSpacingItemDecoration
import com.namastey.utils.SessionManager
import com.namastey.viewModel.SelectFilterViewModel
import kotlinx.android.synthetic.main.fragment_select_filter.*
import javax.inject.Inject


class SelectFilterFragment : BaseFragment<FragmentSelectFilterBinding>(), SelectFilterView,
    View.OnClickListener, SubCategoryAdapter.OnItemClick {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var fragmentSelectFilterBinding: FragmentSelectFilterBinding
    private lateinit var selectFilterViewModel: SelectFilterViewModel
    private lateinit var layoutView: View
    private var subCategoryList: ArrayList<CategoryBean> = ArrayList()

    companion object {
        fun getInstance(
            subCategoryList: ArrayList<CategoryBean>,
            startColor: String,
            endColor: String,
            mainCategoryList: ArrayList<CategoryBean>
        ) =
            SelectFilterFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("subCategoryList", subCategoryList)
                    putString("startColor", startColor)
                    putString("endColor", endColor)
                    putSerializable("categoryList", mainCategoryList)
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

        ivSelectFilter.setOnClickListener(this)
        mainSelectFilterView.setOnClickListener(this)

        if (arguments!!.containsKey("subCategoryList")) {
            subCategoryList =
                arguments!!.getSerializable("subCategoryList") as ArrayList<CategoryBean>

            rvSelectFilter.addItemDecoration(GridSpacingItemDecoration(2, 20, false))
            val subCategoryAdapter = SubCategoryAdapter(subCategoryList, requireActivity(), this)
            rvSelectFilter.adapter = subCategoryAdapter

            val gd = GradientDrawable(
                GradientDrawable.Orientation.TR_BL,
                intArrayOf(
                    Color.parseColor(arguments!!.getString("startColor")),
                    Color.parseColor(arguments!!.getString("endColor"))
                )
            )

            gd.shape = GradientDrawable.RECTANGLE
            gd.cornerRadii = floatArrayOf(50f, 50f, 0f, 0f, 50f, 50f, 0f, 0f)
            llFilterBackground.background = gd
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

    override fun onClick(v: View?) {
        when (v) {
            ivSelectFilter -> {
                val intent = Intent(requireActivity(), FilterActivity::class.java)
                if (arguments!!.containsKey("categoryList")) {
                    intent.putExtra(
                        "categoryList",
                        arguments!!.getSerializable("categoryList") as ArrayList<*>
                    )
                }
                openActivityForResult(requireActivity(), intent, Constants.FILTER_OK)
            }
            mainSelectFilterView -> {
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    override fun onItemClick(subCategoryId: Int) {
        Log.d("Subcategory : ", subCategoryId.toString())

        if (sessionManager.isGuestUser()) {
            addFragment(
                SignUpFragment.getInstance(true
                ),
                Constants.SIGNUP_FRAGMENT
            )
        }else{
            activity!!.onActivityReenter(
                Constants.REQUEST_CODE,
                Intent().putExtra("fromSubCategory", true)
                    .putExtra("subCategoryId", subCategoryId)
            )
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}