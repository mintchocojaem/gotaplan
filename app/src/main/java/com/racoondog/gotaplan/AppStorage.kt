package com.racoondog.gotaplan

import android.content.Context
import android.content.SharedPreferences
import android.widget.RemoteViewsService
import com.google.common.reflect.TypeToken
import com.google.gson.GsonBuilder
import io.realm.RealmResults

class AppStorage(context: Context) {

    private val pref: SharedPreferences = context.getSharedPreferences("app_storage", Context.MODE_PRIVATE)
    private val removeAds = "remove_ads"
    private val helpView = ""
    private val linkageID = "linkageID"
    private val titleList = mutableListOf<String>()

    fun purchasedRemoveAds(): Boolean {
        return pref.getBoolean(removeAds, false)
    }

    fun setPurchasedRemoveAds(flag: Boolean) {
        val editor = pref.edit()
        editor.putBoolean(removeAds, flag)
        editor.apply()
    }
    fun setWidgetDateList(realmResults: RealmResults<SubjectData>) {

        val editor = pref.edit()
        val data = mutableListOf<WidgetItem?>()
        for (i in realmResults.indices){
            data.add(WidgetItem(i,realmResults[i]!!.title,realmResults[i]!!.startHour,realmResults[i]!!.startMinute
                ,realmResults[i]!!.endHour,realmResults[i]!!.endMinute))
        }
        var makeGson = GsonBuilder().create()
        var listType : TypeToken<MutableList<WidgetItem?>> = object : TypeToken<MutableList<WidgetItem?>>() {}
        var strContact = makeGson.toJson(data,listType.type)
        editor.putString("data",strContact)
        editor.apply()
    }
    fun getWidgetDateList() : MutableList<WidgetItem?>?{
        val data = pref.getString("data", "")
        var makeGson = GsonBuilder().create()
        var listType : TypeToken<MutableList<WidgetItem?>> = object : TypeToken<MutableList<WidgetItem?>>() {}
        var  arrayList : MutableList<WidgetItem?>? = null
        arrayList = makeGson.fromJson(data,listType.type)
        return arrayList
    }



    fun setHelpView(flag: Boolean) {
        val editor = pref.edit()
        editor.putBoolean(helpView, flag)
        editor.apply()
    }
    fun showHelpView(): Boolean {
        return pref.getBoolean(helpView,true)
    }
    fun setLinkageID(flag: Boolean) {
        val editor = pref.edit()
        editor.putBoolean(linkageID, flag)
        editor.apply()
    }
    fun initLinkageID(): Boolean {
        return pref.getBoolean(linkageID,true)
    }

}