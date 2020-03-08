package com.racoondog.gotaplan

import android.app.Activity
import android.app.AlertDialog.BUTTON_POSITIVE
import android.app.AlertDialog.Builder
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.Constants
import com.anjlab.android.iab.v3.TransactionDetails
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList


class MainActivity: AppCompatActivity(),BillingProcessor.IBillingHandler {

    //Developer: Void

    private val realm = Realm.getDefaultInstance()

    companion object{
        var mContext:Context? = null
    }
    val weekView by lazy { WeekView(this) }

    private var intentStartTime: Int = 0
    private var intentEndTime: Int = 0
    private var intentFlag: Int = 0
    private val bp by lazy { BillingProcessor(this,getString(R.string.in_app_license_key), this) }
    private val storage:AppStorage by lazy { AppStorage(this) }

    private lateinit var mInterstitialAd: InterstitialAd

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mContext = this
        setSupportActionBar(main_toolbar)  //Actionbar 부분
        supportActionBar?.setDisplayUseLogoEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        bp.initialize()

        loadData()//데이터 불러오기
        changeTheme()// 테마 변경

        if (!storage.purchasedRemoveAds()) {
            MobileAds.initialize(this, getString(R.string.ad_mob_app_id))
            mInterstitialAd = InterstitialAd(this)
            mInterstitialAd.adUnitId = getString(R.string.front_ad_unit_id)
            mInterstitialAd.loadAd(AdRequest.Builder().build())

            mInterstitialAd.adListener = object: AdListener() { //전면 광고의 상태를 확인하는 리스너 등록
                override fun onAdLoaded() {
                    super.onAdLoaded()
                    mInterstitialAd.show()
                }
            }
        }

