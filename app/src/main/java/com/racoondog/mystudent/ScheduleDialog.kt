package com.racoondog.mystudent

import android.Manifest
import android.app.AlertDialog
import android.app.AlertDialog.Builder
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.schedule_dialog.*
import kotlinx.android.synthetic.main.weekview.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import kotlin.system.exitProcess


class ScheduleDialog:Dialog {

    private val realm = Realm.getDefaultInstance()
    lateinit var cnxt:MainActivity

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
        window!!.attributes = layoutParams
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.schedule_dialog)

        editScheduleTitle.setOnClickListener {
            val dialog = EditScheduleTitleDialog(context)
            dialog.cnxt = this
            dialog.show()

        }

        saveSchedule.setOnClickListener{

            cnxt.checkPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE){
                val bitmap1 = getBitmapFromView(cnxt.scheduleView, cnxt.scheduleView.height, cnxt.scheduleView.width)
                val bitmap2 = getBitmapFromView(cnxt.dayLine, cnxt.dayLine.height, cnxt.dayLine.width)
                val bitmap3 = getBitmapFromView(cnxt.main_toolbar, cnxt.main_toolbar.height, cnxt.main_toolbar.width)
                val bitmap = combineImages(bitmap1, bitmap2, bitmap3)
                saveBitmap(bitmap)
                dismiss()}

        }
        shareSchedule.setOnClickListener {

            cnxt.checkPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE){

                val bitmap1 = getBitmapFromView(cnxt.scheduleView, cnxt.scheduleView.height, cnxt.scheduleView.width)
                val bitmap2 = getBitmapFromView(cnxt.dayLine, cnxt.dayLine.height, cnxt.dayLine.width)
                val bitmap3 = getBitmapFromView(cnxt.main_toolbar, cnxt.main_toolbar.height, cnxt.main_toolbar.width)
                val bitmap = combineImages(bitmap1, bitmap2, bitmap3)

                try
                {
                    val cachePath = File(context.cacheDir, "images")
                    cachePath.mkdirs() // don't forget to make the directory
                    val stream = FileOutputStream("$cachePath/image.png") // overwrites this image every time
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    stream.close()
                }
                catch (e: IOException) {
                    e.printStackTrace()
                }

                val imagePath = File(context.cacheDir, "images")
                val newFile = File(imagePath, "image.png")
                val contentUri =
                    FileProvider.getUriForFile(context, "com.example.myapp.fileprovider", newFile)

                if (contentUri != null) {
                    val shareIntent = Intent()
                    shareIntent.action = Intent.ACTION_SEND
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // temp permission for receiving app to read this file
                    shareIntent.setDataAndType(contentUri, cnxt.contentResolver.getType(contentUri))
                    shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
                    cnxt.startActivity(Intent.createChooser(shareIntent,"시간표 공유"))
                }

                dismiss()
            }

        }
        initSchedule.setOnClickListener {
            deleteSchedule()
            dismiss()
        }

    }

    private fun getBitmapFromView(view: View, height:Int, width:Int): Bitmap {

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun combineImages(first: Bitmap, second: Bitmap, third: Bitmap): Bitmap {

        val bitmap = Bitmap.createBitmap(first.width, first.height+second.height+third.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawBitmap(third, Matrix(), null)
        canvas.drawBitmap(second,0.toFloat(), third.height.toFloat(), null)
        canvas.drawBitmap(first, 0.toFloat(), third.height.toFloat()+second.height.toFloat(), null)

        return bitmap
    }

    private fun saveBitmap(bitmap:Bitmap) { // 버튼 onClick 리스너
        // WRITE_EXTERNAL_STORAGE 외부 공간 사용 권한 허용

        val fos: FileOutputStream // FileOutputStream 이용 파일 쓰기 한다
        val strFolderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/myStudent"
        val folder = File(strFolderPath)
        if (!folder.exists())
        { // 해당 폴더 없으면 만들어라
            folder.mkdirs()
        }
        val strFilePath = strFolderPath + "/" + System.currentTimeMillis() + ".png"
        val fileCacheItem = File(strFilePath)
        try
        {
            fos = FileOutputStream(fileCacheItem)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()
        }
        catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        finally
        {
            Toast.makeText(context, "시간표가 갤러리에 저장되었습니다.", Toast.LENGTH_SHORT).show()
            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(File(strFilePath))))
        }

    }
    private fun deleteSchedule(){


        val builder = Builder(context,R.style.MyDialogTheme)

            .setTitle("초기화")
            .setMessage("시간표를 초기화하시겠습니까? \n\n(모든 시간표와 과목의 데이터가 삭제됩니다.)")

            .setPositiveButton("확인") { _, _ ->

                cnxt.weekView_layout.removeView(cnxt.weekView)

                cnxt.addSubjectButton.visibility = View.GONE
                cnxt.schedule_add.visibility = View.VISIBLE
                cnxt.toolbar_title.text = "시간표"

                val subjectData: RealmResults<SubjectData> = realm.where<SubjectData>(SubjectData::class.java)
                    .findAll()

                for(i in subjectData.indices){
                    realm.beginTransaction()
                    subjectData[0]!!.deleteFromRealm()
                    realm.commitTransaction()
                }

                val scheduleData: RealmResults<ScheduleData> =
                    realm.where<ScheduleData>(ScheduleData::class.java)
                        .findAll()
                val data = scheduleData[0]!!

                realm.beginTransaction()
                data.deleteFromRealm()
                realm.commitTransaction()

                cnxt.finishAffinity()
                val intent = Intent(cnxt, MainActivity::class.java)
                cnxt.startActivity(intent)
                Toast.makeText(cnxt,"시간표가 초기화되었습니다.",Toast.LENGTH_SHORT).show()

                exitProcess(0)


            }

            .setNegativeButton("취소") { _, _ ->

            }

            .show()

        builder.window!!.attributes.apply {
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT}

        builder.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        builder.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(cnxt.applicationContext,R.color.colorCancel))
        builder.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(cnxt.applicationContext,R.color.defaultAccentColor))


    }

}