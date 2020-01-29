package com.racoondog.mystudent

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.ContextThemeWrapper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.schedule_widget.*
import kotlinx.android.synthetic.main.weekview.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream



class MainActivity: AppCompatActivity() {

    //Developer: Void

    private val realm = Realm.getDefaultInstance()
    private val weekView by lazy { WeekView(this) }

    private var intentStartTime: Int = 0
    private var intentEndTime: Int = 0
    private var intentFlag: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(my_toolbar)  //Actionbar 부분
        supportActionBar?.setDisplayUseLogoEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        changeTheme()
        // 알림창 객체 생성

        loadData()//데이터 불러오기
        bitmap = R.drawable.color_picker_btn
        weekView_layout.setOnClickListener {
            val scheduleIntent = Intent(this, CreateSchedule::class.java)
            startActivityForResult(scheduleIntent, 100)

        }


        addSubjectButton.setOnClickListener {
            val subjectIntent = Intent(this, CreateSubject::class.java)

            subjectIntent.putExtra("start_time", intentStartTime)
            subjectIntent.putExtra("end_time", intentEndTime)
            subjectIntent.putExtra("day_flag", intentFlag)
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
                    intentFlag = scheduleDayFlag

                    realm.beginTransaction()
                    val dataBase: DataModel = realm.createObject(DataModel::class.java)

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
                    val subjectInfo: SubjectBox = realm.createObject(SubjectBox::class.java)
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
                        this.subjectColor = colorCode

                    }
                    realm.commitTransaction()

                    weekView.createSubject(
                        startHour, startTimeText[2].toInt()
                        , endHour, endTimeText[2].toInt(), dayFlag, intentStartTime, ID, colorCode
                    )
                }
                103 -> {
                    var dataBase: RealmResults<SubjectBox> =
                        realm.where<SubjectBox>(SubjectBox::class.java)
                            .equalTo("id", WeekViewData.ID)
                            .findAll()
                    val title = weekView.findViewWithTag<TextView>("title${dataBase.get(0)!!.id}")
                    title.text = dataBase.get(0)!!.title.toString()
                }

            }
        }
        if (resultCode == 104) {

            when (requestCode) {

                103 -> {
                    weekView.deleteSubject(WeekViewData.ID.toInt())
                }

            }

        }

    }

    private fun loadData() {

        val scheduleData = realm.where(DataModel::class.java).findFirst()
        if (scheduleData != null) {

            schedule_add.visibility = View.INVISIBLE
            addSubjectButton.visibility = View.VISIBLE
            toolbar_title.text = scheduleData.scheduleTitle

            intentFlag = scheduleData.scheduleDayFlag!!
            intentStartTime = scheduleData.scheduleStartHour!!
            intentEndTime = scheduleData.scheduleEndHour!!

            weekView.layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            ).apply {

            }

            weekView.drawSchedule(intentFlag, intentStartTime, intentEndTime)
            weekView_layout.addView(weekView)


            val subjectData: RealmResults<SubjectBox> =
                realm.where<SubjectBox>(SubjectBox::class.java).findAll()
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
        window.statusBarColor = resources.getColor(R.color.whiteColor)
        addSubjectButton.backgroundTintList = resources.getColorStateList(R.color.darkColor)

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
            R.id.setting -> {
                return true
            }
            R.id.saveImage -> {

                checkPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE){
                    val bitmap1 = getBitmapFromView(scheduleView, scheduleView.height, scheduleView.width)
                    val bitmap2 = getBitmapFromView(dayLine, dayLine.height, dayLine.width)
                    val bitmap3 = getBitmapFromView(my_toolbar, my_toolbar.height, my_toolbar.width)
                    val bitmap = combineImages(bitmap1, bitmap2, bitmap3)
                    saveBitmap(bitmap) }

                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()

    }

    private fun getBitmapFromView(view: View, height:Int, width:Int):Bitmap {


        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun combineImages(first:Bitmap, second:Bitmap,third:Bitmap ):Bitmap {

        val bitmap = Bitmap.createBitmap(first.width, first.height+second.height+third.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawBitmap(third, Matrix(), null)
        canvas.drawBitmap(second,0.toFloat(), third.height.toFloat(), null)
        canvas.drawBitmap(first, 0.toFloat(), third.height.toFloat()+second.height.toFloat(), null)

        return bitmap
    }

    private fun saveBitmap(bitmap:Bitmap) { // 버튼 onClick 리스너
        // WRITE_EXTERNAL_STORAGE 외부 공간 사용 권한 허용

        val fos:FileOutputStream // FileOutputStream 이용 파일 쓰기 한다
        val strFolderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/myStudent"
        val folder = File(strFolderPath)
        if (!folder.exists())
        { // 해당 폴더 없으면 만들어라
            folder.mkdirs()
        }
        val strFilePath = strFolderPath + "/" + System.currentTimeMillis() + ".png"
        val fileCacheItem = File(strFilePath)
        try
        {
            fos = FileOutputStream(fileCacheItem)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()
        }
        catch (e:FileNotFoundException) {
            e.printStackTrace()
        }
        finally
        {
            Toast.makeText(this, "시간표가 갤러리에 저장되었습니다.", Toast.LENGTH_SHORT).show()
            sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(File(strFilePath))))
        }

    }

    private fun checkPermissions(permission: String,result: ()->Unit){
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
            //권한 요청!
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

                            val dialog = AlertDialog.Builder(ContextThemeWrapper(this, R.style.Theme_AppCompat_Light_Dialog))
                                .setCancelable(false)
                                .setMessage("이 기능을 사용하기 위해서는 $permission 권한이 필요합니다. 계속 하시겠습니까?")
                                .setPositiveButton("확인") { _, _ ->
                                    ActivityCompat.requestPermissions(this, permissions,100) }

                            dialog.show()

                        }
                    }
                }

            }
        }
    }

}


