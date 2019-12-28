package com.racoondog.mystudent

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_main.*

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

        val scheduleData = realm.where(DataModel::class.java).findFirst()
        if( scheduleData?.dataSaved == true){
            weekview.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT).apply {

            }

            weekview.lastDay = scheduleData.scheduleDayFlag!!
            weekview.startTime = scheduleData.scheduleStartHour!!
            weekview.endTime = scheduleData.scheduleEndHour!!
            title_text.text = scheduleData.scheduleTitle
            weekView.addView(weekview)
            schedule_add.visibility = View.INVISIBLE

            val subjectData: RealmResults<SubjectBox> = realm.where<SubjectBox>(SubjectBox::class.java).findAll()
            for (data in subjectData) {
                weekview.createSubject(
                    data.startHour.toInt(), data.startMinute.toInt()
                    , data.endHour.toInt(), data.endMinute.toInt(),
                    data.dayFlag.toInt(), scheduleData.scheduleStartHour!!.toInt())
            }


        }







    }

    override fun onDestroy() {
        super.onDestroy()

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
                    val data:DataModel = realm.createObject(DataModel::class.java)

                    data.apply {
                        this.dataSaved = true
                        this.scheduleDayFlag = scheduleDayFlag
                        this.scheduleStartHour = scheduleStartHour
                        this.scheduleEndHour = scheduleEndHour
                        this.scheduleTitle = title_text.text.toString()
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


                    realm.beginTransaction()

                    val subjectInfo :SubjectBox = realm.createObject(SubjectBox::class.java)
                    subjectInfo.apply {
                        this.id = weekview.subjectID.toInt()
                        this.dayFlag = dayFlag.toString()
                        this.startHour = startHour.toString()
                        this.startMinute = startTimeText[2]
                        this.endHour = endHour.toString()
                        this.endMinute = endTimeText[2]
                        this.title = subjectTitle
                        this.content = contentText
                        this.time = timeText

                    }
                    realm.commitTransaction()

                    /*
                    SubjectData.TitleText = subjectTitle
                    SubjectData.TimeText = timeText
                    SubjectData.ContentText = contentText
                    SubjectData.id = weekview.subjectID
                    SubjectData.StartHour = startHour
                    SubjectData.StartMinute = startTimeText[2].toInt()
                    SubjectData.EndHour = endHour
                    SubjectData.EndMinute = endTimeText[2].toInt()
                    SubjectData.setData(SubjectData.id)

                     */


                    var data:RealmResults<SubjectBox> = realm.where<SubjectBox>(SubjectBox::class.java)
                        .equalTo("id",WeekViewData.ID)
                        .findAll()

                    weekview.createSubject(startHour,startTimeText[2].toInt()
                        ,endHour,endTimeText[2].toInt(), dayFlag,intentStartTime)



                }
                103->{
                    var data: RealmResults<SubjectBox> = realm.where<SubjectBox>(SubjectBox::class.java)
                        .equalTo("id",WeekViewData.ID)
                        .findAll()
                    val title = weekview.findViewWithTag<TextView>("title${data.get(0)!!.id}")
                    title.text = data.get(0)!!.title.toString()
                }

            }
        }
        if (resultCode == 104) {

            when (requestCode) {

                103->{

                    weekview.deleteSubject(WeekViewData.ID.toInt())
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


