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
import com.namastey.databinding.ActivityEditProfileBinding
import com.namastey.fragment.AlbumFragment
import com.namastey.fragment.EditProfileFragment
import com.namastey.model.ProfileBean
import com.namastey.uiView.ProfileBasicView
import com.namastey.viewModel.ProfileBasicViewModel
import kotlinx.android.synthetic.main.activity_edit_profile.*
import javax.inject.Inject

class EditProfileActivity : BaseActivity<ActivityEditProfileBinding>(), ProfileBasicView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var profileBasicViewModel: ProfileBasicViewModel
    private lateinit var activityEditProfileBinding: ActivityEditProfileBinding

    private lateinit var tabOne: TextView
    private lateinit var tabTwo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        profileBasicViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ProfileBasicViewModel::class.java)
        activityEditProfileBinding = bindViewData()
        activityEditProfileBinding.viewModel = profileBasicViewModel

        initData()
    }

    private fun initData() {
        setupViewPager()
        tabEditProfile.setupWithViewPager(viewpagerEditProfile)
        setupTabIcons()
        if (intent.hasExtra("onClickAlbum"))
            tabEditProfile.getTabAt(1)?.select()
    }

    override fun onSuccessProfileDetails(profileBean: ProfileBean) {
        TODO("Not yet implemented")
    }

    override fun getViewModel() = profileBasicViewModel

    override fun getLayoutId() = R.layout.activity_edit_profile

    override fun getBindingVariable() = BR.viewModel

    private fun setupViewPager() {

        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFrag(EditProfileFragment(), resources.getString(R.string.basic_info))
        adapter.addFrag(AlbumFragment(), resources.getString(R.string.albums))
        viewpagerEditProfile.adapter = adapter

    }

    private fun setupTabIcons() {
        tabOne =
            LayoutInflater.from(this).inflate(R.layout.custom_tab, null) as TextView
        tabOne.background =
            ContextCompat.getDrawable(this, R.drawable.rounded_bottom_left_red_solid)
        tabOne.text = resources.getString(R.string.basic_info)
        tabOne.setTextColor(Color.WHITE)
        tabEditProfile.getTabAt(0)?.customView = tabOne
        tabTwo =
            LayoutInflater.from(this).inflate(R.layout.custom_tab, null) as TextView
        tabTwo.text = resources.getString(R.string.albums)
        tabTwo.setTextColor(Color.GRAY)
        tabTwo.background = ContextCompat.getDrawable(this, R.drawable.rounded_top_right_pink_solid)
        tabEditProfile.getTabAt(1)?.customView = tabTwo

        tabEditProfile.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
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

    /**
     * click on basic view
     */
    fun onClickBasicInfo(view: View) {
        if (getOnInteractionWithFragment() != null) {
            getOnInteractionWithFragment()!!.onClickOfFragmentView(view)
        }
    }

    /**
     *  click on select interest view
     */
    fun onClickSelectInterest(view: View) {
        if (getOnInteractionWithFragment() != null) {
            getOnInteractionWithFragment()!!.onClickOfFragmentView(view)
        }
    }

    fun onClickEditAlbumBack(view: View) {
        onBackPressed()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            finishActivity()
        }
    }
}
