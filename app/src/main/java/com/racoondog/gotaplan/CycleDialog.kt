package com.racoondog.gotaplan

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import io.realm.Realm
import kotlinx.android.synthetic.main.cycle_dialog.*

class CycleDialog:Dialog {

    private val realm = Realm.getDefaultInstance()

    constructor(context: Context) : super(context)
    constructor(context: Context, themeResId: Int) : super(context, themeResId)
    constructor(
        context: Context,
        cancelable: Boolean,
        cancelListener: DialogInterface.OnCancelListener?
    ) : super(context, cancelable, cancelListener)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        layoutParams.dimAmount = 0.8f
        window!!.attributes = layoutParams
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.cycle_dialog)


    }

    fun setTitle(string: String){
        cycle_dialog_title.setText(string)
    }

    fun setContent(string: String){
        cycle_dialog_content.setText(string)
    }

    fun setHint(string: String){
        cycle_dialog_content.setHint(string)
    }

    fun getText():Int{
        return cycle_dialog_content.text.toString().toInt()
    }

    fun setPositiveButton(listener: View.OnClickListener){
        cycle_dialog_apply.setOnClickListener(listener)

    }
    fun setNegativeButton(listener: View.OnClickListener){
        cycle_dialog_cancel.setOnClickListener(listener)

    }

}