package com.racoondog.mystudent

import android.app.Activity
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import kotlinx.android.synthetic.main.subject_detail.*
import kotlinx.android.synthetic.main.time_picker.*


class SubjectDetail : AppCompatActivity() {

    private val realm = Realm.getDefaultInstance()
    private var subjectColorCode = 0
    private var dayFlag = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.subject_detail)

        var saveEditFlag = false

        val subjectData: RealmResults<SubjectData> = realm.where<SubjectData>(SubjectData::class.java)
            .equalTo("id",WeekView.ID)
            .findAll()
        val data = subjectData[0]!!

        subjectColorCode = data.subjectColor
        dayFlag = data.dayFlag

        subject_title.setText(data.title)
        subject_memo.setText(data.content)
        themeChange(data.subjectColor)

        if(subjectData[0]!!.lessonOnOff) lesson_bar.visibility = View.VISIBLE
        else lesson_bar.visibility = View.GONE

        //disableView(subject_detail_time_picker)

        studentName_text.setText(data.studentName)
        studentBirth_text.setText(data.studentBirth)
        studentPhone_text.setText(data.studentPhoneNumber)
        lessonCost_text.setText(data.lessonCost)
        lessonCycle_text.setText(data.lessonCycle)


        subject_detail_quit_btn.setOnClickListener {
               setResult(Activity.RESULT_OK, intent)
               finish()
        }

        subject_detail_save_btn.setOnClickListener {

            if (!saveEditFlag) {

                subject_detail_save_btn.text = "저장"

                subject_title.isEnabled = true
                subject_memo.isEnabled = true

                studentName_text.isEnabled = true
                studentBirth_text.isEnabled = true
                studentPhone_text.isEnabled = true
                lessonCost_text.isEnabled = true
                lessonCycle_text.isEnabled = true

                //enableView(subject_detail_time_picker)


                lesson_cycle_minus_btn.isEnabled = false
                lesson_cycle_plus_btn.isEnabled = false

                saveEditFlag = true

            } else {
                subject_detail_save_btn.text = "수정"

                subject_title.isEnabled = false
                subject_memo.isEnabled = false

                studentName_text.isEnabled = false
                studentBirth_text.isEnabled = false
                studentPhone_text.isEnabled = false
                lessonCost_text.isEnabled = false
                lessonCycle_text.isEnabled = false

                //disableView(subject_detail_time_picker)
                //TimePicker(this).changedTextColor(start_picker_layout, true)
                //TimePicker(this).changedTextColor(end_picker_layout, true)
                //subject_detail_time_picker.hidePicker()
                //subject_detail_view.subjectDetailSave()
                //themeChange(data.subjectColor)

                lesson_cycle_minus_btn.isEnabled = true
                lesson_cycle_plus_btn.isEnabled = true


                realm.beginTransaction()
                data.title = subject_title.text.toString()
                data.content = subject_memo.text.toString()
                data.studentName = studentName_text.text.toString()
                data.studentBirth = studentBirth_text.text.toString()
                data.studentPhoneNumber = studentPhone_text.text.toString()
                data.lessonCost = lessonCost_text.text.toString()
                if(lessonCycle_text.text.toString() == ""){ data.lessonCycle = "0"
                    lessonCycle_text.setText("0")
                }
                else data.lessonCycle = lessonCycle_text.text.toString()
                realm.commitTransaction()

                saveEditFlag = false
            }
        }

        lesson_cycle_plus_btn.setOnClickListener {
            lessonCycle_text.setText((data.lessonCycle.toInt()+1).toString())
            realm.beginTransaction()
            data.lessonCycle = lessonCycle_text.text.toString()
            realm.commitTransaction()
        }
        lesson_cycle_minus_btn.setOnClickListener {

            if(data.lessonCycle.toInt() == 0) lessonCycle_text.setText((data.lessonCycle.toInt()).toString())
            else lessonCycle_text.setText((data.lessonCycle.toInt()-1).toString())
            realm.beginTransaction()
            data.lessonCycle = lessonCycle_text.text.toString()
            realm.commitTransaction()
        }

        val scheduleData = realm.where(ScheduleData::class.java).findFirst()!!

        if (scheduleData.scheduleDayFlag == 6){
            saturday_button.visibility = View.VISIBLE
        }
        else if (scheduleData.scheduleDayFlag == 7){
            saturday_button.visibility = View.VISIBLE
            sunday_button.visibility = View.VISIBLE
        }

        when(dayFlag){
            1 -> monday_button.isChecked = true
            2 -> tuesday_button.isChecked = true
            3 -> wednesday_button.isChecked = true
            4 -> thursday_button.isChecked = true
            5 -> friday_button.isChecked = true
            6 -> saturday_button.isChecked = true
            7 -> sunday_button.isChecked = true
        }

        monday_button.setOnClickListener {
            dayFlag = 1
        }
        tuesday_button.setOnClickListener {
            dayFlag = 2
        }
        wednesday_button.setOnClickListener {
            dayFlag = 3
        }
        thursday_button.setOnClickListener {
            dayFlag = 4
        }
        friday_button.setOnClickListener {
            dayFlag = 5
        }
        saturday_button.setOnClickListener {
            dayFlag = 6
        }
        sunday_button.setOnClickListener {
            dayFlag = 7
        }

        subject_detail_color_picker_bar.setOnClickListener {
            val dialog = ColorPickerDialog(this, object :
                ColorPickerDialog.ICustomDialogEventListener {
                override fun customDialogEvent(colorCode: Int) {
                    // Do something with the value here, e.g. set a variable in the calling activity
                    subjectColorCode = colorCode
                    subject_detail_color_picker_btn.backgroundTintList = ColorStateList.valueOf(subjectColorCode)

                }
            })
            dialog.show()
        }
        subject_detail_color_picker_btn.setOnClickListener {
            val dialog = ColorPickerDialog(this, object :
                ColorPickerDialog.ICustomDialogEventListener {
                override fun customDialogEvent(colorCode: Int) {
                    // Do something with the value here, e.g. set a variable in the calling activity
                    subjectColorCode = colorCode
                    subject_detail_color_picker_btn.backgroundTintList = ColorStateList.valueOf(subjectColorCode)

                }
            })
            dialog.show()
        }


        subject_detail_color_picker_btn.backgroundTintList = ColorStateList.valueOf(subjectColorCode)

        subject_detail_time_picker.subjectPicker(scheduleData.scheduleStartHour,scheduleData.scheduleEndHour)
        subject_detail_time_picker.displayTime(data.startHour,data.startMinute.toInt(),data.endHour,data.endMinute.toInt())

    }

    private fun disableView(v:View){
        v.setOnTouchListener(OnTouchListener { _, _ -> true })
        if (v is ViewGroup) {
            for (i in 0 until v.childCount) {
                val child: View = v.getChildAt(i)
                if (child is ViewGroup) {
                    disableView(child)
                } else {
                    child.isEnabled = false
                }
            }
        }
    }
    private fun enableView(v:View){
        v.setOnTouchListener(OnTouchListener { _, _ -> false })
        if (v is ViewGroup) {
            for (i in 0 until v.childCount) {
                val child: View = v.getChildAt(i)
                if (child is ViewGroup) {
                    enableView(child)
                } else {
                    child.isEnabled = true
                }
            }
        }
    }
    override fun onBackPressed() {

        if(subject_detail_time_picker.ifPickerIsHidden())
        else super.onBackPressed()
    }

    private fun themeChange(colorCode:Int){

        window.statusBarColor = colorCode
        lesson_toolbar.setBackgroundColor(colorCode)

    }

    fun subjectDetailSave(){

        val subjectData: RealmResults<SubjectData> = realm.where<SubjectData>(SubjectData::class.java)
            .equalTo("id",WeekView.ID)
            .findAll()
        val data = subjectData[0]!!

        if (end_hour.value == start_hour.value){
            if(end_minute.displayedValues[end_minute.value].toInt() < start_minute.displayedValues[start_minute.value].toInt()){
                Toast.makeText(this, "시작 시각이 종료 시각보다 클 수 없습니다.", Toast.LENGTH_SHORT).show()
            }else if(start_minute.value == end_minute.value){
                Toast.makeText(this, "시작 시각이 종료 시각과 같을 수 없습니다.", Toast.LENGTH_SHORT).show()
            } else if((end_minute.displayedValues[end_minute.value].toInt()  - start_minute.displayedValues[start_minute.value].toInt() < 30)) {
                Toast.makeText(this, "각 과목의 최소 시간은 30분입니다.", Toast.LENGTH_SHORT).show()
            }else createSubject(dayFlag)

        } else if(end_hour.value - start_hour.value == 1){

            if(end_minute.displayedValues[end_minute.value].toInt()+(60-start_minute.displayedValues[start_minute.value].toInt()) < 30) {
                Toast.makeText(this, "각 과목의 최소 시간은 30분입니다.", Toast.LENGTH_SHORT).show()
            } else createSubject(dayFlag)

        }
        else if (end_hour.value < start_hour.value){
            Toast.makeText(this, "시작 시각이 종료 시각보다 클 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
        else {
            createSubject(dayFlag)
        }

        realm.beginTransaction()
        data.subjectColor = subjectColorCode
        realm.commitTransaction()

    }

    private fun createSubject(dayFlag: Int){

        val subjectData: RealmResults<SubjectData> = realm.where<SubjectData>(SubjectData::class.java)
            .equalTo("id",WeekView.ID)
            .findAll()
        val data = subjectData[0]!!

        if(checkTime(dayFlag)){
            Toast.makeText(this,"해당 시간에 다른 과목이 존재합니다.",Toast.LENGTH_SHORT).show()
        } else {
            realm.beginTransaction()
            data.startHour = start_hour.value
            data.startMinute = start_minute.displayedValues[start_minute.value].toString()
            data.endHour = end_hour.value
            data.endMinute = end_minute.displayedValues[end_minute.value].toString()
            data.dayFlag = dayFlag
            realm.commitTransaction()
            Toast.makeText(this,"시간이 변경되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkTime(dayFlag:Int):Boolean{

        var subjectData: RealmResults<SubjectData> =
            realm.where<SubjectData>(SubjectData::class.java)
                .equalTo("dayFlag", dayFlag)
                .notEqualTo("id",WeekView.ID)
                .findAll()
        val data = subjectData.sort("startHour", Sort.ASCENDING)

        val pickerTime = arrayListOf<Double>()

        pickerTime.add(start_hour.value.toDouble() + (start_minute.displayedValues[start_minute.value].toDouble() / 100))
        pickerTime.add(end_hour.value.toDouble() + (end_minute.displayedValues[end_minute.value].toDouble() / 100))

        val checkTime = arrayListOf<Boolean>()

        if(data.size != 0){

            for ( i in data.indices){

                val subjectTime = arrayListOf<Double>()

                subjectTime.add(data[i]!!.startHour.toDouble()+ (data[i]!!.startMinute.toDouble() / 100))
                subjectTime.add(data[i]!!.endHour.toDouble()+ (data[i]!!.endMinute.toDouble() / 100))

                var checkFlag = when{

                    pickerTime[0] >= subjectTime[1] -> true
                    pickerTime[0] < subjectTime[0] -> pickerTime[1] <= subjectTime[0]
                    else -> false

                }

                checkTime.add(checkFlag)

            }

        }else  checkTime.add(true)

        return checkTime.contains(element = false) // checkTime = true -> 시간표 겹침

    }

}