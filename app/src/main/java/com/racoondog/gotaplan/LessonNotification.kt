package com.racoondog.gotaplan

import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.material.internal.ContextUtils.getActivity
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

        when(intent?.action){
           "apply"-> {

               if (data.linkageID != 0) {
                   for (i in linkageData.indices) {

                       realm.beginTransaction()
                       linkageData[i]!!.currentCycle +=1
                       realm.commitTransaction()

                   }

                   if (data.currentCycle >= data.maxCycle){

                       val i = Intent(context, LessonCalculateDialog::class.java)
                       i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                       i.putExtra("id",data.id)
                       context.startActivity(i)

                   }

               }else{

                   realm.beginTransaction()
                   data.currentCycle +=1
                   realm.commitTransaction()

                   if (data.currentCycle >= data.maxCycle){

                       val i = Intent(context, LessonCalculateDialog::class.java)
                       i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                       i.putExtra("id",data.id)
                       context.startActivity(i)

                   }

               }

               notificationManager.cancel(id)
           }
           "cancel"-> notificationManager.cancel(id)

        }
    }


}