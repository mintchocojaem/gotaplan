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
import kotlinx.android.synthetic.main.create_schedule.view.*
import kotlinx.android.synthetic.main.time_picker.*


class CreateSchedule : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_schedule)

        schedule_picker.schedulePicker()


        var dayFlag = 0
        val intent = Intent()


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


        schedule_day_group.setOnCheckedChangeListener{_,_ ->
            title_text.hideKeyboard()
        }
        scheduleQuit_Button.setOnClickListener {
            finish()
        }


    }

    override fun onBackPressed() {

        if(time_picker.visibility == View.VISIBLE){
            time_picker.visibility = View.GONE
            TimePicker(this).changedTextColor(start_picker_layout,true)
            TimePicker(this).changedTextColor(end_picker_layout,true)
            }

        else super.onBackPressed()
    }


    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)


    }

}