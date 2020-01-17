package com.racoondog.mystudent

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.schedule_color_picker.*


class ScheduleColor : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.schedule_color_picker)
        val colorList = resources.getIntArray(R.array.schedule_color)
        color_picker.colorList(colorList,this)



    }

}