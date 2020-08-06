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

    fun openActivity(
        intent: Intent
    ) {
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
    /**
     * Remove all fragment from stack
     */
    fun removeAllFragment(fragmentManager: FragmentManager) {
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }
}