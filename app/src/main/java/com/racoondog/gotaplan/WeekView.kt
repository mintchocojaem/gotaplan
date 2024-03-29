package com.racoondog.gotaplan

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.*
import android.view.View.GONE
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat.animate
import androidx.customview.widget.ViewDragHelper
import com.google.common.reflect.TypeToken
import com.google.gson.GsonBuilder
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import io.realm.mongodb.App
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.schedule_title_dialog.*
import kotlinx.android.synthetic.main.small_schedule_widget.view.*
import kotlinx.android.synthetic.main.weekview.view.*
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.roundToInt


class WeekView : ConstraintLayout{

    companion object {
        var ID: Int = 0 //weekView subject 클릭시 id 값을 가리키는 포인터
    }

    private val realm = Realm.getDefaultInstance()

    var dayFlag = 0

    private val cnxt = context as MainActivity

    private var cellHeight= 0
    private var cellWidth = 0

    private var screenHeight = 0

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

        val data = realm.where(ScheduleData::class.java).equalTo("id", MainActivity.scheduleID).findFirst()!!
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val disPlay = wm.defaultDisplay
        val size = Point()
        disPlay.getSize(size)
        val w = size.x
        val h = size.y

        screenHeight = h

        cellWidth = (w-(w/7))/day_flag // (w/7)부분이 initPeriod width를 결정
        cellHeight = (w-(w/7))/5

        val day = mutableListOf<String>()
        val dayList = listOf(
            resources.getString(R.string.monday),
            resources.getString(R.string.tuesday),
            resources.getString(R.string.wednesday),
            resources.getString(R.string.thursday),
            resources.getString(R.string.friday),
            resources.getString(R.string.saturday),
            resources.getString(
                R.string.sunday
            )
        )
        // 마지막 요일의 선택에 따라 배열이 추가됨

        val period =
            mutableListOf<String>() //원래는 교시부분이었으나 기획자의 변경에 따라 시간으로 표시되는 배열 ex -> 오후 1시 2시 3시

        //마지막 요일을 선택하고 그에 따라 day_flag 값을 반환 하고 day 배열에 추가
        for(i in 0 until day_flag){
            day.add(dayList[i])
        }

        var timeFlag = 0 //AmPm 구분을 위한 flag 선언

        for (i in start_time until end_time) {  // 24시 형식을 오전과 오후를 구분 하기위한 논리연산
            if (i == start_time && i < 10) {
                period.add("$i\n ${resources.getString(R.string.am)} ")
            } else if (i == start_time && i < 12 && i >= 10) {
                period.add("$i\n ${resources.getString(R.string.am)} ")
            } else if (i == start_time && i > 12) {
                period.add("${i - 12}\n ${resources.getString(R.string.pm)} ")
                timeFlag = 1
            } else if (i == 13) {
                period.add("${i - 12}\n ${resources.getString(R.string.pm)} ")
                timeFlag = 1
            } else if (timeFlag == 1) {
                period.add("${i - 12}")
            }
            else {
                period.add("$i")
            }
        }



        val layout = TableLayout(cnxt)  //전체 TableRow 를 담기위한 TableLayout

        val dayRow = TableRow(cnxt) //initDay 를 담기위한 TableRow

        layout.layoutParams = TableLayout.LayoutParams(
            TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT
        )

        dayRow.layoutParams = TableLayout.LayoutParams(
            TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT
        )
        dayLine.setBackgroundResource(R.color.whiteColor)// 가이드 라인 색
        dayLine.setPadding((w - (cellWidth * day_flag)), 0, 0, 0)

        for (i in 0 until  day.size) {


            
            val dayText = TextView(cnxt) // 요일을 나타내는 부분 ex -> 월 화 수 목
            dayText.gravity = Gravity.CENTER
            dayText.setBackgroundResource(R.color.whiteColor) //day_bar color
            dayText.text = day[i]
            dayText.textSize = 14f
            dayText.setTextColor(ContextCompat.getColor(context, R.color.darkColor))//day_bar text color
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


            val timeRow = TableRow(cnxt) // const_init 을 담기위한 TableRow
            timeRow.layoutParams = TableLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT
            ).apply {

            }
            timeRow.setBackgroundResource(R.drawable.cell_shape) //시간 라인 background color

