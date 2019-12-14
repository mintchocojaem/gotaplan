package com.racoondog.mystudent


import android.app.Activity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.create_subject.*


class CreateSubject :AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        super.setContentView(R.layout.create_subject)

        val intent = getIntent()
        val intentStartHour = intent.getIntExtra("start_time",0)
        val intentEndHour = intent.getIntExtra("end_time",0)
        val intentFlag = intent.getIntExtra("day_flag",0)
        var dayflag = 0
        val displayValue = mutableListOf<String>()
        val initEndHour = intentEndHour -1


        if (intentFlag == 6){
            saturday_button.visibility = View.VISIBLE
        }
        else if (intentFlag == 7){
            saturday_button.visibility = View.VISIBLE
            sunday_button.visibility = View.VISIBLE
        }

        start_AMPM.apply {
            if (intentStartHour < 12 && initEndHour < 12) {
                displayValue.add("오전")
                minValue = 0
                maxValue = 0
                value = 0

            } else if (intentStartHour < 12 && initEndHour >= 12){
                displayValue.add("오전")
                displayValue.add("오후")
                minValue = 0
                maxValue = 1
                value = 0

            } else {
                displayValue.add("오후")
                minValue = 0
                maxValue = 0
                value = 0

            }
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            wrapSelectorWheel = false
            displayedValues = displayValue.toTypedArray()
            startText_AMPM.text ="${displayValue[0]} "
            displayValue.removeAll(displayValue)


        }

        end_AMPM.apply {

            if (intentStartHour < 12 && intentEndHour < 12) {
                displayValue.add("오전")
                minValue = 0
                maxValue = 0
                value = 0
            } else if (intentStartHour < 12 && intentEndHour >= 12){
                displayValue.add("오전")
                displayValue.add("오후")
                minValue = 0
                maxValue = 1
                value = 0
            }
            else{
                displayValue.add("오후")
                minValue = 0
                maxValue = 0
                value = 0
            }
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            wrapSelectorWheel = false
            displayedValues = displayValue.toTypedArray()
            endText_AMPM.text ="${displayValue[0]} "
            displayValue.removeAll(displayValue)

        }


        start_hour.apply {

            minValue = intentStartHour
            maxValue = initEndHour
            value = minValue
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            wrapSelectorWheel = false

            for(i in intentStartHour..initEndHour){
                when{
                    i > 12 -> displayValue.add("${i - 12}")
                    else -> displayValue.add("$i")
                }
            }
            displayedValues = displayValue.toTypedArray()
            startText_hour.text = "${displayedValues[value - minValue]}"
            displayValue.removeAll(displayValue)

        }

        end_hour.apply {
            minValue = intentStartHour
            maxValue = intentEndHour
            value = minValue
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            wrapSelectorWheel = false

            for(i in intentStartHour..intentEndHour){
                when{
                    i > 12-> displayValue.add("${i-12}")
                    else -> displayValue.add("$i")
                }
            }
            displayedValues = displayValue.toTypedArray()
            endText_hour.text = "${displayedValues[value - minValue]}"
            displayValue.removeAll(displayValue)
        }

        start_minute.apply{

            minValue = 0
            maxValue = 11
            value = 0
            displayedValues = arrayOf("00","05","10","15","20","25","30","35","40","45","50","55")
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            wrapSelectorWheel = false

        }

        end_minute.apply{

            minValue = 0
            maxValue = 11
            value = 6
            displayedValues = arrayOf("00","05","10","15","20","25","30","35","40","45","50","55")
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            wrapSelectorWheel = false
            endText_minute.text = displayedValues[value]
        }

        start_AMPM.setOnTouchListener { _: View, event:MotionEvent ->
            //리턴값은 return 없이 아래와 같이
            true // or false
        }
        end_AMPM.setOnTouchListener { _: View, event:MotionEvent ->
            //리턴값은 return 없이 아래와 같이
            true // or false
        }

        start_minute.setOnValueChangedListener{_,i1,i2 ->

            startText_minute.text = start_minute.displayedValues[start_minute.value]

        }
        start_hour.setOnValueChangedListener{_,i1,i2 ->

            startText_hour.text = start_hour.displayedValues[start_hour.value - intentStartHour]
            if(i2 < 12 || i2 == 24) {
                start_AMPM.value = 0
                startText_AMPM.text = start_AMPM.displayedValues[start_AMPM.value]+" "
            }else {
                start_AMPM.value = 1
                startText_AMPM.text = start_AMPM.displayedValues[start_AMPM.value]+" "
            }


        }

        end_minute.setOnValueChangedListener{_,i1,i2 ->

            endText_minute.text = end_minute.displayedValues[end_minute.value]
            if (end_hour.value == end_hour.maxValue) {
                end_minute.value = 0
                endText_minute.text = end_minute.displayedValues[end_minute.value]
            }

        }
        end_hour.setOnValueChangedListener{_,i1,i2 ->

            endText_hour.text = end_hour.displayedValues[end_hour.value - intentStartHour]
            if(i2 < 12 || i2 == 24) {
                end_AMPM.value = 0
                endText_AMPM.text = end_AMPM.displayedValues[end_AMPM.value]+" "
            }else {
                end_AMPM.value = 1
                endText_AMPM.text = end_AMPM.displayedValues[end_AMPM.value]+" "
            }


        }


        createSubject_Button.setOnClickListener{
            if(dayflag != 0 ) {
                if ((start_hour.value < end_hour.value)||
                    (start_hour.value == end_hour.value && ((end_minute.value - start_minute.value) >= 6))) {
                    if(TitleName_text.text.toString() !="") {

                        intent.putExtra("SubjectStartTime", start_hour.value)
                        intent.putExtra("SubjectEndTime", end_hour.value)
                        intent.putExtra("DayFlag", dayflag)
                        intent.putExtra("SubjectTitle", TitleName_text.text.toString())
                        intent.putExtra("StartTimeText", startText_hour.text.toString())
                        intent.putExtra("EndTimeText", endText_hour.text.toString())
                        intent.putExtra("ContentText",Content_text.text?.toString())

                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                    else{
                        Toast.makeText(this, "제목을 입력해 주세요.", Toast.LENGTH_SHORT).show()
                    }
                } else if (start_hour.value == end_hour.value && start_minute.value == end_minute.value) {
                    Toast.makeText(this, "시작 시각이 종료 시각과 같을 수 없습니다.", Toast.LENGTH_SHORT).show()
                } else if ((start_hour.value == end_hour.value && ((end_minute.value - start_minute.value) < 6))){
                    Toast.makeText(this, "각 과목의 최소 시간은 30분입니다.", Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(this, "시작 시각이 종료 시각보다 클 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(this, "날짜를 선택해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }


        startTime.setOnClickListener{
            time_picker.visibility = View.VISIBLE
            start_picker.visibility = View.VISIBLE
            end_picker.visibility = View.INVISIBLE
        }
        endTime.setOnClickListener{
            time_picker.visibility = View.VISIBLE
            end_picker.visibility = View.VISIBLE
            start_picker.visibility = View.INVISIBLE

        }

        monday_button.setOnClickListener {
            dayflag = 1
        }
        tuesday_button.setOnClickListener {
            dayflag = 2
        }
        wednesday_button.setOnClickListener {
            dayflag = 3
        }
        thursday_button.setOnClickListener {
            dayflag = 4
        }
        friday_button.setOnClickListener {
            dayflag = 5
        }
        saturday_button.setOnClickListener {
            dayflag = 6
        }
        sunday_button.setOnClickListener {
            dayflag = 7
        }
        lesson_mode.setOnCheckedChangeListener{compoundButton,b ->
            if (compoundButton.isChecked){
                Toast.makeText(this, "개인 레슨: on", Toast.LENGTH_SHORT).show()
            } else{
                Toast.makeText(this, "개인 레슨: off", Toast.LENGTH_SHORT).show()
            }
        }


    }

}