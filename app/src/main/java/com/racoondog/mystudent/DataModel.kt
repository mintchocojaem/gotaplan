package com.racoondog.mystudent

import io.realm.RealmObject

open class DataModel: RealmObject() {

    var DataSaved:Boolean = false

    object ScheduleData{

        var Title:String? = null
        var ScheduleDayFlag:Int? = null
        var ScheduleStartHour:Int? = null
        var ScheduleEndHour:Int? = null
    }

}