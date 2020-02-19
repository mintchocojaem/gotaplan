package com.racoondog.mystudent

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Point
import android.util.AttributeSet
import android.view.*
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.weekview.view.*



class WeekView : ConstraintLayout{

    companion object {
        var ID: Int = 0 //weekview subject 클릭시 id 값을 가리키는 포인터
    }

    private val realm = Realm.getDefaultInstance()

    var dayFlag = 0

    val cnxt = context as MainActivity

    private var cellHeight= 0
    private var cellWidth = 0

    private var screen = 0

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

        var wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val disPlay = wm.defaultDisplay
        val size = Point()
        disPlay.getSize(size)
        val w = size.x
        val h = size.y

        screen = h

        cellWidth = (w-75)/day_flag
        cellHeight = (w-75)/day_flag

        var day = mutableListOf<String>()
        val dayList = listOf("월","화","수","목","금","토","일")
        // 마지막 요일의 선택에 따라 배열이 추가됨

        var period =
            mutableListOf<String>() //원래는 교시부분이었으나 기획자의 변경에 따라 시간으로 표시되는 배열 ex -> 오후 1시 2시 3시

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
        dayLine.setBackgroundResource(R.color.whiteColor)// 가이드 라인 색
        dayLine.setPadding((w-(cellWidth*day_flag)),0,0,0)

