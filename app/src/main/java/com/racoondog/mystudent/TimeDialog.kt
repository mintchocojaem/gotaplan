package com.racoondog.mystudent

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.WindowManager
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.time_dialog.*
import kotlinx.android.synthetic.main.time_picker.*

class TimeDialog:Dialog {

    private val realm = Realm.getDefaultInstance()
    lateinit var cnxt:WeekView

    constructor(context: Context) : super(context)
    constructor(context: Context, themeResId: Int) : super(context, themeResId)
    constructor(
        context: Context,
        cancelable: Boolean,
        cancelListener: DialogInterface.OnCancelListener?
    ) : super(context, cancelable, cancelListener)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layoutParams = WindowManager.LayoutParams()
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        layoutParams.dimAmount = 0.8f
        window.attributes = layoutParams

        setContentView(R.layout.time_dialog)

        var subjectData: RealmResults<SubjectBox> = realm.where<SubjectBox>(SubjectBox::class.java)
            .equalTo("id",WeekView.ID)
            .findAll()
        val data = subjectData[0]!!

        val scheduleData = realm.where(DataModel::class.java).findFirst()!!
        time_dialog_picker.subjectPicker(scheduleData.scheduleStartHour,scheduleData.scheduleEndHour)
        time_dialog_picker.displayTime(data.startHour,data.startMinute.toInt(),data.endHour,data.endMinute.toInt())

        time_dialog_cancel.setOnClickListener {
            dismiss()
        }

        time_dialog_apply.setOnClickListener {

            val startTimeText = arrayOf(startText_AMPM.text.toString()
                ,startText_hour.text.toString(), startText_minute.text.toString())

            val endTimeText = arrayOf(endText_AMPM.text.toString()
                ,endText_hour.text.toString(), endText_minute.text.toString())

            val timeText =
                "${startTimeText[0]}${startTimeText[1]}:${startTimeText[2]}" + " ~ " + "${endTimeText[0]}${endTimeText[1]}:${endTimeText[2]}" //StartTimeText[ ]은 오전/오후 변환시간

            realm.beginTransaction()
            data.startHour = start_hour.value
            data.startMinute = startTimeText[2]
            data.endHour = end_hour.value
            data.endMinute = endTimeText[2]
            data.time = timeText
            realm.commitTransaction()
            cnxt.refresh(cnxt.cnxt.weekView)
            dismiss()

        }

    }
}