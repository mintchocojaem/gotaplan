package com.racoondog.mystudent

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.createschedule_layout.*
import android.app.TimePickerDialog
import android.graphics.Color
import android.view.View
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView


class CreateSchedule : AppCompatActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {

        var day_flag = 0
        val intent = Intent()


        super.onCreate(savedInstanceState)
        setContentView(R.layout.createschedule_layout)
        start_AMPM.minValue = 0
        start_AMPM.maxValue = 1
        start_AMPM.value = 0
        start_AMPM.displayedValues = arrayOf("오전","오후")
        start_AMPM.wrapSelectorWheel = false
        start_AMPM.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        start_time.minValue = 6
        start_time.maxValue = 18
        start_time.value = 8
        start_time.displayedValues = arrayOf("6","7","8","9","10","11","12","1","2","3","4","5","6")
        start_time.wrapSelectorWheel = false
        start_time.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS


        end_AMPM.minValue = 0
        end_AMPM.maxValue = 1
        end_AMPM.value = 1
        end_AMPM.displayedValues = arrayOf("오전","오후")
        end_AMPM.wrapSelectorWheel = false
        end_AMPM.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        end_time.minValue = 7
        end_time.maxValue = 24
        end_time.value = 20
        end_time.displayedValues = arrayOf("7","8","9","10","11","12","1","2","3","4","5","6","7","8","9","10","11","12")
        end_time.wrapSelectorWheel = false
        end_time.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS


        start_AMPM.setOnValueChangedListener{numberpicker,i1,i2 ->

            when{i2 == 1 -> start_time.value = 18 }
            when {i2 == 1 -> textView_start.text = "오후 "+"${start_time.value-12}"+":00"}

            when{i2 == 0 -> start_time.value = 6}
            when{i2 == 0 -> textView_start.text = "오전 "+"${start_time.value}"+":00"}

        }

        start_time.setOnValueChangedListener{numberpicker,i1,i2 ->

           for ( i in 6 until 19){
               when {i2 == i && i2 < 12 -> textView_start.text = "오전 "+"$i"+":00"
                   i2 == i && i2 > 12 -> textView_start.text = "오후 "+"${i-12}"+":00"
                   i2 == 12 -> textView_start.text = "오후 12:00"
               }

               when (start_AMPM.value == 0){
                   i2 >= 12 -> start_AMPM.value = 1
               }
               when (start_AMPM.value == 1) {
                   i2 < 12 -> start_AMPM.value = 0
               }
           }
        }

        end_AMPM.setOnValueChangedListener{numberpicker,i1,i2 ->

            when{i2 == 1 -> end_time.value = 24 }
            when {i2 == 1 -> textView_end.text = "오후 "+"${end_time.value-12}"+":00"}

            when{i2 == 0 -> end_time.value = 7}
            when{i2 == 0 -> textView_end.text = "오전 "+"${end_time.value}"+":00"}

        }

        end_time.setOnValueChangedListener{numberpicker,i1,i2 ->

            for ( i in 7 until 25){
                when {i2 == i && i2 < 12 -> textView_end.text = "오전 "+"$i"+":00"
                    i2 == i && i2 > 12 -> textView_end.text = "오후 "+"${i-12}"+":00"
                    i2 == 12 -> textView_end.text = "오후 12:00"
                }

                when (end_AMPM.value == 0){
                    i2 >= 12 -> end_AMPM.value = 1
                }
                when (end_AMPM.value == 1) {
                    i2 < 12 -> end_AMPM.value = 0
                }
            }
        }

        //Number Picker

        CreateSchedule_Button.setOnClickListener{

            val titlename = TitleName_text.text.toString()

            if(titlename != "")
            {
                if(day_flag != 0) {
                    if (start_time.value < end_time.value) {
                        intent.putExtra("title", TitleName_text.text.toString())
                        intent.putExtra("day_flag", day_flag)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
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
            Friday.setBackgroundColor(Color.GRAY)
            Saturday.setBackgroundColor(Color.LTGRAY)
            Sunday.setBackgroundColor(Color.LTGRAY)
            day_flag = 1
        }

        Saturday.setOnClickListener{
            Friday.setBackgroundColor(Color.LTGRAY)
            Saturday.setBackgroundColor(Color.GRAY)
            Sunday.setBackgroundColor(Color.LTGRAY)
            day_flag = 2
        }

        Sunday.setOnClickListener{
            Friday.setBackgroundColor(Color.LTGRAY)
            Saturday.setBackgroundColor(Color.LTGRAY)
            Sunday.setBackgroundColor(Color.GRAY)
            day_flag = 3
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

    }


}