package com.racoondog.mystudent

/*object ScheduleData{

   var Title:String? = null
   var ScheduleDayFlag:Int? = null
   var ScheduleStartHour:Int? = null
   var ScheduleEndHour:Int? = null

}
 */

object SubjectData {

    var SubjectInfo = mutableListOf<Array<String>?>()
    val LessonInfo = mutableListOf<Any>()

    lateinit var id: String

    lateinit var StartHour: String
    lateinit var StartMinute: String

    lateinit var EndHour: String
    lateinit var EndMinute: String

    lateinit var SubjectTitle: String
    lateinit var ContentText: String
    lateinit var TimeText: String


    fun getData(ID: String): Any {
        id = ID
        return SubjectInfo[ID.toInt()]!!.contentDeepToString()

    }

    fun setData(ID: String) {

        id = ID
        val dataInfo = arrayOf(SubjectTitle, StartHour,
            StartMinute, EndHour, EndMinute, ContentText, TimeText)

        SubjectInfo.add(dataInfo)



    }

    fun setTitle(Title:String){
        SubjectInfo!![id.toInt()]!![0] = Title
    }
    fun setContent(Content:String){
        SubjectInfo!![id.toInt()]!![3] = Content
    }

}
