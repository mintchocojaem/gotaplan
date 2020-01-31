package com.racoondog.mystudent

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        var config = RealmConfiguration.Builder()
            .schemaVersion(0)
            .deleteRealmIfMigrationNeeded() // 개발 중에는 활성화

            .migration { realm, oldVersion, newVersion ->
                /*
                var oldVer = oldVersion // 이전 schema 버전
                val schema = realm.schema
                //schema migration 작성 예시
                if (oldVer == 0L)
                {
                        schema.get("DataModel")
                        ?.addField("testdata",Int::class.java)
                    oldVer++ // 다음 업데이트로 넘어가서 이어서 적용( 사용자가 2,3단 업데이트 가능 ) / 없으면 여러 업데이트 한번에 적용 불가

                }
                if(oldVer == 1L){
                    schema.get("DataModel")
                        ?.removeField("testdata")
                    oldVer++
                }

                 */
            }
            .build()

        Realm.setDefaultConfiguration(config)
    }
}