package com.racoondog.mystudent

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.schedule_dialog.*
import kotlinx.android.synthetic.main.weekview.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.OutputStream


class ScheduleDialog:Dialog {

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
        window.attributes = layoutParams

        setContentView(R.layout.schedule_dialog)

        scheduleSave.setOnClickListener{

            cnxt.checkPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE){
                val bitmap1 = getBitmapFromView(cnxt.scheduleView, cnxt.scheduleView.height, cnxt.scheduleView.width)
                val bitmap2 = getBitmapFromView(cnxt.dayLine, cnxt.dayLine.height, cnxt.dayLine.width)
                val bitmap3 = getBitmapFromView(cnxt.main_toolbar, cnxt.main_toolbar.height, cnxt.main_toolbar.width)
                val bitmap = combineImages(bitmap1, bitmap2, bitmap3)
                saveBitmap(bitmap)
                dismiss()}

        }
        scheduleShare.setOnClickListener {

            cnxt.checkPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE){

                val bitmap1 = getBitmapFromView(cnxt.scheduleView, cnxt.scheduleView.height, cnxt.scheduleView.width)
                val bitmap2 = getBitmapFromView(cnxt.dayLine, cnxt.dayLine.height, cnxt.dayLine.width)
                val bitmap3 = getBitmapFromView(cnxt.main_toolbar, cnxt.main_toolbar.height, cnxt.main_toolbar.width)
                val bitmap = combineImages(bitmap1, bitmap2, bitmap3)

                val f3 = File(Environment.getExternalStorageDirectory().toString() + "/shared/")
                if (!f3.exists()) f3.mkdirs()
                var outStream: OutputStream? = null
                val file = File(Environment.getExternalStorageDirectory().toString() + "/shared/" + "temp" + ".png")
                try {
                    outStream = FileOutputStream(file)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)
                    outStream.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                val intent = Intent(Intent.ACTION_SEND)
                intent.setType("image/*")
                intent.putExtra(Intent.EXTRA_STREAM, file)
                cnxt.startActivity(Intent.createChooser(intent,"시간표 공유"))
                file.delete()
                dismiss()
            }

        }
        scheduleDelete.setOnClickListener {
            cnxt.deleteSchedule()
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

}