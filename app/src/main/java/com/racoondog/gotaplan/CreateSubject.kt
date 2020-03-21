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
        val scheduleData = realm.where(ScheduleData::class.java).findFirst()!!
        val scheduleStartHour = scheduleData.scheduleStartHour
        val scheduleEndHour = scheduleData.scheduleEndHour
        val scheduleDayFlag = scheduleData.scheduleDayFlag

        var dayFlag = mutableListOf(false,false,false,false,false,false,false)

        subject_time_picker.subjectPicker(scheduleStartHour,scheduleEndHour,scroll_view_main)

        randomSubjectColor(colorList)// subject color

        createSubject_Button.setOnClickListener{
            if(title_text.text.toString() !="") {
                if(dayFlag.contains(true)) {

                    val flag = mutableListOf<Boolean>()

                    for (i in dayFlag.indices){

                        if(dayFlag[i]){
                            val subjectData: RealmResults<SubjectData> =
                                realm.where<SubjectData>(SubjectData::class.java)
                                    .equalTo("dayFlag", i+1)
                                    .findAll()

                            if(!subject_time_picker.nestedTime(subjectData)){
                                flag.add(true)
                            }else flag.add(false)

                        }

                    }
                    for (i in dayFlag.indices){
                        if(dayFlag[i]){
                            if(!flag.contains(false)){
                                if(dayFlag[i]) createSubject(i+1)
                            }
                        }
                    }
                    if(!flag.contains(false)){
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }


                } else{
                    Toast.makeText(this, resources.getString(R.string.choose_subject_day), Toast.LENGTH_SHORT).show()
                }
            }else {
                Toast.makeText(this, resources.getString(R.string.create_subject_toast_enter_title), Toast.LENGTH_SHORT).show()
            }
        }

        lesson_mode.setOnCheckedChangeListener{compoundButton,_ ->

            if (compoundButton.isChecked){

                Toast.makeText(this, "${resources.getString(R.string.lesson)}: On", Toast.LENGTH_SHORT).show()
            } else{

                Toast.makeText(this, "${resources.getString(R.string.lesson)}: Off", Toast.LENGTH_SHORT).show()
            }
        }

        create_subject_color_picker.setOnClickListener {
            create_subject_color_picker.colorPick(this,createSubject_toolbar)
        }

        subjectQuit_Button.setOnClickListener {
            setResult(Activity.RESULT_CANCELED,intent)
            finish()
        }

        monday_button.setOnClickListener {

            if(!dayFlag[0]) {
                dayFlag[0] = true
                monday_button.isChecked = true
            }
            else {
                dayFlag[0] = false
                monday_button.isChecked = false
            }
        }
        tuesday_button.setOnClickListener {

            if(!dayFlag[1]) {
                dayFlag[1] = true
                tuesday_button.isChecked = true
            }
            else {
                dayFlag[1]= false
                tuesday_button.isChecked = false
            }
        }
        wednesday_button.setOnClickListener {

            if(!dayFlag[2]) {
                dayFlag[2] = true
                wednesday_button.isChecked = true
            }
            else {
                dayFlag[2] = false
                wednesday_button.isChecked = false
            }
        }
        thursday_button.setOnClickListener {

            if(!dayFlag[3]) {
                dayFlag[3] = true
                thursday_button.isChecked = true
            }
            else {
                dayFlag[3] = false
                thursday_button.isChecked = false
            }
        }
        friday_button.setOnClickListener {

            if(!dayFlag[4]) {
                dayFlag[4] = true
                friday_button.isChecked = true
            }
            else {
                dayFlag[4] = false
                friday_button.isChecked = false
            }
        }
        saturday_button.setOnClickListener {

            if(!dayFlag[5]) {
                dayFlag[5] = true
                saturday_button.isChecked = true
            }
            else {
                dayFlag[5] = false
                saturday_button.isChecked = false
            }
        }
        sunday_button.setOnClickListener {

            if(!dayFlag[6]) {
                dayFlag[6] = true
                sunday_button.isChecked = true
            }
            else {
                dayFlag[6] = false
                sunday_button.isChecked = false
            }
        }


    }

    private fun createSubject(dayFlag: Int){

        val subjectData: RealmResults<SubjectData> =
            realm.where<SubjectData>(SubjectData::class.java)
                .equalTo("dayFlag", dayFlag)
                .findAll()

            if(!subject_time_picker.nestedTime(subjectData)){
                /*
                intent.putExtra("StartHour",start_hour.value )
                intent.putExtra("EndHour", end_hour.value)
                intent.putExtra("DayFlag", dayFlag)
                intent.putExtra("SubjectTitle", title_text.text.toString())
                intent.putExtra("LessonOnOff",lesson_mode.isChecked)
                intent.putExtra("StartMinute", startText_minute.text.toString())
                intent.putExtra("EndMinute", endText_minute.text.toString())
                intent.putExtra("ContentText",create_subject_memo.text?.toString())
                intent.putExtra("colorCode", create_subject_color_picker.colorCode)

                 */
                val context = MainActivity.mContext as MainActivity
                val id = context.weekView.createID(0, 128)//다음으로 만들어질 weekView 의 id 값을 결정하는 변수

                realm.beginTransaction()
                val subjectInfo: SubjectData = realm.createObject(SubjectData::class.java)
                subjectInfo.apply {
                    this.id = id
                    this.dayFlag = dayFlag
                    this.startHour = start_hour.value
                    this.startMinute = startText_minute.text.toString()
                    this.endHour = end_hour.value
                    this.endMinute = endText_minute.text.toString()
                    this.title = title_text.text.toString()
                    this.content = create_subject_memo.text?.toString()?:""
                    this.lessonOnOff = lesson_mode.isChecked
                    this.subjectColor = create_subject_color_picker.colorCode

                }
                realm.commitTransaction()
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