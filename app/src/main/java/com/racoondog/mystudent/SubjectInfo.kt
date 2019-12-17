package com.racoondog.mystudent

import io.realm.RealmObject

open class SubjectInfo: RealmObject() {

    lateinit var  Title:String
    var ScheduleDayFlag = 0
    var ScheduleStartHour = 0
    var ScheduleEndHour = 0
}