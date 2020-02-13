package com.racoondog.mystudent

import android.graphics.Color
import io.realm.RealmObject

open class ScheduleData: RealmObject() {

    var scheduleTitle:String = ""
    var scheduleDayFlag:Int = 0
    var scheduleStartHour:Int = 0
    var scheduleEndHour:Int = 0
    
}

open class SubjectData: RealmObject(){

    var id = 0
    var lessonOnOff : Boolean = false

    var dayFlag = 0

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

open class ThemeData: RealmObject(){
    var statusBarColor = Color.parseColor("#ffffff")
    var mainButtonColor = Color.parseColor("#494949")
}
