package com.racoondog.mystudent

import android.app.Activity
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm
import kotlinx.android.synthetic.main.theme_setting_layout.*

class ThemeSetting:AppCompatActivity() {

    private val realm = Realm.getDefaultInstance()
    private var statusBarColorCode = -1
    private var mainButtonColorCode = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.theme_setting_layout)

        loadColor()

        themeSettingQuit_btn.setOnClickListener {
            intent.putExtra("statusBarColor",statusBarColorCode)
            intent.putExtra("mainButtonColor",mainButtonColorCode)
            setResult(Activity.RESULT_OK,intent)
            finish()
        }

        status_bar_theme_picker_btn_layout.setOnClickListener {
            val dialog = ColorPickerDialog(this, object :
                ColorPickerDialog.ICustomDialogEventListener {
                override fun customDialogEvent(colorcode: Int) {
                    // Do something with the value here, e.g. set a variable in the calling activity
                    statusBarColorCode = colorcode
                    status_bar_theme_picker_btn.backgroundTintList = ColorStateList.valueOf(statusBarColorCode)

                }
            })
            dialog.show()
        }

        status_bar_theme_init_btn.setOnClickListener {
            initColor(status_bar_theme_picker_btn,null)
            statusBarColorCode = resources.getColor(R.color.statusBarColor)
        }

        mainButton_theme_picker_btn_layout.setOnClickListener {
            val dialog = ColorPickerDialog(this, object :
                ColorPickerDialog.ICustomDialogEventListener {
                override fun customDialogEvent(colorcode: Int) {
                    // Do something with the value here, e.g. set a variable in the calling activity
                    mainButtonColorCode = colorcode
                    main_btn_theme_picker_btn.backgroundTintList = ColorStateList.valueOf(mainButtonColorCode)

                }
            })
            dialog.show()
        }

        mainButton_theme_init_btn.setOnClickListener {
            initColor(main_btn_theme_picker_btn, resources.getColor(R.color.mainButtonColor))
            mainButtonColorCode = resources.getColor(R.color.mainButtonColor)
        }

    }

    private fun initColor(view: View,color:Int?){
        if(color == null)view.backgroundTintList = null
        else view.backgroundTintList = ColorStateList.valueOf(color)
        view.setBackgroundResource(R.drawable.color_picker_btn)
    }


    private fun loadColor(){

        val themeData = realm.where(ThemeData::class.java).findFirst()!!

        statusBarColorCode =  themeData.statusBarColor
        mainButtonColorCode = themeData.mainButtonColor

        if(statusBarColorCode == resources.getColor(R.color.statusBarColor))status_bar_theme_picker_btn.backgroundTintList = null
        else status_bar_theme_picker_btn.backgroundTintList = ColorStateList.valueOf(statusBarColorCode)
        main_btn_theme_picker_btn.backgroundTintList = ColorStateList.valueOf(mainButtonColorCode)

    }

    override fun onBackPressed() {
        intent.putExtra("statusBarColor",statusBarColorCode)
        intent.putExtra("mainButtonColor",mainButtonColorCode)
        setResult(Activity.RESULT_OK,intent)
        finish()
        super.onBackPressed()
    }
}