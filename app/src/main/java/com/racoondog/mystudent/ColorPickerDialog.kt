package com.racoondog.mystudent


import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import kotlinx.android.synthetic.main.subject_color_dialog.*


class ColorPickerDialog(context: Context?, val onCustomDialogEventListener: ICustomDialogEventListener) : Dialog(context) {

    // this is your interface for what you want to do on the calling activity
    interface ICustomDialogEventListener {
        fun customDialogEvent(colorCode: Int)
    }

    // And in onCreate, you set up the click event from your dialog to call the callback
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layoutParams = WindowManager.LayoutParams()
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        layoutParams.dimAmount = 0.8f
        window.attributes = layoutParams

        setContentView(R.layout.subject_color_dialog)

        val colorList = context.resources.getIntArray(R.array.subject_color)
        colorPicker.colorList(colorList,this)

    }

}

