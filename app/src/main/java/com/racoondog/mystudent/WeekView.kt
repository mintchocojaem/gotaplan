package com.racoondog.mystudent

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.weekview.view.*
import me.grantland.widget.AutofitTextView
import kotlin.properties.Delegates


class WeekView : ConstraintLayout{

    private val realm = Realm.getDefaultInstance()

    var dayFlag = 0

    private val cnxt = context as MainActivity
    private val dm:DisplayMetrics = context.resources.displayMetrics
    private val dmWidth:Int = dm.widthPixels
    private  var cellHeight = 0

    constructor(context: Context) : super(context, null) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        initView()
    }

    private fun initView() {
        val inflaterService: String = Context.LAYOUT_INFLATER_SERVICE
        val inflater: LayoutInflater = context.getSystemService(inflaterService) as LayoutInflater
        val view = inflater.inflate(R.layout.weekview, this, false)
        addView(view)

    }

    fun drawSchedule(day_flag: Int, start_time: Int, end_time: Int) {

        var day = mutableListOf<String>()
        val dayList = listOf("월","화","수","목","금","토","일")
        // 마지막 요일의 선택에 따라 배열이 추가됨

        var period =
            mutableListOf<String>() //원래는 교시부분이었으나 기획자의 변경에 따라 시간으로 표시되는 배열 ex -> 오후 1시 2시 3시

        val colorList = realm.where(DataModel::class.java).findFirst()!!.scheduleColor

        //마지막 요일을 선택하고 그에 따라 day_flag 값을 반환 하고 day 배열에 추가
        for(i in 0 until day_flag){
            day.add("${dayList[i]}")
        }

        var timeFlag = 0 //AmPm 구분을 위한 flag 선언

        for (i in start_time until end_time) {  // 24시 형식을 오전과 오후를 구분 하기위한 논리연산
            if (i == start_time && i < 10) {
                period.add("$i\n 오전 ")
            } else if (i == start_time && i < 12 && i >= 10) {
                period.add("$i\n 오전 ")
            } else if (i == start_time && i > 12) {
                period.add("${i - 12}\n 오후 ")
                timeFlag = 1
            } else if (i == 13) {
                period.add("${i - 12}\n 오후 ")
                timeFlag = 1
            } else if (timeFlag == 1) {
                period.add("${i - 12}")
            } else {
                period.add("$i")
            }
        }

        val layout = TableLayout(cnxt)  //전체 TableRow를 담기위한 Tablelayout

        val dayRow = TableRow(cnxt) //initday를 담기위한 TableRow

        layout.layoutParams = TableLayout.LayoutParams(
            TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT
        )

        dayRow.layoutParams = TableLayout.LayoutParams(
            TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT
        )

        Day_Line.setBackgroundColor(colorList)// 가이드 라인 색

        for (i in 0 until  day.size) {


            
            val dayText = TextView(cnxt) // 요일을 나타내는 부분 ex -> 월 화 수 목
            dayText.gravity = Gravity.CENTER

            dayText.setBackgroundColor(colorList) //day_bar color
            dayText.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
            ).apply {
                dayText.text = day[i]
                weight = 3f
            }
            dayRow.addView(dayText)

        }

        day_line.addView(dayRow)

        for (i in 0 until period.size) {


            val timeRow = TableRow(cnxt) // const_init을 담기위한 TableRow
            timeRow.layoutParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT
            ).apply {
                weight = 1f
            }
            timeRow.setBackgroundResource(R.color.White_bg)// 시간 라인 background color

            val constInit = ConstraintLayout(cnxt) //initperiod를 담기위한 Constraintlayout 부분
            constInit.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT
            ).apply {
                weight - 1f
            }

            val initPeriod = TextView(cnxt) // 원래는 교시였으나 기획자의 변경사항에 따라 시간으로 치환된 부분 ex-> 1 2 3 4
            initPeriod.gravity = Gravity.CENTER
            initPeriod.layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
                leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
                //verticalBias = 0.2f
                //time 사용시 period 레이아웃 영역 활성화
            }
            initPeriod.text = period[i]
            initPeriod.textSize = 13f

            initPeriod.setBackgroundResource(R.color.White_bg) // 시간 라인 텍스트 background color
            /*
                val inittime = TextView(this) // 이 부분은 원래 시간이 부분이었으나 기확자의 지시에 따라 initperiod가 시간으로 대체됨 ex-> 오전 8:00시 9:00시
                inittime.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT).apply {
                    inittime.text = time[i]
                    inittime.textSize = 10f
                    topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                    rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
                    leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
                    verticalBias = 0.8f
                }

                const_init.addView(inittime)
                 */ //time 레이아웃 세팅 영역 ()

            constInit.addView(initPeriod)
            timeRow.addView(constInit)

            for (j in 0 until day.size) {

                val timeText = AutofitTextView(cnxt) // 각 시간표 일정이 들어가는 공백 부분
                val tag: String = day[j] + i
                timeText.tag = tag
                timeText.setBackgroundResource(R.drawable.cell_shape)
                timeText.setMinTextSize(10)

                cellHeight = (dmWidth - 30)/day_flag

                // timeText lyaout 설정부분
                timeText.layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                ).apply {
                    width = 0
                    height = cellHeight
                    weight = 3f

                }
                timeRow.addView(timeText)
            }
            layout.addView(timeRow)
        }

        scheduleview.addView(layout) //activity_main 의 스크롤 뷰에 추가

        //시간표위 레이아웃을 그리는 함수
        for (i in 0 until day.size) {

            val tag: Int = i + 1

            val subjectLine = ConstraintLayout(cnxt)

            subjectLine.layoutParams = LinearLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            ).apply {
                width = 0
                weight = 1f
                subjectLine.tag = tag
                subjectLine.setPadding(1,0,1,1)
            }

            canvas.bringToFront()
            canvas.addView(subjectLine)

        }

    }

    fun createSubject(StartHour:Int,StartMinute:Int,EndHour:Int,EndMinute:Int,DayFlag:Int,intentStartTime:Int,id:Int){

        val subjectHeight = (EndHour - StartHour) * cellHeight + (EndMinute - StartMinute) * cellHeight/60
        val subjectMargin = (StartHour - intentStartTime) * cellHeight + StartMinute * cellHeight/60
        val subject = ConstraintLayout(cnxt)
        val titleText = TextView(cnxt)
        val ID = id

        titleText.apply {
            titleText.tag = "title$ID"
        }

        var smallTitle = ""
        var data:RealmResults<SubjectBox> = realm.where<SubjectBox>(SubjectBox::class.java)
            .equalTo("id",ID)
            .findAll()

        if(data.get(0)!!.title.length > 10){

            smallTitle = data.get(0)!!.title.toString().substring(0,10)+".."

        }
        else{
            smallTitle = data.get(0)!!.title.toString()
        }

        titleText.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        if( EndHour - StartHour > 1 ){
            titleText.apply {
                maxLines = 2
                textSize = 12f
                text = smallTitle}
        } else{
            titleText.apply {
                maxLines = 1
                textSize = 12f
                text = smallTitle}
        }


        subject.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            width = ConstraintLayout.LayoutParams.PARENT_ID
            height = subjectHeight.toInt()
            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
            leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
            verticalBias = 0f
            topMargin = subjectMargin.toInt()
            subject.setBackgroundResource(R.color.colorAccent)
            subject.setPadding(5,7,5,7)
            subject.id = ID
            subject.setOnClickListener{
                WeekViewData.ID = ID
                dayFlag = DayFlag

                val intentSubjectDetail = Intent (cnxt, SubjectDetail::class.java)
                cnxt.startActivityForResult(intentSubjectDetail,103)
            }

        }
        subject.addView(titleText)
        findViewWithTag<ConstraintLayout>(DayFlag).addView(subject)

    }

    fun deleteSubject(id:Int){
        Toast.makeText(cnxt, "삭제되었습니다", Toast.LENGTH_SHORT).show()
        findViewWithTag<ConstraintLayout>(dayFlag).removeView(findViewById(id))
    }
    fun createID(Min:Int, Max:Int):Int{
        var result = 0
        out@ for(ID in Min .. Max){

            val data = realm.where<SubjectBox>(SubjectBox::class.java).equalTo("id",ID).findFirst()

            if ( data == null ){
                result = ID

                break@out
            }
            else continue
        }
        return result
    }

}