package com.racoondog.mystudent

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.createschedule_layout.*
import android.app.TimePickerDialog
import android.graphics.Color
import android.widget.Toast


class CreateSchedule : AppCompatActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {

        var day_flag = 0


        super.onCreate(savedInstanceState)
        setContentView(R.layout.createschedule_layout)

        CreateSchedule_Button.setOnClickListener{

            val titlename = TitleName_text.text.toString()

            if(titlename != "")
            {
                if(day_flag != 0) {
                    val intent = Intent()
                    intent.putExtra("title", TitleName_text.text.toString())
                    intent.putExtra("day_flag",day_flag)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
                else{
                    Toast.makeText(this,"마지막 요일을 선택해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(this,"시간표명을 입력하세요.", Toast.LENGTH_SHORT).show()
            }



        }

        textView_hour.setOnClickListener{

            val dialog_hour = TimePickerDialog(this, listener_hour, 8, 0, true)

            dialog_hour.show()
        }
        textView_minute.setOnClickListener{
            val dialog_minute = TimePickerDialog(this, listener_minute, 8, 0, true)

            dialog_minute.show()
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


    }

    val listener_hour = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
        // 설정버튼 눌렀을 때

           textView_hour.text = hourOfDay.toString() + "시 " + minute + "분"

    }
    val listener_minute= TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
        // 설정버튼 눌렀을 때

        textView_minute.text = hourOfDay.toString() + "시 " + minute + "분"

    }

}