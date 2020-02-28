package com.racoondog.gotaplan


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.create_subject.*
import kotlinx.android.synthetic.main.time_picker.*
import java.util.*


class CreateSubject :AppCompatActivity() {

    private val realm = Realm.getDefaultInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        super.setContentView(R.layout.create_subject)

        val colorList = resources.getIntArray(R.array.subject_color)
        val intentStartHour = intent.getIntExtra("start_time",0)
        val intentEndHour = intent.getIntExtra("end_time",0)
        val intentFlag = intent.getIntExtra("day_flag",0)

        subject_time_picker.subjectPicker(intentStartHour,intentEndHour)
        create_subject_day_picker.dayPick(intentFlag)

        randomSubjectColor(colorList)// subject color


        createSubject_Button.setOnClickListener{

            if(create_subject_day_picker.dayFlag != 0 ) {

                createSubject(create_subject_day_picker.dayFlag)

            } else{
                Toast.makeText(this, "날짜를 선택해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        lesson_mode.setOnCheckedChangeListener{compoundButton,_ ->

            if (compoundButton.isChecked){
                Toast.makeText(this, "개인 레슨: On", Toast.LENGTH_SHORT).show()
            } else{
                Toast.makeText(this, "개인 레슨: Off", Toast.LENGTH_SHORT).show()
            }
        }

        create_subject_color_picker.setOnClickListener {
            create_subject_color_picker.colorPick(this,createSubject_toolbar)
        }

        subjectQuit_Button.setOnClickListener {
            setResult(Activity.RESULT_CANCELED,intent)
            finish()
        }

    }

    private fun createSubject(dayFlag: Int){

        var subjectData: RealmResults<SubjectData> =
            realm.where<SubjectData>(SubjectData::class.java)
                .equalTo("dayFlag", dayFlag)
                .findAll()

        if(title_text.text.toString() !="") {

            if(!subject_time_picker.nestedTime(subjectData)){
                intent.putExtra("StartHour",start_hour.value )
                intent.putExtra("EndHour", end_hour.value)
                intent.putExtra("DayFlag", dayFlag)
                intent.putExtra("SubjectTitle", title_text.text.toString())
                intent.putExtra("LessonOnOff",lesson_mode.isChecked)
                intent.putExtra("StartMinute", startText_minute.text.toString())
                intent.putExtra("EndMinute", endText_minute.text.toString())
                intent.putExtra("ContentText",Content_text.text?.toString())
                intent.putExtra("colorCode", create_subject_color_picker.colorCode)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
        else {
            Toast.makeText(this, "제목을 입력해 주세요.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun randomSubjectColor(ColorList: IntArray){

        val random = Random()
        val number = random.nextInt(ColorList.size -1)

        create_subject_color_picker.colorCode = ColorList[number]
        create_subject_color_picker.initColor(create_subject_color_picker.colorCode)
        create_subject_color_picker.changeTheme(this,createSubject_toolbar,create_subject_color_picker.colorCode)

    }


    override fun onBackPressed() {

        if(subject_time_picker.isOpened()) {
            subject_time_picker.clearFocus()
        }
        else super.onBackPressed()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val v: View? = currentFocus
        if (v != null &&
            (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE) &&
            v is EditText &&
            !v.javaClass.name.startsWith("android.webkit.")
        ) {
            val scrcoords = IntArray(2)
            v.getLocationOnScreen(scrcoords)
            val x: Float = ev.rawX + v.getLeft() - scrcoords[0]
            val y: Float = ev.rawY + v.getTop() - scrcoords[1]
            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()) hideKeyboard(
                this
            )
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun hideKeyboard(activity: Activity?) {
        if (!(activity == null || activity.window == null)) {
            val imm =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)
        }
    }


}