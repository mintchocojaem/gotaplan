package com.racoondog.mystudent

/*object ScheduleData{

   var Title:String? = null
   var ScheduleDayFlag:Int? = null
   var ScheduleStartHour:Int? = null
   var ScheduleEndHour:Int? = null

}
 */
object WeekViewData {
    var ID: Int = 0 //weekview subject 클릭시 id 값을 가리키는 포인터
        //var nextID: Int = 0 // 다음으로 만들어질 weekview의 id 값을 결정하는 변수
}
/*
object SubjectData {

    var SubjectInfo = mutableListOf<Array<Any>?>()
    val LessonInfo = mutableListOf<Any>()

    var id: Int = 0

    var StartHour: Int = 0
    var StartMinute: Int = 0

    var EndHour: Int = 0
    var EndMinute: Int = 0

    lateinit var TitleText: String
    lateinit var ContentText: String
    lateinit var TimeText: String


    fun getData(ID: Int): Any {
        id = ID
        return SubjectInfo[ID]!!.contentDeepToString()

    }

    fun setData(ID: Int) {

        id = ID
        val dataInfo = arrayOf(id, StartHour, StartMinute, EndHour, EndMinute, TitleText,
            ContentText, TimeText)

        SubjectInfo.add(dataInfo)



    }



    fun setTitle(Title:String){
        SubjectInfo!![id]!![6] = Title
    }
    fun setContent(Content:String){
        SubjectInfo!![id]!![8] = Content
    }



}


 */
