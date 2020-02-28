package com.racoondog.gotaplan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.license_layout.*

class License :AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.license_layout)

        directFeedbackQuit_Button.setOnClickListener {
            finish()
        }

    }
}