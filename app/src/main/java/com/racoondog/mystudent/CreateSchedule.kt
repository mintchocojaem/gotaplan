package com.racoondog.mystudent

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.create_schedule.*


class CreateSchedule : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        var dayFlag = 0
        val intent = Intent()
        val displayValue = mutableListOf<String>()

        setContentView(R.layout.create_schedule)


        start_AMPM.apply {

            displayValue.add("오전")
            displayValue.add("오후")
            minValue = 0
            maxValue = 1
            value = 0
            startText_AMPM.text ="${displayValue[0]} "

            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            wrapSelectorWheel = false
            displayedValues = displayValue.toTypedArray()

            displayValue.removeAll(displayValue)


        }

        end_AMPM.apply {

            displayValue.add("오전")
            displayValue.add("오후")
            minValue = 0
            maxValue = 1
            value = 1
            endText_AMPM.text ="${displayValue[1]} "

            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            wrapSelectorWheel = false
            displayedValues = displayValue.toTypedArray()

            displayValue.removeAll(displayValue)

        }


        start_hour.apply {

            minValue = 6
            maxValue = 18
            value = 8

            for (i in minValue .. maxValue) {
                when{
                    i > 12-> displayValue.add("${i-12}")
                    else -> displayValue.add("$i")
                }
            }

            displayedValues = displayValue.toTypedArray()
            wrapSelectorWheel = false
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

            displayValue.removeAll(displayValue)
        }

        end_hour.apply {

            minValue = 7
            maxValue = 24
            value = 20
            for (i in minValue .. maxValue) {
                when{
                    i > 12-> displayValue.add("${i-12}")
                    else -> displayValue.add("$i")
                }
            }

            displayedValues = displayValue.toTypedArray()
            wrapSelectorWheel = false
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS


        }


        start_AMPM.setOnTouchListener { _: View, event:MotionEvent ->
            //리턴값은 return 없이 아래와 같이
            true // or false
        }
        end_AMPM.setOnTouchListener { _: View, event:MotionEvent ->
            //리턴값은 return 없이 아래와 같이
            true // or false
        }

        start_hour.setOnValueChangedListener{_,_,i2 ->

            startText_hour.text = start_hour.displayedValues[start_hour.value - start_hour.minValue]
            if(i2 < 12 || i2 == 24) {
                start_AMPM.value = 0
                startText_AMPM.text = start_AMPM.displayedValues[start_AMPM.value]+" "
            }else {
                start_AMPM.value = 1
                startText_AMPM.text = start_AMPM.displayedValues[start_AMPM.value]+" "
            }


        }


        end_hour.setOnValueChangedListener{_,_,i2 ->

            endText_hour.text = end_hour.displayedValues[end_hour.value - end_hour.minValue]
            if(i2 < 12 || i2 == 24) {
                end_AMPM.value = 0
                endText_AMPM.text = end_AMPM.displayedValues[end_AMPM.value]+" "
            }else {
                end_AMPM.value = 1
                endText_AMPM.text = end_AMPM.displayedValues[end_AMPM.value]+" "
            }



        }

        //Number Picker

        createSchedule_Button.setOnClickListener{

            val titleName = title_text.text.toString()

            if(titleName != "")
            {
                if(dayFlag != 0) {
                    if (start_hour.value < end_hour.value) {
                        intent.putExtra("title", title_text.text.toString())
                        intent.putExtra("scheduleDayFlag", dayFlag)
                        intent.putExtra("scheduleStartHour",start_hour.value)
                        intent.putExtra("scheduleEndHour",end_hour.value)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                    else if(start_hour.value == end_hour.value){
                        Toast.makeText(this, "시작 시각이 종료 시각과 같을 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(this,"시작 시각이 종료 시각보다 클 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    Toast.makeText(this,"마지막 요일을 선택해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(this,"시간표명을 입력하세요.", Toast.LENGTH_SHORT).show()
            }

        }

        Friday.setOnClickListener{
            dayFlag = 5
        }

        Saturday.setOnClickListener{

            dayFlag = 6
        }

        Sunday.setOnClickListener{

            dayFlag = 7
        }

        start_picker_layout.setOnClickListener{
            title_text.hideKeyboard()
            time_picker.visibility = View.VISIBLE
            start_picker.visibility = View.VISIBLE
            end_picker.visibility = View.INVISIBLE
            getchild()

        }
        end_picker_layout.setOnClickListener{
            title_text.hideKeyboard()
            time_picker.visibility = View.VISIBLE
            end_picker.visibility = View.VISIBLE
            start_picker.visibility = View.INVISIBLE

        }
        schedule_day_group.setOnCheckedChangeListener{_,_ ->
            title_text.hideKeyboard()
        }


    }

    fun getchild(){
        for (index in 0 until (start_picker_layout as ViewGroup).childCount) {
            val nextChild = (start_picker_layout as ViewGroup).getChildAt(index)
            if(nextChild is TextView) nextChild.setTextColor(resources.getColor(R.color.pinkColor))
        }
    }



    override fun onBackPressed() {

        if(time_picker.visibility == View.VISIBLE){
        time_picker.visibility = View.GONE}
        else super.onBackPressed()
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

}