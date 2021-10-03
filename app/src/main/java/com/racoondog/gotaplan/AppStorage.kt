package com.racoondog.gotaplan

import android.content.Context
import android.content.SharedPreferences
import com.google.common.reflect.TypeToken
import com.google.gson.GsonBuilder
import io.realm.RealmResults

class AppStorage(context: Context) {

    private val pref: SharedPreferences = context.getSharedPreferences("app_storage", Context.MODE_PRIVATE)
    private val removeAds = "remove_ads"
    private val helpView = true
    private val looper = "true"
    private val widgetScheduleID : Int = 0


    fun setWidgetSubjectList(realmResults: RealmResults<SubjectData>) {

        val editor = pref.edit()
        val data = mutableListOf<SubjectItem?>()
        for (i in realmResults.indices){
            data.add(SubjectItem(i,realmResults[i]!!.title,realmResults[i]!!.startHour,realmResults[i]!!.startMinute
                ,realmResults[i]!!.endHour,realmResults[i]!!.endMinute))
        }
        var makeGson = GsonBuilder().create()
        var listType : TypeToken<MutableList<SubjectItem?>> = object : TypeToken<MutableList<SubjectItem?>>() {}
        var strContact = makeGson.toJson(data,listType.type)
        editor.putString("subject",strContact)
        editor.apply()
    }
   fun getWidgetSubjectList() : MutableList<SubjectItem?>?{
        val data = pref.getString("subject", "")
        var makeGson = GsonBuilder().create()
        var listType : TypeToken<MutableList<SubjectItem?>> = object : TypeToken<MutableList<SubjectItem?>>() {}
        var  arrayList : MutableList<SubjectItem?>? = null
        arrayList = makeGson.fromJson(data,listType.type)
        return arrayList
    }

    fun setWidgetScheduleList(realmResults: ScheduleData) {

        val editor = pref.edit()
        val data = mutableListOf<ScheduleItem?>()

        data.add(ScheduleItem(0,realmResults.title))

        var makeGson = GsonBuilder().create()
        var listType : TypeToken<MutableList<ScheduleItem?>> = object : TypeToken<MutableList<ScheduleItem?>>() {}
        var strContact = makeGson.toJson(data,listType.type)
        editor.putString("schedule",strContact)
        editor.apply()
    }


    fun getWidgetScheduleList() : MutableList<ScheduleItem?>?{
        val data = pref.getString("schedule", "")
        var makeGson = GsonBuilder().create()
        var listType : TypeToken<MutableList<ScheduleItem?>> = object : TypeToken<MutableList<ScheduleItem?>>() {}
        var  arrayList : MutableList<ScheduleItem?>? = null
        arrayList = makeGson.fromJson(data,listType.type)
        return arrayList
    }

    fun setHelpView(flag: Boolean) {
        val editor = pref.edit()
        editor.putBoolean("helpView", flag)
        editor.apply()
    }
    fun showHelpView(): Boolean {
        return pref.getBoolean("helpView",true)
    }

    fun setPurchasedRemoveAds(flag: Boolean) {
        val editor = pref.edit()
        editor.putBoolean(removeAds, flag)
        editor.apply()
    }

    fun setWidgetScheduleID(id:Int){
        val editor = pref.edit()
        editor.putInt("widgetScheduleID", id)
        editor.apply()
    }

    fun purchasedRemoveAds(): Boolean {
        return pref.getBoolean(removeAds, false)
    }

    fun getWidgetScheduleID(): Int {
        return pref.getInt("widgetScheduleID", 0)
    }

    fun getLooper(): String {
        return when {
            pref.getString("looper","false") == "false" -> {
                "false"
            }
            pref.getString("looper","false") == "true" -> {
                "true"
            }
            else -> {
                "none"
            }
        }
    }
    fun setLooper(flag:String) {
        val editor = pref.edit()
        editor.putString("looper", flag)
        editor.apply()
    }
}