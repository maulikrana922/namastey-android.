package com.namastey.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentInterestInBinding
import com.namastey.utils.CustomAlertDialog
import com.namastey.utils.SessionManager
import com.namastey.viewModel.SelectCategoryViewModel
import kotlinx.android.synthetic.main.fragment_interest_in.*
import javax.inject.Inject

class InterestInFragment : BaseFragment<FragmentInterestInBinding>(),
    View.OnClickListener {

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var fragmentInterestInBinding: FragmentInterestInBinding
    private lateinit var layoutView: View
    private lateinit var selectCategoryViewModel: SelectCategoryViewModel
    private var interestIn = 0

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
        if (sessionManager.getInterestIn() != 0){
            interestIn = sessionManager.getInterestIn()

            when (interestIn) {
                1 -> setSelectedTextColor(tvInterestMen,true)
                2 -> setSelectedTextColor(tvInterestWomen,true)
                3 -> setSelectedTextColor(tvInterestEveryone,true)
            }
        }
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

        initListener()
        initData()

    }

    private fun initListener() {
        ivCloseInterestIn.setOnClickListener(this)
        btnInterestInDone.setOnClickListener(this)
        tvInterestMen.setOnClickListener(this)
        tvInterestWomen.setOnClickListener(this)
        tvInterestEveryone.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0) {
            ivCloseInterestIn -> {
                fragmentManager!!.popBackStack()
            }
            btnInterestInDone -> {
                if (interestIn != 0){
                    sessionManager.setInterestIn(interestIn)
                    activity!!.onBackPressed()
                }else{
                    object : CustomAlertDialog(
                        activity!!,
                        resources.getString(R.string.msg_select_interest_in), getString(R.string.ok), ""
                    ) {
                        override fun onBtnClick(id: Int) {
                            dismiss()
                        }
                    }.show()

                }
            }
            tvInterestMen -> {
                if (tvInterestMen.currentTextColor == Color.BLACK) {
                    interestIn = 1
                    setSelectedTextColor(tvInterestMen,true)
                }else
                    setSelectedTextColor(tvInterestMen,false)
            }
            tvInterestWomen -> {
                if (tvInterestWomen.currentTextColor == Color.BLACK){
                    interestIn = 2
                    setSelectedTextColor(tvInterestWomen,true)
                }else
                    setSelectedTextColor(tvInterestMen,false)

            }
            tvInterestEveryone -> {
                if (tvInterestEveryone.currentTextColor == Color.BLACK){
                    interestIn = 3
                    setSelectedTextColor(tvInterestEveryone,true)
                }else
                    setSelectedTextColor(tvInterestMen,false)
            }
        }
    }

    private fun setSelectedTextColor(view: TextView,isSelected: Boolean){
        tvInterestMen.setTextColor(Color.BLACK)
        tvInterestWomen.setTextColor(Color.BLACK)
        tvInterestEveryone.setTextColor(Color.BLACK)

        if (isSelected)
            view.setTextColor(Color.RED)
        else
            interestIn = 0
    }

//    override fun onDestroy() {
//        selectCategoryViewModel.onDestroy()
//        super.onDestroy()
//    }
}