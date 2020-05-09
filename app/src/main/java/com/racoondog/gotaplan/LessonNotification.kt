package com.racoondog.gotaplan

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import io.realm.Realm
import io.realm.RealmResults


class LessonNotification : BroadcastReceiver() {
    private val realm = Realm.getDefaultInstance()
    override fun onReceive(context: Context?, intent: Intent?) {

        val id = intent!!.getIntExtra("id",0)

        val subjectData: RealmResults<SubjectData> = realm.where<SubjectData>(SubjectData::class.java)
            .equalTo("id",id)
            .findAll()

        val data = subjectData[0]!!

        val linkageData : RealmResults<SubjectData> = realm.where<SubjectData>(SubjectData::class.java)
            .equalTo("linkageID",data.linkageID)
            .findAll()

        val notificationManager:NotificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(data.currentCycle >= data.maxCycle){
            Toast.makeText(context,context.resources.getString(R.string.lesson_cycle_max),Toast.LENGTH_SHORT).show()
        }else{
            when(intent?.action){
                "apply"-> {

                    if (data.linkageID != 0) {

                        for (i in linkageData.indices) {

                            realm.beginTransaction()
                            linkageData[i]!!.currentCycle +=1
                            realm.commitTransaction()

                        }

                        if (data.currentCycle == data.maxCycle) {

                            val i = Intent(context, LessonCalculateDialog::class.java)
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            i.putExtra("id", data.id)
                            i.action = "apply"
                            context.startActivity(i)
                            notificationManager.cancel(id)

                        } else{
                            notificationManager.cancel(id)
                            val intent = Intent("refresh")
                            context.sendBroadcast(intent)
                        }

                    }else{

                        realm.beginTransaction()
                        data.currentCycle +=1
                        realm.commitTransaction()

                        if (data.currentCycle == data.maxCycle){

                            val i = Intent(context, LessonCalculateDialog::class.java)
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            i.putExtra("id",data.id)
                            i.action = "apply"
                            context.startActivity(i)
                            notificationManager.cancel(id)

                        }else{
                            notificationManager.cancel(id)
                            val intent = Intent("refresh")
                            context.sendBroadcast(intent)
                        }

                    }

                }
                "cancel"-> notificationManager.cancel(id)
            }

        }

    }


}