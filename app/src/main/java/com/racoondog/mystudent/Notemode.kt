package com.racoondog.mystudent

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.notemode_layout.*

class Notemode : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notemode_layout)


        day1.setOnClickListener {
            ui_bg.setBackgroundResource(R.drawable.mint_bgxxhdpi)
            val nextIntent = Intent(this, Schedule::class.java)
            startActivity(nextIntent)
        }

        day2.setOnClickListener {
            ui_bg.setBackgroundResource(R.drawable.pink_bgxxhdpi)
        }

        day3.setOnClickListener {
            ui_bg.setBackgroundResource(R.drawable.purple_bgxxhdpi)
        }

        day4.setOnClickListener {
            ui_bg.setBackgroundResource(R.drawable.orange_bgxxhdpi)
        }

        day5.setOnClickListener {
            ui_bg.setBackgroundResource(R.drawable.yellow_bgxxhdpi)
        }

        day6.setOnClickListener {
            ui_bg.setBackgroundResource(R.drawable.blue_bgxxhdpi)
        }

        day7.setOnClickListener {
            ui_bg.setBackgroundResource(R.drawable.red_bgxxhdpi)
        }
    }


}