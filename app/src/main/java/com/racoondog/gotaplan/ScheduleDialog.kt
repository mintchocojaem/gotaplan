package com.racoondog.gotaplan

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.FileProvider
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.schedule_dialog.*
import kotlinx.android.synthetic.main.weekview.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception


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
            val dialog = ScheduleTitleDialog(context)
            dialog.cnxt = this
            dialog.show()
            dismiss()
        }

        editScheduleTime.setOnClickListener {
            val dialog = ScheduleTimeDialog(context)
            dialog.cnxt = this
            dialog.show()
            dismiss()
        }

        intervalSchedule.setOnClickListener {
            val dialog = ScheduleIntervalDialog(context)
            dialog.cnxt = this
            dialog.show()
            dismiss()
        }

        saveSchedule.setOnClickListener{
            cnxt.checkPermissions(Manifest.permission.READ_EXTERNAL_STORAGE) {
                cnxt.checkPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                    val bitmap1 = getBitmapFromView(
                        cnxt.scheduleView,
                        cnxt.scheduleView.height,
                        cnxt.scheduleView.width
                    )
                    val bitmap2 =
                        getBitmapFromView(cnxt.dayLine, cnxt.dayLine.height, cnxt.dayLine.width)
                    val bitmap3 = getBitmapFromView(
                        cnxt.main_toolbar,
                        cnxt.main_toolbar.height,
                        cnxt.main_toolbar.width
                    )
                    val bitmap4 = getBitmapFromView(cnxt.toolbar_title, cnxt.toolbar_title.height, cnxt.toolbar_title.width)

                    val bitmap = combineImages(bitmap1, bitmap2, bitmap3,bitmap4)

                    try {
                        /*val strPath = File(Environment.DIRECTORY_PICTURES)
                    if(!strPath.exists()){
                        strPath.mkdirs() // don't forget to make the directory
                    }
                    val stream = FileOutputStream("$strPath/${System.currentTimeMillis()}.png") // overwrites this image every time
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    stream.close()

                     */
                        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        if(!path.exists()) path.mkdirs() // don't forget to make the directory
                        val stream =
                            FileOutputStream("$path/${System.currentTimeMillis()}.png") // overwrites this image every time
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        stream.close()
                        Toast.makeText(cnxt, cnxt.applicationContext.resources.getString(R.string.save_timetable_gallery), Toast.LENGTH_LONG).show()
                        context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(path)))

                    } catch (e: Exception) {
                        Log.e("error", e.toString())
                        Toast.makeText(cnxt, cnxt.applicationContext.resources.getString(R.string.none_save_timetable_gallery), Toast.LENGTH_LONG).show()

                    }

                    dismiss()
                }
            }

        }

        shareSchedule.setOnClickListener {

            cnxt.checkPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE){

                val bitmap1 = getBitmapFromView(cnxt.scheduleView, cnxt.scheduleView.height, cnxt.scheduleView.width)
                val bitmap2 = getBitmapFromView(cnxt.dayLine, cnxt.dayLine.height, cnxt.dayLine.width)
                val bitmap3 = getBitmapFromView(cnxt.main_toolbar, cnxt.main_toolbar.height, cnxt.main_toolbar.width)
                val bitmap4 = getBitmapFromView(cnxt.toolbar_title, cnxt.toolbar_title.height, cnxt.toolbar_title.width)
                val bitmap = combineImages(bitmap1, bitmap2, bitmap3,bitmap4)

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
                    FileProvider.getUriForFile(context, "com.racoondog.gotaplan.fileprovider", newFile)

                if (contentUri != null) {
                    val shareIntent = Intent()
                    shareIntent.action = Intent.ACTION_SEND
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // temp permission for receiving app to read this file
                    shareIntent.setDataAndType(contentUri, cnxt.contentResolver.getType(contentUri))
                    shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
                    cnxt.startActivity(Intent.createChooser(shareIntent,
                        cnxt.applicationContext.resources.getString(R.string.schedule_dialog_share_schedule)))
                }

                dismiss()
            }

        }


    }

    private fun getBitmapFromView(view: View, height:Int, width:Int): Bitmap {

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun combineImages(first: Bitmap, second: Bitmap, third: Bitmap,fourth:Bitmap): Bitmap {

        val bitmap = Bitmap.createBitmap(first.width, first.height+second.height+third.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawBitmap(third, Matrix(), null)
        canvas.drawBitmap(second,0.toFloat(), third.height.toFloat(), null)
        canvas.drawBitmap(first, 0.toFloat(), third.height.toFloat()+second.height.toFloat(), null)
        canvas.drawBitmap(fourth,Matrix(), null)

        return bitmap
    }


}