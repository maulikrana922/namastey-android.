package com.namastey.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityCreateAlbumBinding
import com.namastey.uiView.CreateAlbumView
import com.namastey.viewModel.CreateAlbumViewModel
import kotlinx.android.synthetic.main.activity_create_album.*
import kotlinx.android.synthetic.main.view_create_album.view.*
import javax.inject.Inject

class CreateAlbumActivity : BaseActivity<ActivityCreateAlbumBinding>(), CreateAlbumView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var activityCreateAlbumBinding: ActivityCreateAlbumBinding
    private lateinit var createAlbumViewModel: CreateAlbumViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        createAlbumViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(CreateAlbumViewModel::class.java)
        activityCreateAlbumBinding = bindViewData()
        activityCreateAlbumBinding.viewModel = createAlbumViewModel

        initData()

    }

    private fun initData() {


    }

    override fun getViewModel() = createAlbumViewModel

    override fun getLayoutId() = R.layout.activity_create_album

    override fun getBindingVariable() = BR.viewModel

    override fun onBackPressed() {
        finishActivity()
    }

    fun onClickCreateAlbumBack(view: View) {
        onBackPressed()
    }

    /**
     * click on add album generate new layout for add album
     */
    fun onClickAddAlbum(view: View) {
        var layoutInflater = LayoutInflater.from(this@CreateAlbumActivity)

        var view = layoutInflater.inflate(R.layout.view_create_album, llAlbumList, false)

//        view.tvFirstLanguage.setText("Firstlanguage")
        view.edtAlbumName.setText("Dines")
        llAlbumList.addView(view)
    }
}
