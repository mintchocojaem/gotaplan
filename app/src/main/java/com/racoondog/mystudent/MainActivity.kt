package com.racoondog.mystudent

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import androidx.constraintlayout.widget.ConstraintLayout
import io.realm.Realm
import io.realm.kotlin.oneOf


class MainActivity: AppCompatActivity() {

    var intentStartTime: Int = 0
    var intentEndTime: Int = 0
    var intentflag : Int = 0
    val weekview by lazy {WeekView(this)}



    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(my_toolbar)  //Actionbar 부분
        supportActionBar?.setDisplayUseLogoEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        // 알림창 객체 생성

        schedule_add.setOnClickListener {
            val scheduleIntent = Intent(this, CreateSchedule::class.java)
            startActivityForResult(scheduleIntent, 100)

        }


        add_subject.setOnClickListener {
            val subjectIntent = Intent(this, CreateSubject::class.java)

            subjectIntent.putExtra("start_time", intentStartTime)
            subjectIntent.putExtra("end_time", intentEndTime)
            subjectIntent.putExtra("day_flag",intentflag)
            startActivityForResult(subjectIntent, 102)
        }

    }

    //MainActivity로 들어오는 onActivityResult 부분 -> Intent 후 값 반환

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                100 -> {

                    schedule_add.visibility = View.INVISIBLE

                    title_text.text = data!!.getStringExtra("title")
                    val scheduleDayFlag = data.getIntExtra("scheduleDayFlag", 0)
                    val scheduleStartHour = data.getIntExtra("scheduleStartHour", 0)
                    val scheduleEndHour = data.getIntExtra("scheduleEndHour", 0)

                    weekview.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                        ConstraintLayout.LayoutParams.MATCH_PARENT).apply {

                    }

                    weekview.lastDay = scheduleDayFlag
                    weekview.startTime = scheduleStartHour
                    weekview.endTime = scheduleEndHour

                    weekView.addView(weekview)

                    intentStartTime = scheduleStartHour
                    intentEndTime = scheduleEndHour
                    intentflag = scheduleDayFlag

                    Realm.init(this)
                    val realm = Realm.getDefaultInstance()
                    realm.beginTransaction()
                    val subjectDataSave:SubjectInfo = realm.createObject(SubjectInfo::class.java)
                    subjectDataSave.apply {
                        this.ScheduleDayFlag = scheduleDayFlag
                        this.ScheduleStartHour = scheduleStartHour
                        this.ScheduleEndHour = scheduleEndHour
                    }
                    realm.commitTransaction()
                    Toast.makeText(this,"$subjectDataSave",Toast.LENGTH_SHORT).show()


                }
                101 -> {

                }
                102 -> {

                    val startHour = data!!.getIntExtra("StartHour", 0) // 24시 형식 시간
                    val endHour = data!!.getIntExtra("EndHour", 0)// 24시 형식 시간
                    val DayFlag = data!!.getIntExtra("DayFlag", 0)
                    val SubjectTitle = data!!.getStringExtra("SubjectTitle")
                    val StartTimeText = data!!.getStringArrayExtra("StartTimeText") // 24시 형식 시간
                    val EndTimeText = data!!.getStringArrayExtra("EndTimeText")
                    val ContentText = data?.getStringExtra("ContentText")

                    val startTimeText = "${StartTimeText[0]}${StartTimeText[1]}:${StartTimeText[2]}"
                    val endTimeText = "${EndTimeText[0]}${EndTimeText[1]}:${EndTimeText[2]}"


                    SubjectData.SubjectTitle = SubjectTitle
                    SubjectData.StartTimeText = startTimeText
                    SubjectData.EndTimeText = endTimeText
                    SubjectData.ContentText = ContentText
                    SubjectData.id = weekview.subjectID
                    SubjectData.setData(SubjectData.id)

                    weekview.createSubject(startHour,StartTimeText[2].toInt()
                        ,endHour,EndTimeText[2].toInt(), DayFlag,intentStartTime)

                }
                103->{
                    val title = weekview.findViewWithTag<TextView>("title${SubjectData.id}")
                    title.text = SubjectData.SubjectInfo!![SubjectData.id]!![0]
                }

            }
        }
        if (resultCode == 104) {

            when (requestCode) {

                103->{
                    weekview.deleteSubject(SubjectData.id)
                }

            }

        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean { //Menu 추가 부분
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean { //Menu 목록 부분
        when (item.getItemId()) {
            R.id.home -> {
                //onBackPressed()
                return true
            }
            R.id.setting -> {
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }
}