        weekView_layout.setOnClickListener {
            val scheduleData = realm.where(ScheduleData::class.java).findFirst()

            if (scheduleData == null) {
                val scheduleIntent = Intent(this, CreateSchedule::class.java)
                scheduleIntent.flags = (Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivityForResult(scheduleIntent, 100)
            }

        }

        addSubjectButton.setOnClickListener {
            val subjectIntent = Intent(this, CreateSubject::class.java)
            subjectIntent.flags = (Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            subjectIntent.putExtra("start_time", intentStartTime)
            subjectIntent.putExtra("end_time", intentEndTime)
            subjectIntent.putExtra("day_flag", intentFlag)
            startActivityForResult(subjectIntent, 102)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(!bp.handleActivityResult(requestCode, resultCode, data)) {

            super.onActivityResult(requestCode, resultCode, data)
            //MainActivity로 들어오는 onActivityResult 부분 -> Intent 후 값 반환
            if (resultCode == Activity.RESULT_OK) {
                when (requestCode) {
                    100 -> {


                        val scheduleDayFlag = data!!.getIntExtra("scheduleDayFlag", 0)
                        val scheduleStartHour = data.getIntExtra("scheduleStartHour", 0)
                        val scheduleEndHour = data.getIntExtra("scheduleEndHour", 0)

                        schedule_add.visibility = View.INVISIBLE
                        addSubjectButton.visibility = View.VISIBLE

                        toolbar_title.text = data.getStringExtra("title")

                        intentStartTime = scheduleStartHour
                        intentEndTime = scheduleEndHour
                        intentFlag = scheduleDayFlag

                        realm.beginTransaction()
                        val dataBase: ScheduleData = realm.createObject(ScheduleData::class.java)

                        dataBase.apply {
                            this.scheduleDayFlag = scheduleDayFlag
                            this.scheduleStartHour = scheduleStartHour
                            this.scheduleEndHour = scheduleEndHour
                            this.scheduleTitle = toolbar_title.text.toString()
                        }
                        realm.commitTransaction()

                        weekView.layoutParams = ConstraintLayout.LayoutParams(
                            ConstraintLayout.LayoutParams.MATCH_PARENT,
                            ConstraintLayout.LayoutParams.MATCH_PARENT
                        ).apply {

                        }

                        weekView.drawSchedule(scheduleDayFlag, scheduleStartHour, scheduleEndHour)

                        weekView_layout.addView(weekView)
                        main_text.visibility = View.INVISIBLE
                    }
                    101 -> {

                    }
                    102 -> {

                        val startHour = data!!.getIntExtra("StartHour", 0) // 24시 형식 시간
                        val endHour = data.getIntExtra("EndHour", 0)// 24시 형식 시간
                        val dayFlag = data.getIntExtra("DayFlag", 0)
                        val subjectTitle = data.getStringExtra("SubjectTitle")
                        val startMinute = data.getStringExtra("StartMinute") // 오전/오후 형식 시간
                        val endMinute = data.getStringExtra("EndMinute")
                        val contentText = data.getStringExtra("ContentText")
                        val lessonOnOff = data.getBooleanExtra("LessonOnOff", false)
                        val colorCode = data.getIntExtra("colorCode", 0)

                        val id = weekView.createID(0, 128)//다음으로 만들어질 weekView의 id 값을 결정하는 변수

                        realm.beginTransaction()
                        val subjectInfo: SubjectData = realm.createObject(SubjectData::class.java)
                        subjectInfo.apply {
                            this.id = id
                            this.dayFlag = dayFlag
                            this.startHour = startHour
                            this.startMinute = startMinute
                            this.endHour = endHour
                            this.endMinute = endMinute
                            this.title = subjectTitle
                            this.content = contentText
                            this.lessonOnOff = lessonOnOff
                            this.subjectColor = colorCode

                        }
                        realm.commitTransaction()

                        weekView.createSubject(
                            startHour, startMinute.toInt()
                            , endHour, endMinute.toInt(), dayFlag, intentStartTime, id, colorCode
                        )
                    }
                    103 -> {
                        weekView.refresh(weekView)
                    }
                    105 ->{
                        val statusBarColor = data!!.getIntExtra("statusBarColor",0)
                        val mainButtonBarColor = data.getIntExtra("mainButtonColor",0)

                        val themeData = realm.where(ThemeData::class.java).findFirst()!!
                        realm.beginTransaction()
                        themeData.statusBarColor = statusBarColor
                        themeData.mainButtonColor = mainButtonBarColor
                        realm.commitTransaction()

                        changeTheme()// 테마 변경
                    }

                }
            }
            if (resultCode == 104) {

                when (requestCode) {

                    103 -> {
                        weekView.deleteSubject(WeekView.ID)
                    }

                }

            }
        }



    }

    private fun loadData() {

        val scheduleData = realm.where(ScheduleData::class.java).findFirst()
        if (scheduleData != null) {

            main_text.visibility = View.INVISIBLE
            schedule_add.visibility = View.INVISIBLE
            addSubjectButton.visibility = View.VISIBLE
            if(scheduleData?.scheduleTitle != "") {
                toolbar_title?.text = scheduleData?.scheduleTitle
            }

            intentFlag = scheduleData.scheduleDayFlag
            intentStartTime = scheduleData.scheduleStartHour
            intentEndTime = scheduleData.scheduleEndHour

            weekView.layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            ).apply {

            }
            weekView.drawSchedule(intentFlag, intentStartTime, intentEndTime)
            weekView_layout.addView(weekView)


            val subjectData: RealmResults<SubjectData> =
                realm.where<SubjectData>(SubjectData::class.java).findAll()
            for (data in subjectData) {
                weekView.createSubject(
                    data.startHour,
                    data.startMinute.toInt(),
                    data.endHour,
                    data.endMinute.toInt(),
                    data.dayFlag,
                    scheduleData.scheduleStartHour,
                    data.id,
                    data.subjectColor
                )
            }

        }

    }


    private fun changeTheme() {

        //realm.beginTransaction()
        //realm.createObject(ThemeData::class.java)
        //realm.commitTransaction()

        //val themeData = realm.where(ThemeData::class.java).findFirst()!!
        //window.statusBarColor = themeData.statusBarColor
        //addSubjectButton.backgroundTintList = ColorStateList.valueOf(themeData.mainButtonColor)
        //schedule_add.backgroundTintList = ColorStateList.valueOf(themeData.mainButtonColor)


    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean { //Menu 추가 부분
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu, menu)

        if (storage.purchasedRemoveAds()) {
            menu.findItem(R.id.purchasePro).isVisible = false
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean { //Menu 목록 부분
        val scheduleData = realm.where(ScheduleData::class.java).findFirst()
        when (item.itemId) {
            R.id.home -> {
                //onBackPressed()
                return true
            }
            R.id.scheduleSetting -> {
                if (scheduleData == null) {
                    Toast.makeText(this,resources.getString(R.string.add_schedule_first),Toast.LENGTH_SHORT).show()
                }else{
                    val dialog = ScheduleDialog(this)
                    dialog.cnxt = this
                    dialog.show()
                }
                return true
            }
            R.id.subjectSetting ->{
                if (scheduleData == null) {
                    Toast.makeText(this,resources.getString(R.string.add_schedule_first),Toast.LENGTH_SHORT).show()
                }else{
                    val dialog = SubjectDialog(this)
                    dialog.cnxt = this
                    dialog.show()
                }
                return true
            }

            /*
            R.id.themeSetting -> {

                val themeIntent = Intent(this, ThemeSetting::class.java)
                startActivityForResult(themeIntent,105)
                return true
            }

             */
            R.id.purchasePro -> {

                if (storage.purchasedRemoveAds()) {
                    // TODO: 이미 구매하셨습니다. 메세지 띄우기!
                } else {
                    bp.purchase(this, getString(R.string.in_app_no_ads_sku))
                }
                return true
            }

            R.id.license -> {
                val licenseIntent = Intent(this, License::class.java)
                startActivity(licenseIntent)
                return true
            }
            R.id.directFeedback -> {
                val directFeedbackIntent = Intent(this, DirectFeedback::class.java)
                startActivity(directFeedbackIntent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        bp.release()
        super.onDestroy()
        realm.close()

    }

    fun checkPermissions(permission: String,result: ()->Unit){

        //거절되었거나 아직 수락하지 않은 권한(퍼미션)을 저장할 문자열 배열 리스트
        //필요한 퍼미션들을 하나씩 끄집어내서 현재 권한을 받았는지 체크
        val rejectedPermissionList = ArrayList<String>()

        if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            //만약 권한이 없다면 rejectedPermissionList 로 추가
            rejectedPermissionList.add(permission)

        }
        else{
            result()
        }

        //거절된 퍼미션이 있다면...
        if(permission in rejectedPermissionList){
            //권한 요청
            val array = arrayOfNulls<String>(rejectedPermissionList.size)
            ActivityCompat.requestPermissions(this, rejectedPermissionList.toArray(array),100)
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            100->{
                if(grantResults.isNotEmpty()) {
                    for((i, permission) in permissions.withIndex()) {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {

                            //권한 획득 실패

                            val dialog = Builder(this).apply {
                                val n: String = Locale.getDefault().displayLanguage
                                if (n.compareTo("한국어") == 0){
                                    this.setMessage("다음 기능을 사용하기 위해서는 $permission 권한이 필요합니다. 계속 하시겠습니까?")
                                }
                                else {
                                    this.setMessage("$permission permission is required to use the following features: Would you like to go on?")
                                }
                            }
                                .setCancelable(false)
                                .setPositiveButton(resources.getString(R.string.dialog_apply)) { _, _ ->
                                    ActivityCompat.requestPermissions(this, permissions,100) }
                                .show()
                            dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(applicationContext,R.color.defaultAccentColor))

                        }
                    }
                }

            }
        }
    }

    override fun onBillingInitialized() {

        // * 처음에 초기화됬을때.
        storage.setPurchasedRemoveAds(bp.isPurchased(getString(R.string.in_app_no_ads_sku)))
    }

    override fun onPurchaseHistoryRestored() {

        // * 구매 정보가 복원되었을때 호출
        // bp.loadOwnedPurchasesFromGoogle() 하면 호출 가능
        storage.setPurchasedRemoveAds(bp.isPurchased(getString(R.string.in_app_no_ads_sku)))

    }

    override fun onProductPurchased(productId: String, details: TransactionDetails?) {

        // * 구매 완료시 호출
        // productId: 구매한 sku (ex) no_ads)
        // details: 결제 관련 정보

        if (productId == getString(R.string.in_app_no_ads_sku)) {
            // TODO: 구매 해 주셔서 감사합니다! 메세지 보내기
            storage.setPurchasedRemoveAds(bp.isPurchased(getString(R.string.in_app_no_ads_sku)))

            // * 광고 제거는 1번 구매하면 영구적으로 사용하는 것이므로 consume하지 않지만,
            // 만약 게임 아이템 100개를 주는 것이라면 아래 메소드를 실행시켜 다음번에도 구매할 수 있도록 소비처리를 해줘야한다.
            // bp.consumePurchase(Config.Sku)
        }

    }

    override fun onBillingError(errorCode: Int, error: Throwable?) {

        // * 구매 오류시 호출
        // errorCode == Constants.BILLING_RESPONSE_RESULT_USER_CANCELED 일때는
        // 사용자가 단순히 구매 창을 닫은것임으로 이것 제외하고 핸들링하기.
         if (errorCode != Constants.BILLING_RESPONSE_RESULT_USER_CANCELED) {
            Toast.makeText(this, R.string.unknown_error, Toast.LENGTH_SHORT).show()
        }

    }

}

