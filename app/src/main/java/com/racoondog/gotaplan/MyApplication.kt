package com.racoondog.gotaplan

import android.app.Application
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import io.realm.Realm
import io.realm.RealmConfiguration
import java.lang.Exception

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)

        try {

            val config = RealmConfiguration.Builder()
                .schemaVersion(0)// 초기 버전 0 (앱 업데이트 출시마다 버전 1 씩 올려야 함)
                //.deleteRealmIfMigrationNeeded() // 개발 중에는 활성화 (데이터 베이스 필드가 변하면 앱 깔릴 때 데이터 베이스가 초기화 됨)

                .migration { realm, oldVersion, newVersion -> //앱 출시 부터 활성화 (데이터 베이스 정보를 유지하며 업데이트)

                    var oldVer = oldVersion // 이전 schema 버전
                    val schema = realm.schema

                    //schema migration 작성 예시
                    /*

                        if (oldVer == 0L)

                        {
                                schema.get("DataModel")
                                ?.addField("testdata",Int::class.java) // 바뀐 데이터 베이스 필드
                            oldVer++ // 다음 업데이트를 이어서 적용( 사용자가 2,3단 업데이트 가능 ) / 없으면 여러 업데이트 한번에 적용 불가

                        }
                        if(oldVer == 1L){
                            schema.get("DataModel")
                                ?.removeField("testdata") // 바뀐 데이터 베이스 필드
                            oldVer++
                        }

                    */


                }
                .build()

            Realm.setDefaultConfiguration(config)

            Realm.getDefaultInstance()
            remoteConfigInit()
        }catch (e:Exception){
            Realm.deleteRealm(Realm.getDefaultConfiguration())
        }

    }

    private fun remoteConfigInit() {
        //  developer mode enable when debug
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setDeveloperModeEnabled(BuildConfig.DEBUG)
            .build()

        // set in-app defaults
        val remoteConfigDefaults = HashMap<String, Any>()
        remoteConfigDefaults["latest_version"] = "1.0.0"

        // FirebaseRemoteConfig init
        FirebaseRemoteConfig.getInstance().apply {
            setConfigSettings(configSettings)
            setDefaults(remoteConfigDefaults)
            // every 60 minutes refresh cache
            // default value is 12 hours
            fetch(60).addOnCompleteListener { task: Task<Void> ->
                if (task.isSuccessful) {
                    Log.d("RemoteConfig", "remote config is fetched.")
                    activateFetched()
                }
            }
        }
    }

}