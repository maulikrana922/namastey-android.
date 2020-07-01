package com.namastey.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.ProfileInterestActivity
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentAddLinksBinding
import com.namastey.model.SocialAccountBean
import com.namastey.uiView.ProfileInterestView
import com.namastey.utils.Constants
import com.namastey.viewModel.ProfileInterestViewModel
import kotlinx.android.synthetic.main.fragment_add_links.*
import javax.inject.Inject

class AddLinksFragment : BaseFragment<FragmentAddLinksBinding>(), ProfileInterestView,
    View.OnClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var fragmentAddLinksBinding: FragmentAddLinksBinding
    private lateinit var layoutView: View
    private lateinit var profileInterestViewModel: ProfileInterestViewModel

    override fun getLayoutId() = R.layout.fragment_add_links

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance(fromEditProfile: Boolean,socialAccountList: ArrayList<SocialAccountBean>) =
            AddLinksFragment().apply {
                arguments = Bundle().apply {
                    putBoolean("fromEditProfile",fromEditProfile)
                    putSerializable("socialAccountList", socialAccountList)
                }
            }
    }

    private fun setupViewModel() {
        profileInterestViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ProfileInterestViewModel::class.java)
        profileInterestViewModel.setViewInterface(this)

        fragmentAddLinksBinding = getViewBinding()
        fragmentAddLinksBinding.viewModel = profileInterestViewModel

        initListener()
        initData()
    }

    private fun initListener() {
        ivCloseAddLink.setOnClickListener(this)
        tvAddLinkSave.setOnClickListener(this)
    }

    private fun initData() {

        if (arguments!!.containsKey("socialAccountList")) {
            var data: ArrayList<SocialAccountBean> =
                arguments!!.getSerializable("socialAccountList") as ArrayList<SocialAccountBean>
            if (data.any { socialAccountBean -> socialAccountBean.name == getString(R.string.facebook) }) {
                mainFacebook.visibility = View.VISIBLE
                edtFacebook.setText(data.single { s -> s.name == getString(R.string.facebook) }
                    .link)
            }
            if (data.any { socialAccountBean -> socialAccountBean.name == getString(R.string.instagram) }) {
                mainInstagram.visibility = View.VISIBLE
                edtInstagram.setText(data.single { s -> s.name == getString(R.string.instagram) }
                    .link)
            }
            if (data.any { socialAccountBean -> socialAccountBean.name == getString(R.string.snapchat) }) {
                mainSnapchat.visibility = View.VISIBLE
                edtSnapchat.setText(data.single { s -> s.name == getString(R.string.snapchat) }
                    .link)
            }

            if (data.any { socialAccountBean -> socialAccountBean.name == getString(R.string.tiktok) }) {
                mainTikTok.visibility = View.VISIBLE
                edtTiktok.setText(data.single { s -> s.name == getString(R.string.tiktok) }
                    .link)
            }
            if (data.any { socialAccountBean -> socialAccountBean.name == getString(R.string.spotify) }) {
                mainSpotify.visibility = View.VISIBLE
                edtSpotify.setText(data.single { s -> s.name == getString(R.string.spotify) }
                    .link)
            }
            if (data.any { socialAccountBean -> socialAccountBean.name == getString(R.string.linkedin) }) {
                mainLinkedin.visibility = View.VISIBLE
                edtLinkedin.setText(data.single { s -> s.name == getString(R.string.linkedin) }
                    .link)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)
    }

    override fun onSuccessResponse(data: ArrayList<SocialAccountBean>) {
        (activity as ProfileInterestActivity).onActivityReenter(
            Constants.ADD_LINK,
            Intent()
        )
        fragmentManager!!.popBackStack()
    }

    override fun getViewModel() = profileInterestViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutView = view
        setupViewModel()

    }

    override fun onClick(p0: View?) {
        when (p0) {
            ivCloseAddLink -> {
                fragmentManager!!.popBackStack()
            }
            tvAddLinkSave -> {
                createRequest()
            }
        }
    }

    private fun createRequest() {
        var jsonObject = JsonObject()
        var jsonArray = JsonArray()

        var jsonObjectInner: JsonObject
//        if (edtSnapchat.text.toString().isNotEmpty()) {
        jsonObjectInner = JsonObject()
        jsonObjectInner.addProperty(Constants.NAME, getString(R.string.snapchat))
        jsonObjectInner.addProperty(Constants.LINK, edtSnapchat.text.toString().trim())
        jsonArray.add(jsonObjectInner)
//        }
//        if (edtFacebook.text.toString().isNotEmpty()) {
        jsonObjectInner = JsonObject()
        jsonObjectInner.addProperty(Constants.NAME, getString(R.string.facebook))
        jsonObjectInner.addProperty(Constants.LINK, edtFacebook.text.toString().trim())
        jsonArray.add(jsonObjectInner)
//        }
//        if (edtTiktok.text.toString().isNotEmpty()) {
        jsonObjectInner = JsonObject()
        jsonObjectInner.addProperty(Constants.NAME, getString(R.string.tiktok))
        jsonObjectInner.addProperty(Constants.LINK, edtTiktok.text.toString().trim())
        jsonArray.add(jsonObjectInner)
//        }
//        if (edtInstagram.text.toString().isNotEmpty()) {
        jsonObjectInner = JsonObject()
        jsonObjectInner.addProperty(Constants.NAME, getString(R.string.instagram))
        jsonObjectInner.addProperty(Constants.LINK, edtInstagram.text.toString().trim())
        jsonArray.add(jsonObjectInner)
//        }
//        if (edtSpotify.text.toString().isNotEmpty()) {
        jsonObjectInner = JsonObject()
        jsonObjectInner.addProperty(Constants.NAME, getString(R.string.spotify))
        jsonObjectInner.addProperty(Constants.LINK, edtSpotify.text.toString().trim())
        jsonArray.add(jsonObjectInner)
//        }
//        if (edtLinkedin.text.toString().isNotEmpty()) {
        jsonObjectInner = JsonObject()
        jsonObjectInner.addProperty(Constants.NAME, getString(R.string.linkedin))
        jsonObjectInner.addProperty(Constants.LINK, edtLinkedin.text.toString().trim())
        jsonArray.add(jsonObjectInner)
//        }
        jsonObject.add("social_links_details", jsonArray)
        Log.d("AddLinkRequest : ", jsonObject.toString())
        profileInterestViewModel.addSocialLink(jsonObject)
    }

    override fun onDestroy() {
        profileInterestViewModel.onDestroy()
        super.onDestroy()
    }
}