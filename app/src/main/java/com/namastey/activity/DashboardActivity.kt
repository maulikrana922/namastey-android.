package com.namastey.activity

import android.os.Bundle
import android.view.View
import com.namastey.R
import com.namastey.databinding.ActivityDashboardBinding
import com.namastey.viewModel.BaseViewModel

class DashboardActivity : BaseActivity<ActivityDashboardBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
    }

    override fun getViewModel(): BaseViewModel {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLayoutId(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBindingVariable(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


//    Temp open this activity
    fun onClickUser(view: View) {
        openActivity(this, ProfileActivity())
    }
}
