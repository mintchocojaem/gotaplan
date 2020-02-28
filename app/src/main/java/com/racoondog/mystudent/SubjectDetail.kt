package com.racoondog.mystudent


import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.subject_detail.*


class SubjectDetail : AppCompatActivity() {

    private val realm = Realm.getDefaultInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.subject_detail)

        val scheduleData = realm.where(ScheduleData::class.java).findFirst()!!
        val subjectData: RealmResults<SubjectData> = realm.where<SubjectData>(SubjectData::class.java)
            .equalTo("id",WeekView.ID)
            .findAll()
        val data = subjectData[0]!!

        init()

        subject_detail_quit_btn.setOnClickListener {
            onBackPressed()
        }

        subject_detail_save_btn.setOnClickListener {

            val dayFlag = data.dayFlag
            val pickerData: RealmResults<SubjectData> = realm.where<SubjectData>(SubjectData::class.java)
                .equalTo("dayFlag",subject_detail_day_picker.dayFlag)
                .notEqualTo("id",WeekView.ID)
                .findAll()
            val nestedTime =  subject_detail_time_picker.nestedTime(pickerData)
            if(!nestedTime) {
                realm.beginTransaction()
                data.dayFlag = subject_detail_day_picker.dayFlag
                data.startHour = subject_detail_time_picker.startHour()
                data.startMinute = subject_detail_time_picker.startMinute()
                data.endHour = subject_detail_time_picker.endHour()
                data.endMinute = subject_detail_time_picker.endMinute()
                data.subjectColor = subject_detail_color_picker.colorCode
                data.title = subject_title.text.toString()
                data.content = subject_memo.text.toString()

                data.studentName = studentName_text.text.toString()
                data.studentBirth = studentBirth_text.text.toString()
                data.studentPhoneNumber = studentPhone_text.text.toString()
                data.lessonCost = lessonCost_text.text.toString()
                data.lessonCycle = lessonCycle_text.text.toString()
                realm.commitTransaction()

                setResult(Activity.RESULT_OK)
                finish()
            }

        }

        lesson_cycle_plus_btn.setOnClickListener {
           lessonCycle_text.setText((lessonCycle_text.text.toString()?.toInt()+1).toString())
        }
        lesson_cycle_minus_btn.setOnClickListener {

            if(lessonCycle_text.text.toString() == "0") {
                lessonCycle_text.setText("0")
            }
            else lessonCycle_text.setText((lessonCycle_text.text.toString()?.toInt()-1).toString())

        }

        subject_detail_color_picker.initColor(data.subjectColor)
        subject_detail_color_picker.setOnClickListener {
            subject_detail_color_picker.colorPick(this,subject_detail_toolbar)
        }
        subject_detail_color_picker.setOnCustomEventListener(object :
            ColorPicker.OnCustomEventListener {
            override fun onEvent() {
                subject_detail_save_btn.visibility = View.VISIBLE
            }
        })

        subject_detail_day_picker.dayPick(scheduleData.scheduleDayFlag,data.dayFlag)
        subject_detail_day_picker.setOnCustomEventListener(object : DayPicker.OnCustomEventListener {
            override fun onEvent() {
                subject_detail_save_btn.visibility = View.VISIBLE
            }
        })

        subject_detail_time_picker.setCustomEventListener(object : TimePicker.OnCustomEventListener {
            override fun onEvent() {
                subject_detail_save_btn.visibility = View.VISIBLE
            }
        })

        subject_detail_time_picker.subjectPicker(scheduleData.scheduleStartHour,scheduleData.scheduleEndHour)
        subject_detail_time_picker.displayTime(data.startHour,data.startMinute.toInt(),data.endHour,data.endMinute.toInt())

    }

    private fun themeChange(colorCode:Int){
        window.statusBarColor = colorCode
        subject_detail_toolbar.setBackgroundColor(colorCode)

    }

    private  fun init(){

        val subjectData: RealmResults<SubjectData> = realm.where<SubjectData>(SubjectData::class.java)
            .equalTo("id",WeekView.ID)
            .findAll()
        val data = subjectData[0]!!

        val textWatcher = object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if(start != after){
                    subject_detail_save_btn.visibility = View.VISIBLE
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(start != before){
                    subject_detail_save_btn.visibility = View.VISIBLE
                }
            }

        }

        subject_title.setText(data.title)
        subject_memo.setText(data.content)

        subject_title.addTextChangedListener(textWatcher)
        subject_memo.addTextChangedListener(textWatcher)

        studentName_text.setText(data.studentName)
        studentBirth_text.setText(data.studentBirth)
        studentPhone_text.setText(data.studentPhoneNumber)
        lessonCost_text.setText(data.lessonCost)
        lessonCycle_text.setText(data.lessonCycle)

        studentName_text.addTextChangedListener(textWatcher)
        studentBirth_text.addTextChangedListener(textWatcher)
        studentPhone_text.addTextChangedListener(textWatcher)
        lessonCost_text.addTextChangedListener(textWatcher)
        lessonCycle_text.addTextChangedListener(textWatcher)

        themeChange(data.subjectColor)

        if(subjectData[0]!!.lessonOnOff) lesson_bar.visibility = View.VISIBLE
        else lesson_bar.visibility = View.GONE
        subject_detail_save_btn.visibility = View.INVISIBLE
    }

}