package com.namastey.uiView

import com.namastey.model.NotificationOnOffBean

interface AccountSettingsNotificationView : BaseView {
    fun onSuccessResponse(notificationOnOffBean: NotificationOnOffBean)

}