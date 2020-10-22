package com.namastey.activity

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityMatchesScreenBinding
import com.namastey.uiView.MatchesScreenBasicView
import com.namastey.viewModel.MatchesScreenViewModel
import javax.inject.Inject

class MatchesScreenActivity : BaseActivity<ActivityMatchesScreenBinding>(), MatchesScreenBasicView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var matchesScreenViewModel: MatchesScreenViewModel
    private lateinit var activityMatchesScreenBinding: ActivityMatchesScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        matchesScreenViewModel = ViewModelProviders.of(this, viewModelFactory).get(MatchesScreenViewModel::class.java)
        activityMatchesScreenBinding = bindViewData()
        activityMatchesScreenBinding.viewModel = matchesScreenViewModel
    }

    override fun getViewModel() = matchesScreenViewModel

    override fun getLayoutId() = R.layout.activity_matches_screen

    override fun getBindingVariable() = BR.viewModel
}