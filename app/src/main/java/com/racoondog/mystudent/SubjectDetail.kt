package com.racoondog.mystudent

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.subject_detail.*

class SubjectDetail : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.subject_detail)
        lessonQuit_Button.setOnClickListener {

            setResult(Activity.RESULT_OK, intent)
            finish()

        }
        val intent = getIntent()
        val intentTitle = intent.getStringExtra("SubjectTitle")
        val intentStartTimeText = intent.getStringExtra("StartTimeText")
        val intentEndTimeText = intent.getStringExtra("EndTimeText")
        val intentContentText = intent.getStringExtra("ContentText")

        lesson_time.text = "$intentStartTimeText ~ $intentEndTimeText"
        lesson_title.text = "$intentTitle"
        lesson_content.text = "$intentContentText"

    }
}