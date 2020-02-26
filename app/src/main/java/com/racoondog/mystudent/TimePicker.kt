package com.racoondog.mystudent

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.NumberPicker
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.time_picker.view.*

class TimePicker:ConstraintLayout {

    private val displayValue = mutableListOf<String>()

    constructor(context: Context) : super(context){
        initView()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs){
        initView()
        getAttrs(attrs)

    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        initView()
        getAttrs(attrs,0)
    }


    private fun initView(){
        val inflaterService: String = Context.LAYOUT_INFLATER_SERVICE
        val inflater: LayoutInflater = context.getSystemService(inflaterService) as LayoutInflater
        val view = inflater.inflate(R.layout.time_picker,this,false)
        addView(view)
    }

    private fun getAttrs(attrs : AttributeSet, defStyle: Int){
        val typedArray : TypedArray = context.obtainStyledAttributes(attrs,R.styleable.TimePicker, defStyle,0)
        setTypeArray(typedArray)
    }

    private fun getAttrs(attrs: AttributeSet){
        val typedArray : TypedArray = context.obtainStyledAttributes(attrs,R.styleable.TimePicker)
        setTypeArray(typedArray)
    }

    private fun setTypeArray(typedArray: TypedArray){
        val backgroundResourceId = typedArray.getResourceId(R.styleable.TimePicker_bg, R.drawable.time_picker_layout)
        time_bar.setBackgroundResource(backgroundResourceId)

        typedArray.recycle()
    }

