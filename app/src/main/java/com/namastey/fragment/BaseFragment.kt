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
        if (activity != null && requireActivity() is SignUpActivity)
            (requireActivity() as SignUpActivity).showMsg(msgId)
        else if (activity != null && requireActivity() is AccountSettingsActivity)
            (requireActivity() as AccountSettingsActivity).showMsg(msgId)
        else if (activity != null && requireActivity() is DashboardActivity)
            (requireActivity() as DashboardActivity).showMsg(msgId)
        else if (activity != null && requireActivity() is FilterActivity)
            (requireActivity() as FilterActivity).showMsg(msgId)
        else if (activity != null && requireActivity() is MatchesActivity)
            (requireActivity() as MatchesActivity).showMsg(msgId)
        else if (activity != null && requireActivity() is ChatActivity)
            (requireActivity() as ChatActivity).showMsg(msgId)
        else if (activity != null && requireActivity() is MatchesScreenActivity)
            (requireActivity() as MatchesScreenActivity).showMsg(msgId)
        else if (activity != null && requireActivity() is LocationActivity)
            (requireActivity() as LocationActivity).showMsg(msgId)
        else if (activity != null && requireActivity() is LikeProfileActivity)
            (requireActivity() as LikeProfileActivity).showMsg(msgId)
        else if (activity != null && requireActivity() is AlbumDetailActivity)
            (requireActivity() as AlbumDetailActivity).showMsg(msgId)
        else if (activity != null && requireActivity() is ImageSliderActivity)
            (requireActivity() as ImageSliderActivity).showMsg(msgId)
        else if (activity != null && requireActivity() is MembershipActivity)
            (requireActivity() as MembershipActivity).showMsg(msgId)
        else if (activity != null && requireActivity() is PostVideoActivity)
            (requireActivity() as PostVideoActivity).showMsg(msgId)
        else if (activity != null && requireActivity() is ProfileViewActivity)
            (requireActivity() as ProfileViewActivity).showMsg(msgId)
        else if (activity != null && requireActivity() is SettingsActivity)
            (requireActivity() as SettingsActivity).showMsg(msgId)
    }

    override fun showMsg(msg: String) {
        if (activity != null && requireActivity() is SignUpActivity)
            (requireActivity() as SignUpActivity).showMsg(msg)
        else if (activity != null && requireActivity() is AccountSettingsActivity)
            (requireActivity() as AccountSettingsActivity).showMsg(msg)
        else if (activity != null && requireActivity() is DashboardActivity)
            (requireActivity() as DashboardActivity).showMsg(msg)
        else if (activity != null && requireActivity() is FilterActivity)
            (requireActivity() as FilterActivity).showMsg(msg)
        else if (activity != null && requireActivity() is MatchesActivity)
            (requireActivity() as MatchesActivity).showMsg(msg)
        else if (activity != null && requireActivity() is ChatActivity)
            (requireActivity() as ChatActivity).showMsg(msg)
        else if (activity != null && requireActivity() is MatchesScreenActivity)
            (requireActivity() as MatchesScreenActivity).showMsg(msg)
        else if (activity != null && requireActivity() is LocationActivity)
            (requireActivity() as LocationActivity).showMsg(msg)
        else if (activity != null && requireActivity() is LikeProfileActivity)
            (requireActivity() as LikeProfileActivity).showMsg(msg)
        else if (activity != null && requireActivity() is AlbumDetailActivity)
            (requireActivity() as AlbumDetailActivity).showMsg(msg)
        else if (activity != null && requireActivity() is ImageSliderActivity)
            (requireActivity() as ImageSliderActivity).showMsg(msg)
        else if (activity != null && requireActivity() is MembershipActivity)
            (requireActivity() as MembershipActivity).showMsg(msg)
        else if (activity != null && requireActivity() is PostVideoActivity)
            (requireActivity() as PostVideoActivity).showMsg(msg)
        else if (activity != null && requireActivity() is ProfileViewActivity)
            (requireActivity() as ProfileViewActivity).showMsg(msg)
        else if (activity != null && requireActivity() is SettingsActivity)
            (requireActivity() as SettingsActivity).showMsg(msg)
    }

    override fun hideKeyboard() {
        if (activity != null && requireActivity() is SignUpActivity)
            (requireActivity() as SignUpActivity).hideKeyboard()
        else if (activity != null && requireActivity() is AccountSettingsActivity)
            (requireActivity() as AccountSettingsActivity).hideKeyboard()
        else if (activity != null && requireActivity() is DashboardActivity)
            (requireActivity() as DashboardActivity).hideKeyboard()
        else if (activity != null && requireActivity() is FilterActivity)
            (requireActivity() as FilterActivity).hideKeyboard()
        else if (activity != null && requireActivity() is MatchesActivity)
            (requireActivity() as MatchesActivity).hideKeyboard()
        else if (activity != null && requireActivity() is ChatActivity)
            (requireActivity() as ChatActivity).hideKeyboard()
        else if (activity != null && requireActivity() is MatchesScreenActivity)
            (requireActivity() as MatchesScreenActivity).hideKeyboard()
        else if (activity != null && requireActivity() is LocationActivity)
            (requireActivity() as LocationActivity).hideKeyboard()
        else if (activity != null && requireActivity() is LikeProfileActivity)
            (requireActivity() as LikeProfileActivity).hideKeyboard()
        else if (activity != null && requireActivity() is AlbumDetailActivity)
            (requireActivity() as AlbumDetailActivity).hideKeyboard()
        else if (activity != null && requireActivity() is ImageSliderActivity)
            (requireActivity() as ImageSliderActivity).hideKeyboard()
        else if (activity != null && requireActivity() is MembershipActivity)
            (requireActivity() as MembershipActivity).hideKeyboard()
        else if (activity != null && requireActivity() is PostVideoActivity)
            (requireActivity() as PostVideoActivity).hideKeyboard()
        else if (activity != null && requireActivity() is ProfileViewActivity)
            (requireActivity() as ProfileViewActivity).hideKeyboard()
        else if (activity != null && requireActivity() is SettingsActivity)
            (requireActivity() as SettingsActivity).hideKeyboard()
    }

    override fun onSuccess(msg: String) {
        showMsg(msg)
    }

    override fun onFailed(msg: String, error: Int, status: Int) {
        if (activity != null && requireActivity() is SignUpActivity)
            (requireActivity() as SignUpActivity).onFailed(msg, error, status)
        else if (activity != null && requireActivity() is AccountSettingsActivity)
            (requireActivity() as AccountSettingsActivity).onFailed(msg, error, status)
        else if (activity != null && requireActivity() is DashboardActivity)
            (requireActivity() as DashboardActivity).onFailed(msg, error, status)
        else if (activity != null && requireActivity() is FilterActivity)
            (requireActivity() as FilterActivity).onFailed(msg, error, status)
        else if (activity != null && requireActivity() is MatchesActivity)
            (requireActivity() as MatchesActivity).onFailed(msg, error, status)
        else if (activity != null && requireActivity() is ChatActivity)
            (requireActivity() as ChatActivity).onFailed(msg, error, status)
        else if (activity != null && requireActivity() is MatchesScreenActivity)
            (requireActivity() as MatchesScreenActivity).onFailed(msg, error, status)
        else if (activity != null && requireActivity() is LocationActivity)
            (requireActivity() as LocationActivity).onFailed(msg, error, status)
        else if (activity != null && requireActivity() is LikeProfileActivity)
            (requireActivity() as LikeProfileActivity).onFailed(msg, error, status)
        else if (activity != null && requireActivity() is AlbumDetailActivity)
            (requireActivity() as AlbumDetailActivity).onFailed(msg, error, status)
        else if (activity != null && requireActivity() is ImageSliderActivity)
            (requireActivity() as ImageSliderActivity).onFailed(msg, error, status)
        else if (activity != null && requireActivity() is MembershipActivity)
            (requireActivity() as MembershipActivity).onFailed(msg, error, status)
        else if (activity != null && requireActivity() is PostVideoActivity)
            (requireActivity() as PostVideoActivity).onFailed(msg, error, status)
        else if (activity != null && requireActivity() is ProfileViewActivity)
            (requireActivity() as ProfileViewActivity).onFailed(msg, error, status)
        else if (activity != null && requireActivity() is SettingsActivity)
            (requireActivity() as SettingsActivity).onFailed(msg, error, status)
        else if (activity != null && requireActivity() is SplashActivity)
            (requireActivity() as SplashActivity).onFailed(msg, error, status)
    }

    override fun onHandleException(e: Throwable) {
        if (activity != null && requireActivity() is SignUpActivity)
            (requireActivity() as SignUpActivity).onHandleException(e)
        else if (activity != null && requireActivity() is AccountSettingsActivity)
            (requireActivity() as AccountSettingsActivity).onHandleException(e)
        else if (activity != null && requireActivity() is DashboardActivity)
            (requireActivity() as DashboardActivity).onHandleException(e)
        else if (activity != null && requireActivity() is FilterActivity)
            (requireActivity() as FilterActivity).onHandleException(e)
        else if (activity != null && requireActivity() is MatchesActivity)
            (requireActivity() as MatchesActivity).onHandleException(e)
        else if (activity != null && requireActivity() is ChatActivity)
            (requireActivity() as ChatActivity).onHandleException(e)
        else if (activity != null && requireActivity() is MatchesScreenActivity)
            (requireActivity() as MatchesScreenActivity).onHandleException(e)
        else if (activity != null && requireActivity() is LocationActivity)
            (requireActivity() as LocationActivity).onHandleException(e)
        else if (activity != null && requireActivity() is LikeProfileActivity)
            (requireActivity() as LikeProfileActivity).onHandleException(e)
        else if (activity != null && requireActivity() is AlbumDetailActivity)
            (requireActivity() as AlbumDetailActivity).onHandleException(e)
        else if (activity != null && requireActivity() is ImageSliderActivity)
            (requireActivity() as ImageSliderActivity).onHandleException(e)
        else if (activity != null && requireActivity() is MembershipActivity)
            (requireActivity() as MembershipActivity).onHandleException(e)
        else if (activity != null && requireActivity() is PostVideoActivity)
            (requireActivity() as PostVideoActivity).onHandleException(e)
        else if (activity != null && requireActivity() is ProfileViewActivity)
            (requireActivity() as ProfileViewActivity).onHandleException(e)
        else if (activity != null && requireActivity() is SettingsActivity)
            (requireActivity() as SettingsActivity).onHandleException(e)
    }

    override fun showMsgLog(msg: String) {
        Log.e("BaseActivity", "msg:  \t $msg")
    }

    override fun isInternetAvailable() =
        if (activity != null) {
            when {
                requireActivity() is SignUpActivity -> (requireActivity() as SignUpActivity).isInternetAvailable()
                requireActivity() is AccountSettingsActivity -> (requireActivity() as AccountSettingsActivity).isInternetAvailable()
                requireActivity() is DashboardActivity -> (requireActivity() as DashboardActivity).isInternetAvailable()
                requireActivity() is FilterActivity -> (requireActivity() as FilterActivity).isInternetAvailable()
                requireActivity() is MatchesActivity -> (requireActivity() as MatchesActivity).isInternetAvailable()
                requireActivity() is ChatActivity -> (requireActivity() as ChatActivity).isInternetAvailable()
                requireActivity() is MatchesScreenActivity -> (requireActivity() as MatchesScreenActivity).isInternetAvailable()
                requireActivity() is LocationActivity -> (requireActivity() as LocationActivity).isInternetAvailable()
                requireActivity() is AlbumVideoActivity -> (requireActivity() as AlbumVideoActivity).isInternetAvailable()
                requireActivity() is ProfileViewActivity -> (requireActivity() as ProfileViewActivity).isInternetAvailable()
                requireActivity() is LikeProfileActivity -> (requireActivity() as LikeProfileActivity).isInternetAvailable()
                requireActivity() is AlbumDetailActivity -> (requireActivity() as AlbumDetailActivity).isInternetAvailable()
                requireActivity() is ImageSliderActivity -> (requireActivity() as ImageSliderActivity).isInternetAvailable()
                requireActivity() is MembershipActivity -> (requireActivity() as MembershipActivity).isInternetAvailable()
                requireActivity() is PostVideoActivity -> (requireActivity() as PostVideoActivity).isInternetAvailable()
                requireActivity() is SettingsActivity -> (requireActivity() as SettingsActivity).isInternetAvailable()
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
        requireActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
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
        requireActivity().supportFragmentManager.beginTransaction().addToBackStack(tag)
            .replace(R.id.flContainer, fragment, tag)
            .commitAllowingStateLoss()
    }

    fun addFragmentFindFriend(fragment: Fragment, tag: String) {
        requireActivity().supportFragmentManager.beginTransaction().addToBackStack(tag)
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