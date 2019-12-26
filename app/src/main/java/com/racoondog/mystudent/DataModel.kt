package com.racoondog.mystudent

import io.realm.RealmList
import io.realm.RealmObject

open class DataModel: RealmObject() {

    var dataSaved:Boolean = false

    object ScheduleData{

        var Title:String? = null
        var ScheduleDayFlag:Int? = null
        var ScheduleStartHour:Int? = null
        var ScheduleEndHour:Int? = null
    }

    lateinit var SubjectDB:RealmList<SubjectBox>

}

open class SubjectBox: RealmObject(){

        lateinit var id: String

        lateinit var starthour: String
        lateinit var startminute: String

        lateinit var endhour: String
        lateinit var endminute: String

        lateinit var title: String
        lateinit var content: String
        lateinit var time: String


}
