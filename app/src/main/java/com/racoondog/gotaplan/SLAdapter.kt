package com.racoondog.gotaplan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.schedule_linkage_item.view.*


class SLAdapter : RecyclerView.Adapter<SLAdapter.MainViewHolder>() {
    private val realm = Realm.getDefaultInstance()
    private var items: RealmResults<SubjectData> = realm.where<SubjectData>(SubjectData::class.java)
        .findAll()

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = MainViewHolder(parent)

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holer: MainViewHolder, position: Int) {
        items[position]!!.let { item ->
            with(holer) {
                title.text = item.title

            }
        }
    }

    inner class MainViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.schedule_linkage_item, parent, false)) {
        val title = itemView.schedule_linkage_title
    }
}
