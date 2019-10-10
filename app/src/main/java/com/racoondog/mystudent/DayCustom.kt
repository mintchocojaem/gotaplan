package com.racoondog.mystudent

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.day_layout.view.*

class DayCustom  : ConstraintLayout {

    constructor(context: Context?) : super(context, null) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context,attrs) {
        initView()
        getAttrs(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        initView()
        getAttrs(attrs, defStyle)

    }

    private fun initView(){
        val inflaterService : String = Context.LAYOUT_INFLATER_SERVICE
        val inflater : LayoutInflater = context.getSystemService(inflaterService) as LayoutInflater
        val view = inflater.inflate(R.layout.day_layout, this, false)
        addView(view)
    }
    private fun getAttrs(attrs : AttributeSet, defStyle: Int){
        val typedArray : TypedArray = context.obtainStyledAttributes(attrs,R.styleable.day_button, defStyle,0)
        setTypeArray(typedArray)
    }

    private fun getAttrs(attrs: AttributeSet){
        val typedArray : TypedArray = context.obtainStyledAttributes(attrs,R.styleable.day_button)
        setTypeArray(typedArray)
    }

    private fun setTypeArray(typedArray: TypedArray){
        /*
        var backgroundResourceId = typedArray.getResourceId(R.styleable.LoginButton_bg, R.color.naver_color)
        bgLinear.setBackgroundResource(backgroundResourceId)
        */
        val symbolResourceId = typedArray.getResourceId(R.styleable.day_button_bg, R.drawable.mintxxhdpi)
        day.setImageResource(symbolResourceId)
        /*
        var textColor = typedArray.getColor(R.styleable.LoginButton_textColor, 0)
        textLinear.setTextColor(textColor)
        */
        val textString = typedArray.getString(R.styleable.day_button_text)
        text.text = textString

        typedArray.recycle()
    }
}