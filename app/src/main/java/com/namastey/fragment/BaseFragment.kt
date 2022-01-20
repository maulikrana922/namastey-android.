package com.namastey.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
        else if (activity != null && activity!! is ProfileInterestActivity)
            (activity!! as ProfileInterestActivity).showMsg(msgId)
        else if (activity != null && activity!! is EducationListActivity)
            (activity!! as EducationListActivity).showMsg(msgId)
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
        else if (activity != null && activity!! is LikeProfileActivity)
            (activity!! as LikeProfileActivity).showMsg(msgId)
        else if (activity != null && activity!! is AlbumDetailActivity)
            (activity!! as AlbumDetailActivity).showMsg(msgId)
        else if (activity != null && activity!! is CreateAlbumActivity)
            (activity!! as CreateAlbumActivity).showMsg(msgId)
        else if (activity != null && activity!! is ImageSliderActivity)
            (activity!! as ImageSliderActivity).showMsg(msgId)
        else if (activity != null && activity!! is PostVideoActivity)
            (activity!! as PostVideoActivity).showMsg(msgId)
        else if (activity != null && activity!! is ProfileViewActivity)
            (activity!! as ProfileViewActivity).showMsg(msgId)
        else if (activity != null && activity!! is SettingsActivity)
            (activity!! as SettingsActivity).showMsg(msgId)
    }

    override fun showMsg(msg: String) {
        if (activity != null && activity!! is SignUpActivity)
            (activity!! as SignUpActivity).showMsg(msg)
        else if (activity != null && activity!! is ProfileInterestActivity)
            (activity!! as ProfileInterestActivity).showMsg(msg)
        else if (activity != null && activity!! is EducationListActivity)
            (activity!! as EducationListActivity).showMsg(msg)
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
        else if (activity != null && activity!! is LikeProfileActivity)
            (activity!! as LikeProfileActivity).showMsg(msg)
        else if (activity != null && activity!! is AlbumDetailActivity)
            (activity!! as AlbumDetailActivity).showMsg(msg)
        else if (activity != null && activity!! is CreateAlbumActivity)
            (activity!! as CreateAlbumActivity).showMsg(msg)
        else if (activity != null && activity!! is ImageSliderActivity)
            (activity!! as ImageSliderActivity).showMsg(msg)
        else if (activity != null && activity!! is PostVideoActivity)
            (activity!! as PostVideoActivity).showMsg(msg)
        else if (activity != null && activity!! is ProfileViewActivity)
            (activity!! as ProfileViewActivity).showMsg(msg)
        else if (activity != null && activity!! is SettingsActivity)
            (activity!! as SettingsActivity).showMsg(msg)
    }

    override fun hideKeyboard() {
        if (activity != null && activity!! is SignUpActivity)
            (activity!! as SignUpActivity).hideKeyboard()
        else if (activity != null && activity!! is ProfileInterestActivity)
            (activity!! as ProfileInterestActivity).hideKeyboard()
        else if (activity != null && activity!! is EducationListActivity)
            (activity!! as EducationListActivity).hideKeyboard()
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
        else if (activity != null && activity!! is LikeProfileActivity)
            (activity!! as LikeProfileActivity).hideKeyboard()
        else if (activity != null && activity!! is AlbumDetailActivity)
            (activity!! as AlbumDetailActivity).hideKeyboard()
        else if (activity != null && activity!! is CreateAlbumActivity)
            (activity!! as CreateAlbumActivity).hideKeyboard()
        else if (activity != null && activity!! is ImageSliderActivity)
            (activity!! as ImageSliderActivity).hideKeyboard()
        else if (activity != null && activity!! is PostVideoActivity)
            (activity!! as PostVideoActivity).hideKeyboard()
        else if (activity != null && activity!! is ProfileViewActivity)
            (activity!! as ProfileViewActivity).hideKeyboard()
        else if (activity != null && activity!! is SettingsActivity)
            (activity!! as SettingsActivity).hideKeyboard()
    }

    override fun onSuccess(msg: String) {
        showMsg(msg)
    }

    override fun onFailed(msg: String, error: Int, status: Int) {
        if (activity != null && activity!! is SignUpActivity)
            (activity!! as SignUpActivity).onFailed(msg, error, status)
        else if (activity != null && activity!! is ProfileInterestActivity)
            (activity!! as ProfileInterestActivity).onFailed(msg, error, status)
        else if (activity != null && activity!! is EducationListActivity)
            (activity!! as EducationListActivity).onFailed(msg, error, status)
        else if (activity != null && activity!! is AccountSettingsActivity)
            (activity!! as AccountSettingsActivity).onFailed(msg, error, status)
        else if (activity != null && activity!! is DashboardActivity)
            (activity!! as DashboardActivity).onFailed(msg, error, status)
        else if (activity != null && activity!! is FilterActivity)
            (activity!! as FilterActivity).onFailed(msg, error, status)
        else if (activity != null && activity!! is MatchesActivity)
            (activity!! as MatchesActivity).onFailed(msg, error, status)
        else if (activity != null && activity!! is ChatActivity)
            (activity!! as ChatActivity).onFailed(msg, error, status)
        else if (activity != null && activity!! is MatchesScreenActivity)
            (activity!! as MatchesScreenActivity).onFailed(msg, error, status)
        else if (activity != null && activity!! is LocationActivity)
            (activity!! as LocationActivity).onFailed(msg, error, status)
        else if (activity != null && activity!! is LikeProfileActivity)
            (activity!! as LikeProfileActivity).onFailed(msg, error, status)
        else if (activity != null && activity!! is AlbumDetailActivity)
            (activity!! as AlbumDetailActivity).onFailed(msg, error, status)
        else if (activity != null && activity!! is CreateAlbumActivity)
            (activity!! as CreateAlbumActivity).onFailed(msg, error, status)
        else if (activity != null && activity!! is ImageSliderActivity)
            (activity!! as ImageSliderActivity).onFailed(msg, error, status)
        else if (activity != null && activity!! is PostVideoActivity)
            (activity!! as PostVideoActivity).onFailed(msg, error, status)
        else if (activity != null && activity!! is ProfileViewActivity)
            (activity!! as ProfileViewActivity).onFailed(msg, error, status)
        else if (activity != null && activity!! is SettingsActivity)
            (activity!! as SettingsActivity).onFailed(msg, error, status)
        else if (activity != null && activity!! is SplashActivity)
            (activity!! as SplashActivity).onFailed(msg, error, status)
    }

    override fun onHandleException(e: Throwable) {
        if (activity != null && activity!! is SignUpActivity)
            (activity!! as SignUpActivity).onHandleException(e)
        else if (activity != null && activity!! is ProfileInterestActivity)
            (activity!! as ProfileInterestActivity).onHandleException(e)
        else if (activity != null && activity!! is EducationListActivity)
            (activity!! as EducationListActivity).onHandleException(e)
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
        else if (activity != null && activity!! is LikeProfileActivity)
            (activity!! as LikeProfileActivity).onHandleException(e)
        else if (activity != null && activity!! is AlbumDetailActivity)
            (activity!! as AlbumDetailActivity).onHandleException(e)
        else if (activity != null && activity!! is CreateAlbumActivity)
            (activity!! as CreateAlbumActivity).onHandleException(e)
        else if (activity != null && activity!! is ImageSliderActivity)
            (activity!! as ImageSliderActivity).onHandleException(e)
        else if (activity != null && activity!! is PostVideoActivity)
            (activity!! as PostVideoActivity).onHandleException(e)
        else if (activity != null && activity!! is ProfileViewActivity)
            (activity!! as ProfileViewActivity).onHandleException(e)
        else if (activity != null && activity!! is SettingsActivity)
            (activity!! as SettingsActivity).onHandleException(e)
    }

    override fun showMsgLog(msg: String) {
        Log.e("BaseActivity", "msg:  \t $msg")
    }

    override fun isInternetAvailable() =
        if (activity != null) {
            when {
                activity!! is SignUpActivity -> (activity!! as SignUpActivity).isInternetAvailable()
                activity!! is ProfileInterestActivity -> (activity!! as ProfileInterestActivity).isInternetAvailable()
                activity!! is EducationListActivity -> (activity!! as EducationListActivity).isInternetAvailable()
                activity!! is AccountSettingsActivity -> (activity!! as AccountSettingsActivity).isInternetAvailable()
                activity!! is DashboardActivity -> (activity!! as DashboardActivity).isInternetAvailable()
                activity!! is FilterActivity -> (activity!! as FilterActivity).isInternetAvailable()
                activity!! is MatchesActivity -> (activity!! as MatchesActivity).isInternetAvailable()
                activity!! is ChatActivity -> (activity!! as ChatActivity).isInternetAvailable()
                activity!! is MatchesScreenActivity -> (activity!! as MatchesScreenActivity).isInternetAvailable()
                activity!! is LocationActivity -> (activity!! as LocationActivity).isInternetAvailable()
                activity!! is AlbumVideoActivity -> (activity!! as AlbumVideoActivity).isInternetAvailable()
                activity!! is ProfileViewActivity -> (activity!! as ProfileViewActivity).isInternetAvailable()
                activity!! is LikeProfileActivity -> (activity!! as LikeProfileActivity).isInternetAvailable()
                activity!! is AlbumDetailActivity -> (activity!! as AlbumDetailActivity).isInternetAvailable()
                activity!! is CreateAlbumActivity -> (activity!! as CreateAlbumActivity).isInternetAvailable()
                activity!! is ImageSliderActivity -> (activity!! as ImageSliderActivity).isInternetAvailable()
                activity!! is PostVideoActivity -> (activity!! as PostVideoActivity).isInternetAvailable()
                activity!! is SettingsActivity -> (activity!! as SettingsActivity).isInternetAvailable()
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
        startActivityForResult(intent, resultCode)
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

    fun addFragmentFindFriend(fragment: Fragment, tag: String) {
        activity!!.supportFragmentManager.beginTransaction().addToBackStack(tag)
            .replace(R.id.flContainerFindFriend, fragment, tag)
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