package com.racoondog.mystudent

import io.realm.RealmList
import io.realm.RealmObject

open class DataModel: RealmObject() {

    var dataSaved:Boolean = false

    var scheduleTitle:String? = null
    var scheduleDayFlag:Int? = null
    var scheduleStartHour:Int? = null
    var scheduleEndHour:Int? = null

    lateinit var SubjectDB:RealmList<SubjectBox>

}

open class SubjectBox: RealmObject(){

        var id: Int = 0

        lateinit var dayFlag: String

        lateinit var startHour: String
        lateinit var startMinute: String

        lateinit var endHour: String
        lateinit var endMinute: String

        lateinit var title: String
        lateinit var content: String
        lateinit var time: String


}
