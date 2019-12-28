package com.racoondog.mystudent

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.subject_detail.*


class SubjectDetail : AppCompatActivity() {

    val realm = Realm.getDefaultInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.subject_detail)

        var saveEditFlag: Boolean = false

        var data: RealmResults<SubjectBox> = realm.where<SubjectBox>(SubjectBox::class.java)
            .equalTo("id",WeekViewData.ID)
            .findAll()
        subject_time.text = data.get(0)!!.time.toString()
        subject_title.setText(data.get(0)!!.title.toString())
        subject_content.setText(data.get(0)!!.content.toString())

        //Toast.makeText(this,"${WeekViewData.nextID}",Toast.LENGTH_SHORT).show()

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

                realm.beginTransaction()
                data.get(0)!!.title = subject_title.text.toString()
                data.get(0)!!.content = subject_content.text.toString()
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
                data.get(0)!!.deleteFromRealm()
                realm.commitTransaction()

                /*val nextID: RealmResults<SubjectBox> = realm.where<SubjectBox>(SubjectBox::class.java).distinct("id").findAll()
                for (i in 0 until nextID.size){
                    if(i.toString() != nextID[i].toString()) {
                        WeekViewData.nextID = i
                        break
                    }
                    if(i.toString() == nextID[i].toString()){
                        WeekViewData.nextID = nextID.size+1
                    }
                }

                 */

                setResult(104,intent)
                finish()

            }

            builder.setNegativeButton("취소") { _, _ ->

            }
            builder.show()

        }



    }


}