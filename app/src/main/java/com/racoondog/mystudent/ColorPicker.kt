package com.racoondog.mystudent

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.color_picker.view.*

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

        val colorTint:Int = typedArray.getColor(R.styleable.colorPicker_colorTint, 0)
        (colorPickerButton.background as GradientDrawable).setColor(colorTint)
        typedArray.recycle()
    }

    private fun initView(){
        val inflaterService: String = Context.LAYOUT_INFLATER_SERVICE
        val inflater: LayoutInflater = context.getSystemService(inflaterService) as LayoutInflater
        val view = inflater.inflate(R.layout.color_picker, this, false)
        addView(view)

    }

}