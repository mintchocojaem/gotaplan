package com.racoondog.mystudent


import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import android.widget.TableLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.schedule_layout.*





class Schedule : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {

        val day = listOf("월","화","수","목","금","토","일")
        val time = listOf("8","9","10","11","12")
        val subject = listOf("화1","화2")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.schedule_layout)

        val layout = TableLayout(this)
        layout.orientation = TableLayout.VERTICAL

        val dayrow = TableRow(this)

         layout.layoutParams = LinearLayout.LayoutParams(
             LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT).apply {

        }

        dayrow.layoutParams = TableLayout.LayoutParams(
            TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT).apply {

        }

        val initday = TextView(this)
        initday.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT).apply {
            initday.text =""

            weight = 1f
        }

        dayrow.addView(initday)


        for (i in 0 until day.size) {

            val daytxt = TextView(this)
            daytxt.gravity = Gravity.CENTER
            daytxt.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT).apply{
                daytxt.text = day[i]
                weight = 3f

            }

            dayrow.addView(daytxt)
        }

        layout.addView(dayrow)

        for (i in 0 until time.size) {



            val timerow = TableRow(this)
            timerow.layoutParams  = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT).apply {
                weight =1f
            }
            timerow.setBackgroundColor(Color.LTGRAY)

                val inittime = TextView(this)
                inittime.gravity = Gravity.CENTER

                inittime.layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                ).apply {
                    inittime.text = time[i]
                    weight = 1f
                    gravity = Gravity.CENTER
                }
                timerow.addView(inittime)

                for (j in 0 until day.size) {

                    val timetxt = TextView(this)
                    val tag  : String = day[j] + i
                    timetxt.tag = tag
                    timetxt.setBackgroundResource(R.drawable.cell_shape)

                    for (k in 0 until subject.size) {
                        if (timetxt.tag == subject[k]) {
                            timetxt.setBackgroundColor(Color.DKGRAY)
                        }
                    }
                    if(timetxt.tag == subject[0]){

                    }


                    timetxt.layoutParams = TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.MATCH_PARENT
                    ).apply {
                        weight = 3f


                    }

                    timerow.addView(timetxt)

                }



            layout.addView(timerow)
        }

        schedulelayout.addView(layout)
    }


}
