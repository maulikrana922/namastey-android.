package com.namastey.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.AccountSettingsActivity
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentBlockListBinding
import com.namastey.uiView.BlockListView
import com.namastey.viewModel.BlockListViewModel
import javax.inject.Inject


class BlockListFragment : BaseFragment<FragmentBlockListBinding>(), BlockListView {


    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var fragmentBlockListBinding: FragmentBlockListBinding
    private lateinit var blockListViewModel: BlockListViewModel
    private lateinit var layoutView: View


    override fun getViewModel() = blockListViewModel

    override fun getLayoutId() = R.layout.fragment_block_list

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance() =
            BlockListFragment().apply {
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
    }

    private fun setupViewModel() {
        blockListViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(BlockListViewModel::class.java)
        blockListViewModel.setViewInterface(this)
        fragmentBlockListBinding = getViewBinding()
        fragmentBlockListBinding.viewModel = blockListViewModel
    }

    override fun onResume() {
        super.onResume()
        (activity as AccountSettingsActivity).changeHeaderText(getString(R.string.block_list))
    }

    override fun onDestroy() {
        blockListViewModel.onDestroy()
        super.onDestroy()
    }
}