package com.racoondog.gotaplan

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

    var startMinute = ""

    var endHour = 0
    var endMinute = ""

    lateinit var title: String
    lateinit var content: String

    var subjectColor = 0

    lateinit var studentName : String
    lateinit var studentBirth : String
    lateinit var studentPhoneNumber : String
    lateinit var lessonCost : String
    var lessonCycle : String = "0"

}

open class ThemeData: RealmObject(){
    var statusBarColor = Color.parseColor("#ffffff")
    var mainButtonColor = Color.parseColor("#494949")
}
