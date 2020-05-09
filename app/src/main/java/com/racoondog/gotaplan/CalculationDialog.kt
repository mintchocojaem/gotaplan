package com.racoondog.gotaplan

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.calculation_dialog.*


class CalculationDialog(context: Context, private var onCustomDialogEventListener: CalculationDialog.ICustomDialogEventListener) : Dialog(context){

    interface ICustomDialogEventListener {
        fun customDialogEvent(flag:Int)
    }

    private val realm = Realm.getDefaultInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        layoutParams.dimAmount = 0.8f
        window!!.attributes = layoutParams
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.calculation_dialog)
        val subjectData: RealmResults<SubjectData> = realm.where<SubjectData>(SubjectData::class.java)
            .equalTo("id",WeekView.ID)
            .findAll()
        val data = subjectData[0]!!

        calculation_dialog_apply.setOnClickListener {

            onCustomDialogEventListener.customDialogEvent(1)
            dismiss()

        }
        calculation_dialog_cancel.setOnClickListener {
            onCustomDialogEventListener.customDialogEvent(0)
            dismiss() }
        this.setCancelable(false)

        calculation_help.setOnClickListener {
            val introIntent = Intent(context, IntroActivity::class.java)
            introIntent.action = "CalculationGuide"
            context.startActivity(introIntent)
        }
    }

}