package com.namastey.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.namastey.R
import com.namastey.activity.*
import com.namastey.application.NamasteyApplication
import com.namastey.dagger.component.ActivityComponent
import com.namastey.dagger.module.ViewModule
import com.namastey.uiView.BaseView

abstract class BaseFragment<T : ViewDataBinding> : Fragment(), BaseView {

    private lateinit var viewDataBinding: T

    protected fun getActivityComponent(): ActivityComponent {
        return NamasteyApplication.appComponent()
            .activityComponent(ViewModule(this))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        viewDataBinding.lifecycleOwner = this
        return viewDataBinding.root
    }

    fun getViewBinding() = viewDataBinding

    override fun showMsg(msgId: Int) {
        if (activity != null && activity!! is SignUpActivity)
            (activity!! as SignUpActivity).showMsg(msgId)
        else if (activity != null && activity!! is ProfileActivity)
            (activity!! as ProfileActivity).showMsg(msgId)
        else if (activity != null && activity!! is ProfileBasicInfoActivity)
            (activity!! as ProfileBasicInfoActivity).showMsg(msgId)
        else if (activity != null && activity!! is ProfileInterestActivity)
            (activity!! as ProfileInterestActivity).showMsg(msgId)
        else if (activity != null && activity!! is EditProfileActivity)
            (activity!! as EditProfileActivity).showMsg(msgId)
        else if (activity != null && activity!! is JobListingActivity)
            (activity!! as JobListingActivity).showMsg(msgId)
        else if (activity != null && activity!! is EducationListActivity)
            (activity!! as EducationListActivity).showMsg(msgId)
        else if (activity != null && activity!! is FollowingFollowersActivity)
            (activity!! as FollowingFollowersActivity).showMsg(msgId)
        else if (activity != null && activity!! is AccountSettingsActivity)
            (activity!! as AccountSettingsActivity).showMsg(msgId)
        else if (activity != null && activity!! is DashboardActivity)
            (activity!! as DashboardActivity).showMsg(msgId)
        else if (activity != null && activity!! is FilterActivity)
            (activity!! as FilterActivity).showMsg(msgId)
        else if (activity != null && activity!! is MatchesActivity)
            (activity!! as MatchesActivity).showMsg(msgId)
        else if (activity != null && activity!! is ChatActivity)
            (activity!! as ChatActivity).showMsg(msgId)
        else if (activity != null && activity!! is MatchesScreenActivity)
            (activity!! as MatchesScreenActivity).showMsg(msgId)
        else if (activity != null && activity!! is LocationActivity)
            (activity!! as LocationActivity).showMsg(msgId)
    }

    override fun showMsg(msg: String) {
        if (activity != null && activity!! is SignUpActivity)
            (activity!! as SignUpActivity).showMsg(msg)
        else if (activity != null && activity!! is ProfileActivity)
            (activity!! as ProfileActivity).showMsg(msg)
        else if (activity != null && activity!! is ProfileBasicInfoActivity)
            (activity!! as ProfileBasicInfoActivity).showMsg(msg)
        else if (activity != null && activity!! is ProfileInterestActivity)
            (activity!! as ProfileInterestActivity).showMsg(msg)
        else if (activity != null && activity!! is EditProfileActivity)
            (activity!! as EditProfileActivity).showMsg(msg)
        else if (activity != null && activity!! is JobListingActivity)
            (activity!! as JobListingActivity).showMsg(msg)
        else if (activity != null && activity!! is EducationListActivity)
            (activity!! as EducationListActivity).showMsg(msg)
        else if (activity != null && activity!! is FollowingFollowersActivity)
            (activity!! as FollowingFollowersActivity).showMsg(msg)
        else if (activity != null && activity!! is AccountSettingsActivity)
            (activity!! as AccountSettingsActivity).showMsg(msg)
        else if (activity != null && activity!! is DashboardActivity)
            (activity!! as DashboardActivity).showMsg(msg)
        else if (activity != null && activity!! is FilterActivity)
            (activity!! as FilterActivity).showMsg(msg)
        else if (activity != null && activity!! is MatchesActivity)
            (activity!! as MatchesActivity).showMsg(msg)
        else if (activity != null && activity!! is ChatActivity)
            (activity!! as ChatActivity).showMsg(msg)
        else if (activity != null && activity!! is MatchesScreenActivity)
            (activity!! as MatchesScreenActivity).showMsg(msg)
        else if (activity != null && activity!! is LocationActivity)
            (activity!! as LocationActivity).showMsg(msg)
    }

