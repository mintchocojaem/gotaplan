package com.racoondog.gotaplan

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import io.realm.Realm
import io.realm.RealmResults
import kotlin.system.exitProcess

class LessonCalculateDialog:AppCompatActivity() {

    private val realm = Realm.getDefaultInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val id = intent.getIntExtra("id",0)
        val subjectData: RealmResults<SubjectData> = realm.where<SubjectData>(SubjectData::class.java)
            .equalTo("id",id)
            .findAll()
        val data = subjectData[0]!!
        val linkageData : RealmResults<SubjectData> = realm.where<SubjectData>(SubjectData::class.java)
            .equalTo("linkageID",data.linkageID)
            .findAll()

        var lessonCost = "정보 없음"
        if (data.lessonCost != "" ){
            lessonCost = data.lessonCost
        }

        val builder = AlertDialog.Builder(this,R.style.MyDialogTheme)
            .setTitle("수업비용 정산")
            .setMessage("이번달의 수업비용을 정산하시겠습니까?\n비용: $lessonCost")
            .setPositiveButton(this.resources.getString(R.string.dialog_apply)) { _, _ ->

            if(data.linkageID != 0){
                for (i in linkageData.indices) {
                    realm.beginTransaction()
                    linkageData[i]!!.currentCycle = 0
                    realm.commitTransaction()
                    Toast.makeText(applicationContext,"수업비용이 정산되었습니다!",Toast.LENGTH_SHORT).show()
                    this.finish()
                }
            } else{
                realm.beginTransaction()
                data.currentCycle = 0
                realm.commitTransaction()
            }
                Toast.makeText(applicationContext,"수업비용이 정산되었습니다!",Toast.LENGTH_SHORT).show()
                this.finish()
            }

            .setNegativeButton(this.resources.getString(R.string.dialog_cancel)) { _, _ ->
                this.finish()

            }
            .setCancelable(false)
            .show()

        }

}