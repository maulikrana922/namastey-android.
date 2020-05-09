package com.namastey.dagger.module

import android.arch.persistence.room.Room
import android.content.Context
import com.namastey.roomDB.AppDB
import com.namastey.scopes.PerApplication
import dagger.Module
import dagger.Provides

@Module(includes = [ContextModule::class])
class AppDBModule {

    @Provides
    @PerApplication
    fun provideAppDatabase(context: Context): AppDB {
        return Room.databaseBuilder(context, AppDB::class.java, "NamasteyDB").fallbackToDestructiveMigration().build()
    }
}