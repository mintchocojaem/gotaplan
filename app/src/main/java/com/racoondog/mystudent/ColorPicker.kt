package com.racoondog.mystudent

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.color_picker_layout.view.*
import java.util.*

class ColorPicker: ConstraintLayout {

    constructor(context: Context) : super(context){
        initView()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs){
        initView()

    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        initView()

    }


    private fun initView(){
        val inflaterService: String = Context.LAYOUT_INFLATER_SERVICE
        val inflater: LayoutInflater = context.getSystemService(inflaterService) as LayoutInflater
        val view = inflater.inflate(R.layout.color_picker_layout, this, false)
        addView(view)

    }


    fun colorList(colorList : IntArray,dialog: ColorPickerDialog){

        val divide = 4

        var column = ((colorList.size)/divide)
        if(((colorList.size)%divide) != 0) column+=1

        var row = 0

        for (i in 1 .. column) {

            val pickerLine = LinearLayout(context)
            pickerLine.layoutParams =
                LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                    .apply {

                    }
            pickerLine.tag = "$i"
            color_picker_main.addView(pickerLine)
        }

        for (j in colorList.indices) {

            val colorButton = ImageButton(context)

            colorButton.layoutParams = LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {

                rightMargin = 16
                leftMargin = 16
                topMargin = 16
                bottomMargin = 16
            }
            colorButton.apply {
                id = j
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

            findViewWithTag<LinearLayout>("$row").addView(colorButton)


        }

    }

    fun randomColor(colorList: IntArray):Int{

        val random = Random()
        val number = random.nextInt(colorList.size -1)

        return colorList[number]

    }

}