package com.racoondog.gotaplan

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getSystemService
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.notification.view.*
import java.util.*


class Notification:ConstraintLayout {

    constructor(context: Context) : super(context){initView()}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){initView()}
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){initView()}

    private var mListener:OnCustomEventListener? = null
    companion object{
        var notificationFlag:Int = -1
    }
    interface OnCustomEventListener{
        fun onEvent()
    }

    fun setOnCustomEventListener(eventListener: OnCustomEventListener){
        mListener = eventListener
    }

    private fun initView(){
        val inflaterService = Context.LAYOUT_INFLATER_SERVICE
        val inflater = context.getSystemService(inflaterService) as LayoutInflater
        val view = inflater.inflate(R.layout.notification,this,false)
        addView(view)

    }

    fun showDialog(context: Context){
        val dialog = NotificationDialog(context, object : NotificationDialog.ICustomDialogEventListener {
            override fun customDialogEvent(flag: Int,text:String) {
                // Do something with the value here, e.g. set a variable in the calling activity
                mListener?.onEvent()
                notificationFlag = flag
                notify_status.setText(text)
            }
        })
        dialog.show()
    }

    fun setText(notificationFlag:Int){
        var text = ""
        when(notificationFlag){
            -1 -> text = resources.getString(R.string.none)
            0 -> text = resources.getString(R.string.fixed_time)
            5 -> text = resources.getString(R.string.before_5m)
            10 -> text = resources.getString(R.string.before_10m)
            30 -> text = resources.getString(R.string.before_30m)
            60 -> text = resources.getString(R.string.before_1h)
            720 -> text = resources.getString(R.string.before_12h)
        }
        notify_status.setText(text)
    }

    fun setAlarm(id:Int,dayFlag:Int,startHour:Int,startMinute:String,notification:Int,title:String) {

        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, AlarmReceiver::class.java)
        alarmIntent.putExtra("id",id)
        alarmIntent.putExtra("title",title)
        val pendingIntent = PendingIntent.getBroadcast(context,id,alarmIntent,FLAG_UPDATE_CURRENT)


        if(notification == -1){
            alarmMgr.cancel(pendingIntent)
        }else{
            // 현재 지정된 시간으로 알람 시간 설정

            var date = 0
            when(dayFlag){
                1 -> date = 2
                2 -> date = 3
                3 -> date = 4
                4 -> date = 5
                5 -> date = 6
                6 -> date = 7
                7 -> date = 1
            }
            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, startHour)
                set(Calendar.MINUTE, startMinute.toInt() - notification)
                set(Calendar.SECOND, 0)
                set(Calendar.DAY_OF_WEEK, date)
            }

            val weekInterval : Long = (7 * 24 * 60 * 60 * 1000).toLong()

            // setRepeating() lets you specify a precise custom interval--in this case,
            // 20 minutes.
            if (calendar.before(Calendar.getInstance())) {
                // 이미 지난 시간을 지정했다면 다음날 같은 시간으로 설정

                calendar.add(Calendar.DATE, 7)

                alarmMgr.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis, weekInterval, pendingIntent)

            }else{
                alarmMgr.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis, weekInterval, pendingIntent)
            }

           val editor = context.getSharedPreferences("alarm", Context.MODE_PRIVATE).edit()
            editor.putLong("$id", calendar.timeInMillis)
            editor.apply()


            val pm = context.packageManager
            val receiver = ComponentName(context, DeviceBootReceiver::class.java)
            pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )

        }

    }

    fun deleteAlarm(id: Int){
        val alarmIntent = Intent(context, AlarmReceiver::class.java)
        val editor =
            context.getSharedPreferences("alarm", Context.MODE_PRIVATE)
                .edit()
        val am =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val pendingIntent = PendingIntent.getBroadcast(
            context, id, alarmIntent, FLAG_UPDATE_CURRENT)
        am.cancel(pendingIntent)
        editor.remove("$id")
        editor.apply()

    }

}