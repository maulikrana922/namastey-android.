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
import com.namastey.activity.ProfileActivity
import com.namastey.activity.SignUpActivity
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
    }

    override fun showMsg(msg: String) {
        if (activity != null && activity!! is SignUpActivity)
            (activity!! as SignUpActivity).showMsg(msg)
        else if (activity != null && activity!! is ProfileActivity)
            (activity!! as ProfileActivity).showMsg(msg)
    }

    override fun hideKeyboard() {
        if (activity != null && activity!! is SignUpActivity)
            (activity!! as SignUpActivity).hideKeyboard()

    }

    override fun onSuccess(msg: String) {
        showMsg(msg)
    }

    override fun onFailed(msg: String, error: Int) {
        if (activity != null && activity!! is SignUpActivity)
            (activity!! as SignUpActivity).onFailed(msg, error)

    }

    override fun onHandleException(e: Throwable) {
        if (activity != null && activity!! is SignUpActivity)
            (activity!! as SignUpActivity).onHandleException(e)

    }

    override fun isInternetAvailable() =
        if (activity != null) {
            when {
                activity!! is SignUpActivity -> (activity!! as SignUpActivity).isInternetAvailable()
                activity!! is ProfileActivity -> (activity!! as ProfileActivity).isInternetAvailable()
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