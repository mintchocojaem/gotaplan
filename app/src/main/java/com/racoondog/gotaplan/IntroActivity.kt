package com.racoondog.gotaplan

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.AppIntro


class IntroActivity : AppIntro() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window!!.statusBarColor = ContextCompat.getColor(applicationContext,R.color.darkColor)
        window.decorView.systemUiVisibility = 0

        when(intent?.action){
            "TimetableGuide"->{
                addSlide(AppIntroCustomLayoutSlide.newInstance(R.layout.guide_beginning))
                addSlide(AppIntroCustomLayoutSlide.newInstance(R.layout.guide_timetable_first))
                addSlide(AppIntroCustomLayoutSlide.newInstance(R.layout.guide_timetable_second))
                addSlide(AppIntroCustomLayoutSlide.newInstance(R.layout.guide_timetable_second_ex))
                addSlide(AppIntroCustomLayoutSlide.newInstance(R.layout.guide_timetable_third))
                addSlide(AppIntroCustomLayoutSlide.newInstance(R.layout.guide_end))
            }
            "LessonModeGuide"->{

                addSlide(AppIntroCustomLayoutSlide.newInstance(R.layout.guide_beginning))
                addSlide(AppIntroCustomLayoutSlide.newInstance(R.layout.guide_lesson_first))
                addSlide(AppIntroCustomLayoutSlide.newInstance(R.layout.guide_lesson_second))
                addSlide(AppIntroCustomLayoutSlide.newInstance(R.layout.guide_lesson_second_ex))
                addSlide(AppIntroCustomLayoutSlide.newInstance(R.layout.guide_lesson_third))
                addSlide(AppIntroCustomLayoutSlide.newInstance(R.layout.guide_lesson_fourth))
                addSlide(AppIntroCustomLayoutSlide.newInstance(R.layout.guide_lesson_fifth))
                addSlide(AppIntroCustomLayoutSlide.newInstance(R.layout.guide_lesson_sixth))
                addSlide(AppIntroCustomLayoutSlide.newInstance(R.layout.guide_end))

            }
            "CalculationGuide"->{
                addSlide(AppIntroCustomLayoutSlide.newInstance(R.layout.guide_beginning))
                addSlide(AppIntroCustomLayoutSlide.newInstance(R.layout.guide_lesson_third))
                addSlide(AppIntroCustomLayoutSlide.newInstance(R.layout.guide_lesson_fourth))
                addSlide(AppIntroCustomLayoutSlide.newInstance(R.layout.guide_lesson_fifth))
                addSlide(AppIntroCustomLayoutSlide.newInstance(R.layout.guide_lesson_sixth))
                addSlide(AppIntroCustomLayoutSlide.newInstance(R.layout.guide_end))
            }
        }

    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        finish()
    }
    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        finish()
    }

    override fun onSlideChanged(oldFragment: Fragment?, newFragment: Fragment?) {
        super.onSlideChanged(oldFragment, newFragment)
    }

}