package com.racoondog.mystudent

import io.realm.RealmObject

open class SubjectInfo: RealmObject() {

    var Title:String? = null
    var ScheduleDayFlag:Int? = null
    var ScheduleStartHour:Int? = null
    var ScheduleEndHour:Int? = null
}