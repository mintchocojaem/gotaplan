package com.racoondog.gotaplan

import android.content.Context
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

class SideMenu(context: Context?, attrs: AttributeSet?) :
    RelativeLayout(context, attrs), View.OnClickListener {

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
        fun btnLevel1()
    }

    constructor(context: Context?) : this(context, null) {
        init()
    }

    private fun init() {

        LayoutInflater.from(context).inflate(R.layout.side_menu, this, true)
        findViewById<View>(R.id.btn_cancel).setOnClickListener(this)
        //findViewById(R.id.btn_side_level_1).setOnClickListener(this);
        val list: ArrayList<String> = ArrayList()
        for (i in 0..10) {
            list.add(String.format("TEXT %d", i))
        }

        // 리사이클러뷰에 LinearLayoutManager 객체 지정.

        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        val recyclerView: RecyclerView = findViewById(R.id.side_menu_list)
        recyclerView.setLayoutManager(LinearLayoutManager(context))

        // 리사이클러뷰에 SimpleTextAdapter 객체 지정.

        // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
        val adapter = SideMenuAdapter(list)

        recyclerView.adapter = adapter
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_cancel -> listener!!.btnCancel()
            else -> {
            }
        }
    }

    fun addSideView(mainLayout: ViewGroup,viewLayout:ViewGroup,sideLayout:ViewGroup) {
        val sidebar = SideMenu(MainActivity.mContext)
        sideLayout?.addView(sidebar)
        viewLayout?.setOnClickListener(View.OnClickListener { })
        sidebar.setEventListener(object : SideMenu.EventListener {
            override fun btnCancel() {
                //Toast.makeText(MainActivity.mContext,"btnCancel", Toast.LENGTH_LONG).show()
                closeMenu(mainLayout,viewLayout,sideLayout)
            }

            override fun btnLevel1() {
                //Toast.makeText(MainActivity.mContext,"btnLevel1", Toast.LENGTH_LONG).show()
                closeMenu(mainLayout,viewLayout,sideLayout)
            }
        })
        viewLayout?.setOnClickListener {
            closeMenu(mainLayout,viewLayout,sideLayout)
        }
    }

    fun closeMenu(mainLayout:ViewGroup,viewLayout: ViewGroup,sideLayout: ViewGroup) {
        isMenuShow = false
        val slide: Animation = AnimationUtils.loadAnimation(MainActivity.mContext, R.anim.sidebar_hidden)
        sideLayout?.startAnimation(slide)
        Handler().postDelayed(Runnable {
            viewLayout?.setVisibility(View.GONE)
            viewLayout?.setEnabled(false)
            mainLayout?.setEnabled(true) }, 450)
    }

    fun showMenu(mainLayout:ViewGroup,viewLayout: ViewGroup,sideLayout: ViewGroup) {
        isMenuShow = true
        val slide: Animation = AnimationUtils.loadAnimation(context, R.anim.sidebar_show)
        sideLayout?.startAnimation(slide)
        viewLayout?.setVisibility(View.VISIBLE)
        viewLayout?.setEnabled(true)
        mainLayout?.setEnabled(false)
    }

}