        for (i in 0 until  day.size) {


            
            val dayText = TextView(cnxt) // 요일을 나타내는 부분 ex -> 월 화 수 목
            dayText.gravity = Gravity.CENTER
            dayText.setBackgroundResource(R.color.whiteColor) //day_bar color
            dayText.text = day[i]
            dayText.textSize = 14f
            dayText.setTextColor(resources.getColor(R.color.darkColor))//day_bar text color
            dayText.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
            ).apply {
                width = cellWidth
            }
            dayRow.addView(dayText)

        }

        day_line.addView(dayRow)

        for (i in 0 until period.size) {


            val timeRow = TableRow(cnxt) // const_init을 담기위한 TableRow
            timeRow.layoutParams = TableLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT
            ).apply {

            }
            timeRow.setBackgroundResource(R.drawable.cell_shape) //시간 라인 background color

            val constInit = ConstraintLayout(cnxt) //initperiod를 담기위한 Constraintlayout 부분
            constInit.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT
            ).apply {
                width = (w-(cellWidth*day_flag))
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
            initPeriod.textSize = 12f
            initPeriod.setTextColor(resources.getColor(R.color.darkColor))

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

                val timeText = TextView(cnxt) // 각 시간표 일정이 들어가는 공백 부분
                val tag: String = day[j] + i
                timeText.tag = tag
                /*
                val random = Random()
                val ColorList:IntArray = context.resources.getIntArray(R.array.subject_color)
                val number = random.nextInt(ColorList.size -1)
                val colorCode = ColorList[number]

                 */

                timeText.setBackgroundResource(R.drawable.cell_shape)
                timeText.textSize = 10f

                // timeText lyaout 설정부분
                timeText.layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                ).apply {
                    width = cellWidth
                    height = cellHeight

                }
                timeRow.addView(timeText)
            }
            layout.addView(timeRow)
        }
        scheduleView.addView(layout) //activity_main 의 스크롤 뷰에 추가

        //시간표위 레이아웃을 그리는 함수

        val canvas = ConstraintLayout(context)
        canvas.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_CONSTRAINT).apply{
            width = w - (w-(cellWidth*day_flag))
            topToTop = LayoutParams.PARENT_ID
            bottomToBottom = LayoutParams.PARENT_ID
            leftToLeft = LayoutParams.PARENT_ID
            rightToRight = LayoutParams.PARENT_ID
            horizontalBias = 1f
        }
        canvas.tag = "canvas"
        scheduleView.bringToFront()
        scheduleView.addView(canvas)

    }

    fun createSubject(StartHour:Int,StartMinute:Int,EndHour:Int,EndMinute:Int,DayFlag:Int,intentStartTime:Int,id:Int,colorCode:Int) {

        val subjectWidth = cellWidth
        val subjectHeight = (EndHour - StartHour) * cellHeight + (EndMinute - StartMinute) * cellHeight / 60
        val subjectMargin =
            (StartHour - intentStartTime) * cellHeight + StartMinute * cellHeight / 60
        val subject = ConstraintLayout(cnxt)
        val titleText = TextView(cnxt)
        val colorImage = ConstraintLayout(context)
        val id = id

        val subjectData: RealmResults<SubjectData> =
            realm.where<SubjectData>(SubjectData::class.java)
                .equalTo("id", id)
                .findAll()


        titleText.apply {
            text = subjectData[0]!!.title.replace(" ", "\u00A0")
            tag = "title$id"
            textSize = 14f
            maxLines = 1

        }

        var emCount = EndMinute
        var smCount = StartMinute
        for (i in 0 until (EndHour - StartHour)) {
            emCount += 60
        }
        for (i in 1 until ((emCount / 20) - (smCount / 20))) {
            titleText.maxLines++
        }

        subject.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            width = subjectWidth
            height = subjectHeight.toInt()
            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
            leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
            leftMargin = ((DayFlag - 1) * subjectWidth)
            horizontalBias = 0f
            verticalBias = 0f
            topMargin = subjectMargin.toInt()

        }

        colorImage.layoutParams = LayoutParams(LayoutParams.MATCH_CONSTRAINT, LayoutParams.MATCH_CONSTRAINT).apply {
            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
            leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
        }
        colorImage.setPadding(4,5,4,5)
        colorImage.setBackgroundResource(R.drawable.round_subject_bg_layout)
        colorImage.backgroundTintList = ColorStateList.valueOf(colorCode)

        subject.setPadding(1, 0, 1, 0)//5,5,5,5
        subject.id = id

        subject.setOnClickListener {
            ID = id
            dayFlag = DayFlag
            val intentSubjectDetail = Intent(cnxt, SubjectDetail::class.java)
            intentSubjectDetail.flags =
                (Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            cnxt.startActivityForResult(intentSubjectDetail, 103)
        }

        subject.setOnLongClickListener {

            ID = id
            dayFlag = DayFlag
            /*
            val dialog = SubjectDetailDialog(context)
            dialog.cnxt = this@WeekView
            dialog.show()

             */

            subject.setOnTouchListener { v, event ->

                v.parent.requestDisallowInterceptTouchEvent(true)
                when (event.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_UP -> v.parent.requestDisallowInterceptTouchEvent(false)
                }

                val parentWidth = (v.parent as ViewGroup).width // 부모 View 의 Width
                val parentHeight = (v.parent as ViewGroup).height // 부모 View 의 Height

                if (event.action === MotionEvent.ACTION_DOWN) { // 뷰 누름

                    val oldXvalue = event.x
                    val oldYvalue = event.y



                } else if (event.action === MotionEvent.ACTION_MOVE) { // 뷰 이동 중

                    v.x = v.x + event.x - v.width / 2
                    v.y = v.y + event.y - v.height / 2

                    if (v.x <= 0) v.x = 0f else if(v.x > parentWidth) v.x =  (parentWidth - v.width).toFloat()
                    if (v.y <= 0) v.y = 0f else if(v.y + v.height > parentHeight) v.y =  (parentHeight- v.height).toFloat()

                    val scheduleData = realm.where(ScheduleData::class.java).findFirst()!!
                    for(i in 0 until scheduleData.scheduleDayFlag){
                        if (v.x > (cellWidth * i) && v.x <= (cellWidth * (i+1))) v.x = (cellWidth * i).toFloat()
                    }

                    val h = IntArray(2)
                    v.getLocationOnScreen(h)
                    when(h[1]){
                        in 0..(screen/3) -> {

                            scrollView.scrollTo(0,scrollView.scrollY-30)

                        }
                        in ((screen/3)*2)..screen -> {


                            scrollView.scrollTo(0,scrollView.scrollY+30)

                        }
                    }
                        //scrollView.smoothScrollTo(v.x.toInt(),100)

                } else if (event.action === MotionEvent.ACTION_UP) { // 뷰에서 손을 뗌


                    if (v.x < 0) {
                        v.x = 0f
                    }
                    else if (v.x + v.width > parentWidth) {
                        v.x = (parentWidth - v.width).toFloat()
                    }
                    if (v.y < 0) {
                        v.y = 0f
                    } else if (v.y + v.height > parentHeight) {
                        v.y = (parentHeight - v.height).toFloat()
                    }
                    subject.setOnTouchListener(null)

                }

                true
            }
            true

        }


        colorImage.addView(titleText)
        subject.addView(colorImage)
        findViewWithTag<ConstraintLayout>("canvas").addView(subject)
    }

    fun deleteSubject(id:Int){
        findViewWithTag<ConstraintLayout>("canvas").removeView(findViewById(id))
    }
    fun refresh(view: WeekView){

        val scheduleData = realm.where(ScheduleData::class.java).findFirst()

        if (scheduleData != null) {

            view.findViewWithTag<ConstraintLayout>("canvas").removeAllViews()


            val subjectData: RealmResults<SubjectData> =
                realm.where<SubjectData>(SubjectData::class.java).findAll()
            for (data in subjectData) {
                view.createSubject(
                    data.startHour.toInt(),
                    data.startMinute.toInt()
                    ,
                    data.endHour.toInt(),
                    data.endMinute.toInt(),
                    data.dayFlag.toInt(),
                    scheduleData.scheduleStartHour!!.toInt(),
                    data.id.toInt(),
                    data.subjectColor
                )
            }

        }

    }
    fun createID(Min:Int, Max:Int):Int{
        var result = 0
        out@ for(ID in Min .. Max){

            val data = realm.where<SubjectData>(SubjectData::class.java).equalTo("id",ID).findFirst()

            if ( data == null ){
                result = ID

                break@out
            }
            else continue
        }
        return result
    }


}