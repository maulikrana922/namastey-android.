package com.namastey.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.CreateAlbumActivity
import com.namastey.adapter.AlbumListAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentAlbumBinding
import com.namastey.model.AlbumBean
import com.namastey.uiView.AlbumView
import com.namastey.viewModel.AlbumViewModel
import kotlinx.android.synthetic.main.fragment_album.*
import java.util.*
import javax.inject.Inject

class AlbumFragment : BaseFragment<FragmentAlbumBinding>(), AlbumView, View.OnClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var fragmentAlbumBinding: FragmentAlbumBinding
    private lateinit var albumViewModel: AlbumViewModel
    private lateinit var layoutView: View

    override fun onSuccessAlbumList(arrayList: ArrayList<AlbumBean>) {
        rvAlbumList.apply {
            adapter = AlbumListAdapter(arrayList, activity!!)
        }

    }


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
        initData()
    }

    private fun initData() {

        btnAddAlbum.setOnClickListener(this)
        albumViewModel.getAlbumList()
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
        albumViewModel.onDestroy()
    }

    override fun onClick(v: View?) {
        when (v) {
            btnAddAlbum -> {
                var intent = Intent(activity, CreateAlbumActivity::class.java)
                intent.putExtra("fromAlbumList", true)
                openActivity(intent)
            }
        }
    }

}