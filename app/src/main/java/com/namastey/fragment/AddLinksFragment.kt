package com.namastey.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentAddLinksBinding
import com.namastey.uiView.ProfileInterestView
import com.namastey.viewModel.ProfileInterestViewModel
import kotlinx.android.synthetic.main.fragment_add_links.*
import kotlinx.android.synthetic.main.fragment_interest_in.*
import javax.inject.Inject

class AddLinksFragment : BaseFragment<FragmentAddLinksBinding>(),ProfileInterestView,
    View.OnClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var fragmentAddLinksBinding: FragmentAddLinksBinding
    private lateinit var layoutView: View
    private lateinit var profileInterestViewModel: ProfileInterestViewModel

    override fun getLayoutId() = R.layout.fragment_add_links

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance() =
            AddLinksFragment().apply {

            }
    }

    private fun setupViewModel() {
        profileInterestViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ProfileInterestViewModel::class.java)
        profileInterestViewModel.setViewInterface(this)

        fragmentAddLinksBinding = getViewBinding()
        fragmentAddLinksBinding.viewModel = profileInterestViewModel

        initData()
    }

    private fun initData() {
        ivCloseAddLink.setOnClickListener(this)
        tvAddLinkSave.setOnClickListener(this)

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)
    }

    override fun getViewModel() = profileInterestViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutView = view
        setupViewModel()

    }

    override fun onClick(p0: View?) {
        when (p0) {
            ivCloseAddLink -> {
                fragmentManager!!.popBackStack()
            }
            tvAddLinkSave -> {
                fragmentManager!!.popBackStack()
            }
        }
    }

//    override fun onDestroy() {
//        profileInterestViewModel.onDestroy()
//        super.onDestroy()
//    }
}