            val constInit = ConstraintLayout(cnxt) //initPeriod 를 담기위한 ConstraintLayout 부분
            constInit.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT
            ).apply {
                width = (w-(cellWidth*day_flag))
            }
            constInit.tag = "constInit"


            val initPeriod = TextView(cnxt) // 원래는 교시였으나 기획자의 변경사항에 따라 시간으로 치환된 부분 ex-> 1 2 3 4
            initPeriod.gravity = Gravity.CENTER
            initPeriod.layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            ).apply {
                topToTop = LayoutParams.PARENT_ID
                bottomToBottom = LayoutParams.PARENT_ID
                rightToRight = LayoutParams.PARENT_ID
                leftToLeft = LayoutParams.PARENT_ID
                //verticalBias = 0.2f
                //time 사용시 period 레이아웃 영역 활성화
            }
            initPeriod.tag = "iP$i"
            initPeriod.text = period[i]
            initPeriod.textSize = 12f
            initPeriod.setTextColor(ContextCompat.getColor(context, R.color.darkColor))

            /*
            val initTime = TextView(this) // 이 부분은 원래 시간이 부분이었으나 기확자의 지시에 따라 initPeriod 가 시간으로 대체됨 ex-> 오전 8:00시 9:00시
            initTime.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT).apply {
                initTime.text = time[i]
                initTime.textSize = 10f
                topToTop = LayoutParams.PARENT_ID
                bottomToBottom = LayoutParams.PARENT_ID
                rightToRight = LayoutParams.PARENT_ID
                leftToLeft = LayoutParams.PARENT_ID
                verticalBias = 0.8f
            }

            const_init.addView(initTime)
            */ //time 레이아웃 세팅 영역 ()

            constInit.addView(initPeriod)
            timeRow.addView(constInit)

            for (j in 1 .. day.size) {

                val timeText = TextView(cnxt) // 각 시간표 일정이 들어가는 공백 부분
                val tag = "day$j$i"
                timeText.tag = tag
                if(data.interval){
                    timeText.setBackgroundResource(R.drawable.cell_half_shape)
                }else{
                    timeText.setBackgroundResource(R.drawable.cell_shape)
                }
                timeText.setOnLongClickListener {
                    val subjectIntent = Intent(cnxt, CreateSubject::class.java)
                    subjectIntent.flags = (Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    subjectIntent.putExtra("start_time", start_time + i)
                    subjectIntent.putExtra("end_time", end_time)
                    subjectIntent.putExtra("day_flag", j)
                    cnxt.startActivityForResult(subjectIntent, 102)
                    true }
                // timeText layout 설정부분
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
        canvas.layoutParams = LayoutParams(LayoutParams.PARENT_ID, LayoutParams.PARENT_ID).apply{
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



        /*
        val scheduleData = realm.where(ScheduleData::class.java).findFirst()!!

        for(i in 0 until (scheduleData.scheduleEndHour - scheduleData.scheduleStartHour)) {

            val halfLine = ConstraintLayout(context)
            halfLine.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT).apply {
                leftToLeft = LayoutParams.PARENT_ID
                rightToRight = LayoutParams.PARENT_ID
                height = (cellHeight)/2
            }
            halfLine.setBackgroundResource(R.drawable.half_line_shape)
            halfLine.y = (cellHeight*i).toFloat()
            halfLine.visibility = View.GONE
            halfLine.tag = "halfLine$i"
            findViewWithTag<ConstraintLayout>("canvas").addView(halfLine)
        }

         */

    }

    @SuppressLint("ClickableViewAccessibility")
    fun createSubject(
        StartHour: Int, StartMinute: Int, EndHour: Int, EndMinute: Int, DayFlag: Int, intentStartTime: Int, id: Int, colorCode: Int) {

        val subjectWidth = cellWidth
        val subjectHeight = (EndHour - StartHour) * cellHeight + (EndMinute - StartMinute) * cellHeight / 60
        val subjectMargin =
            (StartHour - intentStartTime) * cellHeight + StartMinute * cellHeight / 60
        val subject = ConstraintLayout(context)
        val titleText = TextView(context)
        val colorImage = ConstraintLayout(context)

        val subjectData = realm.where<SubjectData>(SubjectData::class.java)
            .equalTo("id", id)
            .findAll()

        val scheduleData = realm.where(ScheduleData::class.java).equalTo(
            "id",
            MainActivity.scheduleID
        ).findFirst()!!


        titleText.apply {
            text = subjectData[0]!!.title.replace(" ", "\u00A0")
            tag = "title$id"
            textSize = 14f
            maxLines = 1
            typeface = AppStorage(context).applyFontStyle(context)

        }
        var mCount = EndMinute - StartMinute
        for (i in 0 until (EndHour - StartHour)) {
            mCount += 60
        }
        for (i in 1 until ((mCount)/ 20)) {
            titleText.maxLines++
        }


        colorImage.layoutParams = LayoutParams(
            LayoutParams.MATCH_CONSTRAINT,
            LayoutParams.MATCH_CONSTRAINT
        ).apply {
            topToTop = LayoutParams.PARENT_ID
            bottomToBottom = LayoutParams.PARENT_ID
            rightToRight = LayoutParams.PARENT_ID
            leftToLeft = LayoutParams.PARENT_ID
        }
        colorImage.setPadding(4, 5, 4, 5)
        colorImage.setBackgroundResource(R.drawable.round_subject_bg_layout)
        colorImage.backgroundTintList = ColorStateList.valueOf(colorCode)


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

            subject.bringToFront()

            /*
            for(i in 0 until (scheduleData.scheduleEndHour - scheduleData.scheduleStartHour)) {

                findViewWithTag<ConstraintLayout>("halfLine$i")?.visibility = View.VISIBLE

            }
             */
            var subjectDayFlag = subjectData[0]!!.dayFlag

            val scheduleStartHour = scheduleData.startHour
            var subjectStartHour = subjectData[0]!!.startHour
            var subjectStartMinute = 0
            var subjectEndHour: Int
            var subjectEndMinute: Int

            val oldStartMinute = subjectData[0]!!.startMinute

            subject.setOnTouchListener { v, event ->
                v.parent.requestDisallowInterceptTouchEvent(true)
                when (event.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_UP -> v.parent.requestDisallowInterceptTouchEvent(false)
                }

                val parentWidth = (v.parent as ViewGroup).width // 부모 View 의 Width
                val parentHeight = (v.parent as ViewGroup).height // 부모 View 의 Height

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> { // 뷰 누름

                        //val oldXvalue = event.x
                        //val oldYvalue = event.y

                    }
                    MotionEvent.ACTION_MOVE -> { // 뷰 이동 중

                        v.x = v.x + event.x - v.width / 2
                        v.y = v.y + event.y - v.height / 2

                        if (v.x <= 0) v.x = 0f else if (v.x > parentWidth) v.x =
                            (parentWidth - v.width).toFloat()
                        if (v.y <= 0) v.y = 0f else if (v.y + v.height > parentHeight) v.y =
                            (parentHeight - v.height).toFloat()

                        for (i in 0 until scheduleData.dayFlag) {
                            if (v.x + (cellWidth / 2) > (cellWidth * i) && v.x + (cellWidth / 2) <= (cellWidth * (i + 1))) {
                                v.x = (cellWidth * i).toFloat()
                                subjectDayFlag = i + 1
                            }
                        }

                        val h = IntArray(2)
                        v.getLocationOnScreen(h)
                        when (h[1]) {
                            in 0..(screenHeight / 3) -> {

                                scrollView.scrollTo(0, scrollView.scrollY - 20)

                            }
                            in ((screenHeight / 3) * 2)..screenHeight -> {


                                scrollView.scrollTo(0, scrollView.scrollY + 20)

                            }
                        }

                        //scrollView.smoothScrollTo(v.x.toInt(),100)

                    }
                    MotionEvent.ACTION_UP -> { // 뷰에서 손을 뗌

                        if (v.x < 0) v.x = 0f else if (v.x + v.width > parentWidth) v.x =
                            (parentWidth - v.width).toFloat()
                        if (v.y < 0) v.y = 0f else if (v.y + v.height > parentHeight) v.y =
                            (parentHeight - v.height).toFloat()

                        for (i in 0 until (scheduleData.endHour - scheduleData.startHour) * 2) {

                            if (v.y >= (cellHeight * i) / 2 && v.y < (cellHeight * (i + 1)) / 2) {

                                v.y = (cellHeight * i) / 2.toFloat()

                                subjectStartHour = scheduleStartHour + (i / 2)
                                subjectStartMinute = (i % 2) * 30
                            }
                        }

                        subjectEndHour =
                            (v.y + v.height).roundToInt() / cellHeight + scheduleData.startHour
                        subjectEndMinute =
                            subjectData[0]!!.endMinute.toInt() + (subjectStartMinute - oldStartMinute.toInt()) + (v.y).roundToInt() / cellHeight * 60
                        for (i in 1..subjectEndMinute / 60) {
                            subjectEndMinute -= 60

                        }
                        //if(subjectEndMinute == 0)subjectEndHour+=1
                        if (subjectEndMinute < 0) subjectEndMinute += 60
                        //Toast.makeText(context,"$subjectEndHour,$subjectEndMinute",Toast.LENGTH_SHORT).show()
                        //if(v.x == oldX && v.y == oldY) Toast.makeText(context,"변경된 시간이 같음",Toast.LENGTH_SHORT).show()

                        //Toast.makeText(context,"${v.y},$cellHeight",Toast.LENGTH_SHORT).show()

                        applyTime(
                            ID,
                            subjectDayFlag,
                            subjectStartHour,
                            subjectStartMinute,
                            subjectEndHour,
                            subjectEndMinute
                        )
                        /*
                        for(i in 0 until (scheduleData.scheduleEndHour - scheduleData.scheduleStartHour)) {

                            findViewWithTag<ConstraintLayout>("halfLine$i")?.visibility = View.GONE

                        }

                         */
                        //refresh(cnxt.weekView)//checkTime 임시기능?
                        subject.setOnTouchListener(null)

                    }
                }
                true
            }
            true
        }
        colorImage.addView(titleText)
        subject.addView(colorImage)

        findViewWithTag<ConstraintLayout>("canvas").addView(subject)

        val subjectParams = (subject.layoutParams as LayoutParams).apply {
            topToTop = LayoutParams.PARENT_ID
            bottomToBottom = LayoutParams.PARENT_ID
            leftToLeft = LayoutParams.PARENT_ID
            rightToRight = LayoutParams.PARENT_ID
            horizontalBias = 0f
            verticalBias = 0f
        }
        subjectParams.setMargins(((DayFlag - 1) * subjectWidth),subjectMargin,0,0)
        subjectParams.width = subjectWidth
        subjectParams.height = subjectHeight

        subject.setPadding(1, 0, 1, 0)//5,5,5,5
        subject.layoutParams = subjectParams

    }

    private fun applyTime(
        subjectID: Int,
        subjectDayFlag: Int,
        startHour: Int,
        startMinute: Int,
        endHour: Int,
        endMinute: Int
    ){

        val subjectData: RealmResults<SubjectData> = realm.where<SubjectData>(SubjectData::class.java)
                .equalTo("id", subjectID)
                .findAll()

        val data = subjectData[0]!!

        var newStartMinute = startMinute.toString()
        if (startMinute == 0) newStartMinute ="00"
        if(newStartMinute.length <= 1) newStartMinute = "0$newStartMinute"

        var newEndMinute = endMinute.toString()
        if (endMinute == 0) newEndMinute ="00"
        if(newEndMinute.length <= 1) newEndMinute = "0$newEndMinute"

        if (checkTime(subjectDayFlag, startHour, startMinute, endHour, endMinute)){
            Toast.makeText(
                cnxt,
                resources.getString(R.string.subject_exist_already),
                Toast.LENGTH_SHORT
            ).show()
            refresh(cnxt.weekView)
        }else {

            val builder = AlertDialog.Builder(context, R.style.MyDialogTheme).apply {

                val n: String = Locale.getDefault().displayLanguage
                if (n.compareTo("한국어") == 0){
                    this.setMessage(
                        "해당 일정의 시작시간을 \n${dayFlagToText(data.dayFlag)} ${data.startHour}:${data.startMinute}" +
                                " -> ${dayFlagToText(subjectDayFlag)} $startHour:$newStartMinute 으로 바꾸시겠습니까?"
                    )
                }
                else {
                    this.setMessage(
                        "Would you like to change the start time for the schedule from ${
                            dayFlagToText(
                                data.dayFlag
                            )
                        } ${data.startHour}:${data.startMinute}" +
                                " to ${dayFlagToText(subjectDayFlag)} $startHour:$newStartMinute ?"
                    )
                }
            }

                .setTitle(resources.getString(R.string.change_time))

                .setPositiveButton(resources.getString(R.string.dialog_apply)) { _, _ ->

                    realm.beginTransaction()
                    data.dayFlag = subjectDayFlag
                    data.startHour = startHour
                    data.endHour = endHour
                    data.startMinute = newStartMinute
                    data.endMinute = newEndMinute
                    realm.commitTransaction()
                    refresh(cnxt.weekView)

                    Notification.notificationFlag = data.notification
                    Notification(context).deleteAlarm(data.id)
                    Notification(context).setAlarm(
                        data.id,
                        data.dayFlag,
                        data.startHour,
                        data.startMinute,
                        data.notification,
                        data.title
                    )
                    Notification.notificationFlag = -1


                }

                .setNegativeButton(resources.getString(R.string.dialog_cancel)) { _, _ ->
                    refresh(cnxt.weekView)
                }
                .setCancelable(false)

                .show()
            builder.window!!.attributes.apply {
                width = LayoutParams.WRAP_CONTENT
                height = LayoutParams.WRAP_CONTENT}
            builder.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            builder.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                ContextCompat.getColor(
                    cnxt.applicationContext,
                    R.color.colorCancel
                )
            )
            builder.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                ContextCompat.getColor(
                    cnxt.applicationContext,
                    R.color.defaultAccentColor
                )
            )

        }


    }

    private fun dayFlagToText(dayFlag: Int):String{

        var dayFlagText =""

        when(dayFlag){
            1 -> dayFlagText = resources.getString(R.string.monday)
            2 -> dayFlagText = resources.getString(R.string.tuesday)
            3 -> dayFlagText = resources.getString(R.string.wednesday)
            4 -> dayFlagText = resources.getString(R.string.thursday)
            5 -> dayFlagText = resources.getString(R.string.friday)
            6 -> dayFlagText = resources.getString(R.string.saturday)
            7 -> dayFlagText = resources.getString(R.string.sunday)
        }
        return dayFlagText
    }

    fun deleteSubject(id: Int){
        findViewWithTag<ConstraintLayout>("canvas").removeView(findViewById(id))
    }
    fun refresh(view: WeekView = cnxt.weekView){

        val scheduleData = realm.where(ScheduleData::class.java).equalTo("id", MainActivity.scheduleID).findFirst()

        if (scheduleData != null) {

            view.findViewById<ConstraintLayout>(R.id.scheduleView).removeAllViews()
            view.drawSchedule(scheduleData.dayFlag, scheduleData.startHour, scheduleData.endHour)

            view.findViewWithTag<ConstraintLayout>("canvas").removeAllViews()

            val subjectData = scheduleData.subjectData.where().findAll()
            for (data in subjectData) {
                view.createSubject(
                    data.startHour,
                    data.startMinute.toInt(),
                    data.endHour,
                    data.endMinute.toInt(),
                    data.dayFlag,
                    scheduleData.startHour,
                    data.id,
                    data.subjectColor
                )
            }

        }

        updateWidget()

    }

    fun createSubjectID(Min: Int, Max: Int):Int{
        var result = 0
        out@ for(ID in Min .. Max){

            val data = realm.where<SubjectData>(SubjectData::class.java).equalTo("id", ID).findFirst()

            if ( data == null ){
                result = ID

                break@out
            }
            else continue
        }
        return result
    }
    fun createLinkageID(Min: Int, Max: Int):Int{
        var result = 0
        out@ for(ID in Min .. Max){

            val data = realm.where<SubjectData>(SubjectData::class.java).equalTo("linkageID", ID).findFirst()

            if ( data == null ){
                result = ID

                break@out
            }
            else continue
        }
        return result
    }
    private fun checkTime(
        dayFlag: Int,
        startHour: Int,
        startMinute: Int,
        endHour: Int,
        endMinute: Int
    ):Boolean{

        val subjectData = realm.where(ScheduleData::class.java).equalTo(
            "id",
            MainActivity.scheduleID
        ).findFirst()!!
            .subjectData.where()
            .equalTo("dayFlag", dayFlag)
            .notEqualTo("id", ID)
            .findAll()
        val data = subjectData.sort("startHour", Sort.ASCENDING)

        val pickerTime = arrayListOf<Double>()

        pickerTime.add(startHour.toDouble() + (startMinute.toDouble() / 100))
        pickerTime.add(endHour.toDouble() + (endMinute.toDouble() / 100))

        val checkTime = arrayListOf<Boolean>()

        if(data.size != 0){

            for ( i in data.indices){

                val subjectTime = arrayListOf<Double>()

                subjectTime.add(data[i]!!.startHour.toDouble() + (data[i]!!.startMinute.toDouble() / 100))
                subjectTime.add(data[i]!!.endHour.toDouble() + (data[i]!!.endMinute.toDouble() / 100))

                val checkFlag = when{

                    pickerTime[0] >= subjectTime[1] -> true
                    pickerTime[0] < subjectTime[0] -> pickerTime[1] <= subjectTime[0]
                    else -> false

                }

                checkTime.add(checkFlag)

            }

        }else  checkTime.add(true)

        return checkTime.contains(element = false) // checkTime = true -> 시간표 겹침

    }
    fun updateWidget() {
        val cal = Calendar.getInstance()
        var date = 0
        when(cal.get(Calendar.DAY_OF_WEEK)){
            1 -> date = 7
            2 -> date = 1
            3 -> date = 2
            4 -> date = 3
            5 -> date = 4
            6 -> date = 5
            7 -> date = 6
        }
        val realm = Realm.getDefaultInstance()
        /*val data = realm.where(ScheduleData::class.java).findFirst()!!.subjectData.where()
            .equalTo("dayFlag", date)
            .findAll()

         */

        val initScheduleData = realm.where(ScheduleData::class.java).findFirst()!!
        var subjectData = realm.where(ScheduleData::class.java)?.equalTo("id",AppStorage(context).getWidgetScheduleID())
            ?.findFirst()?.subjectData?.where()
            ?.equalTo("dayFlag", date)
            ?.findAll()
        if ( subjectData == null){
            val initSubjectData = initScheduleData.subjectData.where()
                ?.equalTo("dayFlag", date)
                ?.findAll()
            if (initSubjectData != null){
                subjectData = initSubjectData
            }


        }

        if(subjectData != null){
            //AppStorage(context).setWidgetScheduleID(initScheduleData.id)
            val sortedDate = subjectData.sort("startHour", Sort.ASCENDING).sort("endHour", Sort.ASCENDING)
            //Toast.makeText(context,"$date",Toast.LENGTH_LONG).show()
            AppStorage(cnxt).setWidgetSubjectList(sortedDate)
        }



        smallWidgetUpdate()
        largeWidgetUpdate()

    }

    private fun smallWidgetUpdate(){

        val data = realm.where(ScheduleData::class.java)
            .equalTo("id",AppStorage(context).getWidgetScheduleID()).findFirst()
            ?: realm.where(ScheduleData::class.java).findFirst()!!

        AppStorage(context).setWidgetScheduleList(data)

        val appWidgetManager = AppWidgetManager.getInstance(context)

        val smallAppWidgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(
                context,
                SmallScheduleWidget::class.java
            )
        )
        appWidgetManager.notifyAppWidgetViewDataChanged(
            smallAppWidgetIds,
            R.id.small_schedule_widget_listview
        )

        val smallWidget = RemoteViews(context.packageName, R.layout.small_schedule_widget)
        val smallIntent = Intent(context, SmallScheduleWidget::class.java)
        smallIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

        cnxt.sendBroadcast(smallIntent)

        smallWidget.setTextViewText(R.id.small_schedule_widget_title,  data.title)
        appWidgetManager.updateAppWidget(smallAppWidgetIds, smallWidget)


    }

    private fun largeWidgetUpdate(){
        val data = realm.where(ScheduleData::class.java)
            .equalTo("id",AppStorage(context).getWidgetScheduleID()).findFirst()
            ?: realm.where(ScheduleData::class.java).findFirst()!!

        AppStorage(context).setWidgetScheduleList(data)

        val appWidgetManager = AppWidgetManager.getInstance(context)

        val largeAppWidgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(
                context,
                LargeScheduleWidget::class.java
            )
        )

        appWidgetManager.notifyAppWidgetViewDataChanged(
            largeAppWidgetIds,
            R.id.large_schedule_widget_listview
        )
        val largeWidget = RemoteViews(context.packageName, R.layout.large_schedule_widget)
        val largeIntent = Intent(context, LargeScheduleWidget::class.java)
        largeIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

        cnxt.sendBroadcast(largeIntent)
        largeWidget.setTextViewText(R.id.large_schedule_widget_title, data.title)
        appWidgetManager.updateAppWidget(largeAppWidgetIds, largeWidget)

    }

}