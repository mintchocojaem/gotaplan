package com.racoondog.mystudent

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.schedule_color_picker.*


class ScheduleColor : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.schedule_color_picker)

        color_picker.createColorButton(this)



    }

}