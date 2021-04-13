package com.racoondog.gotaplan

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import kotlinx.android.synthetic.main.side_menu.view.*

class SideMenu(context: Context?, attrs: AttributeSet?) :
    RelativeLayout(context, attrs), View.OnClickListener {
    private val realm = Realm.getDefaultInstance()
    lateinit var cnxt:MainActivity

    /** 메뉴버튼 클릭 이벤트 리스너  */
    var listener: EventListener? = null
    private fun setEventListener(l: EventListener?) {
        listener = l
    }
    //private var mainLayout:ViewGroup? =null //사이드 나왔을때 클릭방지할 영역
    //private var viewLayout:ViewGroup? =null //전체 감싸는 영역
    //private var sideLayout:ViewGroup? =null//사이드바만 감싸는 영역
    private var isMenuShow = false

    /** 메뉴버튼 클릭 이벤트 리스너 인터페이스  */
    interface EventListener {
        // 닫기 버튼 클릭 이벤트
        fun btnCancel()
        fun createSubject()
    }

    constructor(context: Context?) : this(context, null) {
        init()
    }

    private fun init() {

        LayoutInflater.from(context).inflate(R.layout.side_menu, this, true)
        findViewById<View>(R.id.btn_cancel).setOnClickListener(this)
        findViewById<View>(R.id.side_menu_add).setOnClickListener(this)

        //findViewById(R.id.btn_side_level_1).setOnClickListener(this)

        val list: ArrayList<String> = ArrayList()
        val idList: ArrayList<String> = ArrayList()

        val scheduleData = realm.where(ScheduleData::class.java).findAll()
        for (i in scheduleData.indices) {
            list.add(String.format("${scheduleData[i]?.title}",i))
            idList.add(String.format("${scheduleData[i]?.id}",i))
        }

        // 리사이클러뷰에 LinearLayoutManager 객체 지정.

        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        val recyclerView: RecyclerView = findViewById(R.id.side_menu_list)
        recyclerView.setLayoutManager(LinearLayoutManager(context))

        // 리사이클러뷰에 SimpleTextAdapter 객체 지정.

        // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
        val adapter = SideMenuAdapter(list,idList)
        recyclerView.adapter = adapter


    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_cancel -> listener!!.btnCancel()
            R.id.side_menu_add -> listener!!.createSubject()
            else -> {
            }

        }
    }

    fun addSideView(mainLayout: ViewGroup, viewLayout: ViewGroup, sideLayout: ViewGroup) {
        val sidebar = SideMenu(MainActivity.mContext)
        sideLayout?.addView(sidebar)
        viewLayout?.setOnClickListener(View.OnClickListener { })
        sidebar.setEventListener(object : SideMenu.EventListener {
            override fun btnCancel() {
                closeMenu(mainLayout, viewLayout, sideLayout)
            }

            override fun createSubject() {
                val subjectIntent = Intent(context, CreateSchedule::class.java)
                subjectIntent.flags =
                    (Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                closeMenu(mainLayout, viewLayout, sideLayout)
                cnxt.startActivityForResult(subjectIntent, 100)

            }
        })
        viewLayout?.setOnClickListener {
            closeMenu(mainLayout, viewLayout, sideLayout)
        }
    }

    fun closeMenu(mainLayout: ViewGroup, viewLayout: ViewGroup, sideLayout: ViewGroup) {
        isMenuShow = false
        val slide: Animation = AnimationUtils.loadAnimation(MainActivity.mContext, R.anim.sidebar_hidden)
        sideLayout?.startAnimation(slide)
        Handler().postDelayed(Runnable {
            viewLayout?.setVisibility(View.GONE)
            viewLayout?.setEnabled(false)
            mainLayout?.setEnabled(true)
        }, 450)

    }

    fun showMenu(mainLayout: ViewGroup, viewLayout: ViewGroup, sideLayout: ViewGroup) {
        isMenuShow = true
        val slide: Animation = AnimationUtils.loadAnimation(context, R.anim.sidebar_show)
        sideLayout?.startAnimation(slide)
        viewLayout?.setVisibility(View.VISIBLE)
        viewLayout?.setEnabled(true)
        mainLayout?.setEnabled(false)
    }

}