    override fun hideKeyboard() {
        if (activity != null && activity!! is SignUpActivity)
            (activity!! as SignUpActivity).hideKeyboard()
        else if (activity != null && activity!! is ProfileActivity)
            (activity!! as ProfileActivity).hideKeyboard()
        else if (activity != null && activity!! is ProfileBasicInfoActivity)
            (activity!! as ProfileBasicInfoActivity).hideKeyboard()
        else if (activity != null && activity!! is ProfileInterestActivity)
            (activity!! as ProfileInterestActivity).hideKeyboard()
        else if (activity != null && activity!! is EditProfileActivity)
            (activity!! as EditProfileActivity).hideKeyboard()
        else if (activity != null && activity!! is JobListingActivity)
            (activity!! as JobListingActivity).hideKeyboard()
        else if (activity != null && activity!! is EducationListActivity)
            (activity!! as EducationListActivity).hideKeyboard()
        else if (activity != null && activity!! is FollowingFollowersActivity)
            (activity!! as FollowingFollowersActivity).hideKeyboard()
        else if (activity != null && activity!! is AccountSettingsActivity)
            (activity!! as AccountSettingsActivity).hideKeyboard()
        else if (activity != null && activity!! is DashboardActivity)
            (activity!! as DashboardActivity).hideKeyboard()
        else if (activity != null && activity!! is FilterActivity)
            (activity!! as FilterActivity).hideKeyboard()
        else if (activity != null && activity!! is MatchesActivity)
            (activity!! as MatchesActivity).hideKeyboard()
        else if (activity != null && activity!! is ChatActivity)
            (activity!! as ChatActivity).hideKeyboard()
        else if (activity != null && activity!! is MatchesScreenActivity)
            (activity!! as MatchesScreenActivity).hideKeyboard()
        else if (activity != null && activity!! is LocationActivity)
            (activity!! as LocationActivity).hideKeyboard()
    }

    override fun onSuccess(msg: String) {
        showMsg(msg)
    }

    override fun onFailed(msg: String, error: Int) {
        if (activity != null && activity!! is SignUpActivity)
            (activity!! as SignUpActivity).onFailed(msg, error)
        else if (activity != null && activity!! is ProfileActivity)
            (activity!! as ProfileActivity).onFailed(msg, error)
        else if (activity != null && activity!! is ProfileBasicInfoActivity)
            (activity!! as ProfileBasicInfoActivity).onFailed(msg, error)
        else if (activity != null && activity!! is ProfileInterestActivity)
            (activity!! as ProfileInterestActivity).onFailed(msg,error)
        else if (activity != null && activity!! is EditProfileActivity)
            (activity!! as EditProfileActivity).onFailed(msg,error)
        else if (activity != null && activity!! is JobListingActivity)
            (activity!! as JobListingActivity).onFailed(msg,error)
        else if (activity != null && activity!! is EducationListActivity)
            (activity!! as EducationListActivity).onFailed(msg,error)
        else if (activity != null && activity!! is FollowingFollowersActivity)
            (activity!! as FollowingFollowersActivity).onFailed(msg,error)
        else if (activity != null && activity!! is AccountSettingsActivity)
            (activity!! as AccountSettingsActivity).onFailed(msg,error)
        else if (activity != null && activity!! is DashboardActivity)
            (activity!! as DashboardActivity).onFailed(msg,error)
        else if (activity != null && activity!! is FilterActivity)
            (activity!! as FilterActivity).onFailed(msg,error)
        else if (activity != null && activity!! is MatchesActivity)
            (activity!! as MatchesActivity).onFailed(msg,error)
        else if (activity != null && activity!! is ChatActivity)
            (activity!! as ChatActivity).onFailed(msg,error)
        else if (activity != null && activity!! is MatchesScreenActivity)
            (activity!! as MatchesScreenActivity).onFailed(msg,error)
        else if (activity != null && activity!! is LocationActivity)
            (activity!! as LocationActivity).onFailed(msg,error)
    }

