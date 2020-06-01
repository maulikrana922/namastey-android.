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
import com.namastey.databinding.ActivityFollowingFollowersBinding
import com.namastey.fragment.FollowersFragment
import com.namastey.fragment.FollowingFragment
import com.namastey.uiView.FolloFollowersView
import com.namastey.utils.SessionManager
import com.namastey.viewModel.FollowFollowersViewModel
import kotlinx.android.synthetic.main.activity_following_followers.*
import javax.inject.Inject


class FollowingFollowersActivity : BaseActivity<ActivityFollowingFollowersBinding>(),
    FolloFollowersView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var activityFollowingFollowersBinding: ActivityFollowingFollowersBinding
    private lateinit var followFollowersViewModel: FollowFollowersViewModel
    private lateinit var tabOne: TextView
    private lateinit var tabTwo: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        followFollowersViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(FollowFollowersViewModel::class.java)
        activityFollowingFollowersBinding = bindViewData()
        activityFollowingFollowersBinding.viewModel = followFollowersViewModel

        initData()
    }

    private fun initData() {

        searchFollow.queryHint = resources.getString(R.string.search)
        setupViewPager()
        tabFollow.setupWithViewPager(viewpagerFollow)
        setupTabIcons()

    }

    private fun setupViewPager() {

        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFrag(FollowingFragment(), resources.getString(R.string.following))
        adapter.addFrag(FollowersFragment(), resources.getString(R.string.followers))
        viewpagerFollow.adapter = adapter

    }

    private fun setupTabIcons() {
        tabOne =
            LayoutInflater.from(this).inflate(R.layout.custom_tab, null) as TextView
        tabOne.background =
            ContextCompat.getDrawable(this, R.drawable.rounded_bottom_left_red_solid)
        tabOne.text = resources.getString(R.string.following)
        tabOne.setTextColor(Color.WHITE)
        tabFollow.getTabAt(0)?.customView = tabOne
        tabTwo =
            LayoutInflater.from(this).inflate(R.layout.custom_tab, null) as TextView
        tabTwo.text = resources.getString(R.string.followers)
        tabTwo.setTextColor(Color.GRAY)
        tabTwo.background = ContextCompat.getDrawable(this, R.drawable.rounded_top_right_pink_solid)
        tabFollow.getTabAt(1)?.customView = tabTwo

        tabFollow.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
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
     * change backgrond and text color according to select tab
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

    override fun getViewModel() = followFollowersViewModel

    override fun getLayoutId() = R.layout.activity_following_followers

    override fun getBindingVariable() = BR.viewModel

    override fun onBackPressed() {
        finishActivity()
    }

    fun onClickFollowBack(view: View) {
        onBackPressed()
    }
}