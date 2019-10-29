package com.racoondog.mystudent

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.lesson_detail.*

class LessonDetail : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lesson_detail)
        lessonQuit_Button.setOnClickListener {

            setResult(Activity.RESULT_OK, intent)
            finish()

        }
        val intent = getIntent()
        val intentTitle = intent.getStringExtra("LessonTitle")
        val intentStartTimeText = intent.getStringExtra("StartTimeText")
        val intentEndTimeText = intent.getStringExtra("EndTimeText")

        lesson_time.text = "$intentStartTimeText ~ $intentEndTimeText"
        lesson_title.text = "$intentTitle"

    }
}