package com.namastey.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.facebook.FacebookSdk
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityProfileInterestBinding
import com.namastey.fragment.AddLinksFragment
import com.namastey.model.SocialAccountBean
import com.namastey.uiView.ProfileInterestView
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import com.namastey.utils.Utils
import com.namastey.viewModel.ProfileInterestViewModel
import kotlinx.android.synthetic.main.view_profile_select_interest.*
import kotlinx.android.synthetic.main.view_profile_tag.view.*
import java.util.*
import javax.inject.Inject

class ProfileInterestActivity : BaseActivity<ActivityProfileInterestBinding>(),
    ProfileInterestView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var profileInterestViewModel: ProfileInterestViewModel
    private lateinit var activityProfileInterestBinding: ActivityProfileInterestBinding

    companion object {
        var categoryIdList: ArrayList<Int> = ArrayList()
        var socialAccountList: ArrayList<SocialAccountBean> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        if (!FacebookSdk.isInitialized())
            FacebookSdk.sdkInitialize(this@ProfileInterestActivity)

        profileInterestViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ProfileInterestViewModel::class.java)
        activityProfileInterestBinding = bindViewData()
        activityProfileInterestBinding.viewModel = profileInterestViewModel

        initData()
    }

    private fun initData() {
        categoryIdList = ArrayList()
        socialAccountList = ArrayList()
//        Log.d("TAG", sessionManager.getCategoryList().toString())
        getSocialLinkAPI()
        generateProfileTagUI()
    }

    /**
     * Get social link api
     */
    private fun getSocialLinkAPI() {
        profileInterestViewModel.getSocialLink()
    }

    /**
     * Create dynamic layout as per category selected
     */
    private fun generateProfileTagUI() {
        var profileTagCount = 0
        if (sessionManager.getCategoryList().size > 0) {

            for (categoryBean in sessionManager.getCategoryList()) {
                val layoutInflater = LayoutInflater.from(this@ProfileInterestActivity)
                val view = layoutInflater.inflate(R.layout.view_profile_tag, llProfileTag, false)
                view.tvCategory.text = categoryBean.name.toString()

                when {
                    categoryBean.startColor.isEmpty() -> {
                        categoryBean.startColor = "#B2BAF2"     // Default set if colours not found
                        categoryBean.endColor = "#28BAD3"
                    }
                }

                Utils.rectangleShapeGradient(
                    view, intArrayOf(
                        Color.parseColor(categoryBean.startColor),
                        Color.parseColor(categoryBean.endColor)
                    )
                )
                for (subCategoryBean in categoryBean.sub_category) {
                    val tvCategory = TextView(this)
                    tvCategory.layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )

                    tvCategory.text = subCategoryBean.name.toString()
                    tvCategory.setPadding(40, 20, 40, 20)
                    tvCategory.setTextColor(Color.BLACK)
                    tvCategory.setBackgroundResource(R.drawable.rounded_white_solid_white_corner)

                    view.chipProfileTag.addView(tvCategory)

                    tvCategory.setOnClickListener {

                        if (tvCategory.background.constantState == ContextCompat.getDrawable(
                                this@ProfileInterestActivity,
                                R.drawable.rounded_white_solid_white_corner
                            )?.constantState
                        ) {
                            categoryIdList.add(subCategoryBean.id)
                            ++profileTagCount
                            tvCategory.setTextColor(Color.WHITE)
                            tvCategory.setBackgroundResource(R.drawable.rounded_green_solid_all_corner)

                        } else {
                            categoryIdList.remove(subCategoryBean.id)
                            --profileTagCount
                            tvCategory.setTextColor(Color.BLACK)
                            tvCategory.setBackgroundResource(R.drawable.rounded_white_solid_white_corner)
                        }
                        tvCountProfileTag.text = profileTagCount.toString()
                    }
                }

                llProfileTag.addView(view)

                view.tvCategory.setOnClickListener {
                    //                    if (categoryBean.sub_category.size > 0) {
                    if (view.chipProfileTag.visibility == View.VISIBLE) {
                        view.chipProfileTag.visibility = View.GONE
                        view.tvCategory.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_arrow_down_gray,
                            0
                        )
                    } else {
                        val count = llProfileTag.childCount - 1
                        for (index in 0..count) {
                            val viewCategory = llProfileTag.getChildAt(index)
                            viewCategory.chipProfileTag.visibility = View.GONE
                            viewCategory.tvCategory.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                0,
                                0,
                                R.drawable.ic_arrow_down_gray,
                                0
                            )
                        }
                        view.chipProfileTag.visibility = View.VISIBLE
                        view.tvCategory.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_arrow_up_gray,
                            0
                        )
                    }
