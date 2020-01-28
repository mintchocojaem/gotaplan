package com.racoondog.mystudent


import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.subject_color_dialog.*
import java.util.*


class ColorPickerDialog(context: Context?, private var onCustomDialogEventListener: ICustomDialogEventListener) : Dialog(context) {

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
        colorList(colorList,this)



    }

    private fun colorList(colorList : IntArray,dialog: ColorPickerDialog){

        val divide = 4

        var column = ((colorList.size)/divide)
        if(((colorList.size)%divide) != 0) column+=1

        var row = 0

        for (i in 1 .. column) {

            val pickerLine = LinearLayout(context)
            pickerLine.layoutParams =
                LinearLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                    .apply {

                    }
            pickerLine.tag = i
            color_picker_main.addView(pickerLine)
        }

        for (j in colorList.indices) {

            val colorButton = ImageButton(context)

            colorButton.layoutParams = LinearLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {

                rightMargin = 16
                leftMargin = 16
                topMargin = 16
                bottomMargin = 16
            }
            colorButton.apply {
                setBackgroundResource(R.drawable.color_picker_btn)
                backgroundTintList = ColorStateList.valueOf(colorList[j])
            }
            colorButton.setOnClickListener {
                //activity.intent.putExtra("colorCode",colorList[j])
                //activity.setResult(Activity.RESULT_OK,activity.intent)
                //activity.finish()
                dialog.apply {
                    onCustomDialogEventListener.customDialogEvent(colorList[j])
                    dismiss()
                }

            }

            if(j%divide == 0)row+=1

            color_picker_main.findViewWithTag<LinearLayout>(row).addView(colorButton)

        }
        //customColorButton(dialog,row)

    }

    private fun customColorButton(dialog: ColorPickerDialog,tag: Int){

        val customColorButton = ImageButton(context)

        customColorButton.layoutParams = LinearLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {

            rightMargin = 16
            leftMargin = 16
            topMargin = 16
            bottomMargin = 16
        }
        customColorButton.setPadding(16,16,16,16)
        customColorButton.apply {
            setBackgroundResource(R.drawable.color_picker_btn)
            setImageResource(R.drawable.icon_add)

        }
        customColorButton.setOnClickListener {
            //activity.intent.putExtra("colorCode",colorList[j])
            //activity.setResult(Activity.RESULT_OK,activity.intent)
            //activity.finish()
            dialog.apply {

                dismiss()
            }

        }
        color_picker_main.findViewWithTag<LinearLayout>(tag).addView(customColorButton)
    }


}

