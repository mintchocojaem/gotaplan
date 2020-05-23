package com.racoondog.gotaplan

import android.content.Context
import android.content.res.TypedArray
import android.transition.ChangeBounds
import android.transition.Transition
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import io.realm.RealmResults
import io.realm.Sort
import kotlinx.android.synthetic.main.time_picker.view.*


class TimePicker:ConstraintLayout {

    /*
    var mListener: OnCustomEventListener? = null

    interface OnCustomEventListener {
        fun onEvent()
    }

    fun setCustomEventListener(eventListener: OnCustomEventListener) {
        mListener = eventListener
    }

     */

    private var mListener: OnCustomEventListener? = null

    interface OnCustomEventListener {
        fun onEvent()
    }

    fun setCustomEventListener(eventListener: OnCustomEventListener) {
        mListener = eventListener
    }

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

    fun schedulePicker(viewGroup: ViewGroup){

        start_AMPM.setSaveFromParentEnabled(false)
        start_AMPM.setSaveEnabled(false)
        start_hour.setSaveFromParentEnabled(false)
        start_hour.setSaveEnabled(false)
        start_minute.setSaveFromParentEnabled(false)
        start_minute.setSaveEnabled(false)

        end_AMPM.setSaveFromParentEnabled(false)
        end_AMPM.setSaveEnabled(false)
        end_hour.setSaveFromParentEnabled(false)
        end_hour.setSaveEnabled(false)
        end_minute.setSaveFromParentEnabled(false)
        end_minute.setSaveEnabled(false)


        start_minute.visibility = View.GONE
        end_minute.visibility = View.GONE

        changedTextColor(start_picker_layout, true)
        changedTextColor(end_picker_layout, true)


        start_AMPM.apply {

            displayValue.add(resources.getString(R.string.am))
            displayValue.add(resources.getString(R.string.pm))
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

            displayValue.add(resources.getString(R.string.am))
            displayValue.add(resources.getString(R.string.pm))
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

            start_picker_layout.requestFocus()
        }
        start_picker_layout.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus){
                changedTextColor(start_picker_layout,true)
                changedTextColor(end_picker_layout,false)

                start_picker.visibility = View.VISIBLE
                end_picker.visibility = View.INVISIBLE

                if (!isOpened()){
                    val changeBounds: Transition = ChangeBounds()
                    changeBounds.duration = 300
                    TransitionManager.beginDelayedTransition(viewGroup, changeBounds)
                    time_picker.visibility = View.VISIBLE
                }
            }else {
                changedTextColor(start_picker_layout,true)
                changedTextColor(end_picker_layout,true)
                if(!end_picker_layout.hasFocus())onBackPressed(viewGroup)
            }
        }

        end_picker_layout.setOnClickListener {

            end_picker_layout.requestFocus()
        }
        end_picker_layout.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus){
                changedTextColor(start_picker_layout, false)
                changedTextColor(end_picker_layout, true)

                end_picker.visibility = View.VISIBLE
                start_picker.visibility = View.INVISIBLE

                if (!isOpened()) {
                    val changeBounds: Transition = ChangeBounds()
                    changeBounds.duration = 300
                    TransitionManager.beginDelayedTransition(viewGroup, changeBounds)
                    time_picker.visibility = View.VISIBLE
                }

            }else{
                changedTextColor(start_picker_layout,true)
                changedTextColor(end_picker_layout,true)
                if(!start_picker_layout.hasFocus())onBackPressed(viewGroup)
            }
        }

    }

    fun subjectPicker(intentStartHour:Int,intentEndHour:Int,viewGroup: ViewGroup){

        val initEndHour = intentEndHour -1

        start_AMPM.setSaveFromParentEnabled(false)
        start_AMPM.setSaveEnabled(false)
        start_hour.setSaveFromParentEnabled(false)
        start_hour.setSaveEnabled(false)
        start_minute.setSaveFromParentEnabled(false)
        start_minute.setSaveEnabled(false)

        end_AMPM.setSaveFromParentEnabled(false)
        end_AMPM.setSaveEnabled(false)
        end_hour.setSaveFromParentEnabled(false)
        end_hour.setSaveEnabled(false)
        end_minute.setSaveFromParentEnabled(false)
        end_minute.setSaveEnabled(false)

        start_minute.visibility = View.VISIBLE
        end_minute.visibility = View.VISIBLE

        changedTextColor(start_picker_layout, true)
        changedTextColor(end_picker_layout, true)

        start_AMPM.apply {

            displayValue.add(resources.getString(R.string.am))
            displayValue.add(resources.getString(R.string.pm))
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

            displayValue.add(resources.getString(R.string.am))
            displayValue.add(resources.getString(R.string.pm))
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

        start_hour.setOnValueChangedListener{_,_,i2 ->

            startText_hour.text = start_hour.displayedValues[start_hour.value - intentStartHour]
            if(i2 < 12 || i2 == 24) {
                start_AMPM.value = 0
                startText_AMPM.text = start_AMPM.displayedValues[start_AMPM.value]
            }else {
                start_AMPM.value = 1
                startText_AMPM.text = start_AMPM.displayedValues[start_AMPM.value]
            }

            mListener?.onEvent()

        }

        start_minute.setOnValueChangedListener{_,_,_ ->

            startText_minute.text = start_minute.displayedValues[start_minute.value]
            mListener?.onEvent()
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
            mListener?.onEvent()
        }

        end_minute.setOnValueChangedListener{_,_,_ ->

            endText_minute.text = end_minute.displayedValues[end_minute.value]

            if (end_hour.value == end_hour.maxValue) {
                end_minute.value = 0
                endText_minute.text = end_minute.displayedValues[end_minute.value]
            }
            mListener?.onEvent()
        }

        start_picker_layout.setOnClickListener{

            start_picker_layout.requestFocus()
        }
        start_picker_layout.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus){
                changedTextColor(start_picker_layout,true)
                changedTextColor(end_picker_layout,false)

                start_picker.visibility = View.VISIBLE
                end_picker.visibility = View.INVISIBLE

                if (!isOpened()) {
                    val changeBounds: Transition = ChangeBounds()
                    changeBounds.duration = 300
                    TransitionManager.beginDelayedTransition(viewGroup,changeBounds)
                    time_picker.visibility = View.VISIBLE
                }
            }else {
                changedTextColor(start_picker_layout,true)
                changedTextColor(end_picker_layout,true)
                if(!end_picker_layout.hasFocus())onBackPressed(viewGroup)
            }
        }

        end_picker_layout.setOnClickListener {

            end_picker_layout.requestFocus()
        }
        end_picker_layout.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus){
                changedTextColor(start_picker_layout, false)
                changedTextColor(end_picker_layout, true)

                end_picker.visibility = View.VISIBLE
                start_picker.visibility = View.INVISIBLE

                if (!isOpened()) {
                    val changeBounds: Transition = ChangeBounds()
                    changeBounds.duration = 300
                    TransitionManager.beginDelayedTransition(viewGroup,changeBounds)
                    time_picker.visibility = View.VISIBLE
                }

            }else{
                changedTextColor(start_picker_layout,true)
                changedTextColor(end_picker_layout,true)
                if(!start_picker_layout.hasFocus())onBackPressed(viewGroup)
            }
        }
    }

    fun isOpened():Boolean{
        return time_picker.visibility == View.VISIBLE
    }

    private fun onBackPressed(viewGroup: ViewGroup){

        val changeBounds: Transition = ChangeBounds()
        changeBounds.duration = 300
        TransitionManager.beginDelayedTransition(viewGroup, changeBounds)
        time_picker.visibility = View.GONE
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

    private fun changedTextColor(viewGroup: ViewGroup, focused:Boolean) = if(focused){
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

    fun nestedTime(realmResults: RealmResults<SubjectData>):Boolean{

        when {
            end_hour.value == start_hour.value -> {
                return when {
                    end_minute.displayedValues[end_minute.value].toInt() < start_minute.displayedValues[start_minute.value].toInt() -> {
                        Toast.makeText(context, resources.getString(R.string.startTime_big), Toast.LENGTH_SHORT).show()
                        true
                    }
                    start_minute.value == end_minute.value -> {
                        Toast.makeText(context, resources.getString(R.string.startTime_equal), Toast.LENGTH_SHORT).show()
                        true
                    }
                    end_minute.displayedValues[end_minute.value].toInt()  - start_minute.displayedValues[start_minute.value].toInt() < 30 -> {
                        Toast.makeText(context, resources.getString(R.string.startMinute_minimum), Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> checkTime(realmResults)
                }

            }
            end_hour.value - start_hour.value == 1 -> {

                return if(end_minute.displayedValues[end_minute.value].toInt()+(60-start_minute.displayedValues[start_minute.value].toInt()) < 30) {
                    Toast.makeText(context, resources.getString(R.string.startMinute_minimum), Toast.LENGTH_SHORT).show()
                    true
                } else checkTime(realmResults)

            }
            end_hour.value < start_hour.value -> {
                Toast.makeText(context, resources.getString(R.string.startTime_big), Toast.LENGTH_SHORT).show()
                return true
            }
            else -> {

                return checkTime(realmResults)

            }
        }


    }

    private fun checkTime(realmResults: RealmResults<SubjectData>):Boolean{


        val data = realmResults.sort("startHour", Sort.ASCENDING)

        val pickerTime = arrayListOf<Double>()

        pickerTime.add(start_hour.value.toDouble() + (start_minute.displayedValues[start_minute.value].toDouble() / 100))
        pickerTime.add(end_hour.value.toDouble() + (end_minute.displayedValues[end_minute.value].toDouble() / 100))

        val checkTime = arrayListOf<Boolean>()

        if(data.size != 0){

            for ( i in data.indices){

                val subjectTime = arrayListOf<Double>()

                subjectTime.add(data[i]!!.startHour.toDouble()+ (data[i]!!.startMinute.toDouble() / 100))
                subjectTime.add(data[i]!!.endHour.toDouble()+ (data[i]!!.endMinute.toDouble() / 100))

                var checkFlag = when{

                    pickerTime[0] >= subjectTime[1] -> true
                    pickerTime[0] < subjectTime[0] -> pickerTime[1] <= subjectTime[0]
                    else -> false //false == nested subject

                }
                checkTime.add(checkFlag)

            }

        }else  checkTime.add(true)
        return if (checkTime.contains(element = false)){
            Toast.makeText(context,resources.getString(R.string.subject_exist_already), Toast.LENGTH_SHORT).show()
            true
        }else false
    }

    fun startHour():Int {return start_hour.value}
    fun startMinute():String {
        return start_minute.displayedValues[start_minute.value].toString()
    }
    fun endHour():Int {return end_hour.value}
    fun endMinute():String {
        return start_minute.displayedValues[end_minute.value].toString()
    }

}
