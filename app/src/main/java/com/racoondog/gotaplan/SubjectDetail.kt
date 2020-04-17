package com.racoondog.gotaplan


import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.transition.ChangeBounds
import android.transition.Transition
import android.transition.TransitionManager
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import io.realm.Realm
import io.realm.RealmResults

import kotlinx.android.synthetic.main.subject_detail.*


class SubjectDetail : AppCompatActivity() {

    private val realm = Realm.getDefaultInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.subject_detail)

        val scheduleData = realm.where(ScheduleData::class.java).findFirst()!!
        val subjectData: RealmResults<SubjectData> = realm.where<SubjectData>(SubjectData::class.java)
            .equalTo("id",WeekView.ID)
            .findAll()
        val data = subjectData[0]!!

        init()

        subject_detail_quit_btn.setOnClickListener {
            onBackPressed()
        }

        subject_detail_save_btn.setOnClickListener {

            val pickerData: RealmResults<SubjectData> = realm.where<SubjectData>(SubjectData::class.java)
                .equalTo("dayFlag",subject_detail_day_picker.dayFlag)
                .notEqualTo("id",WeekView.ID)
                .findAll()
            val nestedTime =  subject_detail_time_picker.nestedTime(pickerData)
            if(!nestedTime) {
                if(currentCycle_text.text.toString().toInt() > maxCycle_text.text.toString().toInt()){
                    Toast.makeText(this,"레슨 사이클의 현재값은 최댓값보다 클 수 없습니다.",Toast.LENGTH_SHORT).show()
                }
                else{
                    val linkageID = realm.where<SubjectData>(SubjectData::class.java).equalTo("linkageID",data.linkageID).findAll()
                    if (data.linkageID != 0){
                        for (i in linkageID.indices){
                            realm.beginTransaction()

                            linkageID[i]!!.subjectColor = subject_detail_color_picker.colorCode
                            linkageID[i]!!.title = subject_title.text.toString()
                            linkageID[i]!!.content = subject_memo.text.toString()

                            linkageID[i]!!.studentName = studentName_text.text.toString()
                            linkageID[i]!!.studentBirth = studentBirth_text.text.toString()
                            linkageID[i]!!.studentPhoneNumber = studentPhone_text.text.toString()
                            linkageID[i]!!.lessonCost = lessonCost_text.text.toString()
                            linkageID[i]!!.lessonOnOff = subject_detail_lesson_mode_switch.isChecked
                            linkageID[i]!!.notification = Notification.notificationFlag
                            linkageID[i]!!.currentCycle = currentCycle_text.text.toString().toInt()
                            linkageID[i]!!.maxCycle = maxCycle_text.text.toString().toInt()

                            realm.commitTransaction()

                            subject_detail_notification.deleteAlarm(linkageID[i]!!.id)
                            subject_detail_notification.setAlarm(linkageID[i]!!.startHour,linkageID[i]!!.startMinute.toInt(),linkageID[i]!!.dayFlag,linkageID[i]!!.id)
                        }
                    }else{
                        realm.beginTransaction()
                        data.dayFlag = subject_detail_day_picker.dayFlag
                        data.startHour = subject_detail_time_picker.startHour()
                        data.startMinute = subject_detail_time_picker.startMinute()
                        data.endHour = subject_detail_time_picker.endHour()
                        data.endMinute = subject_detail_time_picker.endMinute()
                        data.subjectColor = subject_detail_color_picker.colorCode
                        data.title = subject_title.text.toString()
                        data.content = subject_memo.text.toString()

                        data.studentName = studentName_text.text.toString()
                        data.studentBirth = studentBirth_text.text.toString()
                        data.studentPhoneNumber = studentPhone_text.text.toString()
                        data.lessonCost = lessonCost_text.text.toString()
                        data.lessonOnOff = subject_detail_lesson_mode_switch.isChecked
                        data.notification = Notification.notificationFlag
                        data.currentCycle = currentCycle_text.text.toString().toInt()
                        data.maxCycle = maxCycle_text.text.toString().toInt()
                        realm.commitTransaction()

                        subject_detail_notification.deleteAlarm(data.id)
                        subject_detail_notification.setAlarm(data.startHour,data.startMinute.toInt(),data.dayFlag,data.id)

                    }

                    setResult(Activity.RESULT_OK)
                    finish()

                }
            }

        }

        subject_detail_delete_btn.setOnClickListener {

            val builder = AlertDialog.Builder(this,R.style.MyDialogTheme)
                .setTitle(resources.getString(R.string.delete))
                .setMessage(resources.getString(R.string.delete_subject))
                .setPositiveButton(resources.getString(R.string.dialog_apply)) { _, _ ->

                    val linkageID = realm.where<SubjectData>(SubjectData::class.java).equalTo("linkageID",data.linkageID).findAll()
                    if(data.linkageID != 0){
                        for (i in linkageID.indices){

                            subject_detail_notification.deleteAlarm(linkageID[0]!!.id)
                            ((MainActivity.mContext) as MainActivity).weekView.deleteSubject(linkageID[0]!!.id)

                            realm.beginTransaction()
                            linkageID[0]!!.deleteFromRealm()
                            realm.commitTransaction()
                        }

                    }else{
                        subject_detail_notification.deleteAlarm(data.id)
                        ((MainActivity.mContext) as MainActivity).weekView.deleteSubject(data.id)
                        realm.beginTransaction()
                        data.deleteFromRealm()
                        realm.commitTransaction()
                    }

                    Toast.makeText(this,resources.getString(R.string.subject_deleted), Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK,intent)
                    finish()

                }

                .setNegativeButton(resources.getString(R.string.dialog_cancel)) { _, _ ->

                }
                .show()

            builder.window!!.attributes.apply {
                width = WindowManager.LayoutParams.WRAP_CONTENT
                height = WindowManager.LayoutParams.WRAP_CONTENT}

            builder.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            builder.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this,R.color.colorCancel))
            builder.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this,R.color.defaultAccentColor))

        }

        subject_detail_color_picker.initColor(data.subjectColor)
        subject_detail_color_picker.setOnClickListener {
            subject_detail_color_picker.colorPick(this,subject_detail_toolbar)
        }
        subject_detail_color_picker.setOnCustomEventListener(object :
            ColorPicker.OnCustomEventListener {
            override fun onEvent() {
                subject_detail_save_btn.visibility = View.VISIBLE
            }
        })

        subject_detail_day_picker.dayPick(scheduleData.scheduleDayFlag,data.dayFlag)
        subject_detail_day_picker.setOnCustomEventListener(object : DayPicker.OnCustomEventListener {
            override fun onEvent() {
                subject_detail_save_btn.visibility = View.VISIBLE
            }
        })

        subject_detail_time_picker.setCustomEventListener(object : TimePicker.OnCustomEventListener {
            override fun onEvent() {
                subject_detail_save_btn.visibility = View.VISIBLE
            }
        })

        subject_detail_time_picker.subjectPicker(scheduleData.scheduleStartHour,scheduleData.scheduleEndHour,subject_detail_main)
        subject_detail_time_picker.displayTime(data.startHour,data.startMinute.toInt(),data.endHour,data.endMinute.toInt())

        subject_detail_lesson_mode_switch.setOnCheckedChangeListener{compoundButton,_ ->

            subject_detail_save_btn.visibility = View.VISIBLE

            if (compoundButton.isChecked){
                val changeBounds: Transition = ChangeBounds()
                changeBounds.duration = 300
                TransitionManager.beginDelayedTransition(subject_detail_main, changeBounds)
                lesson_bar.visibility = View.VISIBLE
                Toast.makeText(this, "${resources.getString(R.string.lesson)}: On", Toast.LENGTH_SHORT).show()
            } else{
                val changeBounds: Transition = ChangeBounds()
                changeBounds.duration = 300
                TransitionManager.beginDelayedTransition(subject_detail_main, changeBounds)
                lesson_bar.visibility = View.GONE
                Toast.makeText(this, "${resources.getString(R.string.lesson)}: Off", Toast.LENGTH_SHORT).show()
            }
        }
        subject_detail_notification.setOnClickListener {
            subject_detail_notification.showDialog(this)
        }
        subject_detail_notification.setOnCustomEventListener(object : Notification.OnCustomEventListener {
            override fun onEvent() {
                subject_detail_save_btn.visibility = View.VISIBLE
            }
        })

        lesson_calculate.setOnClickListener {

            val linkageID = realm.where<SubjectData>(SubjectData::class.java).equalTo("linkageID",data.linkageID).findAll()
            var lessonCost = "정보 없음"
            if (data.lessonCost != "" ){
                lessonCost = data.lessonCost
            }

            val builder = AlertDialog.Builder(this,R.style.MyDialogTheme)
                .setTitle("수업비용 정산")
                .setMessage("이번달의 수업비용을 정산하시겠습니까?\n비용: $lessonCost")
                .setPositiveButton(resources.getString(R.string.dialog_apply)) { _, _ ->
                    if (data.linkageID != 0){
                        for (i in linkageID.indices){
                            realm.beginTransaction()
                            linkageID[i]!!.currentCycle = 0
                            realm.commitTransaction()
                        }
                    }else{
                        realm.beginTransaction()
                        data.currentCycle = 0
                        realm.commitTransaction()
                    }
                    currentCycle_text.setText("0")
                    Toast.makeText(this,"수업비용이 정산되었습니다!",Toast.LENGTH_SHORT).show()
                }

                .setNegativeButton(resources.getString(R.string.dialog_cancel)) { _, _ ->

                }
                .show()

        }

    }

    private fun themeChange(colorCode:Int){
        window.statusBarColor = colorCode
        subject_detail_toolbar.setBackgroundColor(colorCode)
    }

    private  fun init(){

        val subjectData: RealmResults<SubjectData> = realm.where<SubjectData>(SubjectData::class.java)
            .equalTo("id",WeekView.ID)
            .findAll()
        val data = subjectData[0]!!

        val textWatcher = object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if(start != after){
                    subject_detail_save_btn.visibility = View.VISIBLE
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(start != before){
                    subject_detail_save_btn.visibility = View.VISIBLE
                }
            }

        }

        subject_title.setText(data.title)
        subject_memo.setText(data.content)

        subject_title.addTextChangedListener(textWatcher)
        subject_memo.addTextChangedListener(textWatcher)

        studentName_text.setText(data.studentName)
        studentBirth_text.setText(data.studentBirth)
        studentPhone_text.setText(data.studentPhoneNumber)
        lessonCost_text.setText(data.lessonCost)
        currentCycle_text.setText(data.currentCycle.toString())
        maxCycle_text.setText(data.maxCycle.toString())



        studentName_text.addTextChangedListener(textWatcher)
        studentBirth_text.addTextChangedListener(textWatcher)
        studentPhone_text.addTextChangedListener(textWatcher)
        lessonCost_text.addTextChangedListener(textWatcher)
        currentCycle_text.addTextChangedListener(textWatcher)
        maxCycle_text.addTextChangedListener(textWatcher)

        themeChange(data.subjectColor)

        if(subjectData[0]!!.lessonOnOff) {
            subject_detail_lesson_mode_switch.isChecked = true
            lesson_bar.visibility = View.VISIBLE
        } else {
            subject_detail_lesson_mode_switch.isChecked = false
            lesson_bar.visibility = View.GONE
        }
        subject_detail_save_btn.visibility = View.INVISIBLE
        subject_detail_notification.setText(data.notification)
        Notification.notificationFlag = data.notification

        Toast.makeText(this,"${data.linkageID}",Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {

        if(subject_detail_time_picker.isOpened()) {
            subject_detail_time_picker.clearFocus()
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