package com.namastey.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.tabs.TabLayout
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.ViewPagerAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityMatchesBinding
import com.namastey.fcm.MyFirebaseMessagingService
import com.namastey.fragment.MatchesProfileFragment
import com.namastey.fragment.NotificationFragment
import com.namastey.listeners.FragmentRefreshListener
import com.namastey.uiView.MatchesBasicView
import com.namastey.utils.Constants
import com.namastey.utils.Constants.KEY_NOTIFICATION
import com.namastey.utils.SessionManager
import com.namastey.viewModel.MatchesBasicViewModel
import kotlinx.android.synthetic.main.activity_matches.*
import javax.inject.Inject

class MatchesActivity : BaseActivity<ActivityMatchesBinding>(), MatchesBasicView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var matchesBasicViewModel: MatchesBasicViewModel
    private lateinit var activityMatchesProfileBinding: ActivityMatchesBinding
    private var fragmentRefreshListener: FragmentRefreshListener? = null

    private lateinit var tabOne: TextView
    private lateinit var tabTwo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        matchesBasicViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(MatchesBasicViewModel::class.java)
        activityMatchesProfileBinding = bindViewData()
        activityMatchesProfileBinding.viewModel = matchesBasicViewModel

        initData()
        getDataFromIntent(intent!!)
    }

    private fun initData() {
        setupViewPager()
        tabMatchesProfile.setupWithViewPager(viewPagerMatchesProfile)
        setupTabIcons()
        if (intent.hasExtra("onClickMatches"))
            tabMatchesProfile.getTabAt(0)?.select()
    }

    override fun getViewModel() = matchesBasicViewModel

    override fun getLayoutId() = R.layout.activity_matches

    override fun getBindingVariable() = BR.viewModel

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
//        matchesBasicViewModel.getUserDetails()

    }

    private fun setupViewPager() {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFrag(MatchesProfileFragment(), resources.getString(R.string.matches))
        adapter.addFrag(NotificationFragment(), resources.getString(R.string.notification))
        viewPagerMatchesProfile.adapter = adapter
    }

    private fun setupTabIcons() {
        tabOne = LayoutInflater.from(this).inflate(R.layout.custom_tab, null) as TextView
        tabOne.background =
            ContextCompat.getDrawable(this, R.drawable.rounded_bottom_left_red_solid)
        tabOne.text = resources.getString(R.string.matches)
        tabOne.setTextColor(Color.WHITE)
        tabMatchesProfile.getTabAt(0)?.customView = tabOne
        tabTwo = LayoutInflater.from(this).inflate(R.layout.custom_tab, null) as TextView
        tabTwo.text = resources.getString(R.string.notification)
        tabTwo.setTextColor(Color.GRAY)
        tabTwo.background = ContextCompat.getDrawable(this, R.drawable.rounded_top_right_pink_solid)
        tabMatchesProfile.getTabAt(1)?.customView = tabTwo

        tabMatchesProfile.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
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

    fun onClickMatchesBack(view: View) {
        onBackPressed()
    }

    private fun getDataFromIntent(intent: Intent) {
        if (intent.hasExtra(Constants.ACTION_ACTION_TYPE) && intent.getStringExtra(Constants.ACTION_ACTION_TYPE) == "notification") {
            if (intent.hasExtra(Constants.NOTIFICATION_TYPE) && intent.getStringExtra(Constants.NOTIFICATION_TYPE) == Constants.NOTIFICATION_PENDING_INTENT) {
                sessionManager.setNotificationCount(sessionManager.getNotificationCount() + 1)
            }
            addFragmentWithoutCurrentFrag(NotificationFragment(), Constants.NOTIFICATION_FRAGMENT)
        }
    }

    private val notificationBroadcast = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            sessionManager.setNotificationCount(sessionManager.getNotificationCount() + 1)
            if (getCurrentFragment() is NotificationFragment && getFragmentRefreshListener() != null && intent!!.hasExtra(KEY_NOTIFICATION)) {
                getFragmentRefreshListener()!!.onRefresh(intent.getParcelableExtra(KEY_NOTIFICATION))
            } else {
                if (intent!!.hasExtra(MyFirebaseMessagingService.KEY_NOTI_TITLE) && intent.hasExtra(MyFirebaseMessagingService.KEY_NOTI_MESSAGE)) {
                    showNotification(
                        intent.getStringExtra(MyFirebaseMessagingService.KEY_NOTI_TITLE),
                        intent.getStringExtra(MyFirebaseMessagingService.KEY_NOTI_MESSAGE)
                    )
                }
            }
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            finishActivity()
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(notificationBroadcast, IntentFilter(MyFirebaseMessagingService.NOTIFICATION_ACTION))
    }

    private fun addFragmentWithoutCurrentFrag(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction().addToBackStack(tag)
            .replace(R.id.flContainer, fragment).commitAllowingStateLoss()
    }

    override fun onDestroy() {
        matchesBasicViewModel.onDestroy()
        super.onDestroy()
        unregisterReceiver(notificationBroadcast)
    }

    fun getCurrentFragment(): Fragment? {
        return supportFragmentManager.findFragmentById(R.id.flContainer)
    }

    fun getFragmentRefreshListener(): FragmentRefreshListener? {
        return fragmentRefreshListener
    }

    fun setFragmentRefreshListener(fragmentRefreshListener: FragmentRefreshListener) {
        this.fragmentRefreshListener = fragmentRefreshListener
    }

}