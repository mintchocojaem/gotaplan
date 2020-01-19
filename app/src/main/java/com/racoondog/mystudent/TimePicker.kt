package com.racoondog.mystudent

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.NumberPicker
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.time_picker_layout.*
import kotlinx.android.synthetic.main.time_picker_layout.view.*

class TimePicker:ConstraintLayout {

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

    private fun getAttrs(attrs: AttributeSet?) {
        val typedArray : TypedArray  = getContext().obtainStyledAttributes(attrs, R.styleable.timePicker)
        setTypeArray(typedArray)
    }

    private fun getAttrs(attrs: AttributeSet?,defStyle:Int) {
        val typedArray : TypedArray = getContext().obtainStyledAttributes(attrs, R.styleable.timePicker, defStyle, 0)
        setTypeArray(typedArray)

    }
    private fun setTypeArray(typedArray: TypedArray) {
        val option : String = typedArray.getString(R.styleable.timePicker_option)
        if(option == "schedule"){
            schedulePicker()
        }

        typedArray.recycle()
    }

    private fun initView(){
        val inflaterService: String = Context.LAYOUT_INFLATER_SERVICE
        val inflater: LayoutInflater = context.getSystemService(inflaterService) as LayoutInflater
        val view = inflater.inflate(R.layout.time_picker_layout,this,false)
        addView(view)
    }

    private fun schedulePicker(){

       val displayValue = mutableListOf<String>()

        changedTextColor(start_picker_layout, true)
        changedTextColor(end_picker_layout, true)


        start_AMPM.apply {

            displayValue.add("오전")
            displayValue.add("오후")
            minValue = 0
            maxValue = 1
            value = 0
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            wrapSelectorWheel = false
            displayedValues = displayValue.toTypedArray()

        }

        startText_AMPM.text = "${displayValue[0]} "
        displayValue.removeAll(displayValue)

        end_AMPM.apply {

            displayValue.add("오전")
            displayValue.add("오후")
            minValue = 0
            maxValue = 1
            value = 1
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            wrapSelectorWheel = false
            displayedValues = displayValue.toTypedArray()

        }

        endText_AMPM.text ="${displayValue[1]} "
        displayValue.removeAll(displayValue)

        start_hour.apply {

            minValue = 6
            maxValue = 18
            value = 8

            for (i in minValue .. maxValue) {
                when{
                    i > 12-> displayValue.add("${i-12}")
                    else -> displayValue.add("$i")
                }
            }

            displayedValues = displayValue.toTypedArray()
            wrapSelectorWheel = false
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        }

        displayValue.removeAll(displayValue)

        end_hour.apply {

            minValue = 7
            maxValue = 24
            value = 20
            for (i in minValue .. maxValue) {
                when{
                    i > 12-> displayValue.add("${i-12}")
                    else -> displayValue.add("$i")
                }
            }

            displayedValues = displayValue.toTypedArray()
            wrapSelectorWheel = false
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS


        }


        start_AMPM.setOnTouchListener { _: View, event: MotionEvent ->
            //리턴값은 return 없이 아래와 같이
            true // or false
        }
        end_AMPM.setOnTouchListener { _: View, event: MotionEvent ->
            //리턴값은 return 없이 아래와 같이
            true // or false
        }

        start_hour.setOnValueChangedListener{_,_,i2 ->

            startText_hour.text = start_hour.displayedValues[start_hour.value - start_hour.minValue]
            if(i2 < 12 || i2 == 24) {
                start_AMPM.value = 0
                startText_AMPM.text = start_AMPM.displayedValues[start_AMPM.value]+" "
            }else {
                start_AMPM.value = 1
                startText_AMPM.text = start_AMPM.displayedValues[start_AMPM.value]+" "
            }


        }


        end_hour.setOnValueChangedListener{_,_,i2 ->

            endText_hour.text = end_hour.displayedValues[end_hour.value - end_hour.minValue]
            if(i2 < 12 || i2 == 24) {
                end_AMPM.value = 0
                endText_AMPM.text = end_AMPM.displayedValues[end_AMPM.value]+" "
            }else {
                end_AMPM.value = 1
                endText_AMPM.text = end_AMPM.displayedValues[end_AMPM.value]+" "
            }



        }
        start_picker_layout.setOnClickListener{
            hideKeyboard()
            time_picker.visibility = View.VISIBLE
            start_picker.visibility = View.VISIBLE
            end_picker.visibility = View.INVISIBLE
            changedTextColor(start_picker_layout,true)
            changedTextColor(end_picker_layout,false)
        }
        end_picker_layout.setOnClickListener {
            hideKeyboard()
            time_picker.visibility = View.VISIBLE
            end_picker.visibility = View.VISIBLE
            start_picker.visibility = View.INVISIBLE
            changedTextColor(start_picker_layout, false)
            changedTextColor(end_picker_layout, true)

        }

    }


    fun changedTextColor(viewGroup: ViewGroup, focused:Boolean){

        if(focused){
            for (index in 0 until (viewGroup as ViewGroup).childCount) {
                val nextChild = (viewGroup as ViewGroup).getChildAt(index)
                if(nextChild is TextView) nextChild.setTextColor(resources.getColor(R.color.defaultAccentColor))
            }
        }else{
            for (index in 0 until (viewGroup as ViewGroup).childCount) {
                val nextChild = (viewGroup as ViewGroup).getChildAt(index)
                if(nextChild is TextView) nextChild.setTextColor(resources.getColor(R.color.defaultContemptColor))
            }
        }

    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)


    }

    fun option(option:String) {

        if(option == "schedule"){
            schedulePicker()
        }

    }




}
