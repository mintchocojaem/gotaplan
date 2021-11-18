package com.racoondog.gotaplan

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import io.realm.RealmResults
import kotlinx.android.synthetic.main.font_setting_dialog.*
import kotlinx.android.synthetic.main.subject_dialog.*
import java.util.*

class FontSettingDialog: Dialog {
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
        setContentView(R.layout.font_setting_dialog)
        if (AppStorage(context).getFontStyle() == 0){
            font_setting_dialog_systemFont.isChecked = true
        }else if(AppStorage(context).getFontStyle() == 1){
            font_setting_dialog_appFont.isChecked = true
        }
        font_setting_dialog_systemFont.setOnClickListener {
            AppStorage(context).setFontStyle(0)
            Toast.makeText(context,R.string.font_setting_dialog_apply,Toast.LENGTH_SHORT).show()
        }
        font_setting_dialog_appFont.setOnClickListener {
            AppStorage(context).setFontStyle(1)
            Toast.makeText(context,R.string.font_setting_dialog_apply,Toast.LENGTH_SHORT).show()
        }


    }


}