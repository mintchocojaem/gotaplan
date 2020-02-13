package com.racoondog.mystudent

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity: AppCompatActivity() {

    //Developer: Void

    private val realm = Realm.getDefaultInstance()
    val weekView by lazy { WeekView(this) }

    var intentStartTime: Int = 0
    var intentEndTime: Int = 0
    var intentflag: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(main_toolbar)  //Actionbar 부분
        supportActionBar?.setDisplayUseLogoEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        loadData()//데이터 불러오기
        changeTheme()// 테마 변경

        weekView_layout.setOnClickListener {

            val scheduleData = realm.where(ScheduleData::class.java).findFirst()

            if (scheduleData == null) {
                val scheduleIntent = Intent(this, CreateSchedule::class.java)
                scheduleIntent.flags = (Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivityForResult(scheduleIntent, 100)
            }

        }


        addSubjectButton.setOnClickListener {
            val subjectIntent = Intent(this, CreateSubject::class.java)
            subjectIntent.flags = (Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            subjectIntent.putExtra("start_time", intentStartTime)
            subjectIntent.putExtra("end_time", intentEndTime)
            subjectIntent.putExtra("day_flag", intentflag)
            startActivityForResult(subjectIntent, 102)
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //MainActivity로 들어오는 onActivityResult 부분 -> Intent 후 값 반환
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                100 -> {


                    val scheduleDayFlag = data!!.getIntExtra("scheduleDayFlag", 0)
                    val scheduleStartHour = data.getIntExtra("scheduleStartHour", 0)
                    val scheduleEndHour = data.getIntExtra("scheduleEndHour", 0)

                    schedule_add.visibility = View.INVISIBLE
                    addSubjectButton.visibility = View.VISIBLE

                    toolbar_title.text = data.getStringExtra("title")

                    intentStartTime = scheduleStartHour
                    intentEndTime = scheduleEndHour
                    intentflag = scheduleDayFlag

                    realm.beginTransaction()
                    val dataBase: ScheduleData = realm.createObject(ScheduleData::class.java)

                    dataBase.apply {
                        this.scheduleDayFlag = scheduleDayFlag
                        this.scheduleStartHour = scheduleStartHour
                        this.scheduleEndHour = scheduleEndHour
                        this.scheduleTitle = toolbar_title.text.toString()
                    }
                    realm.commitTransaction()

                    weekView.layoutParams = ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.MATCH_PARENT,
                        ConstraintLayout.LayoutParams.MATCH_PARENT
                    ).apply {

                    }

                    weekView.drawSchedule(scheduleDayFlag, scheduleStartHour, scheduleEndHour)

                    weekView_layout.addView(weekView)
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
                    val lessonOnOff = data.getBooleanExtra("LessonOnOff", false)
                    val timeText =
                        "${startTimeText[0]}${startTimeText[1]}:${startTimeText[2]}" + " ~ " + "${endTimeText[0]}${endTimeText[1]}:${endTimeText[2]}" //StartTimeText[ ]은 오전/오후 변환시간
                    val colorCode = data.getIntExtra("colorCode", 0)

                    val ID = weekView.createID(0, 128)//다음으로 만들어질 weekview의 id 값을 결정하는 변수

                    realm.beginTransaction()
                    val subjectInfo: SubjectData = realm.createObject(SubjectData::class.java)
                    subjectInfo.apply {
                        this.id = ID.toInt()
                        this.dayFlag = dayFlag
                        this.startHour = startHour.toInt()
                        this.startMinute = startTimeText[2]
                        this.endHour = endHour.toInt()
                        this.endMinute = endTimeText[2]
                        this.title = subjectTitle
                        this.content = contentText
                        this.time = timeText
                        this.lessonOnOff = lessonOnOff
                        this.subjectColor = colorCode

                    }
                    realm.commitTransaction()

                    weekView.createSubject(
                        startHour, startTimeText[2].toInt()
                        , endHour, endTimeText[2].toInt(), dayFlag, intentStartTime, ID, colorCode
                    )
                }
                103 -> {
                  weekView.refresh(weekView)
                }
                105 ->{
                    val statusBarColor = data!!.getIntExtra("statusBarColor",0)
                    val mainButtonBarColor = data.getIntExtra("mainButtonColor",0)

                    val themeData = realm.where(ThemeData::class.java).findFirst()!!
                    realm.beginTransaction()
                    themeData.statusBarColor = statusBarColor
                    themeData.mainButtonColor = mainButtonBarColor
                    realm.commitTransaction()

                    changeTheme()// 테마 변경
                }

            }
        }
        if (resultCode == 104) {

            when (requestCode) {

                103 -> {
                    weekView.deleteSubject(WeekView.ID.toInt())
                }

            }

        }

    }

    fun loadData() {

        val scheduleData = realm.where(ScheduleData::class.java).findFirst()
        if (scheduleData != null) {



            schedule_add.visibility = View.INVISIBLE
            addSubjectButton.visibility = View.VISIBLE
            toolbar_title.text = scheduleData.scheduleTitle

            intentflag = scheduleData.scheduleDayFlag!!
            intentStartTime = scheduleData.scheduleStartHour!!
            intentEndTime = scheduleData.scheduleEndHour!!

            weekView.layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            ).apply {

            }

            weekView.drawSchedule(intentflag, intentStartTime, intentEndTime)
            weekView_layout.addView(weekView)


            val subjectData: RealmResults<SubjectData> =
                realm.where<SubjectData>(SubjectData::class.java).findAll()
            for (data in subjectData) {
                weekView.createSubject(
                    data.startHour.toInt(),
                    data.startMinute.toInt()
                    ,
                    data.endHour.toInt(),
                    data.endMinute.toInt(),
                    data.dayFlag.toInt(),
                    scheduleData.scheduleStartHour!!.toInt(),
                    data.id.toInt(),
                    data.subjectColor
                )
            }


        }


    }


    private fun changeTheme() {

        realm.beginTransaction()
        val Data = realm.createObject(ThemeData::class.java)
        realm.commitTransaction()

        val themeData = realm.where(ThemeData::class.java).findFirst()!!
        window.statusBarColor = themeData.statusBarColor
        addSubjectButton.backgroundTintList = ColorStateList.valueOf(themeData.mainButtonColor)

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean { //Menu 추가 부분
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean { //Menu 목록 부분
        when (item.itemId) {
            R.id.home -> {
                //onBackPressed()
                return true
            }
            R.id.scheduleSetting -> {
                if(weekView_layout.childCount == 2){
                    Toast.makeText(this,"시간표를 먼저 추가해 주세요.",Toast.LENGTH_SHORT).show()
                }else{
                    val dialog = ScheduleDialog(this)
                    dialog.cnxt = this
                    dialog.show()
                }
                return true
            }
            R.id.subjectSetting ->{
                if(weekView_layout.childCount == 2){
                    Toast.makeText(this,"시간표를 먼저 추가해 주세요.",Toast.LENGTH_SHORT).show()
                }else{
                    val dialog = SubjectDialog(this)
                    dialog.cnxt = this
                    dialog.show()
                }
                return true
            }

            R.id.themeSetting -> {

                val themeIntent = Intent(this, ThemeSetting::class.java)
                startActivityForResult(themeIntent,105)
                return true
            }

            R.id.license -> {
                val licenseIntent = Intent(this, License::class.java)
                startActivity(licenseIntent)
                return true
            }
            R.id.directFeedback -> {
                val directFeedbackIntent = Intent(this, DirectFeedback::class.java)
                startActivity(directFeedbackIntent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()

    }

    fun checkPermissions(permission: String,result: ()->Unit){

        //거절되었거나 아직 수락하지 않은 권한(퍼미션)을 저장할 문자열 배열 리스트
        //필요한 퍼미션들을 하나씩 끄집어내서 현재 권한을 받았는지 체크
        var rejectedPermissionList = ArrayList<String>()

        if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            //만약 권한이 없다면 rejectedPermissionList에 추가
            rejectedPermissionList.add(permission)

        }
        else{
            result()
        }

        //거절된 퍼미션이 있다면...
        if(permission in rejectedPermissionList){
            //권한 요청
            val array = arrayOfNulls<String>(rejectedPermissionList.size)
            ActivityCompat.requestPermissions(this, rejectedPermissionList.toArray(array),100)
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            100->{
                if(grantResults.isNotEmpty()) {
                    for((i, permission) in permissions.withIndex()) {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {

                            //권한 획득 실패

                            val dialog = AlertDialog.Builder(this)
                                .setCancelable(false)
                                .setMessage("다음 기능을 사용하기 위해서는 $permission 권한이 필요합니다. 계속 하시겠습니까?")
                                .setPositiveButton("확인") { _, _ ->
                                    ActivityCompat.requestPermissions(this, permissions,100) }
                                .show()
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.defaultAccentColor))

                        }
                    }
                }

            }
        }
    }

}


