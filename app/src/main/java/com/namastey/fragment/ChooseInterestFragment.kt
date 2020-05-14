package com.namastey.fragment

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.DashboardActivity
import com.namastey.adapter.InterestAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentChooseInterestBinding
import com.namastey.model.InterestBean
import com.namastey.uiView.ChooseInterestView
import com.namastey.utils.GridSpacingItemDecoration
import com.namastey.viewModel.ChooseInterestViewModel
import kotlinx.android.synthetic.main.fragment_choose_interest.*
import javax.inject.Inject

class ChooseInterestFragment : BaseFragment<FragmentChooseInterestBinding>(), ChooseInterestView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var fragmentChooseInterestBinding: FragmentChooseInterestBinding
    private lateinit var chooseInterestViewModel: ChooseInterestViewModel
    private lateinit var layoutView: View
    private val interestList: ArrayList<InterestBean> = ArrayList()
    private lateinit var interestAdapter: InterestAdapter

    override fun onClose() {
        fragmentManager!!.popBackStack()
    }

    override fun onNext() {

        ivSplash.visibility = View.VISIBLE

        Handler().postDelayed({
            startActivity(Intent(activity, DashboardActivity::class.java))
            activity!!.finish()
        }, 1000)
    }

    override fun getViewModel() = chooseInterestViewModel

    override fun getLayoutId() = R.layout.fragment_choose_interest

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance(title: String) =
            ChooseInterestFragment().apply {
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

        setLanguageList()

    }

    private fun setLanguageList() {

//        Set static data for UI
        for (int in 0..10) {
            var interestBean = InterestBean()
            interestBean.title = "Dance"
            interestBean.image = ""
            interestList.add(interestBean)
        }
//        Set static data for UI

        rvChooseInterest.addItemDecoration(GridSpacingItemDecoration(3, 10, false))
        interestAdapter = InterestAdapter(interestList, activity!!)
        rvChooseInterest.adapter = interestAdapter
    }

    private fun setupViewModel() {
        chooseInterestViewModel = ViewModelProviders.of(this, viewModelFactory).get(
            ChooseInterestViewModel::class.java
        )
        chooseInterestViewModel.setViewInterface(this)

        fragmentChooseInterestBinding = getViewBinding()
        fragmentChooseInterestBinding.viewModel = chooseInterestViewModel
    }
}