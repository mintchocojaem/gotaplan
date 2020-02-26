package com.racoondog.mystudent

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.subject_detail.*
import kotlinx.android.synthetic.main.time_picker.*
import kotlinx.android.synthetic.main.time_picker.view.*


class SubjectDetail : AppCompatActivity() {

    private val realm = Realm.getDefaultInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.subject_detail)

        var saveEditFlag = false

        val subjectData: RealmResults<SubjectData> = realm.where<SubjectData>(SubjectData::class.java)
            .equalTo("id",WeekView.ID)
            .findAll()
        val data = subjectData[0]!!

        disableView(subject_detail_view)


        val startAmPm: String
        val startHour = subjectData[0]!!.startHour
        val startMinute = (subjectData[0]!!.startMinute)
        val textStartHour = when (startHour) {
            in 13..23 -> startHour - 12
            else -> startHour
        }
        startAmPm = when (startHour) {
            in 12..23 -> "오후"
            else -> "오전"
        }

        val endAmPm: String
        val endHour = subjectData[0]!!.endHour
        val endMinute = (subjectData[0]!!.endMinute)
        val textEndHour = when (endHour) {
            in 13..23 -> endHour - 12
            else -> endHour
        }
        endAmPm = when (endHour) {
            in 12..23 -> "오후"
            else -> "오전"
        }

        subject_time.text = "$startAmPm $textStartHour:${startMinute}" + " ~ " + "$endAmPm $textEndHour:${endMinute}"
        subject_title.setText(data.title)
        subject_content.setText(data.content)
        themeChange(data.subjectColor)

        if(subjectData[0]!!.lessonOnOff) lesson_bar.visibility = View.VISIBLE
        else lesson_bar.visibility = View.GONE


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
                subject_content.isEnabled = true

                studentName_text.isEnabled = true
                studentBirth_text.isEnabled = true
                studentPhone_text.isEnabled = true
                lessonCost_text.isEnabled = true
                lessonCycle_text.isEnabled = true

                enableView(subject_detail_view)


                lesson_cycle_minus_btn.isEnabled = false
                lesson_cycle_plus_btn.isEnabled = false

                saveEditFlag = true

            } else {
                subject_detail_save_btn.text = "수정"

                subject_title.isEnabled = false
                subject_content.isEnabled = false

                studentName_text.isEnabled = false
                studentBirth_text.isEnabled = false
                studentPhone_text.isEnabled = false
                lessonCost_text.isEnabled = false
                lessonCycle_text.isEnabled = false

                disableView(subject_detail_view)
                subject_detail_view.hidePicker()

                lesson_cycle_minus_btn.isEnabled = true
                lesson_cycle_plus_btn.isEnabled = true


                realm.beginTransaction()
                data.title = subject_title.text.toString()
                data.content = subject_content.text.toString()
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

        if(time_picker.visibility == View.VISIBLE)subject_detail_view.hidePicker()
        else super.onBackPressed()
    }


    private fun themeChange(colorCode:Int){

        window.statusBarColor = colorCode
        lesson_toolbar.setBackgroundColor(colorCode)

    }


}