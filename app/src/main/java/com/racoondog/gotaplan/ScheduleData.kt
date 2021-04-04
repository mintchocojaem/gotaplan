package com.racoondog.gotaplan

import android.graphics.Color
import io.realm.RealmObject
import io.realm.annotations.Required

open class ScheduleData: RealmObject() {

    var scheduleTitle:String = ""
    var scheduleDayFlag:Int = 0
    var scheduleStartHour:Int = 0
    var scheduleEndHour:Int = 0
    var scheduleInterval: Boolean = false
    
}

open class SubjectData: RealmObject(){
    var id = 0
    var lessonOnOff : Boolean = false

    var dayFlag = 0
    var startHour = 0

    var startMinute = ""

    var endHour = 0
    var endMinute = ""

    var notification: Int = -1

    var title: String =""
    var content: String =""

    var subjectColor = 0

    var studentName : String =""
    var studentBirth : String =""
    var studentPhoneNumber : String =""
    var lessonCost : String = ""
    var currentCycle : Int = 0
    var maxCycle : Int = 0
    var linkageID: Int = 0
    var calculation: Boolean = false

}

open class ThemeData: RealmObject(){
    var statusBarColor = Color.parseColor("#ffffff")
    var mainButtonColor = Color.parseColor("#494949")
}
