package com.racoondog.mystudent

import io.realm.RealmList
import io.realm.RealmObject

open class DataModel: RealmObject() {

    var scheduleTitle:String? = null
    var scheduleDayFlag:Int? = null
    var scheduleStartHour:Int? = null
    var scheduleEndHour:Int? = null

    lateinit var subjectDB:RealmList<SubjectBox>

}

open class SubjectBox: RealmObject(){

    var id = 0
    var lessonOnOff : Boolean = false

    lateinit var dayFlag: String

    var startHour = 0

    lateinit var startMinute: String

    var endHour = 0
    lateinit var endMinute: String

    lateinit var title: String
    lateinit var content: String
    lateinit var time: String

    var subjectColor:Int = 0

    lateinit var studentName : String
    lateinit var studentBirth : String
    lateinit var studentPhoneNumber : String
    lateinit var lessonCost : String
    lateinit var lessonCycle : String

}
