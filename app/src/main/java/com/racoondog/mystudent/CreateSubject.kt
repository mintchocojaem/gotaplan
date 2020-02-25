package com.racoondog.mystudent


import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.racoondog.mystudent.ColorPickerDialog.ICustomDialogEventListener
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import kotlinx.android.synthetic.main.create_subject.*
import kotlinx.android.synthetic.main.time_picker.*
import java.util.*


class CreateSubject :AppCompatActivity() {

    private val realm = Realm.getDefaultInstance()
    private var colorCode = -1 // init color White

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        super.setContentView(R.layout.create_subject)

        val colorList = resources.getIntArray(R.array.subject_color)
        val intentStartHour = intent.getIntExtra("start_time",0)
        val intentEndHour = intent.getIntExtra("end_time",0)
        subject_picker.subjectPicker(intentStartHour,intentEndHour)

        val intentFlag = intent.getIntExtra("day_flag",0)
        //val subjectFlag = intent.getIntExtra("subject_day_flag",0)

        var dayFlag = intent.getIntExtra("subject_day_flag",0)

        when(dayFlag){
            1 -> monday_button.isChecked = true
            2 -> tuesday_button.isChecked = true
            3 -> wednesday_button.isChecked = true
            4 -> thursday_button.isChecked = true
            5 -> friday_button.isChecked = true
            6 -> saturday_button.isChecked = true
            7 -> sunday_button.isChecked = true
        }

        if (intentFlag == 6){
            saturday_button.visibility = View.VISIBLE
        }
        else if (intentFlag == 7){
            saturday_button.visibility = View.VISIBLE
            sunday_button.visibility = View.VISIBLE
        }

        randomSubjectColor(colorList)// subject color


