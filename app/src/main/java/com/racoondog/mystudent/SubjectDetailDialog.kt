package com.racoondog.mystudent

import android.app.Activity
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
import kotlinx.android.synthetic.main.subject_detail_dialog.*

class SubjectDetailDialog:Dialog {

    private val realm = Realm.getDefaultInstance()
    lateinit var cnxt:WeekView

    constructor(context: Context) : super(context)
    constructor(context: Context, themeResId: Int) : super(context, themeResId)
    constructor(
        context: Context,
        cancelable: Boolean,
        cancelListener: DialogInterface.OnCancelListener?
    ) : super(context, cancelable, cancelListener)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layoutparams = WindowManager.LayoutParams()
        layoutparams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        layoutparams.dimAmount = 0.8f
        window.attributes = layoutparams

        setContentView(R.layout.subject_detail_dialog)


        var subjectData: RealmResults<SubjectBox> = realm.where<SubjectBox>(SubjectBox::class.java)
            .equalTo("id",WeekView.ID)
            .findAll()
        val data = subjectData[0]!!

        deleteSubjectDetail.setOnClickListener {
            val builder = AlertDialog.Builder(context)
                .setTitle("삭제")
                .setMessage("해당 과목을 삭제하시겠습니까?")

                .setPositiveButton("확인") { _, _ ->

                    cnxt.deleteSubject(data.id)
                    realm.beginTransaction()
                    data.deleteFromRealm()
                    realm.commitTransaction()
                    Toast.makeText(context,"해당 과목이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    dismiss()

                }

                .setNegativeButton("취소") { _, _ ->

                }
                .show()
            builder.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.resources.getColor(R.color.colorCancel))
            builder.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.resources.getColor(R.color.defaultAccentColor))
        }
    }
}