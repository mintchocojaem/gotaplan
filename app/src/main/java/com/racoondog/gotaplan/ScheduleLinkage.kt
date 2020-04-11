package com.racoondog.gotaplan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.schedule_linkage_recycler_view.*


class ScheduleLinkage :AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.schedule_linkage_recycler_view)


        schedule_linkage_recycler_main.adapter = SLAdapter()
        schedule_linkage_recycler_main.layoutManager = LinearLayoutManager(this)

    }
}