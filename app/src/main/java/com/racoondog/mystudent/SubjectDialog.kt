package com.racoondog.mystudent

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.subject_dialog.*

class SubjectDialog: Dialog {

    lateinit var cnxt:MainActivity

    private val realm = Realm.getDefaultInstance()

    constructor(context: Context) : super(context)
    constructor(context: Context, themeResId: Int) : super(context, themeResId)
    constructor(
        context: Context,
        cancelable: Boolean,
        cancelListener: DialogInterface.OnCancelListener?
    ) : super(context, cancelable, cancelListener)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        layoutParams.dimAmount = 0.8f
        window.attributes = layoutParams

        setContentView(R.layout.subject_dialog)

        initSubject.setOnClickListener {
            deleteSubject()
            dismiss()
        }

    }

    private fun deleteSubject(){

        val builder = AlertDialog.Builder(context)

            .setTitle("초기화")
            .setMessage("과목을 초기화하시겠습니까? \n\n(모든 과목의 데이터가 삭제됩니다.)")

            .setPositiveButton("확인") { _, _ ->

                val scheduleData = realm.where(ScheduleData::class.java).findFirst()

                var subjectData: RealmResults<SubjectData> = realm.where<SubjectData>(SubjectData::class.java)
                    .findAll()

                for(i in subjectData.indices){
                    realm.beginTransaction()
                    subjectData[0]!!.deleteFromRealm()
                    realm.commitTransaction()
                }

                for(i in 1 .. scheduleData!!.scheduleDayFlag){
                    cnxt.weekView.findViewWithTag<ConstraintLayout>(i).removeAllViews()
                }

                Toast.makeText(context,"과목이 초기화되었습니다.",Toast.LENGTH_SHORT).show()

            }

            .setNegativeButton("취소") { _, _ ->

            }

            .show()
        builder.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(cnxt.resources.getColor(R.color.colorCancel))
        builder.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(cnxt.resources.getColor(R.color.defaultAccentColor))

    }

}