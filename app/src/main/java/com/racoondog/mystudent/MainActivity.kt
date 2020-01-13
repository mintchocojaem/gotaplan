package com.racoondog.mystudent

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity: AppCompatActivity() {

    var intentStartTime: Int = 0
    var intentEndTime: Int = 0
    var intentflag : Int = 0
    private val weekView by lazy {WeekView(this)}

    private val realm = Realm.getDefaultInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadData()//데이터 불러오기

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //MainActivity로 들어오는 onActivityResult 부분 -> Intent 후 값 반환
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                100 -> {

                    schedule_add.visibility = View.INVISIBLE
                    add_subject.visibility = View.VISIBLE

                    toolbar_title.text = data!!.getStringExtra("title")
                    val scheduleDayFlag = data.getIntExtra("scheduleDayFlag", 0)
                    val scheduleStartHour = data.getIntExtra("scheduleStartHour", 0)
                    val scheduleEndHour = data.getIntExtra("scheduleEndHour", 0)

                    weekView.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                        ConstraintLayout.LayoutParams.MATCH_PARENT).apply {

                    }

                    weekView.lastDay = scheduleDayFlag
                    weekView.startTime = scheduleStartHour
                    weekView.endTime = scheduleEndHour

                    weekView_layout.addView(weekView)

                    intentStartTime = scheduleStartHour
                    intentEndTime = scheduleEndHour
                    intentflag = scheduleDayFlag

                    realm.beginTransaction()
                    val dataBase:DataModel = realm.createObject(DataModel::class.java)

                    dataBase.apply {
                        this.scheduleDayFlag = scheduleDayFlag
                        this.scheduleStartHour = scheduleStartHour
                        this.scheduleEndHour = scheduleEndHour
                        this.scheduleTitle = toolbar_title.text.toString()
                    }
                    realm.commitTransaction()
                }
                101 -> {

                }
                102 -> {

                    val startHour = data!!.getIntExtra("StartHour", 0) // 24시 형식 시간
                    val endHour = data.getIntExtra("EndHour", 0)// 24시 형식 시간
                    val dayFlag = data.getIntExtra("DayFlag", 0)
                    val subjectTitle = data.getStringExtra("SubjectTitle")
                    val startTimeText = data.getStringArrayExtra("StartTimeText") // 오전/오후 형식 시간
                    val endTimeText = data.getStringArrayExtra("EndTimeText")
                    val contentText = data.getStringExtra("ContentText")
                    val lessonOnOff = data.getBooleanExtra("LessonOnOff",false)
                    val timeText = "${startTimeText[0]}${startTimeText[1]}:${startTimeText[2]}"+ " ~ " + "${endTimeText[0]}${endTimeText[1]}:${endTimeText[2]}" //StartTimeText[ ]은 오전/오후 변환시간

                    val ID = weekView.createID(0,128)//다음으로 만들어질 weekview의 id 값을 결정하는 변수

                    realm.beginTransaction()
                    val subjectInfo :SubjectBox = realm.createObject(SubjectBox::class.java)
                    subjectInfo.apply {
                        this.id = ID.toInt()
                        this.dayFlag = dayFlag.toString()
                        this.startHour = startHour.toString()
                        this.startMinute = startTimeText[2]
                        this.endHour = endHour.toString()
                        this.endMinute = endTimeText[2]
                        this.title = subjectTitle
                        this.content = contentText
                        this.time = timeText
                        this.lessonOnOff = lessonOnOff

                    }
                    realm.commitTransaction()

                    weekView.createSubject(startHour,startTimeText[2].toInt()
                        ,endHour,endTimeText[2].toInt(), dayFlag,intentStartTime,ID)
                }
                103->{
                    var dataBase: RealmResults<SubjectBox> = realm.where<SubjectBox>(SubjectBox::class.java)
                        .equalTo("id",WeekViewData.ID)
                        .findAll()
                    val title = weekView.findViewWithTag<TextView>("title${dataBase.get(0)!!.id}")
                    title.text = dataBase.get(0)!!.title.toString()
                }

            }
        }
        if (resultCode == 104) {

            when (requestCode) {

                103->{
                    weekView.deleteSubject(WeekViewData.ID.toInt())
                }

            }

        }

    }
    private fun loadData(){

        val scheduleData = realm.where(DataModel::class.java).findFirst()
        if( scheduleData != null) {

            schedule_add.visibility = View.INVISIBLE
            add_subject.visibility = View.VISIBLE

            intentStartTime = scheduleData.scheduleStartHour!!
            intentEndTime = scheduleData.scheduleEndHour!!
            intentflag = scheduleData.scheduleDayFlag!!

            weekView.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT).apply {

            }

            weekView.lastDay = scheduleData.scheduleDayFlag!!
            weekView.startTime = scheduleData.scheduleStartHour!!
            weekView.endTime = scheduleData.scheduleEndHour!!
            toolbar_title.text = scheduleData.scheduleTitle
            weekView_layout.addView(weekView)
            schedule_add.visibility = View.INVISIBLE

            val subjectData: RealmResults<SubjectBox> = realm.where<SubjectBox>(SubjectBox::class.java).findAll()
            for (data in subjectData) {
                weekView.createSubject(
                    data.startHour.toInt(), data.startMinute.toInt()
                    , data.endHour.toInt(), data.endMinute.toInt(),
                    data.dayFlag.toInt(), scheduleData.scheduleStartHour!!.toInt(),data.id.toInt())
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

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }


}


