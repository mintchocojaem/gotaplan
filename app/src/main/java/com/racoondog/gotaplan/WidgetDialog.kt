package com.racoondog.gotaplan

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.subject_dialog.*
import java.util.*

class WidgetDialog: Dialog {

    lateinit var cnxt:MainActivity

    private val realm = Realm.getDefaultInstance()

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
        setContentView(R.layout.widget_dialog)

        initSubject.setOnClickListener {
            initWidget()
            dismiss()
        }

    }

    private fun initWidget(){

        val builder = AlertDialog.Builder(context,R.style.MyDialogTheme).apply {
            val n: String = Locale.getDefault().displayLanguage
            if (n.compareTo("한국어") == 0){
                this.setMessage("해당 시간표를 위젯으로 지정하시겠습니까?")
            }
            else {
                this.setMessage("Are you sure you want to designate that timetable as a widget?")
            }
        }

            .setTitle(cnxt.applicationContext.getString(R.string.widget_dialog_toolbar_title))

            .setPositiveButton(cnxt.applicationContext.resources.getString(R.string.dialog_apply)) { _, _ ->
                val scheduleData = realm.where(ScheduleData::class.java).equalTo("id",MainActivity.scheduleID).findFirst()!!
                AppStorage(context).setWidgetScheduleID(scheduleData.id)

                cnxt.weekView.refresh(cnxt.weekView)
                Toast.makeText(context,cnxt.applicationContext.getString(R.string.widget_dialog_init_success),Toast.LENGTH_SHORT).show()

            }

            .setNegativeButton(cnxt.applicationContext.resources.getString(R.string.dialog_cancel)) { _, _ ->

            }

            .show()
        builder.window!!.attributes.apply {
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT}
        builder.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        builder.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(cnxt.applicationContext,R.color.defaultAccentColor))
        builder.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(cnxt.applicationContext,R.color.defaultAccentColor))

    }

}