package com.racoondog.mystudent

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.theme_setting_layout.*

class ThemeSetting:AppCompatActivity() {

    private var statusBarColorCode = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.theme_setting_layout)

        theme_picker_btn_layout.setOnClickListener {
            val dialog = ColorPickerDialog(this, object :
                ColorPickerDialog.ICustomDialogEventListener {
                override fun customDialogEvent(colorcode: Int) {
                    // Do something with the value here, e.g. set a variable in the calling activity
                    statusBarColorCode = colorcode
                    theme_picker_btn.backgroundTintList = ColorStateList.valueOf(statusBarColorCode)
                    changeTheme(statusBarColorCode)
                }
            })
            dialog.show()
        }

    }

    private fun changeTheme(colorList:Int){

    }
}