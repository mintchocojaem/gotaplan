package com.racoondog.gotaplan

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.create_schedule.*
import kotlinx.android.synthetic.main.time_picker.*

class CreateSchedule : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_schedule)

        window!!.statusBarColor = ContextCompat.getColor(applicationContext,R.color.darkColor)
        window.decorView.systemUiVisibility = 0
        schedule_picker.schedulePicker()


        var dayFlag = 0

        createSchedule_Button.setOnClickListener{

            val titleName = schedule_title_text.text.toString()

            if(titleName != "")
            {
                if(dayFlag != 0) {
                    if (start_hour.value < end_hour.value) {
                        intent.putExtra("title", schedule_title_text.text.toString())
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

        schedule_title_text.setOnFocusChangeListener { v, hasFocus ->
            //if(!hasFocus) hideKeyboard()
        }

        scheduleQuit_Button.setOnClickListener {
            finish()
        }

    }

    override fun onBackPressed() {

        if(schedule_picker.isOpened()) {
            schedule_picker.clearFocus()
        }
        else super.onBackPressed()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val v: View? = currentFocus
        if (v != null &&
            (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE) &&
            v is EditText &&
            !v.javaClass.name.startsWith("android.webkit.")
        ) {
            val scrcoords = IntArray(2)
            v.getLocationOnScreen(scrcoords)
            val x: Float = ev.rawX + v.getLeft() - scrcoords[0]
            val y: Float = ev.rawY + v.getTop() - scrcoords[1]
            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()) hideKeyboard(
                this
            )
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun hideKeyboard(activity: Activity?) {
        if (!(activity == null || activity.window == null)) {
            val imm =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)
        }
    }

}