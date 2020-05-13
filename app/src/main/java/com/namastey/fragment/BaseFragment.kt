package com.namastey.fragment

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    }

    override fun showMsg(msg: String) {
        if (activity != null && activity!! is SignUpActivity)
            (activity!! as SignUpActivity).showMsg(msg)

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
}