        createSubject_Button.setOnClickListener{
            if(dayFlag != 0 ) {
                if (end_hour.value == start_hour.value){
                    if(end_minute.displayedValues[end_minute.value].toInt() < start_minute.displayedValues[start_minute.value].toInt()){
                        Toast.makeText(this, "시작 시각이 종료 시각보다 클 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }else if(start_minute.value == end_minute.value){
                        Toast.makeText(this, "시작 시각이 종료 시각과 같을 수 없습니다.", Toast.LENGTH_SHORT).show()
                    } else if((end_minute.displayedValues[end_minute.value].toInt()  - start_minute.displayedValues[start_minute.value].toInt() < 30)) {
                        Toast.makeText(this, "각 과목의 최소 시간은 30분입니다.", Toast.LENGTH_SHORT).show()
                    }else createSubject(dayFlag)

                } else if(end_hour.value - start_hour.value == 1){

                    if(end_minute.displayedValues[end_minute.value].toInt()+(60-start_minute.displayedValues[start_minute.value].toInt()) < 30) {
                        Toast.makeText(this, "각 과목의 최소 시간은 30분입니다.", Toast.LENGTH_SHORT).show()
                    } else createSubject(dayFlag)

                }
                else if (end_hour.value < start_hour.value){
                    Toast.makeText(this, "시작 시각이 종료 시각보다 클 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
                else {

                    createSubject(dayFlag)

                }

            }
            else{
                Toast.makeText(this, "날짜를 선택해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        monday_button.setOnClickListener {
            dayFlag = 1
        }
        tuesday_button.setOnClickListener {
            dayFlag = 2
        }
        wednesday_button.setOnClickListener {
            dayFlag = 3
        }
        thursday_button.setOnClickListener {
            dayFlag = 4
        }
        friday_button.setOnClickListener {
            dayFlag = 5
        }
        saturday_button.setOnClickListener {
            dayFlag = 6
        }
        sunday_button.setOnClickListener {
            dayFlag = 7
        }

        lesson_mode.setOnCheckedChangeListener{compoundButton,b ->
            title_text.hideKeyboard()
            Content_text.hideKeyboard()

            if (compoundButton.isChecked){
                Toast.makeText(this, "개인 레슨: On", Toast.LENGTH_SHORT).show()
            } else{
                Toast.makeText(this, "개인 레슨: Off", Toast.LENGTH_SHORT).show()
            }
        }

        subject_day_group.setOnCheckedChangeListener{_,_->
            title_text.hideKeyboard()
            Content_text.hideKeyboard()
        }

        colorPickerButton_layout.setOnClickListener {

            val dialog = ColorPickerDialog(this, object : ICustomDialogEventListener {
                override fun customDialogEvent(colorcode: Int) {
                    // Do something with the value here, e.g. set a variable in the calling activity
                    colorCode = colorcode
                    colorPickerButton.backgroundTintList = ColorStateList.valueOf(colorCode)
                    changeTheme(colorCode)
                }
            })
            dialog.show()
        }

        subjectQuit_Button.setOnClickListener {
            setResult(Activity.RESULT_CANCELED,intent)
            finish()
        }

    }

    private fun checkTime(dayFlag:Int):Boolean{

        var subjectData: RealmResults<SubjectData> =
            realm.where<SubjectData>(SubjectData::class.java)
                .equalTo("dayFlag", dayFlag)
                .findAll()
        val data = subjectData.sort("startHour",Sort.ASCENDING)

        val pickerTime = arrayListOf<Double>()

        pickerTime.add(start_hour.value.toDouble() + (start_minute.displayedValues[start_minute.value].toDouble() / 100))
        pickerTime.add(end_hour.value.toDouble() + (end_minute.displayedValues[end_minute.value].toDouble() / 100))

        val checkTime = arrayListOf<Boolean>()

        if(data.size != 0){

            for ( i in data.indices){

                val subjectTime = arrayListOf<Double>()

                subjectTime.add(data[i]!!.startHour.toDouble()+ (data[i]!!.startMinute.toDouble() / 100))
                subjectTime.add(data[i]!!.endHour.toDouble()+ (data[i]!!.endMinute.toDouble() / 100))

                var checkFlag = when{

                    pickerTime[0] >= subjectTime[1] -> true
                    pickerTime[0] < subjectTime[0] -> pickerTime[1] <= subjectTime[0]
                    else -> false

                }

                checkTime.add(checkFlag)

            }

        }else  checkTime.add(true)

        return checkTime.contains(element = false) // checkTime = true -> 시간표 겹침

    }


    private fun createSubject(dayFlag: Int){
        if(title_text.text.toString() !="") {

            if(checkTime(dayFlag)){
                Toast.makeText(this,"해당 시간에 다른 과목이 존재합니다.",Toast.LENGTH_SHORT).show()
            } else {
                intent.putExtra("StartHour",start_hour.value )
                intent.putExtra("EndHour", end_hour.value)
                intent.putExtra("DayFlag", dayFlag)
                intent.putExtra("SubjectTitle", title_text.text.toString())
                intent.putExtra("LessonOnOff",lesson_mode.isChecked)
                intent.putExtra("StartMinute", startText_minute.text.toString())

                intent.putExtra("EndMinute", endText_minute.text.toString())

                intent.putExtra("ContentText",Content_text.text?.toString())
                intent.putExtra("colorCode", this.colorCode)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
        else {
            Toast.makeText(this, "제목을 입력해 주세요.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun changeTheme(colorList:Int){
        window.statusBarColor = colorList
        createSubject_toolbar.setBackgroundColor(colorList)
    }

    private fun randomSubjectColor(ColorList: IntArray){

        val random = Random()
        val number = random.nextInt(ColorList.size -1)

        colorCode = ColorList[number]
        colorPickerButton.backgroundTintList = ColorStateList.valueOf(colorCode)
        changeTheme(colorCode)

    }


    override fun onBackPressed() {

        if(time_picker.visibility == View.VISIBLE) {
            time_picker.visibility = View.GONE
            TimePicker(this).changedTextColor(start_picker_layout, true)
            TimePicker(this).changedTextColor(end_picker_layout, true)
        }
        else super.onBackPressed()
    }


    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }


}