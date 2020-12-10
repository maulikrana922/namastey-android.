package com.namastey.activity

import android.content.Intent
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
import com.namastey.fragment.FollowRequestFragment
import com.namastey.fragment.MatchesProfileFragment
import com.namastey.fragment.NotificationFragment
import com.namastey.listeners.FragmentRefreshListener
import com.namastey.model.Notification
import com.namastey.uiView.MatchesBasicView
import com.namastey.utils.Constants
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
    private var notification: Notification = Notification()

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
        //getDataFromIntent(intent!!)
    }

    private fun initData() {
        setupViewPager()
        tabMatchesProfile.setupWithViewPager(viewPagerMatchesProfile)
        setupTabIcons()
        if (intent.hasExtra("onClickMatches")) {
            val onClickMatches = intent.getBooleanExtra("onClickMatches", false)
            if (onClickMatches)
                tabMatchesProfile.getTabAt(0)?.select()
            else
                tabMatchesProfile.getTabAt(1)?.select()
        }

        if (intent.hasExtra("onFollowRequest")) {
            val onFollowRequest = intent.getBooleanExtra("onFollowRequest", false)
            if (onFollowRequest) {
                tabMatchesProfile.getTabAt(0)?.select()
                val followRequestFragment = FollowRequestFragment.getInstance()
                addFragment(
                    followRequestFragment,
                    Constants.FOLLOW_REQUEST_FRAGMENT
                )
            }
        }
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

    /*private fun getDataFromIntent(intent: Intent) {
        if (intent.hasExtra(Constants.ACTION_ACTION_TYPE) && intent.getStringExtra(Constants.ACTION_ACTION_TYPE) == "notification") {
            if (intent.hasExtra(Constants.NOTIFICATION_TYPE) && intent.getStringExtra(Constants.NOTIFICATION_TYPE) == Constants.NOTIFICATION_PENDING_INTENT) {
                sessionManager.setNotificationCount(sessionManager.getNotificationCount() + 1)
            }
            if (intent.hasExtra(KEY_NOTIFICATION)) {
                notification = intent.getParcelableExtra(KEY_NOTIFICATION)!!
                Log.e("MatchesActivity", "notification: \t $notification")
                Log.e("MatchesActivity", "notification: \t ${notification.isNotificationType}")

                when (notification.isNotificationType) {
                    "0" -> { // for Comment Notification
                        val adapter = ViewPagerAdapter(supportFragmentManager)
                        adapter.addFrag(NotificationFragment(), Constants.NOTIFICATION_FRAGMENT)
                        viewPagerMatchesProfile.adapter = adapter
                        tabMatchesProfile.setupWithViewPager(viewPagerMatchesProfile)
                        tabMatchesProfile.getTabAt(1)?.select()
                        //addFragment(NotificationFragment(), Constants.NOTIFICATION_FRAGMENT)
                    }
                    "1" -> { // for mention in comment Notification
                        addFragment(NotificationFragment(), Constants.NOTIFICATION_FRAGMENT)
                    }
                    "2" -> { // for follow Notification
                        //TODO: Change to FollowingFollowersActivity screen (For that ProfileBean required)
                        val intentProfileActivity = Intent(this@MatchesActivity, ProfileActivity::class.java)
                        intentProfileActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intentProfileActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intentProfileActivity)
                        addFragment(NotificationFragment(), Constants.NOTIFICATION_FRAGMENT)
                    }
                    "3" -> { // for new video Notification
                        val intentDashboardActivity = Intent(this@MatchesActivity, DashboardActivity::class.java)
                        intentDashboardActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intentDashboardActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intentDashboardActivity)
                        //addFragment(NotificationFragment(), Constants.NOTIFICATION_FRAGMENT)
                    }
                    "4" -> { // for Matches Notification
                        addFragment(MatchesProfileFragment(), Constants.NOTIFICATION_FRAGMENT)
                    }
                    "5" -> { // for mention in post Notification
                        addFragment(NotificationFragment(), Constants.NOTIFICATION_FRAGMENT)
                    }
                    "6" -> { // for follow request Notification
                        addFragment(FollowRequestFragment(), Constants.FOLLOW_REQUEST_FRAGMENT)
                    }
                    else -> { // Default
                        addFragment(NotificationFragment(), Constants.NOTIFICATION_FRAGMENT)
                    }
                }
            }
        }
    }

    private val notificationBroadcast = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            sessionManager.setNotificationCount(sessionManager.getNotificationCount() + 1)
            if (getCurrentFragment() is NotificationFragment && getFragmentRefreshListener() != null && intent!!.hasExtra(
                    KEY_NOTIFICATION
                )
            ) {
                getFragmentRefreshListener()!!.onRefresh(intent.getParcelableExtra(KEY_NOTIFICATION))
            } else {
                if (intent!!.hasExtra(MyFirebaseMessagingService.KEY_NOTI_TITLE) && intent.hasExtra(
                        MyFirebaseMessagingService.KEY_NOTI_MESSAGE
                    )
                ) {
                    showNotification(
                        intent.getStringExtra(MyFirebaseMessagingService.KEY_NOTI_TITLE),
                        intent.getStringExtra(MyFirebaseMessagingService.KEY_NOTI_MESSAGE)
                    )
                }
            }
        }
    }*/

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            finishActivity()
        }
    }

    override fun onResume() {
        super.onResume()
        /*registerReceiver(
            notificationBroadcast,
            IntentFilter(MyFirebaseMessagingService.NOTIFICATION_ACTION)
        )*/
    }

    private fun addFragmentWithoutCurrentFrag(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction().addToBackStack(tag)
            .replace(R.id.flContainer, fragment).commitAllowingStateLoss()
    }

    override fun onDestroy() {
        matchesBasicViewModel.onDestroy()
        super.onDestroy()
        // unregisterReceiver(notificationBroadcast)
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