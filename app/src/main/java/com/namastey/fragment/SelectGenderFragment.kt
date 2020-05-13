package com.namastey.fragment

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentSelectGenderBinding
import com.namastey.uiView.SelectGenderView
import com.namastey.viewModel.BaseViewModel
import com.namastey.viewModel.SelectGenderViewModel
import javax.inject.Inject

class SelectGenderFragment : BaseFragment<FragmentSelectGenderBinding>(),SelectGenderView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var fragmentSelectGenderBinding: FragmentSelectGenderBinding
    private lateinit var selectGenderViewModel: SelectGenderViewModel
    private lateinit var layoutView: View

    override fun onClose() {
        activity!!.onBackPressed()
    }

    override fun onNext() {
    }

    override fun getViewModel() = selectGenderViewModel

    override fun getLayoutId() = R.layout.fragment_select_gender

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance(title: String) =
            SelectGenderFragment().apply {
                arguments = Bundle().apply {
                    putString("user", title)
                }
            }
    }

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

    }

    private fun setupViewModel() {
        selectGenderViewModel = ViewModelProviders.of(this,viewModelFactory).get(SelectGenderViewModel::class.java)
        selectGenderViewModel.setViewInterface(this)

        fragmentSelectGenderBinding = getViewBinding()
        fragmentSelectGenderBinding.viewModel = selectGenderViewModel
    }
}