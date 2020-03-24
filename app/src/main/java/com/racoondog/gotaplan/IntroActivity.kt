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

        addSlide(AppIntroCustomLayoutSlide.newInstance(R.layout.app_intro))
        addSlide(AppIntroCustomLayoutSlide.newInstance(R.layout.app_intro_first))
        addSlide(AppIntroCustomLayoutSlide.newInstance(R.layout.app_intro_second))
        addSlide(AppIntroCustomLayoutSlide.newInstance(R.layout.app_intro_second_ex))
        addSlide(AppIntroCustomLayoutSlide.newInstance(R.layout.app_intro_third))
        addSlide(AppIntroCustomLayoutSlide.newInstance(R.layout.app_intro_last))

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