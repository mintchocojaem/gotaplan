package com.racoondog.gotaplan

class WidgetItem(private var _id: Int, var content: String) {

    fun get_id(): Int {
        return _id
    }

    fun set_id(_id: Int) {
        this._id = _id
    }

}