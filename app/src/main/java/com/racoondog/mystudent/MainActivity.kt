package com.racoondog.mystudent

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /*
        Fabric.with(this, Crashlytics())
        Fabric.with(this, Answers())


        // TODO: Use your own attributes to track content views in your app
        Answers.getInstance().logContentView(
            ContentViewEvent()
                .putContentName("Tweet")
                .putContentType("Video")
                .putContentId("1234")
                .putCustomAttribute("Favorites Count", 20)
                .putCustomAttribute("Screen Orientation", "Landscape")
        )

        weekView.monthChangeListener = MonthLoader.MonthChangeListener { newYear, newMonth ->
            listOf(
                WeekViewEvent(
                    1,
                    "김인수",
                    Calendar.getInstance().apply {
                        set(Calendar.HOUR, 6)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.AM_PM, Calendar.AM)
                    },
                    Calendar.getInstance().apply {
                        set(Calendar.HOUR, 8)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.AM_PM, Calendar.AM)
                    }
                )
            )
        }

        weekView.setOnEventClickListener { event, eventRect ->
            startActivity(Intent(this, LessonDetailActivity::class.java))
        }
        */

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
