package com.namastey.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentInterestInBinding
import com.namastey.viewModel.SelectCategoryViewModel
import kotlinx.android.synthetic.main.fragment_interest_in.*
import javax.inject.Inject

class InterestInFragment : BaseFragment<FragmentInterestInBinding>(),
    View.OnClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var fragmentInterestInBinding: FragmentInterestInBinding
    private lateinit var layoutView: View
    private lateinit var selectCategoryViewModel: SelectCategoryViewModel

    override fun getLayoutId() = R.layout.fragment_interest_in

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance() =
            InterestInFragment().apply {

            }
    }

    private fun setupViewModel() {
        selectCategoryViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(SelectCategoryViewModel::class.java)
        selectCategoryViewModel.setViewInterface(this)

        fragmentInterestInBinding = getViewBinding()
        fragmentInterestInBinding.viewModel = selectCategoryViewModel

        initData()
    }

    private fun initData() {
        ivCloseInterestIn.setOnClickListener(this)
        btnInterestInDone.setOnClickListener(this)
        tvInterestMen.setOnClickListener(this)
        tvInterestWomen.setOnClickListener(this)
        tvInterestEveryone.setOnClickListener(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)
    }

    override fun getViewModel() = selectCategoryViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutView = view
//        setupViewModel()

        initData()

    }

    override fun onClick(p0: View?) {
        when (p0) {
            ivCloseInterestIn -> {
                fragmentManager!!.popBackStack()
            }
            btnInterestInDone -> {
                fragmentManager!!.popBackStack()
            }
            tvInterestMen -> {
                if (tvInterestMen.currentTextColor == Color.BLACK)
                    tvInterestMen.setTextColor(Color.RED)
                else
                    tvInterestMen.setTextColor(Color.BLACK)
            }
            tvInterestWomen -> {
                if (tvInterestWomen.currentTextColor == Color.BLACK)
                    tvInterestWomen.setTextColor(Color.RED)
                else
                    tvInterestWomen.setTextColor(Color.BLACK)
            }
            tvInterestEveryone -> {
                if (tvInterestEveryone.currentTextColor == Color.BLACK)
                    tvInterestEveryone.setTextColor(Color.RED)
                else
                    tvInterestEveryone.setTextColor(Color.BLACK)
            }
        }
    }

//    override fun onDestroy() {
//        selectCategoryViewModel.onDestroy()
//        super.onDestroy()
//    }
}