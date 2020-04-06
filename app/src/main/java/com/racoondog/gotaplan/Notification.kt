package com.racoondog.gotaplan

import android.app.AlarmManager
import android.app.PendingIntent
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
import kotlinx.android.synthetic.main.notification.view.*
import java.util.*


class Notification:ConstraintLayout {

    constructor(context: Context?) : super(context){initView()}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){initView()}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
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

    fun setAlarm(startHour:Int,startMinute:Int,dayFlag:Int,id:Int) {
        if(notificationFlag == -1){
            deleteAlarm(id)
        }else{
            // 현재 지정된 시간으로 알람 시간 설정
            val calendar = Calendar.getInstance()
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
            calendar.timeInMillis = System.currentTimeMillis()
            calendar[Calendar.HOUR_OF_DAY] = startHour
            calendar[Calendar.MINUTE] = startMinute - notificationFlag
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.DAY_OF_WEEK] = date

            // 이미 지난 시간을 지정했다면 다음날 같은 시간으로 설정
            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DATE, 7)
            }


            //  Preference에 설정한 값 저장
            val editor = context.getSharedPreferences("alarm", Context.MODE_PRIVATE).edit()
            editor.putLong("$id", calendar.timeInMillis)
            editor.apply()

            val pm = context.packageManager
            val receiver = ComponentName(context, DeviceBootReceiver::class.java)
            val alarmIntent = Intent(context, AlarmReceiver::class.java)
            alarmIntent.putExtra("id",id)
            val pendingIntent = PendingIntent.getBroadcast(context, id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            val alarmManager =
                context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            /*
            Toast.makeText(
                context.applicationContext,
                resources.getString(R.string.notification_on),
                Toast.LENGTH_SHORT
            ).show()
             */

            // 사용자가 매일 알람을 허용했다면

            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,calendar.timeInMillis,pendingIntent)
            }else{
                alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.timeInMillis,pendingIntent)
            }
            // 부팅 후 실행되는 리시버 사용가능하게 설정
            pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
            notificationFlag = -1
        }

    }

    fun deleteAlarm(id: Int){
        val alarmIntent = Intent(context, AlarmReceiver::class.java)
        alarmIntent.putExtra("id",id)
        val editor =
            context.getSharedPreferences("alarm", Context.MODE_PRIVATE)
                .edit()
        val am =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val pendingIntent = PendingIntent.getBroadcast(
            context, id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        am.cancel(pendingIntent)
        editor.remove("$id")
        editor.apply()

    }

}