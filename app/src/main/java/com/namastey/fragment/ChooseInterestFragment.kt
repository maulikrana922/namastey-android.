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
import com.namastey.listeners.OnImageItemClick
import com.namastey.model.InterestBean
import com.namastey.uiView.ChooseInterestView
import com.namastey.utils.Constants
import com.namastey.utils.CustomAlertDialog
import com.namastey.utils.GridSpacingItemDecoration
import com.namastey.viewModel.ChooseInterestViewModel
import kotlinx.android.synthetic.main.fragment_choose_interest.*
import javax.inject.Inject

class ChooseInterestFragment : BaseFragment<FragmentChooseInterestBinding>(), ChooseInterestView,OnImageItemClick {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var fragmentChooseInterestBinding: FragmentChooseInterestBinding
    private lateinit var chooseInterestViewModel: ChooseInterestViewModel
    private lateinit var layoutView: View
    private val interestList: ArrayList<InterestBean> = ArrayList()
    private lateinit var interestAdapter: InterestAdapter
    private var selectInterestIdList: ArrayList<Int> = ArrayList()

    override fun onClose() {
        fragmentManager!!.popBackStack()
    }

    override fun onNext() {

        if (noOfSelectedImage >= Constants.MIN_CHOOSE_INTEREST){
            ivSplashBackground.visibility = View.VISIBLE
            ivSplash.visibility = View.VISIBLE

            ivSplash.animate()
                .setStartDelay(500)
                .setDuration(1000)
                .scaleX(20f)
                .scaleY(20f)
                .alpha(0f);


            Handler().postDelayed({
                startActivity(Intent(activity, DashboardActivity::class.java))
                activity!!.finish()
            }, 1000)
        }else{
            object : CustomAlertDialog(
                activity!!,
                resources.getString(R.string.msg_min_choose_interest), getString(R.string.ok), ""
            ){
                override fun onBtnClick(id: Int) {
                    dismiss()
                }
            }.show()
        }

    }

    override fun onSuccess(interestList: ArrayList<InterestBean>) {
        rvChooseInterest.addItemDecoration(GridSpacingItemDecoration(3, 10, false))
        interestAdapter = InterestAdapter(interestList, activity!!,this)
        rvChooseInterest.adapter = interestAdapter
    }

    override fun getViewModel() = chooseInterestViewModel

    override fun getLayoutId() = R.layout.fragment_choose_interest

    override fun getBindingVariable() = BR.viewModel

    companion object {
        var noOfSelectedImage = 0

        fun getInstance(title: String) =
            ChooseInterestFragment().apply {
                arguments = Bundle().apply {
                    putString("user", title)
                }
                noOfSelectedImage = 0
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

        chooseInterestViewModel.getChooseInterestList()

    }

    private fun setupViewModel() {
        chooseInterestViewModel = ViewModelProviders.of(this, viewModelFactory).get(
            ChooseInterestViewModel::class.java
        )
        chooseInterestViewModel.setViewInterface(this)

        fragmentChooseInterestBinding = getViewBinding()
        fragmentChooseInterestBinding.viewModel = chooseInterestViewModel
    }

    override fun onDestroy() {
        chooseInterestViewModel.onDestroy()
        super.onDestroy()
    }

    /**
     * click on any item then display top select count and store id list
     */
    override fun onImageItemClick(interestBean: InterestBean) {
        tvSelectLabel.setText(resources.getString(R.string.tv_select) + " " + noOfSelectedImage)

        if (selectInterestIdList.contains(interestBean.id))
            selectInterestIdList.remove(interestBean.id)
        else
            selectInterestIdList.add(interestBean.id)
    }
}