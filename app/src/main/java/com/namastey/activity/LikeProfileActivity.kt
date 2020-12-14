package com.namastey.activity

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.tabs.TabLayout
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.ViewPagerAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityLikeProfileBinding
import com.namastey.fragment.LikeUserPostFragment
import com.namastey.fragment.LikedUserPostFragment
import com.namastey.model.VideoBean
import com.namastey.uiView.ProfileLikeView
import com.namastey.utils.SessionManager
import com.namastey.viewModel.LikeProfileViewModel
import kotlinx.android.synthetic.main.activity_like_profile.*
import java.util.*
import javax.inject.Inject

class LikeProfileActivity : BaseActivity<ActivityLikeProfileBinding>(), ProfileLikeView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var likeProfileViewModel: LikeProfileViewModel
    private lateinit var activityLikeProfileBinding: ActivityLikeProfileBinding

    private lateinit var tabOne: TextView
    private lateinit var tabTwo: TextView
    private var likeUserCount = 0
    private var lastUserProfile = ""
    private var tabOneTitle = ""

    override fun getViewModel() = likeProfileViewModel

    override fun getLayoutId() = R.layout.activity_like_profile

    override fun getBindingVariable() = BR.viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        likeProfileViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(LikeProfileViewModel::class.java)
        activityLikeProfileBinding = bindViewData()
        activityLikeProfileBinding.viewModel = likeProfileViewModel

        initData()
    }

    private fun initData() {
        likeUserCount = intent.getIntExtra("likeUserCount", 0)
        lastUserProfile = intent.getStringExtra("lastUserProfile")!!

        tabOneTitle = likeUserCount.toString() + " " +resources.getString(R.string.likes)

        setupViewPager()
        tabLikeProfile.setupWithViewPager(viewPagerLikeProfile)
        setupTabIcons()
        tabLikeProfile.getTabAt(0)?.select()
    }

    private fun setupViewPager() {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        //adapter.addFrag(LikeUserPostFragment(), resources.getString(R.string.likes))
        adapter.addFrag(LikeUserPostFragment(),tabOneTitle)
        adapter.addFrag(LikedUserPostFragment(), resources.getString(R.string.likes_sent))
        viewPagerLikeProfile.adapter = adapter
    }

    private fun setupTabIcons() {
        tabOne = LayoutInflater.from(this).inflate(R.layout.custom_tab, null) as TextView
        tabOne.background =
            ContextCompat.getDrawable(this, R.drawable.rounded_bottom_left_red_solid)
        tabOne.text = tabOneTitle
        tabOne.setTextColor(Color.WHITE)
        tabLikeProfile.getTabAt(0)?.customView = tabOne
        tabTwo = LayoutInflater.from(this).inflate(R.layout.custom_tab, null) as TextView
        tabTwo.text = resources.getString(R.string.likes_sent)
        tabTwo.setTextColor(Color.GRAY)
        tabTwo.background = ContextCompat.getDrawable(this, R.drawable.rounded_top_right_pink_solid)
        tabLikeProfile.getTabAt(1)?.customView = tabTwo

        tabLikeProfile.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                changeSelectedTab(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })
    }

    /**
     * change background and text color according to select tab
     */
    private fun changeSelectedTab(position: Int) {
        if (position == 0) {
            tabOne.background =
                ContextCompat.getDrawable(this, R.drawable.rounded_bottom_left_red_solid)
            tabOne.setTextColor(Color.WHITE)
            tabTwo.background =
                ContextCompat.getDrawable(this, R.drawable.rounded_top_right_pink_solid)
            tabTwo.setTextColor(Color.GRAY)
        } else {
            tabOne.background =
                ContextCompat.getDrawable(this, R.drawable.rounded_bottom_left_pink_solid)
            tabOne.setTextColor(Color.GRAY)
            tabTwo.background =
                ContextCompat.getDrawable(this, R.drawable.rounded_top_right_red_solid)
            tabTwo.setTextColor(Color.WHITE)
        }
    }

    fun onClickLikeProfileBack(view: View) {
        onBackPressed()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            finishActivity()
        }
    }

    override fun onSuccess(data: ArrayList<VideoBean>) {
        TODO("Not yet implemented")
    }


    override fun onDestroy() {
        likeProfileViewModel.onDestroy()
        super.onDestroy()
    }
}