package com.racoondog.mystudent

object ScheduleData{
    val ScheduleInfo = mutableListOf<Array<Int>>()

    var scheduleDayFlag = 0
    var scheduleStartHour = 0
    var scheduleEndHour = 0

    fun setData() {

        val dataInfo = arrayOf(scheduleDayFlag, scheduleStartHour, scheduleStartHour)
        ScheduleInfo.add(dataInfo)

    }
}

object SubjectData {

    var SubjectInfo = mutableListOf<Array<String>?>()
    val LessonInfo = mutableListOf<Any>()

    var id: Int = 0

    var SubjectTitle:String = ""
    var StartTimeText:String = ""
    var EndTimeText:String = ""
    var ContentText:String = ""


    fun getData(ID: Int): Any {
        id = ID
        return SubjectInfo[ID]!!.contentDeepToString()

    }

    fun setData(ID: Int) {
        id = ID
        val dataInfo = arrayOf(SubjectTitle,StartTimeText,EndTimeText,ContentText)
        SubjectInfo.add(dataInfo)



    }

    fun setTitle(Title:String){
        SubjectInfo!![id]!![0] = Title
    }
    fun setContent(Content:String){
        SubjectInfo!![id]!![3] = Content
    }

}
