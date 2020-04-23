package com.racoondog.gotaplan

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.guide_dialog.*
import kotlinx.android.synthetic.main.subject_dialog.*
import java.util.*

class GuideDialog: Dialog {

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
        setContentView(R.layout.guide_dialog)

        guide_timetable.setOnClickListener {
            val introIntent = Intent(context, IntroActivity::class.java)
            introIntent.action = "TimetableGuide"
            context.startActivity(introIntent)
            dismiss()
        }
        guide_lesson.setOnClickListener {
            val introIntent = Intent(context, IntroActivity::class.java)
            introIntent.action = "LessonModeGuide"
            context.startActivity(introIntent)
            dismiss()
        }

    }

}