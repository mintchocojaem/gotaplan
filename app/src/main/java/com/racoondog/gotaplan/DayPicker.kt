package com.racoondog.gotaplan

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.day_picker.view.*

class DayPicker:ConstraintLayout {

    constructor(context: Context?) : super(context!!){initView()}
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs){initView()}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!,
        attrs,
        defStyleAttr
    ){initView()}

    var dayFlag:Int = 0

    private var mListener:OnCustomEventListener? = null

    interface OnCustomEventListener{
        fun onEvent()
    }

    fun setOnCustomEventListener(eventListener: OnCustomEventListener){
        mListener = eventListener
    }

    private fun initView(){
        val inflaterService = Context.LAYOUT_INFLATER_SERVICE
        val inflater = context.getSystemService(inflaterService) as LayoutInflater
        val view = inflater.inflate(R.layout.day_picker,this,false)
        addView(view)
    }

    fun dayPick(initFlag:Int,day:Int=0){

        dayFlag = day

        if (initFlag == 6){
            saturday_button.visibility = View.VISIBLE
        }
        else if (initFlag == 7){
            saturday_button.visibility = View.VISIBLE
            sunday_button.visibility = View.VISIBLE
        }

        when(day){
            1 -> monday_button.isChecked = true
            2 -> tuesday_button.isChecked = true
            3 -> wednesday_button.isChecked = true
            4 -> thursday_button.isChecked = true
            5 -> friday_button.isChecked = true
            6 -> saturday_button.isChecked = true
            7 -> sunday_button.isChecked = true
        }

        monday_button.setOnClickListener {
            dayFlag = 1
            mListener?.onEvent()
        }
        tuesday_button.setOnClickListener {
            dayFlag = 2
            mListener?.onEvent()
        }
        wednesday_button.setOnClickListener {
            dayFlag = 3
            mListener?.onEvent()
        }
        thursday_button.setOnClickListener {
            dayFlag = 4
            mListener?.onEvent()
        }
        friday_button.setOnClickListener {
            dayFlag = 5
            mListener?.onEvent()
        }
        saturday_button.setOnClickListener {
            dayFlag = 6
            mListener?.onEvent()
        }
        sunday_button.setOnClickListener {
            dayFlag = 7
            mListener?.onEvent()
        }

    }


}