    fun schedulePicker(){

        start_minute.visibility = View.GONE
        end_minute.visibility = View.GONE

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
        startText_AMPM.text = "${displayValue[0]}"
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

        endText_AMPM.text = "${displayValue[1]}"
        displayValue.removeAll(displayValue)

        start_hour.apply {

            minValue = 6
            maxValue = 18
            value = 8

            for (i in minValue .. maxValue) {
                when{
                    i > 12-> displayValue.add("${i-12} ")
                    else -> displayValue.add("$i ")
                }
            }

            displayedValues = displayValue.toTypedArray()
            wrapSelectorWheel = false
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        }
        startText_hour.text = "8 "
        displayValue.removeAll(displayValue)

        end_hour.apply {

            minValue = 7
            maxValue = 24
            value = 18
            for (i in minValue .. maxValue) {
                when{
                    i > 12-> displayValue.add("${i-12} ")
                    else -> displayValue.add("$i ")
                }
            }

            displayedValues = displayValue.toTypedArray()
            wrapSelectorWheel = false
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS


        }
        endText_hour.text = "6 "
        displayValue.removeAll(displayValue)

        start_AMPM.setOnTouchListener { _: View, _: MotionEvent ->
            //리턴값은 return 없이 아래와 같이
            true // or false
        }
        end_AMPM.setOnTouchListener { _: View, _: MotionEvent ->
            //리턴값은 return 없이 아래와 같이
            true // or false
        }

        start_hour.setOnValueChangedListener{_,_,i2 ->

            startText_hour.text = start_hour.displayedValues[start_hour.value - start_hour.minValue]
            if(i2 < 12 || i2 == 24) {
                start_AMPM.value = 0
                startText_AMPM.text = start_AMPM.displayedValues[start_AMPM.value]
            }else {
                start_AMPM.value = 1
                startText_AMPM.text = start_AMPM.displayedValues[start_AMPM.value]
            }


        }


        end_hour.setOnValueChangedListener{_,_,i2 ->

            endText_hour.text = end_hour.displayedValues[end_hour.value - end_hour.minValue]
            if(i2 < 12 || i2 == 24) {
                end_AMPM.value = 0
                endText_AMPM.text = end_AMPM.displayedValues[end_AMPM.value]
            }else {
                end_AMPM.value = 1
                endText_AMPM.text = end_AMPM.displayedValues[end_AMPM.value]
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

    fun subjectPicker(intentStartHour:Int,intentEndHour:Int){

        val initEndHour = intentEndHour -1

        start_minute.visibility = View.VISIBLE
        end_minute.visibility = View.VISIBLE

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

        if(intentStartHour < 12) {
            start_AMPM.value = 0
            startText_AMPM.text ="${displayValue[0]}"
        }
        else {
            start_AMPM.value = 1
            startText_AMPM.text ="${displayValue[1]}"
        }
        displayValue.removeAll(displayValue)

        end_AMPM.apply {

            displayValue.add("오전")
            displayValue.add("오후")
            minValue = 0
            maxValue = 1
            value = 0

            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            wrapSelectorWheel = false
            displayedValues = displayValue.toTypedArray()

        }

        if(intentStartHour < 12) {
            end_AMPM.value = 0
            endText_AMPM.text ="${displayValue[0]}"

        }
        else {
            end_AMPM.value = 1
            endText_AMPM.text ="${displayValue[1]}"
        }
        displayValue.removeAll(displayValue)


        start_hour.apply {

            minValue = intentStartHour
            maxValue = initEndHour
            value = minValue
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            wrapSelectorWheel = false

            for(i in intentStartHour..initEndHour){
                when{
                    i > 12 -> displayValue.add("${i-12} ")
                    else -> displayValue.add("$i ")
                }
            }
            displayedValues = displayValue.toTypedArray()

        }
        startText_hour.text = "${start_hour.displayedValues[start_hour.value - start_hour.minValue]}"
        displayValue.removeAll(displayValue)

        end_hour.apply {
            minValue = intentStartHour
            maxValue = intentEndHour
            value = minValue
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            wrapSelectorWheel = false

            for(i in intentStartHour..intentEndHour){
                when{
                    i > 12-> displayValue.add("${i-12} ")
                    else -> displayValue.add("$i ")
                }
            }
            displayedValues = displayValue.toTypedArray()

        }
        endText_hour.text = "${end_hour.displayedValues[end_hour.value - end_hour.minValue]}"
        displayValue.removeAll(displayValue)

        start_minute.apply{

            minValue = 0
            maxValue = 11
            value = 0
            displayedValues = arrayOf("00","05","10","15","20","25","30","35","40","45","50","55")
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            wrapSelectorWheel = false

        }

        end_minute.apply{

            minValue = 0
            maxValue = 11
            value = 6
            displayedValues = arrayOf("00","05","10","15","20","25","30","35","40","45","50","55")
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            wrapSelectorWheel = false

        }
        endText_minute.text = end_minute.displayedValues[end_minute.value]

        start_AMPM.setOnTouchListener { _: View, _:MotionEvent ->
            //리턴값은 return 없이 아래와 같이
            true // or false
        }
        end_AMPM.setOnTouchListener { _: View, _:MotionEvent ->
            //리턴값은 return 없이 아래와 같이
            true // or false
        }

        start_minute.setOnValueChangedListener{_,_,_ ->

            startText_minute.text = start_minute.displayedValues[start_minute.value]


        }
        start_hour.setOnValueChangedListener{_,_,i2 ->

            startText_hour.text = start_hour.displayedValues[start_hour.value - intentStartHour]
            if(i2 < 12 || i2 == 24) {
                start_AMPM.value = 0
                startText_AMPM.text = start_AMPM.displayedValues[start_AMPM.value]
            }else {
                start_AMPM.value = 1
                startText_AMPM.text = start_AMPM.displayedValues[start_AMPM.value]
            }


        }

        end_minute.setOnValueChangedListener{_,_,_ ->

            endText_minute.text = end_minute.displayedValues[end_minute.value]

            if (end_hour.value == end_hour.maxValue) {
                end_minute.value = 0
                endText_minute.text = end_minute.displayedValues[end_minute.value]
            }

        }
        end_hour.setOnValueChangedListener{_,_,i2 ->

            endText_hour.text = end_hour.displayedValues[end_hour.value - intentStartHour]
            if(i2 < 12 || i2 == 24) {
                end_AMPM.value = 0
                endText_AMPM.text = end_AMPM.displayedValues[end_AMPM.value]
            }else {
                end_AMPM.value = 1
                endText_AMPM.text = end_AMPM.displayedValues[end_AMPM.value]
            }

            if (end_hour.value == end_hour.maxValue){
                end_minute.value = 0
                endText_minute.text = end_minute.displayedValues[end_minute.value]
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

    fun displayTime(startHour:Int,startMinute:Int,endHour:Int,endMinute:Int){

        if (startHour in 12..23) {
            start_AMPM.value = 1
        }
        else start_AMPM.value = 0
        startText_AMPM.text = start_AMPM.displayedValues[start_AMPM.value]
        start_hour.value = startHour
        startText_hour.text = start_hour.displayedValues[start_hour.value - start_hour.minValue]
        start_minute.value = (startMinute / 5)
        startText_minute.text = start_minute.displayedValues[start_minute.value]

        if(endHour in 12..23){
            end_AMPM.value = 1
        }else end_AMPM.value = 0
        endText_AMPM.text = end_AMPM.displayedValues[end_AMPM.value]
        end_hour.value = endHour
        endText_hour.text = end_hour.displayedValues[end_hour.value - end_hour.minValue]
        end_minute.value = (endMinute / 5)
        endText_minute.text = end_minute.displayedValues[end_minute.value]

    }

    fun changedTextColor(viewGroup: ViewGroup, focused:Boolean) = if(focused){
        for (index in 0 until (viewGroup).childCount) {
            val nextChild = (viewGroup).getChildAt(index)
            if(nextChild is TextView) nextChild.setTextColor(ContextCompat.getColor(context,R.color.defaultAccentColor))
        }
    }else{
        for (index in 0 until (viewGroup).childCount) {
            val nextChild = (viewGroup).getChildAt(index)
            if(nextChild is TextView) nextChild.setTextColor(ContextCompat.getColor(context,R.color.defaultContemptColor))
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
