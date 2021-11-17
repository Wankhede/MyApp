package com.example.myapp

import android.content.Context
import android.content.SharedPreferences

class SharedPref(context: Context) {

    var MY_SHARED_PREF_NAME = "Note creator"

    private val preferences: SharedPreferences = context.getSharedPreferences(MY_SHARED_PREF_NAME,Context.MODE_PRIVATE)

    var intExamplePref: String?
        get() = preferences.getString(MY_SHARED_PREF_NAME, "-1")
        set(value) = preferences.edit().putString(MY_SHARED_PREF_NAME, value).apply()



}