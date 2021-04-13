package com.racoondog.gotaplan

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.side_menu_item.view.*
import kotlinx.android.synthetic.main.subject_detail.*


class SideMenuAdapter internal constructor(list: ArrayList<String>?,idList: ArrayList<String>?) :
    RecyclerView.Adapter<SideMenuAdapter.ViewHolder>() {

    private var data: ArrayList<String>? = null
    private var idData: ArrayList<String>? = null

    var cnxt:MainActivity = MainActivity.mContext as MainActivity
    private val realm = Realm.getDefaultInstance()

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    inner class ViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.side_menu_item_title)

        init {
            val data = realm.where(ScheduleData::class.java).findAll()

            if (data.size == 1) itemView.side_menu_item_delete.visibility = View.INVISIBLE
            itemView.setOnClickListener {
                //Toast.makeText(MainActivity.mContext, "$adapterPosition", Toast.LENGTH_LONG).show()

                MainActivity.scheduleID = idData?.get(adapterPosition)!!.toInt()
                cnxt.loadData()
            }
            // 뷰 객체에 대한 참조. (hold strong reference)
            itemView.side_menu_item_delete.setOnClickListener {
                val beforeScheduleID = MainActivity.scheduleID
                var initFlag = false
                if(MainActivity.scheduleID == idData?.get(adapterPosition)!!.toInt()){
                    initFlag = true
                }

                val builder = AlertDialog.Builder(cnxt, R.style.MyDialogTheme)
                    .setTitle(cnxt.resources.getString(R.string.delete))
                    .setMessage(cnxt.resources.getString(R.string.delete_schedule))
                    .setPositiveButton(cnxt.resources.getString(R.string.dialog_apply)) { _, _ ->

                        MainActivity.scheduleID = idData?.get(adapterPosition)!!.toInt()
                        val scheduleData = realm.where(ScheduleData::class.java).equalTo("id",MainActivity.scheduleID).findFirst()

                        if (scheduleData != null){
                            var subjectList = scheduleData.subjectData.where().findAll()

                            for (i in subjectList.indices){
                                val nestedData = realm.where<SubjectData>(SubjectData::class.java).equalTo("id",subjectList[0]?.id)
                                    .findFirst()
                                if(nestedData != null){
                                    Notification(cnxt.applicationContext).deleteAlarm(nestedData.id)
                                    realm.beginTransaction()
                                    nestedData.deleteFromRealm()
                                    realm.commitTransaction()

                                }

                            }

                            realm.beginTransaction()
                            scheduleData.deleteFromRealm()
                            realm.commitTransaction()



                            Toast.makeText(cnxt.applicationContext,cnxt.applicationContext.getString(R.string.schedule_deleted)
                                ,Toast.LENGTH_SHORT).show()

                            if (initFlag){
                                cnxt.initSchedule()
                            }else {
                                MainActivity.scheduleID = beforeScheduleID
                            }

                            cnxt.loadData()
                        }


                        SideMenu(cnxt).closeMenu(cnxt.main, cnxt.fl_silde, cnxt.view_sildebar)
                    }

                    .setNegativeButton(cnxt.resources.getString(R.string.dialog_cancel)) { _, _ ->

                    }.show()

                builder.window!!.attributes.apply {
                    width = WindowManager.LayoutParams.WRAP_CONTENT
                    height = WindowManager.LayoutParams.WRAP_CONTENT}

                builder.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                builder.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                    ContextCompat.getColor(
                        cnxt.applicationContext,
                        R.color.colorCancel
                    )
                )
                builder.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                    ContextCompat.getColor(
                        cnxt.applicationContext,
                        R.color.defaultAccentColor
                    )
                )

            }
        }
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context: Context = parent.context
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(R.layout.side_menu_item, parent, false)

        return ViewHolder(view)
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val text = data!![position]
        holder.title.text = text

    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    override fun getItemCount(): Int {
        return data!!.size

    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    init {
        data = list
        idData= idList
    }
}