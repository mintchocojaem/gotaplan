package com.racoondog.mystudent

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.text.TextWatcher

import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.subject_detail.*

import android.text.Editable
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.create_subject.*


class SubjectDetail : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.subject_detail)

       var saveEditFlag: Boolean = false

        /*
        val intent = getIntent()
        val intentTitle = intent.getStringExtra("SubjectTitle")
        val intentStartTimeText = intent.getStringExtra("StartTimeText")
        val intentEndTimeText = intent.getStringExtra("EndTimeText")
        val intentContentText = intent.getStringExtra("ContentText")
        */


        subject_time.setText(SubjectData.SubjectInfo[SubjectData.id][1] +"~"+ SubjectData.SubjectInfo[SubjectData.id][2])
        subject_title.setText(SubjectData.SubjectInfo[SubjectData.id][0])
        subject_content.setText(SubjectData.SubjectInfo[SubjectData.id][3])


        lessonQuit_Button.setOnClickListener {

            setResult(Activity.RESULT_OK, intent)
            finish()

        }

        lessonSave_Button.setOnClickListener {

            if(saveEditFlag == false){

                lessonSave_Button.text = "저장"

                subject_title.isEnabled = true
                subject_content.isEnabled = true

                saveEditFlag = true

            } else {
                lessonSave_Button.text = "수정"

                subject_title.isEnabled = false
                subject_content.isEnabled = false

                SubjectData.setTitle(subject_title.text.toString())
                SubjectData.setContent(subject_content.text.toString())

                Toast.makeText(
                    this,
                    "${SubjectData.SubjectInfo[SubjectData.id].contentDeepToString()}",
                    Toast.LENGTH_SHORT
                ).show()

                saveEditFlag = false
            }
        }


    }
}