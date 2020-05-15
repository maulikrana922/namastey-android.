package com.namastey.dagger.module

import android.content.Context
import com.namastey.scopes.PerApplication
import com.namastey.utils.SessionManager
import dagger.Module
import dagger.Provides

@Module(includes = [ContextModule::class])
class SessionManagerModule {

    @Provides
    @PerApplication
    fun provideSessionManger(context: Context) = SessionManager(context)
}