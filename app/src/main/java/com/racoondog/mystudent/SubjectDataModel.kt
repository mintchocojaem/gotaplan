package com.racoondog.mystudent

object SubjectData {

    var SubjectInfo = Array(100, {arrayOf("Title","StartTime","EndTime","Content")})
    val LessonInfo = mutableListOf<Any>()
    var id: Int = 0

    var SubjectTitle:String = ""
    var StartTimeText:String = ""
    var EndTimeText:String = ""
    var ContentText:String = ""





    fun getData(ID: Int): Any {
        id = ID
        return SubjectInfo[ID].contentDeepToString()

    }

    fun setData(ID: Int) {
        id = ID
        val DataInfo = arrayOf(SubjectTitle,StartTimeText,EndTimeText,ContentText)
        SubjectInfo[ID] = DataInfo


    }

    fun setTitle(Title:String){
        SubjectInfo[id][0] = Title
    }

}