package com.racoondog.gotaplan

import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import io.realm.Realm
import io.realm.RealmResults
import java.util.*
import kotlin.system.exitProcess

class LessonCalculateDialog:AppCompatActivity() {

    private val realm = Realm.getDefaultInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val id = intent.getIntExtra("id", 0)
        val subjectData: RealmResults<SubjectData> =
            realm.where<SubjectData>(SubjectData::class.java)
                .equalTo("id", id)
                .findAll()
        val data = subjectData[0]!!
        val linkageID =
            realm.where<SubjectData>(SubjectData::class.java).equalTo("linkageID", data.linkageID)
                .findAll()
        when (intent.action) {
            "apply" -> {
                val builder = AlertDialog.Builder(this, R.style.MyDialogTheme)
                    .setTitle(resources.getString(R.string.lesson_calculate))
                    .setCancelable(false)
                    .setPositiveButton(resources.getString(R.string.dialog_apply)) { _, _ ->
                        if (data.linkageID != 0) {
                            for (i in linkageID.indices) {
                                realm.beginTransaction()
                                linkageID[i]!!.currentCycle = 0
                                realm.commitTransaction()
                            }
                        } else {
                            realm.beginTransaction()
                            data.currentCycle = 0
                            realm.commitTransaction()
                        }
                        Toast.makeText(applicationContext, resources.getString(R.string.lesson_calculated), Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent("refresh")
                        this.sendBroadcast(intent)
                        finish()
                    }
                    .setNegativeButton(resources.getString(R.string.dialog_cancel)) { _, _ ->
                        finish()
                    }
                if (data.lessonCost.toString() == "") {
                    val lessonCost = resources.getString(R.string.lesson_unknown)


                    val n: String = Locale.getDefault().displayLanguage
                    if (n.compareTo("한국어") == 0){
                        builder.setMessage(
                            "이번 달의 수업비용을 정산하시겠습니까?\n(이번 달 총 ${data.maxCycle}회 중 ${data.currentCycle}회 수업하셨습니다)\n\n" + "비용: $lessonCost")
                    }
                    else {
                        builder.setMessage("Are you willing to calculate this month's tuition?\n(You have taken ${data.currentCycle} of the ${data.maxCycle} Lessons this month)\n\n" +
                                "Cost: $lessonCost")
                    }
                } else {

                    val n: String = Locale.getDefault().displayLanguage
                    if (n.compareTo("한국어") == 0){
                        builder.setMessage(
                            "이번 달의 수업비용을 정산하시겠습니까?\n(이번 달 총 ${data.maxCycle}회 중 ${data.currentCycle}회 수업하셨습니다)\n\n" +
                                    "비용: ${data.lessonCost.toInt()}"
                        )
                    }
                    else {
                        builder.setMessage("Are you willing to calculate this month's tuition?\n(You have taken ${data.currentCycle} of the ${data.maxCycle} Lessons this month)\n\n" +
                                "Cost: ${data.lessonCost.toInt()}")
                    }
                }
                builder.show()

            }

        }
    }
}