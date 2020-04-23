package com.racoondog.gotaplan


import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.create_subject.*
import kotlinx.android.synthetic.main.time_picker.*
import java.util.*


class CreateSubject :AppCompatActivity() {

    private val realm = Realm.getDefaultInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        super.setContentView(R.layout.create_subject)

        val colorList = resources.getIntArray(R.array.subject_color)
        val scheduleData = realm.where(ScheduleData::class.java).findFirst()!!
        val scheduleStartHour = intent?.getIntExtra("start_time",scheduleData.scheduleStartHour)?:scheduleData.scheduleStartHour
        val scheduleEndHour = intent?.getIntExtra("end_time",scheduleData.scheduleEndHour)?:scheduleData.scheduleEndHour
        val scheduleDayFlag = scheduleData.scheduleDayFlag
        val subjectDayFlag = intent.getIntExtra("day_flag",0)

        val dayFlag = mutableListOf(false,false,false,false,false,false,false)

        Notification.notificationFlag = 10
        create_subject_notification.setText(10)

        when(subjectDayFlag){
            1->{
                monday_button.isChecked=true
                dayFlag[0] = true
            }
            2->{tuesday_button.isChecked=true
                dayFlag[1] = true
            }
            3->{wednesday_button.isChecked=true
                dayFlag[2] = true
            }
            4->{thursday_button.isChecked=true
                dayFlag[3] = true
            }
            5->{friday_button.isChecked=true
                dayFlag[4] = true
            }
            6->{saturday_button.isChecked=true
                dayFlag[5] = true
            }
            7->{sunday_button.isChecked=true
                dayFlag[6] = true
            }
        }

        when(scheduleDayFlag){
            6 -> saturday_button.visibility = View.VISIBLE
            7 -> {
                saturday_button.visibility = View.VISIBLE
                sunday_button.visibility = View.VISIBLE
            }
        }

        subject_time_picker.subjectPicker(scheduleStartHour,scheduleEndHour,scroll_view_main)

        randomSubjectColor(colorList)// subject color

        create_subject_notification.setOnClickListener {
            create_subject_notification.showDialog(this)
        }

        createSubject_Button.setOnClickListener{
            if(title_text.text.toString() !="") {
                if(dayFlag.contains(true)) {

                    val flag = mutableListOf<Boolean>()
                    var linkageFlag = 0

                    val context = MainActivity.mContext as MainActivity
                    val id = context.weekView.createLinkageID(1, 128)//다음으로 만들어질 weekView 의 id 값을 결정하는 변수

                    for (i in dayFlag.indices){

                        if(dayFlag[i]){
                            val subjectData: RealmResults<SubjectData> =
                                realm.where<SubjectData>(SubjectData::class.java)
                                    .equalTo("dayFlag", i+1)
                                    .findAll()

                            if(!subject_time_picker.nestedTime(subjectData)){
                                flag.add(true)
                                linkageFlag++
                            }else flag.add(false)

                        }

                    }
                    for (i in dayFlag.indices){
                        if(dayFlag[i]){
                            if(!flag.contains(false)){

                                if(linkageFlag > 1) {

                                    createSubject(i+1,id)
                                }
                                else createSubject(i+1,null)
                            }
                        }
                    }


                    if(!flag.contains(false)){
                        Notification.notificationFlag = -1
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }


                } else{
                    Toast.makeText(this, resources.getString(R.string.choose_subject_day), Toast.LENGTH_SHORT).show()
                }
            }else {
                Toast.makeText(this, resources.getString(R.string.create_subject_toast_enter_title), Toast.LENGTH_SHORT).show()
            }
        }

        lesson_mode.setOnCheckedChangeListener{compoundButton,_ ->

            if (compoundButton.isChecked){

                Toast.makeText(this, "${resources.getString(R.string.lesson)}: On", Toast.LENGTH_SHORT).show()

            } else{

                Toast.makeText(this, "${resources.getString(R.string.lesson)}: Off", Toast.LENGTH_SHORT).show()

            }
        }

        create_subject_color_picker.setOnClickListener {
            create_subject_color_picker.colorPick(this,createSubject_toolbar)
        }

        lesson_help.setOnClickListener {
            val introIntent = Intent(this, IntroActivity::class.java)
            introIntent.action = "LessonModeGuide"
            this.startActivity(introIntent)
        }

