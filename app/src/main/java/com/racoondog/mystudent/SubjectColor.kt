package com.racoondog.mystudent

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.subject_color.*


class SubjectColor : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.subject_color)
        window.statusBarColor = resources.getColor(R.color.whiteColor)
        val colorList = resources.getIntArray(R.array.subject_color)
        colorPicker.colorList(colorList,this)



    }

}