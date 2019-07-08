package com.racoondog.mystudent

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alamkanak.weekview.MonthLoader
import com.alamkanak.weekview.WeekView
import com.alamkanak.weekview.WeekViewEvent
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import com.crashlytics.android.answers.ContentViewEvent
import com.crashlytics.android.answers.Answers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics())
        Fabric.with(this, Answers())
        setContentView(R.layout.activity_main)

        // TODO: Use your own attributes to track content views in your app
        Answers.getInstance().logContentView(
            ContentViewEvent()
                .putContentName("Tweet")
                .putContentType("Video")
                .putContentId("1234")
                .putCustomAttribute("Favorites Count", 20)
                .putCustomAttribute("Screen Orientation", "Landscape")
        )

        weekView.monthChangeListener = MonthLoader.MonthChangeListener { newYear, newMonth ->
            listOf(
                WeekViewEvent(
                    1,
                    "김인수",
                    Calendar.getInstance().apply {
                        set(Calendar.HOUR, 6)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.AM_PM, Calendar.AM)
                    },
                    Calendar.getInstance().apply {
                        set(Calendar.HOUR, 8)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.AM_PM, Calendar.AM)
                    }
                )
            )
        }

        weekView.setOnEventClickListener { event, eventRect ->
            startActivity(Intent(this, LessonDetailActivity::class.java))
        }
    }
}
