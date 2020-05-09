package com.namastey.dagger.module

import com.namastey.scopes.PerActivity
import com.namastey.uiView.BaseView
import dagger.Module
import dagger.Provides


@Module
class ViewModule(private val view: BaseView) {

    @PerActivity
    @Provides
    fun provideView(): BaseView {
        return view
    }
}