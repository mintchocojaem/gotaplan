package com.racoondog.gotaplan


import android.app.Activity
import android.app.AlertDialog
import android.content.*
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
import java.util.*


class SubjectDetail : AppCompatActivity() {

    private val realm = Realm.getDefaultInstance()
    companion object{
        var mContext:Context? = null

    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.subject_detail)
        mContext = this
        registerReceiver(mMessageReceiver, IntentFilter("refresh"))

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

            if(subject_title.text.toString() == ""){
                Toast.makeText(this,resources.getString(R.string.create_subject_toast_enter_title),Toast.LENGTH_SHORT).show()
            }
            else{

                val nestedTime =  subject_detail_time_picker.nestedTime(pickerData)
                if(!nestedTime) {

                    if(Notification.notificationFlag ==-1){
                        if(lesson_bar.visibility != View.VISIBLE && cycle_switch.isChecked){

                            saveData()

                            if (data.linkageID != 0){

                                val linkageID = realm.where<SubjectData>(SubjectData::class.java).equalTo("linkageID",data.linkageID).findAll()
                                for (i in linkageID.indices){
                                    realm.beginTransaction()
                                    linkageID[i]!!.calculation = false
                                    realm.commitTransaction()
                                }
                            }else{
                                realm.beginTransaction()
                                data.calculation = false
                                realm.commitTransaction()
                            }
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                        else if(lesson_bar.visibility == View.VISIBLE && cycle_switch.isChecked){

                            val builder = AlertDialog.Builder(this,R.style.MyDialogTheme)
                                .setTitle(resources.getString(R.string.guide_auto_help_title))
                                .setMessage(resources.getString(R.string.subject_detail_cycle_no_notification))
                                .setPositiveButton(resources.getString(R.string.dialog_apply)) { _, _ ->
                                    saveData()
                                    val linkageID = realm.where<SubjectData>(SubjectData::class.java).equalTo("linkageID",data.linkageID).findAll()

                                    if (data.linkageID != 0){
                                        for (i in linkageID.indices){
                                            realm.beginTransaction()
                                            linkageID[i]!!.calculation = false
                                            realm.commitTransaction()
                                        }
                                    }else{
                                        realm.beginTransaction()
                                        data.calculation = false
                                        realm.commitTransaction()
                                    }
                                    setResult(Activity.RESULT_OK)
                                    finish()
                                }

                                .setNegativeButton(resources.getString(R.string.dialog_cancel)) { _, _ ->

                                }
                                .setCancelable(false)
                                .show()

                        }
                        else{
                            saveData()
                            setResult(Activity.RESULT_OK)
                            finish()
                        }

                    }else {
                        saveData()
                        setResult(Activity.RESULT_OK)
                        finish()
                    }

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
                Toast.makeText(this, "${resources.getString(R.string.lesson)} On", Toast.LENGTH_SHORT).show()

            } else{
                val changeBounds: Transition = ChangeBounds()
                changeBounds.duration = 300
                TransitionManager.beginDelayedTransition(subject_detail_main, changeBounds)
                lesson_bar.visibility = View.GONE
                Toast.makeText(this, "${resources.getString(R.string.lesson)} Off", Toast.LENGTH_SHORT).show()
            }
        }
        cycle_switch.setOnCheckedChangeListener{compoundButton,_ ->

            if (compoundButton.isChecked){

                if(Notification.notificationFlag == -1){
                    Toast.makeText(this,resources.getString(R.string.cycle_dialog_no_notification),Toast.LENGTH_SHORT).show()
                    compoundButton.isChecked = false
                    val linkageID = realm.where<SubjectData>(SubjectData::class.java).equalTo("linkageID",data.linkageID).findAll()

                    if (data.linkageID != 0){
                        for (i in linkageID.indices){
                            realm.beginTransaction()
                            linkageID[i]!!.calculation = false
                            realm.commitTransaction()
                        }
                    }else{
                        realm.beginTransaction()
                        data.calculation = false
                        realm.commitTransaction()
                    }

                }else{
                    val dialog = CalculationDialog(this, object : CalculationDialog.ICustomDialogEventListener {
                        override fun customDialogEvent(flag: Int) {
                            // Do something with the value here, e.g. set a variable in the calling activity
                            val linkageID = realm.where<SubjectData>(SubjectData::class.java).equalTo("linkageID",data.linkageID).findAll()

                            if (data.linkageID != 0){
                                for (i in linkageID.indices){
                                    compoundButton.isChecked = flag ==1
                                    realm.beginTransaction()
                                    linkageID[i]!!.calculation = compoundButton.isChecked
                                    realm.commitTransaction()
                                }
                            }else{
                                compoundButton.isChecked = flag ==1
                                realm.beginTransaction()
                                data.calculation = compoundButton.isChecked
                                realm.commitTransaction()
                            }
                        }
                    })
                    dialog.show()
                }

            }else {
                val linkageID = realm.where<SubjectData>(SubjectData::class.java).equalTo("linkageID",data.linkageID).findAll()

                if (data.linkageID != 0){
                    for (i in linkageID.indices){
                        realm.beginTransaction()
                        linkageID[i]!!.calculation = false
                        realm.commitTransaction()
                    }
                }else{
                    realm.beginTransaction()
                    data.calculation = false
                    realm.commitTransaction()
                }
            }
        }
        lesson_help.setOnClickListener {
            val introIntent = Intent(this, IntroActivity::class.java)
            introIntent.action = "LessonModeGuide"
            this.startActivity(introIntent)
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

            if(data.maxCycle == 0 || data.currentCycle == 0){
                Toast.makeText(this,resources.getString(R.string.lesson_calculate_min),Toast.LENGTH_SHORT).show()
            }else if(data.maxCycle < data.currentCycle){
                Toast.makeText(this,resources.getString(R.string.lesson_calculate_max),Toast.LENGTH_SHORT).show()

            }else{
                val builder = AlertDialog.Builder(this, R.style.MyDialogTheme)
                    .setTitle(resources.getString(R.string.lesson_calculate))
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
                        Toast.makeText(this,resources.getString(R.string.lesson_calculated),Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton(resources.getString(R.string.dialog_cancel)) { _, _ ->

                    }
                if (data.lessonCost == ""){
                    val lessonCost = resources.getString(R.string.lesson_unknown)
                    val maxCycle = data.maxCycle
                    val currentCycle = data.currentCycle

                    val n: String = Locale.getDefault().displayLanguage
                    if (n.compareTo("한국어") == 0){
                        builder.setMessage("이번 달의 수업비용을 정산하시겠습니까?\n(이번 달 총 ${maxCycle}회 중 ${currentCycle}회 수업하셨습니다)\n\n" +
                                "비용: $lessonCost")
                    }
                    else {
                        builder.setMessage("Are you willing to calculate this month's tuition?\n(You have taken ${currentCycle} of the ${maxCycle} Lessons this month)\n\n" +
                                "Cost: $lessonCost")
                    }

                }
                else{
                    val lessonCost = data.lessonCost.toInt()
                    val maxCycle = data.maxCycle
                    val currentCycle = data.currentCycle
                    val rest = (lessonCost.toFloat()%maxCycle)/maxCycle

                    val n: String = Locale.getDefault().displayLanguage
                    if (n.compareTo("한국어") == 0){
                        if(rest == 0f){
                            builder.setMessage("이번 달의 수업비용을 정산하시겠습니까?\n(이번 달 총 ${maxCycle}회 중 ${currentCycle}회 수업하셨습니다)\n\n" +
                                    "비용: ${lessonCost} - (${(lessonCost/maxCycle)} * ${maxCycle - currentCycle})" +
                                    "\n= ${lessonCost - ((lessonCost/maxCycle)*(maxCycle - currentCycle))}")
                        }else{
                            builder.setMessage("이번 달의 수업비용을 정산하시겠습니까?\n(이번 달 총 ${maxCycle}회 중 ${currentCycle}회 수업하셨습니다)\n\n" +
                                    "비용: ${lessonCost} - (${(lessonCost.toFloat()/maxCycle)} * ${maxCycle - currentCycle})" +
                                    "\n= ${lessonCost - ((lessonCost/maxCycle + rest)*(maxCycle - currentCycle))}")
                        }
                    }
                    else {
                        if(rest == 0f){
                            builder.setMessage("Are you willing to calculate this month's tuition?\n(You have taken ${currentCycle} of the ${maxCycle} Lessons this month)\n\n" +
                                    "Cost: ${lessonCost} - (${(lessonCost/maxCycle)} * ${maxCycle - currentCycle})" +
                                    "\n= ${lessonCost - ((lessonCost/maxCycle)*(maxCycle - currentCycle))}")
                        }else{
                            builder.setMessage("Are you willing to calculate this month's tuition?\n(You have taken ${currentCycle} of the ${maxCycle} Lessons this month)\n\n" +
                                    "Cost: ${lessonCost} - (${(lessonCost/maxCycle)} * ${maxCycle - currentCycle})" +
                                    "\n= ${lessonCost - ((lessonCost/maxCycle + rest)*(maxCycle - currentCycle))}")

                        }

                    }

                }

                builder.show()

            }

        }

        currentCycle_text.setOnClickListener {
            val builder = CycleDialog(this)
            builder.show()
            builder.setTitle(resources.getString(R.string.cycle_dialog_title_current))
            builder.setHint("${data.currentCycle}")
            builder.setContent("${data.currentCycle}")
            builder.setPositiveButton(View.OnClickListener {

                if(builder.getText() > maxCycle_text.text.toString().toInt()){
                    Toast.makeText(this,resources.getString(R.string.cycle_dialog_max),Toast.LENGTH_LONG).show()
                }
                else{
                    val linkageID = realm.where<SubjectData>(SubjectData::class.java).equalTo("linkageID",data.linkageID).findAll()
                    if (data.linkageID != 0) {
                        for (i in linkageID.indices) {
                            realm.beginTransaction()
                            linkageID[i]!!.currentCycle = builder.getText()
                            realm.commitTransaction()
                            builder.dismiss()
                            currentCycle_text.text = builder.getText().toString()
                        }
                    }else{
                        realm.beginTransaction()
                        data.currentCycle = builder.getText()
                        realm.commitTransaction()
                        builder.dismiss()
                        currentCycle_text.text = builder.getText().toString()
                    }
                }
                
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

            })
            builder.setNegativeButton(View.OnClickListener {

                builder.dismiss()
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

            })

        }
        maxCycle_text.setOnClickListener {

            val builder = CycleDialog(this)
            builder.show()
            builder.setTitle(resources.getString(R.string.cycle_dialog_title_month))
            builder.setHint("${data.maxCycle}")
            builder.setContent("${data.maxCycle}")
            builder.setPositiveButton(View.OnClickListener {
                if(currentCycle_text.text.toString().toInt() > builder.getText()){
                    Toast.makeText(this,resources.getString(R.string.cycle_dialog_min),Toast.LENGTH_LONG).show()
                }else{
                    val linkageID = realm.where<SubjectData>(SubjectData::class.java).equalTo("linkageID",data.linkageID).findAll()
                    if (data.linkageID != 0) {
                        for (i in linkageID.indices) {
                            realm.beginTransaction()
                            linkageID[i]!!.maxCycle = builder.getText()
                            realm.commitTransaction()
                            builder.dismiss()
                            maxCycle_text.text = builder.getText().toString()
                        }
                    }else{
                        realm.beginTransaction()
                        data.maxCycle = builder.getText()
                        realm.commitTransaction()
                        builder.dismiss()
                        maxCycle_text.text = builder.getText().toString()
                    }
                }

            })
            builder.setNegativeButton(View.OnClickListener {

                builder.dismiss()

            })
        }

    }


    private fun saveData(){

        val subjectData: RealmResults<SubjectData> = realm.where<SubjectData>(SubjectData::class.java)
            .equalTo("id",WeekView.ID)
            .findAll()
        val data = subjectData[0]!!
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
                //linkageID[i]!!.currentCycle = currentCycle_text.text.toString().toInt()
                //linkageID[i]!!.maxCycle = maxCycle_text.text.toString().toInt()
                linkageID[i]!!.calculation = cycle_switch.isChecked

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
            //data.currentCycle = currentCycle_text.text.toString().toInt()
            //data.maxCycle = maxCycle_text.text.toString().toInt()
            data.calculation = cycle_switch.isChecked
            realm.commitTransaction()

            subject_detail_notification.deleteAlarm(data.id)
            subject_detail_notification.setAlarm(data.startHour,data.startMinute.toInt(),data.dayFlag,data.id)

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
        //currentCycle_text.addTextChangedListener(textWatcher)
        //maxCycle_text.addTextChangedListener(textWatcher)

        themeChange(data.subjectColor)

        if(subjectData[0]!!.lessonOnOff) {
            subject_detail_lesson_mode_switch.isChecked = true
            lesson_bar.visibility = View.VISIBLE
        } else {
            subject_detail_lesson_mode_switch.isChecked = false
            lesson_bar.visibility = View.GONE
        }
        cycle_switch.isChecked = subjectData[0]!!.calculation

        subject_detail_save_btn.visibility = View.INVISIBLE
        subject_detail_notification.setText(data.notification)
        Notification.notificationFlag = data.notification

    }

    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mMessageReceiver)
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