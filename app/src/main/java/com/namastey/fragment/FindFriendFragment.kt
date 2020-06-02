package com.namastey.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentFindFriendBinding
import com.namastey.uiView.FindFriendView
import com.namastey.viewModel.FindFriendViewModel
import kotlinx.android.synthetic.main.fragment_find_friend.*
import javax.inject.Inject

class FindFriendFragment : BaseFragment<FragmentFindFriendBinding>(), FindFriendView, View.OnClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var fragmentFindFriendBinding: FragmentFindFriendBinding
    private lateinit var findFriendViewModel: FindFriendViewModel
    private lateinit var layoutView: View


    override fun getViewModel() = findFriendViewModel

    override fun getLayoutId() = R.layout.fragment_find_friend

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance() =
            FindFriendFragment().apply {

            }
    }

    private fun setupViewModel() {
        findFriendViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(FindFriendViewModel::class.java)
        findFriendViewModel.setViewInterface(this)

        fragmentFindFriendBinding = getViewBinding()
        fragmentFindFriendBinding.viewModel = findFriendViewModel

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

        ivFindClose.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v){
            ivFindClose ->{
                fragmentManager!!.popBackStack()
            }
        }
    }
}