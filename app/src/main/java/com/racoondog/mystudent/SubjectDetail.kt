package com.racoondog.mystudent

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.subject_detail.*


class SubjectDetail : AppCompatActivity() {

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


        subject_time.setText(SubjectData.SubjectInfo!![SubjectData.id]!![1] + "~" + SubjectData.SubjectInfo!![SubjectData.id]!![2])
        subject_title.setText(SubjectData.SubjectInfo!![SubjectData.id]!![0])
        subject_content.setText(SubjectData.SubjectInfo!![SubjectData.id]!![3])


        lessonQuit_Button.setOnClickListener {

               setResult(Activity.RESULT_OK, intent)
               finish()


        }

        lessonSave_Button.setOnClickListener {

            if (saveEditFlag == false) {

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
                    "${SubjectData.SubjectInfo[SubjectData.id]!!.contentDeepToString()}",
                    Toast.LENGTH_SHORT
                ).show()

                saveEditFlag = false
            }
        }

        subject_delete.setOnClickListener{

            val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.Theme_AppCompat_Light_Dialog))
            builder.setTitle("삭제")
            builder.setMessage("과목을 삭제하시겠습니까?")

            builder.setPositiveButton("확인") { _, _ ->

                SubjectData.SubjectInfo.set(SubjectData.id,null)
                setResult(104,intent)
                finish()

            }

            builder.setNegativeButton("취소") { _, _ ->

            }
            builder.show()

        }



    }


}