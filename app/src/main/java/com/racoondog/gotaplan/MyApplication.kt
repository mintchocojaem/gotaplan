package com.racoondog.gotaplan

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmList
import io.realm.RealmResults
import io.realm.annotations.Required
import java.lang.Exception

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)

        try {

            val config = RealmConfiguration.Builder()
                .schemaVersion(4)// 초기 버전 0 (앱 업데이트 출시마다 버전 1 씩 올려야 함)
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


                    if (oldVer == 0L)
                    {
                        schema.get("SubjectData")
                            ?.addField("notification",Int::class.java) // 바뀐 데이터 베이스 필드
                        schema["SubjectData"]!!.transform { obj -> obj.setInt("notification", -1)}
                        oldVer++ // 다음 업데이트를 이어서 적용( 사용자가 2,3단 업데이트 가능 ) / 없으면 여러 업데이트 한번에 적용 불가

                    }
                    if (oldVer == 1L){
                        schema.get("SubjectData")
                            ?.removeField("lessonCycle") // 바뀐 데이터 베이스 필드
                            ?.addField("currentCycle",Int::class.java) // 바뀐 데이터 베이스 필드
                            ?.addField("maxCycle",Int::class.java) // 바뀐 데이터 베이스 필드
                            ?.addField("linkageID",Int::class.java) // 바뀐 데이터 베이스 필드
                            ?.addField("calculation",Boolean::class.java) // 바뀐 데이터 베이스 필드
                        schema["SubjectData"]!!.transform { obj -> obj.setString("lessonCost", "")}

                        oldVer++
                    }
                    if (oldVer == 2L)
                    {
                        schema.get("ScheduleData")
                            ?.addField("scheduleInterval",Boolean::class.java) // 바뀐 데이터 베이스 필드
                        oldVer++ // 다음 업데이트를 이어서 적용( 사용자가 2,3단 업데이트 가능 ) / 없으면 여러 업데이트 한번에 적용 불가

                    }
                    if (oldVer == 3L)
                    {

                        schema.get("ScheduleData")
                            ?.addField("id",Int::class.java) // 바뀐 데이터 베이스 필드
                            ?.addRealmListField("subjectData", schema.get("SubjectData"))
                            ?.renameField("scheduleTitle","title")
                            ?.renameField("scheduleDayFlag","dayFlag")
                            ?.renameField("scheduleStartHour","startHour")
                            ?.renameField("scheduleEndHour","endHour")
                            ?.renameField("scheduleInterval","interval")

                        schema.get("SubjectData")
                            ?.removeField("studentName") // 바뀐 데이터 베이스 필드
                            ?.removeField("studentBirth") // 바뀐 데이터 베이스 필드
                            ?.removeField("lessonOnOff") // 바뀐 데이터 베이스 필드
                            ?.removeField("studentPhoneNumber") // 바뀐 데이터 베이스 필드
                            ?.removeField("lessonCost") // 바뀐 데이터 베이스 필드
                            ?.removeField("currentCycle") // 바뀐 데이터 베이스 필드
                            ?.removeField("maxCycle") // 바뀐 데이터 베이스 필드
                            ?.removeField("calculation") // 바뀐 데이터 베이스 필드


                        oldVer++ // 다음 업데이트를 이어서 적용( 사용자가 2,3단 업데이트 가능 ) / 없으면 여러 업데이트 한번에 적용 불가

                    }




                }
                .build()

            Realm.setDefaultConfiguration(config)

            Realm.getDefaultInstance()
        }catch (e:Exception){
            //Realm.deleteRealm(Realm.getDefaultConfiguration())
        }

    }


}