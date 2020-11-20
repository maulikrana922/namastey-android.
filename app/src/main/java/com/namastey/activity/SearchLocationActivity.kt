package com.namastey.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivitySearchLocationBinding
import com.namastey.uiView.LocationView
import com.namastey.utils.SessionManager
import com.namastey.viewModel.LocationViewModel
import javax.inject.Inject

class SearchLocationActivity : BaseActivity<ActivitySearchLocationBinding>(), LocationView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var activitySearchLocationBinding: ActivitySearchLocationBinding
    private lateinit var locationViewModel: LocationViewModel

    override fun getViewModel() = locationViewModel

    override fun getLayoutId() = R.layout.activity_search_location

    override fun getBindingVariable() = BR.viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)
        locationViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(LocationViewModel::class.java)
        locationViewModel.setViewInterface(this)
        activitySearchLocationBinding = bindViewData()
        activitySearchLocationBinding.viewModel = locationViewModel

    }

    override fun onBackPressed() {
        finishActivity()
    }

    fun onClickSearchLocationBack(view: View) {
        onBackPressed()
    }

    override fun onDestroy() {
        locationViewModel.onDestroy()
        super.onDestroy()
    }
}