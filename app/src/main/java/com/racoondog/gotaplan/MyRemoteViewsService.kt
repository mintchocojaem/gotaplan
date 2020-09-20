package com.racoondog.gotaplan

import android.content.Intent
import android.widget.RemoteViewsService

class MyRemoteViewsService : RemoteViewsService() {
    //필수 오버라이드 함수 : RemoteViewsFactory를 반환한다.
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return MyRemoteViewsFactory(this.applicationContext)
    }
}
