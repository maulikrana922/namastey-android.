package com.namastey.dagger.module

import android.content.Context
import com.namastey.scopes.PerApplication
import dagger.Module
import dagger.Provides

@Module
class ContextModule(val context: Context) {

    @Provides
    @PerApplication
    fun providesContext(): Context {
        return context
    }
}
