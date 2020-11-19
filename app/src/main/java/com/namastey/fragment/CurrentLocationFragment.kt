package com.namastey.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.CurrentLocationAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentCurrentLocationBinding
import com.namastey.uiView.LocationView
import com.namastey.viewModel.LocationViewModel
import kotlinx.android.synthetic.main.fragment_current_location.*
import javax.inject.Inject


class CurrentLocationFragment : BaseFragment<FragmentCurrentLocationBinding>(),
    LocationView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var fragmentAccountBinding: FragmentCurrentLocationBinding
    private lateinit var locationViewModel: LocationViewModel
    private lateinit var layoutView: View
    private lateinit var currentLocationAdapter: CurrentLocationAdapter

    override fun getViewModel() = locationViewModel

    override fun getLayoutId() = R.layout.fragment_current_location

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance() =
            CurrentLocationFragment().apply {
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

    private fun setupViewModel() {
        locationViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(LocationViewModel::class.java)
        locationViewModel.setViewInterface(this)
        fragmentAccountBinding = getViewBinding()
        fragmentAccountBinding.viewModel = locationViewModel
    }


    private fun initUI() {
        currentLocationAdapter = CurrentLocationAdapter(requireActivity())
        rvLocation.adapter = currentLocationAdapter
    }

}