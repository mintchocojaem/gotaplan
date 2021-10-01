package com.racoondog.gotaplan


import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import kotlinx.android.synthetic.main.notification_dialog.*


class NotificationDialog(context: Context, private var onCustomDialogEventListener: ICustomDialogEventListener)
    : Dialog(context){

    interface ICustomDialogEventListener {
        fun customDialogEvent(flag:Int,text:String)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val layoutParams = WindowManager.LayoutParams()
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        layoutParams.dimAmount = 0.8f
        window!!.attributes = layoutParams
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.notification_dialog)
        notify_null.setOnClickListener {
            onCustomDialogEventListener.customDialogEvent(-1,context.resources.getString(R.string.none))
            dismiss()
        }

        notify_on_time.setOnClickListener {
            onCustomDialogEventListener.customDialogEvent(0,context.resources.getString(R.string.fixed_time))
            dismiss()
        }

        notify_before_5m.setOnClickListener {
            onCustomDialogEventListener.customDialogEvent(5,context.resources.getString(R.string.before_5m))
            dismiss()
        }

        notify_before_10m.setOnClickListener {
            onCustomDialogEventListener.customDialogEvent(10,context.resources.getString(R.string.before_10m))
            dismiss()
        }

        notify_before_30m.setOnClickListener {
            onCustomDialogEventListener.customDialogEvent(30,context.resources.getString(R.string.before_30m))
            dismiss()
        }

        notify_before_1h.setOnClickListener {
            onCustomDialogEventListener.customDialogEvent(60,context.resources.getString(R.string.before_1h))
            dismiss()
        }

        notify_before_12h.setOnClickListener {
            onCustomDialogEventListener.customDialogEvent(720,context.resources.getString(R.string.before_12h))
            dismiss()
        }

    }


}