//                    }
                }
            }
        }
    }

    override fun onSuccessResponse(data: ArrayList<SocialAccountBean>) {
        socialAccountList = data
        if (data.any { socialAccountBean -> socialAccountBean.name == getString(R.string.facebook) }) {
            mainFacebook.visibility = View.VISIBLE
            tvFacebookLink.text = data.single { s -> s.name == getString(R.string.facebook) }
                .link
        } else {
            mainFacebook.visibility = View.GONE
        }

        if (data.any { socialAccountBean -> socialAccountBean.name == getString(R.string.instagram) }) {
            mainInstagram.visibility = View.VISIBLE
            tvInstagramLink.text = data.single { s -> s.name == getString(R.string.instagram) }
                .link
        } else {
            mainInstagram.visibility = View.GONE
        }

//        if (data.any{ socialAccountBean -> socialAccountBean.name == getString(R.string.snapchat) }){
//            mainSnapchat.visibility = View.VISIBLE
//            tvSnapchatLink.text = data.single { s -> s.name == getString(R.string.snapchat) }
//                .link
//        }else{
//            mainSnapchat.visibility = View.GONE
//        }
//
//        if (data.any{ socialAccountBean -> socialAccountBean.name == getString(R.string.tiktok) }){
//            mainTikTok.visibility = View.VISIBLE
//            tvTiktokLink.text = data.single { s -> s.name == getString(R.string.tiktok) }
//                .link
//        }else{
//            mainTikTok.visibility = View.GONE
//        }
        if (data.any { socialAccountBean -> socialAccountBean.name == getString(R.string.spotify) }) {
            mainSpotify.visibility = View.VISIBLE
            tvSpotifyLink.text = data.single { s -> s.name == getString(R.string.spotify) }
                .link
        } else {
            mainSpotify.visibility = View.GONE
        }

        if (data.any { socialAccountBean -> socialAccountBean.name == getString(R.string.twitter) }) {
            mainTwitter.visibility = View.VISIBLE
            tvSpotifyLink.text = data.single { s -> s.name == getString(R.string.twitter) }
                .link
        } else {
            mainTwitter.visibility = View.GONE
        }
//        if (data.any{ socialAccountBean -> socialAccountBean.name == getString(R.string.linkedin) }){
//            mainLinkedin.visibility = View.VISIBLE
//            tvLinkedinLink.text = data.single { s -> s.name == getString(R.string.linkedin) }
//                .link
//        }else{
//            mainLinkedin.visibility = View.GONE
//        }
    }

    override fun getViewModel() = profileInterestViewModel

    override fun getLayoutId() = R.layout.activity_profile_interest

    override fun getBindingVariable() = BR.viewModel

    fun onClickInterestBack(view: View) {
        onBackPressed()
    }

    override fun onBackPressed() {

        if (supportFragmentManager.backStackEntryCount > 0)
            supportFragmentManager.popBackStack()
        else
            finishActivity()
    }

    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        super.onActivityReenter(resultCode, data)
        if (resultCode == Constants.ADD_LINK) {
            profileInterestViewModel.getSocialLink()
        }
    }

    fun onClickSelectInterest(view: View) {
        when (view) {
            ivAddLink -> {
                addFragment(
                    AddLinksFragment.getInstance(false, socialAccountList),
                    Constants.ADD_LINKS_FRAGMENT
                )
            }
        }
    }

    /**
     * click on next button open create album screen
     */
    fun onClickNextInterest(view: View) {
//        Log.d("Profile: ",categoryIdList.toString())
        Log.d("CAtegpru od ", categoryIdList.toString())
        openActivity(this@ProfileInterestActivity, CreateAlbumActivity())
    }

    override fun onSuccessSpotify(sporifyUrl: String) {
    }

    override fun onDestroy() {
        profileInterestViewModel.onDestroy()
        super.onDestroy()
    }
}
