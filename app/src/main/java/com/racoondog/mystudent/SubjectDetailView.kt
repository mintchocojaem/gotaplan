package com.racoondog.mystudent

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import kotlinx.android.synthetic.main.subject_detail.view.*
import kotlinx.android.synthetic.main.subject_detail_view.view.*
import kotlinx.android.synthetic.main.time_picker.view.*

class SubjectDetailView: ConstraintLayout {
    private val realm = Realm.getDefaultInstance()
    constructor(context: Context?) : super(context) {
        initView()
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        initView()
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){
        initView()
    }
    private val subjectData: RealmResults<SubjectData> = realm.where<SubjectData>(SubjectData::class.java)
        .equalTo("id",WeekView.ID)
        .findAll()
    private val data = subjectData[0]!!

    private var subjectColorCode:Int = data.subjectColor
    private var dayFlag = data.dayFlag

    private fun initView(){
        val inflaterService: String = Context.LAYOUT_INFLATER_SERVICE
        val inflater: LayoutInflater = context.getSystemService(inflaterService) as LayoutInflater
        val view = inflater.inflate(R.layout.subject_detail_view,this,false)
        addView(view)

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

        subject_detail_theme_picker_btn_layout.setOnClickListener {
            val dialog = ColorPickerDialog(context, object :
                ColorPickerDialog.ICustomDialogEventListener {
                override fun customDialogEvent(colorCode: Int) {
                    // Do something with the value here, e.g. set a variable in the calling activity
                    subjectColorCode = colorCode
                    subject_detail_theme_picker_btn.backgroundTintList = ColorStateList.valueOf(subjectColorCode)

                }
            })
            dialog.show()
        }

        subject_detail_theme_picker_btn.backgroundTintList = ColorStateList.valueOf(subjectColorCode)

        subject_detail_time_picker.subjectPicker(scheduleData.scheduleStartHour,scheduleData.scheduleEndHour)
        subject_detail_time_picker.displayTime(data.startHour,data.startMinute.toInt(),data.endHour,data.endMinute.toInt())

    }

    fun subjectDetailSave(){

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

        realm.beginTransaction()
        data.subjectColor = subjectColorCode
        realm.commitTransaction()

    }

    private fun createSubject(dayFlag: Int){

        if(checkTime(dayFlag)){
            Toast.makeText(context,"해당 시간에 다른 과목이 존재합니다.",Toast.LENGTH_SHORT).show()
        } else {
            realm.beginTransaction()
            data.startHour = start_hour.value
            data.startMinute = start_minute.value.toString()
            data.endHour = end_hour.value
            data.endMinute = end_minute.value.toString()
            data.dayFlag = dayFlag
            realm.commitTransaction()
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

    fun hidePicker() {

        if(time_picker.visibility == View.VISIBLE) {
            time_picker.visibility = View.GONE
            TimePicker(context).changedTextColor(start_picker_layout, true)
            TimePicker(context).changedTextColor(end_picker_layout, true)
        }

    }


}