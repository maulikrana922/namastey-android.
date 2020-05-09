package com.namastey.viewModel

import android.text.TextUtils
import com.namastey.R
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.roomDB.entity.User
import com.namastey.uiView.BaseView
import com.namastey.uiView.DemoView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DemoViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var demoView: DemoView = baseView as DemoView
    private lateinit var job: Job

    private fun saveUserToDB(user: User) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val data = dbHelper.setLoggedInUser(user)
                if (data.isNotEmpty())
                    demoView.onLoginSuccess()
            } catch (t: Throwable) {
                demoView.onHandleException(t)
            }
        }
    }

    fun login() {
        demoView.onLogin()
    }

    fun login(name: String, email: String, pass: String) {
        if (isValid(name, email, pass)) {
            setIsLoading(true)
            job = GlobalScope.launch(Dispatchers.Main) {
                try {
                    networkService.requestLogin(email, pass).let { responseLogin ->
                        setIsLoading(false)
                        if (responseLogin.status) {
                            saveUserToDB(User(1, name, email, pass))
                        } else {
                            demoView.onFailed(responseLogin.message, responseLogin.error)
                        }
                    }
                } catch (t: Throwable) {
                    setIsLoading(false)
                    demoView.onHandleException(t)
                }
            }
        }
    }

    private fun isValid(name: String, email: String, pass: String): Boolean {

        var msgId = 0

        when {
            TextUtils.isEmpty(name) -> msgId = R.string.msg_empty_name
            TextUtils.isEmpty(email) -> msgId = R.string.msg_empty_email
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> msgId =
                R.string.msg_valid_email
            TextUtils.isEmpty(pass) -> msgId = R.string.msg_empty_password
        }

        if (msgId > 0) {
            demoView.showMsg(msgId)
        }

        return msgId == 0
    }
}
