package com.racoondog.gotaplan

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.schedule_title_dialog.*

class ScheduleTitleDialog:Dialog {

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
        setContentView(R.layout.schedule_title_dialog)

        val scheduleData = realm.where(ScheduleData::class.java).findFirst()!!

        scheduleTitle.setText(scheduleData.scheduleTitle)
        scheduleTitle.setHint(scheduleData.scheduleTitle)

        scheduleTitle_dialog_apply.setOnClickListener {
            realm.beginTransaction()
            if (scheduleTitle.text.toString() == "") scheduleData.scheduleTitle = scheduleTitle.hint.toString()
            else scheduleData.scheduleTitle = scheduleTitle.text.toString()
            realm.commitTransaction()
            Toast.makeText(context,cnxt.context.resources.getString(R.string.edit_schedule_title_applied),Toast.LENGTH_SHORT).show()
            dismiss()
            cnxt.dismiss()
            cnxt.cnxt.toolbar_title.text = scheduleData.scheduleTitle.toString()
        }
        scheduleTitle_dialog_cancel.setOnClickListener{
            dismiss()
        }
    }

}