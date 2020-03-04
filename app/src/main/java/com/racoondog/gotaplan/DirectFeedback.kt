package com.racoondog.gotaplan

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.direct_feedback_layout.*

class DirectFeedback:AppCompatActivity() {

    private var mailClientOpened = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.direct_feedback_layout)

        directFeedbackQuit_Button.setOnClickListener {
            finish()
        }

        directFeedbackSend_Button.setOnClickListener {

            val title = directFeedback_title_text.text.toString()
            val content = directFeedback_content_text.text.toString()
            val address = arrayOf<String>("void0629@gmail.com")

            val email = Intent(Intent.ACTION_SEND)
            email.setType ("text/plain")
            val resInfo = packageManager.queryIntentActivities(email, 0)
            if (!resInfo.isEmpty())
            {
                for (info in resInfo)
                {
                    if (info.activityInfo.packageName.toLowerCase().contains("android.gm") || info.activityInfo.name.toLowerCase().contains("android.gm"))
                    {
                        email.putExtra(Intent.EXTRA_EMAIL, address)
                        email.putExtra(Intent.EXTRA_SUBJECT, title)
                        email.putExtra(Intent.EXTRA_TEXT, content)
                        email.setPackage(info.activityInfo.packageName)
                        try
                        {
                            startActivityForResult(Intent.createChooser(email, ""),100)

                        }
                        catch (e:ActivityNotFoundException) {
                            Toast.makeText(this, resources.getString(R.string.cannot_find_g_mail_app), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 100){
            finish()
        }
    }



}