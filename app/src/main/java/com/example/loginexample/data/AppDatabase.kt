package com.example.loginexample.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.loginexample.data.cache.dao.UserDao
import com.example.loginexample.models.User

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}