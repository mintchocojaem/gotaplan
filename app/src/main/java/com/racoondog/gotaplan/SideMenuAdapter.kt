package com.racoondog.gotaplan

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView


class SideMenuAdapter internal constructor(list: ArrayList<String>?) :
    RecyclerView.Adapter<SideMenuAdapter.ViewHolder>() {

    private var data: ArrayList<String>? = null

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    inner class ViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var scheduleTitle: TextView = itemView.findViewById(R.id.side_menu_item_title)

        init {
        itemView.setOnClickListener {
            Toast.makeText(MainActivity.mContext, "$adapterPosition", Toast.LENGTH_LONG).show()

        }
            // 뷰 객체에 대한 참조. (hold strong reference)
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
        holder.scheduleTitle.text = text
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    override fun getItemCount(): Int {
        return data!!.size
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    init {
        data = list
    }
}