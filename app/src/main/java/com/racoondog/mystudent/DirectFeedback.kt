package com.racoondog.mystudent

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.direct_feedback_layout.*

class DirectFeedback:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.direct_feedback_layout)

        directFeedbackQuit_Button.setOnClickListener {
            finish()
        }

        directFeedbackSend_Button.setOnClickListener {

           val email = Intent(Intent.ACTION_SEND)

            val title = directFeedback_title_text.text.toString()
            val content = directFeedback_content_text.text.toString()
            val address = arrayOf<String>("void0629@gmail.com")

            email.setType("message/rfc822")

            email.putExtra(Intent.EXTRA_EMAIL, address)
            email.putExtra(Intent.EXTRA_SUBJECT, "$title")
            email.putExtra(Intent.EXTRA_TEXT, "$content")
            startActivity(email)

        }
    }

}