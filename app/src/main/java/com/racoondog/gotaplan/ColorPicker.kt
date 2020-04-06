package com.racoondog.gotaplan

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.color_picker.view.*

class ColorPicker:ConstraintLayout {

    interface OnCustomEventListener{
        fun onEvent()
    }
    private var mListener:OnCustomEventListener? = null

    fun setOnCustomEventListener(eventListener: OnCustomEventListener){
        mListener = eventListener
    }

    constructor(context: Context?) : super(context){initView()}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){initView()}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){initView()}

    var colorCode = -1

    private fun initView(){
        val inflaterService = Context.LAYOUT_INFLATER_SERVICE
        val inflater = context.getSystemService(inflaterService) as LayoutInflater
        val view = inflater.inflate(R.layout.color_picker,this,false)
        addView(view)
    }

    fun colorPick(activity:Activity?,themeView:View?){

        val dialog = ColorPickerDialog(context, object : ColorPickerDialog.ICustomDialogEventListener {
            override fun customDialogEvent(colorCode: Int) {
                // Do something with the value here, e.g. set a variable in the calling activity
                this@ColorPicker.colorCode = colorCode
                color_picker_btn.backgroundTintList = ColorStateList.valueOf(this@ColorPicker.colorCode)
                changeTheme(activity,themeView,colorCode)
                mListener?.onEvent()
            }
        })
        dialog.show()


    }

    fun initColor(colorCode:Int){
        color_picker_btn.backgroundTintList = ColorStateList.valueOf(colorCode)
        this@ColorPicker.colorCode = colorCode
    }

    fun changeTheme(activity: Activity?,v:View?,colorList:Int){
        activity?.window?.statusBarColor = colorList
        v?.setBackgroundColor(colorList)
    }

}