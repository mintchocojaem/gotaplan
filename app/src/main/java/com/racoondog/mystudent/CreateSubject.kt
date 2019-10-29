package com.racoondog.mystudent


import android.app.ActionBar
import android.app.Activity
import android.content.Context

import android.os.Bundle
import android.view.View

import android.widget.Button
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.create_subject.*
import kotlinx.android.synthetic.main.schedule_image_layout.*


class CreateSubject :AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        super.setContentView(R.layout.create_subject)

        val intent = getIntent()
        val intentStartTime = intent.getIntExtra("start_time",0)
        val intentEndTime = intent.getIntExtra("end_time",0)
        val intentFlag = intent.getIntExtra("day_flag",0)
        var dayflag = 0

        if (intentFlag == 2){
            saturday_button.visibility = View.VISIBLE
        }
        else if (intentFlag == 3){
            saturday_button.visibility = View.VISIBLE
            sunday_button.visibility = View.VISIBLE
        }

        val initStartTime = intentStartTime + 1
        val initEndTime = intentEndTime -1

        start_AMPM.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        start_AMPM.wrapSelectorWheel = false
        end_AMPM.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        end_AMPM.wrapSelectorWheel = false

        if (intentStartTime < 12 && intentEndTime <= 12){

            start_AMPM.minValue = 0
            start_AMPM.maxValue = 0
            start_AMPM.value = 0
            start_AMPM.displayedValues = arrayOf("오전")

            end_AMPM.minValue = 0
            end_AMPM.maxValue = 0
            end_AMPM.value = 0
            end_AMPM.displayedValues = arrayOf("오전")

            textView_start.text = "오전 $intentStartTime:00"
            textView_end.text = "오전 $initStartTime:00"

        }

        if(intentStartTime > 12 && intentEndTime > 12) {

            start_AMPM.minValue = 1
            start_AMPM.maxValue = 1
            start_AMPM.value = 1
            start_AMPM.displayedValues = arrayOf("오후")

            end_AMPM.minValue = 1
            end_AMPM.maxValue = 1
            end_AMPM.value = 1
            end_AMPM.displayedValues = arrayOf("오후")

            textView_start.text = "오후 ${intentStartTime-12}:00"
            textView_end.text = "오후 ${initStartTime-12}:00"

            }

        if(intentStartTime < 12 && intentEndTime > 12) {

            start_AMPM.minValue = 0
            start_AMPM.maxValue = 1
            start_AMPM.value = 0
            start_AMPM.displayedValues = arrayOf("오전","오후")

            end_AMPM.minValue = 0
            end_AMPM.maxValue = 1
            end_AMPM.value = 0
            end_AMPM.displayedValues = arrayOf("오전","오후")

            textView_start.text = "오전 $intentStartTime:00"
            textView_end.text = "오전 $initStartTime:00"

        }

        if(intentStartTime == 12 && intentEndTime > 12) {

            start_AMPM.minValue = 0
            start_AMPM.maxValue = 1
            start_AMPM.value = 0
            start_AMPM.displayedValues = arrayOf("오전","오후")

            end_AMPM.minValue = 0
            end_AMPM.maxValue = 1
            end_AMPM.value = 0
            end_AMPM.displayedValues = arrayOf("오전","오후")

            textView_start.text = "오전 $intentStartTime:00"
            textView_end.text = "오후 ${initStartTime-12}:00"

        }



        start_time.minValue = intentStartTime
        start_time.maxValue = initEndTime
        start_time.value = intentStartTime
        start_time.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        start_time.wrapSelectorWheel = false

        end_time.minValue = initStartTime
        end_time.maxValue = intentEndTime
        end_time.value = initStartTime
        end_time.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        end_time.wrapSelectorWheel = false

        val initDisplay = intentEndTime - intentStartTime

        var startDisplay = Array<String>(initDisplay,{"1"})
        var endDisplay = Array<String>(initDisplay,{"1"})

        for(i in 0 until initDisplay){

            val countDisplay = i+intentStartTime

            if(countDisplay<=12) {
                startDisplay[i] = "$countDisplay"
            }
            else if (countDisplay>12){
                startDisplay[i] = "${countDisplay-12}"
            }


        }

        for(i in 0 until initDisplay){

            val countDisplay = i+initStartTime

            if(countDisplay<=12) {
               endDisplay[i] = "$countDisplay"
            }
            else if (countDisplay>12){
                endDisplay[i] = "${countDisplay-12}"
            }


        }

        start_time.displayedValues = startDisplay
        end_time.displayedValues = endDisplay

        start_AMPM.setOnValueChangedListener{numberpicker,i1,i2 ->

            when{i2 == 1 -> start_time.value = start_time.maxValue }
            when {i2 == 1 -> textView_start.text = "오후 "+"${start_time.value-12}"+":00"}

            when{i2 == 0 -> start_time.value = start_time.minValue}
            when{i2 == 0 -> textView_start.text = "오전 "+"${start_time.value}"+":00"}

        }

        start_time.setOnValueChangedListener{numberpicker,i1,i2 ->

            for ( i in intentStartTime..initEndTime){
                when {i2 == i && i2 < 12 -> textView_start.text = "오전 "+"$i"+":00"
                    i2 == i && i2 > 12 -> textView_start.text = "오후 "+"${i-12}"+":00"
                    i2 == 12 -> textView_start.text = "오전 12:00"

                }

                when (start_AMPM.value == 0){
                    i2 > 12 -> start_AMPM.value = 1
                }
                when (start_AMPM.value == 1) {
                    i2 <= 12 -> start_AMPM.value = 0
                }
            }
        }

        end_AMPM.setOnValueChangedListener{numberpicker,i1,i2 ->

            when{i2 == 1 -> end_time.value = end_time.maxValue }
            when {i2 == 1 -> textView_end.text = "오후 "+"${end_time.value-12}"+":00"}

            when{i2 == 0 -> end_time.value = end_time.minValue}
            when{i2 == 0 -> textView_end.text = "오전 "+"${end_time.value}"+":00"}

        }

        end_time.setOnValueChangedListener{numberpicker,i1,i2 ->

            for ( i in initStartTime..intentEndTime){
                when {i2 == i && i2 < 12 -> textView_end.text = "오전 "+"$i"+":00"
                    i2 == i && i2 > 12 -> textView_end.text = "오후 "+"${i-12}"+":00"
                    i2 == 12 -> textView_end.text = "오전 12:00"

                }

                when (end_AMPM.value == 0){
                    i2 > 12 -> end_AMPM.value = 1
                }
                when (end_AMPM.value == 1) {
                    i2 <= 12 -> end_AMPM.value = 0
                }
            }
        }

        createSubject_Button.setOnClickListener{
            if(dayflag != 0 ) {
                if (start_time.value < end_time.value) {
                    if(TitleName_text.text.toString() !="") {
                        intent.putExtra("SubjectStartTime", start_time.value)
                        intent.putExtra("SubjectEndTime", end_time.value)
                        intent.putExtra("DayFlag", dayflag)
                        intent.putExtra("SubjectTitle", TitleName_text.text.toString())
                        intent.putExtra("StartTimeText", textView_start.text.toString())
                        intent.putExtra("EndTimeText", textView_end.text.toString())
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                    else{
                        Toast.makeText(this, "제목을 입력해 주세요.", Toast.LENGTH_SHORT).show()
                    }
                } else if (start_time.value == end_time.value) {
                    Toast.makeText(this, "시작 시각이 종료 시각과 같을 수 없습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "시작 시각이 종료 시각보다 클 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(this, "날짜를 선택해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }


        textView_start.setOnClickListener{
            time_picker.visibility = View.VISIBLE
            end_AMPM.visibility = View.INVISIBLE
            end_time.visibility = View.INVISIBLE
            start_AMPM.visibility = View.VISIBLE
            start_time.visibility = View.VISIBLE
        }
        textView_end.setOnClickListener{
            time_picker.visibility = View.VISIBLE
            end_AMPM.visibility = View.VISIBLE
            end_time.visibility = View.VISIBLE
            start_AMPM.visibility = View.INVISIBLE
            start_time.visibility = View.INVISIBLE

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


    }
}