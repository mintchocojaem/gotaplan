package com.racoondog.gotaplan

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import com.racoondog.gotaplan.MainActivity.Companion.mContext
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.schedule_interval_dialog.*
import kotlinx.android.synthetic.main.schedule_title_dialog.*

class ScheduleIntervalDialog:Dialog {

    private val realm = Realm.getDefaultInstance()
    lateinit var cnxt:ScheduleDialog

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
        window!!.attributes = layoutParams
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.schedule_interval_dialog)

        val data = realm.where(ScheduleData::class.java).findFirst()!!
        var intervalFlag:Boolean = data.scheduleInterval

        if (!data.scheduleInterval){
            interval_1h.isChecked = true
        }else{
            interval_30m.isChecked = true
        }
        interval_1h.setOnClickListener {
            intervalFlag = false
        }
        interval_30m.setOnClickListener {
            intervalFlag = true
        }

        scheduleInterval_dialog_apply.setOnClickListener {
            realm.beginTransaction()
            data.scheduleInterval = intervalFlag
            realm.commitTransaction()
            cnxt.cnxt.weekView.refresh()
            dismiss()

        }
        scheduleInterval_dialog_cancel.setOnClickListener{
            dismiss()
        }

    }

}