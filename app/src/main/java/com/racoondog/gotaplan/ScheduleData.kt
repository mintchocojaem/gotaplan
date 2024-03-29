package com.racoondog.gotaplan

import android.graphics.Color
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Required

open class ScheduleData: RealmObject() {

    var id:Int = 0
    var title:String = ""
    var dayFlag:Int = 0
    var startHour:Int = 0
    var endHour:Int = 0
    var interval: Boolean = false

    var subjectData : RealmList<SubjectData> = RealmList()
    
}

open class SubjectData: RealmObject(){

    var id = 0
    var dayFlag = 0
    var startHour = 0
    var startMinute = ""
    var endHour = 0
    var endMinute = ""
    var notification: Int = -1
    @Required lateinit var title: String
    @Required lateinit var content: String
    var subjectColor = 0
    var linkageID: Int = 0

}






open class ThemeData: RealmObject(){
    var statusBarColor = Color.parseColor("#ffffff")
    var mainButtonColor = Color.parseColor("#494949")
}
