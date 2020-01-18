package com.racoondog.mystudent

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.subject_detail.*


class SubjectDetail : AppCompatActivity() {

    private val realm = Realm.getDefaultInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.subject_detail)

        var saveEditFlag = false

        var subjectData: RealmResults<SubjectBox> = realm.where<SubjectBox>(SubjectBox::class.java)
            .equalTo("id",WeekViewData.ID)
            .findAll()
        val data = subjectData[0]!!
        subject_time.text = data.time.toString()
        subject_title.setText(data.title.toString())
        subject_content.setText(data.content.toString())
        themeChange(data.subjectColor)

        if(subjectData[0]!!.lessonOnOff) lesson_bar.visibility = View.VISIBLE
        else lesson_bar.visibility = View.GONE


        studentName_text.setText(data.studentName.toString())
        studentBirth_text.setText(data.studentBirth.toString())
        studentPhone_text.setText(data.studentPhoneNumber.toString())
        lessonCost_text.setText(data.lessonCost.toString())
        lessonCycle_text.setText(data.lessonCycle.toString())


        lessonQuit_Button.setOnClickListener {
               setResult(Activity.RESULT_OK, intent)
               finish()
        }

        lessonSave_Button.setOnClickListener {

            if (!saveEditFlag) {

                lessonSave_Button.text = "저장"

                subject_title.isEnabled = true
                subject_content.isEnabled = true
                subject_content.setOnKeyListener { v, keyCode, event ->

                    if(event.getAction() == KeyEvent.ACTION_DOWN){
                        if(keyCode == KeyEvent.KEYCODE_ENTER){
                            false
                        }
                    }
                    true

                }

                studentName_text.isEnabled = true
                studentBirth_text.isEnabled = true
                studentPhone_text.isEnabled = true
                lessonCost_text.isEnabled = true
                lessonCycle_text.isEnabled = true

                saveEditFlag = true

            } else {
                lessonSave_Button.text = "수정"

                subject_title.isEnabled = false
                subject_content.isEnabled = false

                studentName_text.isEnabled = false
                studentBirth_text.isEnabled = false
                studentPhone_text.isEnabled = false
                lessonCost_text.isEnabled = false
                lessonCycle_text.isEnabled = false


                realm.beginTransaction()
                data.title = subject_title.text.toString()
                data.content = subject_content.text.toString()

                data.studentName = studentName_text.text.toString()
                data.studentBirth = studentBirth_text.text.toString()
                data.studentPhoneNumber = studentPhone_text.text.toString()
                data.lessonCost = lessonCost_text.text.toString()
                data.lessonCycle = lessonCycle_text.text.toString()

                realm.commitTransaction()

                saveEditFlag = false
            }
        }

        subject_delete.setOnClickListener{

            val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.Theme_AppCompat_Light_Dialog))
            builder.setTitle("삭제")
            builder.setMessage("과목을 삭제하시겠습니까?")

            builder.setPositiveButton("확인") { _, _ ->

                realm.beginTransaction()
                data.deleteFromRealm()
                realm.commitTransaction()


                setResult(104,intent)
                finish()

            }

            builder.setNegativeButton("취소") { _, _ ->

            }
            builder.show()

        }



    }

    private fun themeChange(colorCode:Int){

        window.statusBarColor = colorCode
        lesson_toolbar.setBackgroundColor(colorCode)

    }


}