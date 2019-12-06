package com.racoondog.mystudent

import io.realm.RealmObject

open class SubjectInfo: RealmObject() {

    lateinit var SubjectTitle: String
    lateinit var StartTimeText:String
    lateinit var EndTimeText:String
    lateinit var ContentText:String
}