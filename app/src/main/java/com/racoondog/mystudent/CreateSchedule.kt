package com.racoondog.mystudent

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.createschedule_layout.*




class CreateSchedule : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.createschedule_layout)
        CreateSchedule_Button.setOnClickListener{

            val intent = Intent()
            intent.putExtra("title",TitleName_text.text.toString())
            setResult(Activity.RESULT_OK, intent)
            finish()




        }

    }
}