package com.namastey.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentAlbumBinding
import com.namastey.uiView.FollowersView
import com.namastey.viewModel.AlbumViewModel
import javax.inject.Inject

class AlbumFragment : BaseFragment<FragmentAlbumBinding>(), FollowersView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var fragmentAlbumBinding: FragmentAlbumBinding
    private lateinit var albumViewModel: AlbumViewModel
    private lateinit var layoutView: View


    override fun getViewModel() = albumViewModel

    override fun getLayoutId() = R.layout.fragment_album

    override fun getBindingVariable() = BR.viewModel

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

    }

    private fun setupViewModel() {
        albumViewModel = ViewModelProviders.of(this, viewModelFactory).get(
            AlbumViewModel::class.java
        )
        albumViewModel.setViewInterface(this)

        fragmentAlbumBinding = getViewBinding()
        fragmentAlbumBinding.viewModel = albumViewModel
    }

    override fun onDestroy() {
        super.onDestroy()
    }


}