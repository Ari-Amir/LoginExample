package com.example.loginexample.di

import android.app.Application
import androidx.room.Room
import com.example.loginexample.data.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)

@Module
object DatabaseModule {

    @Singleton
    @Provides
    fun providesAppDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(application, AppDatabase::class.java, "LoginExample.db")
            .fallbackToDestructiveMigration()
            .build()
    }

}