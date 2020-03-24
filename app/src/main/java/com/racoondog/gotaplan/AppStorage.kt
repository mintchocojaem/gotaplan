package com.racoondog.gotaplan

import android.content.Context
import android.content.SharedPreferences

class AppStorage(context: Context) {

    private val pref: SharedPreferences = context.getSharedPreferences("app_storage", Context.MODE_PRIVATE)
    private val removeAds = "remove_ads"
    private val helpView = ""

    fun purchasedRemoveAds(): Boolean {
        return pref.getBoolean(removeAds, false)
    }

    fun setPurchasedRemoveAds(flag: Boolean) {
        val editor = pref.edit()
        editor.putBoolean(removeAds, flag)
        editor.apply()
    }
    fun setHelpView(flag: Boolean) {
        val editor = pref.edit()
        editor.putBoolean(helpView, flag)
        editor.apply()
    }
    fun showHelpView(): Boolean {
        return pref.getBoolean(helpView,true)
    }
}