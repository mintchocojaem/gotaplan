package com.racoondog.mystudent

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.*
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import androidx.constraintlayout.widget.ConstraintLayout
import io.realm.Realm


class MainActivity: AppCompatActivity() {

    var intentStartTime: Int = 0
    var intentEndTime: Int = 0
    var intentflag : Int = 0
    val weekview by lazy {WeekView(this)}

    val realm = Realm.getDefaultInstance()

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

        val data = DataModel.ScheduleData
        val save = realm.where(DataModel::class.java).findFirst()

        if( save?.dataSaved == true){
            weekview.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT).apply {

            }

            weekview.lastDay = data.ScheduleDayFlag!!
            weekview.startTime = data.ScheduleStartHour!!
            weekview.endTime = data.ScheduleEndHour!!
            title_text.text = data.Title
            weekView.addView(weekview)
            schedule_add.visibility = View.INVISIBLE

        }





    }

    override fun onDestroy() {
        super.onDestroy()

        realm.beginTransaction()
        val data:DataModel = realm.createObject(DataModel::class.java)
        data.apply {
            this.dataSaved = true
        }
        realm.commitTransaction()

        realm.close()
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

                    realm.beginTransaction()
                    DataModel.ScheduleData.apply {
                        this.ScheduleDayFlag = scheduleDayFlag
                        this.ScheduleStartHour = scheduleStartHour
                        this.ScheduleEndHour = scheduleEndHour
                        this.Title = title_text.text.toString()
                    }
                    realm.commitTransaction()



                }
                101 -> {

                }
                102 -> {

                    val startHour = data!!.getIntExtra("StartHour", 0) // 24시 형식 시간
                    val endHour = data!!.getIntExtra("EndHour", 0)// 24시 형식 시간
                    val dayFlag = data!!.getIntExtra("DayFlag", 0)
                    val subjectTitle = data!!.getStringExtra("SubjectTitle")
                    val startTimeText = data!!.getStringArrayExtra("StartTimeText") // 오전/오후 형식 시간
                    val endTimeText = data!!.getStringArrayExtra("EndTimeText")
                    val contentText = data?.getStringExtra("ContentText")

                    val timeText = "${startTimeText[0]}${startTimeText[1]}:${startTimeText[2]}"+ " ~ " + "${endTimeText[0]}${endTimeText[1]}:${endTimeText[2]}" //StartTimeText[ ]은 오전/오후 변환시간

                    /*realm.beginTransaction()
                    val info:SubjectBox = realm.createObject(SubjectBox::class.java)
                    info.apply {
                        id = weekview.subjectID.toString()
                        title = subjectTitle
                        content = contentText
                        starthour = startHour.toString()
                        startminute = startTimeText[2]
                        endhour = endHour.toString()
                        endminute = endTimeText[2]
                        time = timeText
                    }
                    realm.commitTransaction()
                     */


                    SubjectData.TitleText = subjectTitle
                    SubjectData.TimeText = timeText
                    SubjectData.ContentText = contentText
                    SubjectData.id = weekview.subjectID
                    SubjectData.StartHour = startHour
                    SubjectData.StartMinute = startTimeText[2].toInt()
                    SubjectData.EndHour = endHour
                    SubjectData.EndMinute = endTimeText[2].toInt()
                    SubjectData.setData(SubjectData.id)



                    weekview.createSubject(startHour,startTimeText[2].toInt()
                        ,endHour,endTimeText[2].toInt(), dayFlag,intentStartTime)

                }
                103->{
                    val title = weekview.findViewWithTag<TextView>("title${SubjectData.id}")
                    title.text = SubjectData.SubjectInfo!![SubjectData.id]!![5].toString()
                }

            }
        }
        if (resultCode == 104) {

            when (requestCode) {

                103->{
                    weekview.deleteSubject(SubjectData.id.toInt())
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


