package com.racoondog.mystudent

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.text.TextWatcher

import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.subject_detail.*

import android.text.Editable
import android.widget.Toast
import androidx.core.content.ContextCompat


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

        subject_time.setText("$intentStartTimeText ~ $intentEndTimeText")
        subject_title.setText("$intentTitle")
        subject_content.setText("$intentContentText")


        val initTitle = subject_title.text.toString()


        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(edit: Editable) {
                // Text가 바뀌고 동작할 코드

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Text가 바뀌기 전 동작할 코드

            }

            //
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // 바뀌는 중에 동작 코드

                if (subject_title.text.toString() == initTitle){
                    lessonSave_Button.isClickable = false
                    lessonSave_Button.setTextColor(resources.getColor(R.color.originalText))
                }
                else{
                    lessonSave_Button.isClickable = true
                    lessonSave_Button.setTextColor(Color.DKGRAY)
                }

            }
        }

        subject_title.addTextChangedListener(textWatcher)
        subject_content.addTextChangedListener(textWatcher)
        studentName_text.addTextChangedListener(textWatcher)
        studentBirth_text.addTextChangedListener(textWatcher)
        studentPhone_text.addTextChangedListener(textWatcher)
        lessonCycle_text.addTextChangedListener(textWatcher)
        lessonFee_text.addTextChangedListener(textWatcher)

        lessonSave_Button.setOnClickListener {
            if(subject_title.isClickable == true) {
                Toast.makeText(this, "true", Toast.LENGTH_SHORT).show()
            }
        }

    }
}