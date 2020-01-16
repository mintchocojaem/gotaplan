package com.racoondog.mystudent

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.color_picker.view.*
import kotlinx.android.synthetic.main.create_schedule.view.*
import kotlin.math.acosh

class ColorPicker: ConstraintLayout {

    constructor(context: Context) : super(context){
        initView()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs){
        initView()
        getAttrs(attrs)
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        initView()
        getAttrs(attrs, defStyleAttr)
    }
    private fun getAttrs(attrs: AttributeSet) {
        val typedArray : TypedArray  = getContext().obtainStyledAttributes(attrs, R.styleable.colorPicker)
        setTypeArray(typedArray)
    }

    private fun getAttrs(attrs: AttributeSet,defStyle:Int) {
        val typedArray : TypedArray = getContext().obtainStyledAttributes(attrs, R.styleable.colorPicker, defStyle, 0)
        setTypeArray(typedArray)

    }
    private fun setTypeArray(typedArray: TypedArray) {

        typedArray.recycle()
    }

    private fun initView(){
        val inflaterService: String = Context.LAYOUT_INFLATER_SERVICE
        val inflater: LayoutInflater = context.getSystemService(inflaterService) as LayoutInflater
        val view = inflater.inflate(R.layout.color_picker, this, false)
        addView(view)

    }


    fun colorList(colorList : IntArray,activity: Activity){


        for ( i in colorList.indices){

            val colorButton = RadioButton(activity.applicationContext)
            colorButton.layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                weight = 1f
                setPadding(64,32,0,32)
            }
            colorButton.setButtonDrawable(R.drawable.color_picker_btn)
            colorButton.buttonTintList = ColorStateList.valueOf(colorList[i])
            colorButton.setOnClickListener {
                activity.intent.putExtra("colorCode",colorList[i])
                activity.setResult(Activity.RESULT_OK,activity.intent)
                activity.finish()
            }
            color_group.addView(colorButton)



        }
    }


}