    override fun onHandleException(e: Throwable) {
        if (activity != null && activity!! is SignUpActivity)
            (activity!! as SignUpActivity).onHandleException(e)
        else if (activity != null && activity!! is ProfileActivity)
            (activity!! as ProfileActivity).onHandleException(e)
        else if (activity != null && activity!! is ProfileBasicInfoActivity)
            (activity!! as ProfileBasicInfoActivity).onHandleException(e)
        else if (activity != null && activity!! is ProfileInterestActivity)
            (activity!! as ProfileInterestActivity).onHandleException(e)
        else if (activity != null && activity!! is EditProfileActivity)
            (activity!! as EditProfileActivity).onHandleException(e)
        else if (activity != null && activity!! is JobListingActivity)
            (activity!! as JobListingActivity).onHandleException(e)
        else if (activity != null && activity!! is EducationListActivity)
            (activity!! as EducationListActivity).onHandleException(e)
        else if (activity != null && activity!! is FollowingFollowersActivity)
            (activity!! as FollowingFollowersActivity).onHandleException(e)
        else if (activity != null && activity!! is AccountSettingsActivity)
            (activity!! as AccountSettingsActivity).onHandleException(e)
        else if (activity != null && activity!! is DashboardActivity)
            (activity!! as DashboardActivity).onHandleException(e)
        else if (activity != null && activity!! is FilterActivity)
            (activity!! as FilterActivity).onHandleException(e)
        else if (activity != null && activity!! is MatchesActivity)
            (activity!! as MatchesActivity).onHandleException(e)
        else if (activity != null && activity!! is ChatActivity)
            (activity!! as ChatActivity).onHandleException(e)
        else if (activity != null && activity!! is MatchesScreenActivity)
            (activity!! as MatchesScreenActivity).onHandleException(e)
        else if (activity != null && activity!! is LocationActivity)
            (activity!! as LocationActivity).onHandleException(e)
    }

    override fun isInternetAvailable() =
        if (activity != null) {
            when {
                activity!! is SignUpActivity -> (activity!! as SignUpActivity).isInternetAvailable()
                activity!! is ProfileActivity -> (activity!! as ProfileActivity).isInternetAvailable()
                activity!! is ProfileBasicInfoActivity -> (activity!! as ProfileBasicInfoActivity).isInternetAvailable()
                activity!! is ProfileInterestActivity -> (activity!! as ProfileInterestActivity).isInternetAvailable()
                activity!! is EditProfileActivity -> (activity!! as EditProfileActivity).isInternetAvailable()
                activity!! is JobListingActivity -> (activity!! as JobListingActivity).isInternetAvailable()
                activity!! is EducationListActivity -> (activity!! as EducationListActivity).isInternetAvailable()
                activity!! is FollowingFollowersActivity -> (activity!! as FollowingFollowersActivity).isInternetAvailable()
                activity!! is AccountSettingsActivity -> (activity!! as AccountSettingsActivity).isInternetAvailable()
                activity!! is DashboardActivity -> (activity!! as DashboardActivity).isInternetAvailable()
                activity!! is FilterActivity -> (activity!! as FilterActivity).isInternetAvailable()
                activity!! is MatchesActivity -> (activity!! as MatchesActivity).isInternetAvailable()
                activity!! is ChatActivity -> (activity!! as ChatActivity).isInternetAvailable()
                activity!! is MatchesScreenActivity -> (activity!! as MatchesScreenActivity).isInternetAvailable()
                activity!! is LocationActivity -> (activity!! as LocationActivity).isInternetAvailable()
                activity!! is AlbumVideoActivity -> (activity!! as AlbumVideoActivity).isInternetAvailable()
                activity!! is ProfileViewActivity -> (activity!! as ProfileViewActivity).isInternetAvailable()
                else -> false
            }
        } else false

    fun openActivity(
        activity: Activity,
        destinationActivity: Activity
    ) {
        startActivity(Intent(activity, destinationActivity::class.java))
        activity.overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    fun openActivity(intent: Intent) {
        startActivity(intent)
        activity!!.overridePendingTransition(R.anim.enter, R.anim.exit);
    }
    fun openActivityWithResultCode(
        activity: Activity,
        destinationActivity: Activity,
        result_code: Int
    ) {
        startActivityForResult(Intent(activity, destinationActivity::class.java), result_code)
        activity.overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    fun openActivityForResult(
        activity: Activity,
        intent: Intent,
        resultCode: Int
    ) {
        startActivityForResult(intent,resultCode)
        activity.overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    fun finishActivity(activity: Activity) {
        activity.finish()
        activity.overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left)
    }

    fun addFragment(fragment: Fragment, tag: String) {
        activity!!.supportFragmentManager.beginTransaction().addToBackStack(tag)
            .replace(R.id.flContainer, fragment, tag)
            .commitAllowingStateLoss()
    }

    fun addFragmentChild(childFragmentManager: FragmentManager, fragment: Fragment, tag: String) {
        childFragmentManager.beginTransaction().addToBackStack(tag)
            .add(R.id.flContainer, fragment, tag)
            .commitAllowingStateLoss()
    }

    /**
     * Remove all fragment from stack
     */
    fun removeAllFragment(fragmentManager: FragmentManager) {
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }
}