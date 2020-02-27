package com.racoondog.mystudent

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import kotlinx.android.synthetic.main.time_dialog.*
import kotlinx.android.synthetic.main.time_picker.*

class TimeDialog:Dialog {

    private val realm = Realm.getDefaultInstance()
    lateinit var cnxt:SubjectDetailDialog

    constructor(context: Context) : super(context)
    constructor(context: Context, themeResId: Int) : super(context, themeResId)
    constructor(
        context: Context,
        cancelable: Boolean,
        cancelListener: DialogInterface.OnCancelListener?
    ) : super(context, cancelable, cancelListener)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layoutParams = WindowManager.LayoutParams()
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        layoutParams.dimAmount = 0.8f
        window!!.attributes = layoutParams
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.time_dialog)

        val scheduleData = realm.where(ScheduleData::class.java).findFirst()!!

        val subjectData: RealmResults<SubjectData> = realm.where<SubjectData>(SubjectData::class.java)
            .equalTo("id",WeekView.ID)
            .findAll()
        val data = subjectData[0]!!

        var dayFlag = data.dayFlag

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

        time_dialog_picker.subjectPicker(scheduleData.scheduleStartHour,scheduleData.scheduleEndHour)
        time_dialog_picker.displayTime(data.startHour,data.startMinute.toInt(),data.endHour,data.endMinute.toInt())

        time_dialog_cancel.setOnClickListener {
            dismiss()
        }

        time_dialog_apply.setOnClickListener {

            if (end_hour.value == start_hour.value){
                if(end_minute.displayedValues[end_minute.value].toInt() < start_minute.displayedValues[start_minute.value].toInt()){
                    Toast.makeText(context, "시작 시각이 종료 시각보다 클 수 없습니다.", Toast.LENGTH_SHORT).show()
                }else if(start_minute.value == end_minute.value){
                    Toast.makeText(context, "시작 시각이 종료 시각과 같을 수 없습니다.", Toast.LENGTH_SHORT).show()
                } else if((end_minute.displayedValues[end_minute.value].toInt()  - start_minute.displayedValues[start_minute.value].toInt() < 30)) {
                    Toast.makeText(context, "각 과목의 최소 시간은 30분입니다.", Toast.LENGTH_SHORT).show()
                }else createSubject(dayFlag)

            } else if(end_hour.value - start_hour.value == 1){

                if(end_minute.displayedValues[end_minute.value].toInt()+(60-start_minute.displayedValues[start_minute.value].toInt()) < 30) {
                    Toast.makeText(context, "각 과목의 최소 시간은 30분입니다.", Toast.LENGTH_SHORT).show()
                } else createSubject(dayFlag)

            }
            else if (end_hour.value < start_hour.value){
                Toast.makeText(context, "시작 시각이 종료 시각보다 클 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
            else {
                createSubject(dayFlag)
            }

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

    }

    private fun createSubject(dayFlag: Int){

        val subjectData: RealmResults<SubjectData> = realm.where<SubjectData>(SubjectData::class.java)
            .equalTo("id",WeekView.ID)
            .findAll()
        val data = subjectData[0]!!

        if(checkTime(dayFlag)){
            Toast.makeText(context,"해당 시간에 다른 과목이 존재합니다.",Toast.LENGTH_SHORT).show()
        } else {
            realm.beginTransaction()
            data.startHour = start_hour.value
            data.startMinute = start_minute.displayedValues[start_minute.value].toString()
            data.endHour = end_hour.value
            data.endMinute = end_minute.displayedValues[end_minute.value].toString()
            data.dayFlag = dayFlag
            realm.commitTransaction()
            cnxt.cnxt.refresh(cnxt.cnxt.cnxt.weekView)
            dismiss()
            cnxt.dismiss()
            Toast.makeText(context,"시간이 변경되었습니다.", Toast.LENGTH_SHORT).show()
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