        subjectQuit_Button.setOnClickListener {
            setResult(Activity.RESULT_CANCELED,intent)
            finish()
        }

        monday_button.setOnClickListener {

            if(!dayFlag[0]) {
                dayFlag[0] = true
                monday_button.isChecked = true
            }
            else {
                dayFlag[0] = false
                monday_button.isChecked = false
            }
        }
        tuesday_button.setOnClickListener {

            if(!dayFlag[1]) {
                dayFlag[1] = true
                tuesday_button.isChecked = true
            }
            else {
                dayFlag[1]= false
                tuesday_button.isChecked = false
            }
        }
        wednesday_button.setOnClickListener {

            if(!dayFlag[2]) {
                dayFlag[2] = true
                wednesday_button.isChecked = true
            }
            else {
                dayFlag[2] = false
                wednesday_button.isChecked = false
            }
        }
        thursday_button.setOnClickListener {

            if(!dayFlag[3]) {
                dayFlag[3] = true
                thursday_button.isChecked = true
            }
            else {
                dayFlag[3] = false
                thursday_button.isChecked = false
            }
        }
        friday_button.setOnClickListener {

            if(!dayFlag[4]) {
                dayFlag[4] = true
                friday_button.isChecked = true
            }
            else {
                dayFlag[4] = false
                friday_button.isChecked = false
            }
        }
        saturday_button.setOnClickListener {

            if(!dayFlag[5]) {
                dayFlag[5] = true
                saturday_button.isChecked = true
            }
            else {
                dayFlag[5] = false
                saturday_button.isChecked = false
            }
        }
        sunday_button.setOnClickListener {

            if(!dayFlag[6]) {
                dayFlag[6] = true
                sunday_button.isChecked = true
            }
            else {
                dayFlag[6] = false
                sunday_button.isChecked = false
            }
        }


    }

    private fun createSubject(dayFlag: Int,linkageID:Int?){

        val subjectData: RealmResults<SubjectData> =
            realm.where<SubjectData>(SubjectData::class.java)
                .equalTo("dayFlag", dayFlag)
                .findAll()

            if(!subject_time_picker.nestedTime(subjectData)){

                val context = MainActivity.mContext as MainActivity
                val id = context.weekView.createID(0, 128)//다음으로 만들어질 weekView 의 id 값을 결정하는 변수

                realm.beginTransaction()
                val subjectInfo: SubjectData = realm.createObject(SubjectData::class.java)
                subjectInfo.apply {
                    this.id = id
                    this.dayFlag = dayFlag
                    this.startHour = start_hour.value
                    this.startMinute = startText_minute.text.toString()
                    this.endHour = end_hour.value
                    this.endMinute = endText_minute.text.toString()
                    this.title = title_text.text.toString()
                    this.content = create_subject_memo.text?.toString()?:""
                    this.lessonOnOff = lesson_mode.isChecked
                    this.subjectColor = create_subject_color_picker.colorCode
                    this.notification = Notification.notificationFlag
                    this.linkageID = linkageID?:0
                }
                realm.commitTransaction()

                // 현재 지정된 시간으로 알람 시간 설정

               create_subject_notification.setAlarm(subject_time_picker.startHour(),
                   subject_time_picker.startMinute().toInt(), dayFlag,id)


            }

    }


    private fun randomSubjectColor(ColorList: IntArray){

        val random = Random()
        val number = random.nextInt(ColorList.size -1)

        create_subject_color_picker.colorCode = ColorList[number]
        create_subject_color_picker.initColor(create_subject_color_picker.colorCode)
        create_subject_color_picker.changeTheme(this,createSubject_toolbar,create_subject_color_picker.colorCode)

    }


    override fun onBackPressed() {

        if(subject_time_picker.isOpened()) {
            subject_time_picker.clearFocus()
        }
        else super.onBackPressed()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val v: View? = currentFocus
        if (v != null &&
            (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE) &&
            v is EditText &&
            !v.javaClass.name.startsWith("android.webkit.")
        ) {
            val scrcoords = IntArray(2)
            v.getLocationOnScreen(scrcoords)
            val x: Float = ev.rawX + v.getLeft() - scrcoords[0]
            val y: Float = ev.rawY + v.getTop() - scrcoords[1]
            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()) hideKeyboard(
                this
            )
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun hideKeyboard(activity: Activity?) {
        if (!(activity == null || activity.window == null)) {
            val imm =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)
        }
    }


}