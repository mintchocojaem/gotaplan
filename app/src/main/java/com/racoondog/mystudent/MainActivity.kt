package com.racoondog.mystudent

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.racoondog.mystudent.databinding.ItemNameBinding
import kotlinx.android.synthetic.main.activity_main.*
import android.content.DialogInterface
import android.widget.*


class MainActivity: AppCompatActivity() {

    val memo = arrayListOf<Memo>()
    var intentStartTime: Int = 0
    var intentEndTime: Int = 0
    var intentflag : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(my_toolbar)  //Actionbar 부분
        supportActionBar?.setDisplayUseLogoEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        // 알림창 객체 생성

        memo_view.apply {
            layoutManager = LinearLayoutManager(this@MainActivity).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }
            adapter = MemoAdapter(memo) {

                val builder = AlertDialog.Builder(this@MainActivity)
                builder.setTitle("메모 삭제")  // 제목 설정
                    .setMessage("메모를 삭제하시겠습니까?")        // 메세지 설정
                    .setCancelable(true)        // 뒤로 버튼 클릭시 취소 가능 설정

                    .setPositiveButton(
                        "확인",
                        DialogInterface.OnClickListener { dialog, whichButton ->

                            memo.remove(Memo("${it.name}"))
                            adapter?.notifyDataSetChanged()
                            Toast.makeText(this@MainActivity, "메모가 삭제되었습니다.", Toast.LENGTH_SHORT)
                                .show()
                        })

                    .setNegativeButton(
                        "취소",
                        DialogInterface.OnClickListener { dialog, whichButton ->
                            //Toast.makeText(this@MainActivity,"취소되었습니다.",Toast.LENGTH_SHORT).show()
                            // 취소 버튼 클릭시 설정, 왼쪽 버튼입니다.
                            //원하는 클릭 이벤트를 넣으시면 됩니다.
                        })
                val dialog = builder.create()
                dialog.setOnShowListener {
                    // Dialog Button Text Color Setting
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                }
                dialog.show()

            }
        }

        open_list.setOnClickListener {
            title_bar.visibility = View.VISIBLE
            open_list.visibility = View.INVISIBLE
            close_list.visibility = View.VISIBLE
        }
        title_text.setOnClickListener {

        }
        close_list.setOnClickListener {
            title_bar.visibility = View.INVISIBLE
            open_list.visibility = View.VISIBLE
            close_list.visibility = View.INVISIBLE
        }

        schedule_add.setOnClickListener {
            val scheduleIntent = Intent(this, CreateSchedule::class.java)
            startActivityForResult(scheduleIntent, 100)
            schedule_add.visibility = View.INVISIBLE
        }

        memo_add.setOnClickListener {
            val memoIntent = Intent(this, CreateMemo::class.java)
            startActivityForResult(memoIntent, 101)
        }

        add_subject.setOnClickListener {
            val subjectIntent = Intent(this, CreateSubject::class.java)

            subjectIntent.putExtra("start_time", intentStartTime)
            subjectIntent.putExtra("end_time", intentEndTime)
            subjectIntent.putExtra("day_flag",intentflag)
            startActivityForResult(subjectIntent, 102)
        }

    }

    //MainActivity로 들어오는 onActivityResult 부분 -> Intent 후 값 반환

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                100 -> {
                    title_text.text = data!!.getStringExtra("title")
                    val dayflag = data.getIntExtra("day_flag", 0)
                    val start_timer = data.getIntExtra("start_time", 0)
                    val end_timer = data.getIntExtra("end_time", 0)

                    intentStartTime = start_timer
                    intentEndTime = end_timer
                    intentflag = dayflag
                }
                /*101 -> {
                    memo.add(Memo(data!!.getStringExtra("memo").toString() + ",  "))
                }
                102 -> {
                    val SubjectStartTime = data!!.getIntExtra("SubjectStartTime", 0)
                    val SubjectEndTime = data!!.getIntExtra("SubjectEndTime", 0)
                    val DayFlag = data!!.getIntExtra("DayFlag", 0)
                    val SubjectTitle = data!!.getStringExtra("SubjectTitle")
                    val StartTimeText = data!!.getStringExtra("StartTimeText")
                    val EndTimeText = data!!.getStringExtra("EndTimeText")
                    val ContentText = data?.getStringExtra("ContentText")
                    createSubjectLine(SubjectStartTime, SubjectEndTime, DayFlag, SubjectTitle,StartTimeText,EndTimeText,ContentText)

                }*/
                103->{

                }

            }
        }

    }
/*
    // 시간표를 그리는 함수


    fun initSubjectLine() {

        for(i in 0 until day.size) {

            val id: Int = i + 1

            val subjectLine = ConstraintLayout(this)

            subjectLine.layoutParams = LinearLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            ).apply {
                width = 0
                weight = 1f
                subjectLine.id = id
                subjectLine.setPadding(3,0,3,0)
            }

            canvas.bringToFront()
            canvas.addView(subjectLine)


        }

    }

    fun createSubjectLine(StartTime:Int,EndTime:Int,DayFlag:Int,SubjectTitle:String,StartTimeText:String,EndTimeText:String,
                          ContentText:String?){

        val subjectHeight = (EndTime - StartTime) * 150
        val subjectMargin = (StartTime - intentStartTime) * 150
        val subject = ConstraintLayout(this)
        val titleText = TextView(this)
        var smallTitle : String = ""

        if(SubjectTitle.length > 10){
            smallTitle = SubjectTitle.substring(0,10)+".."
        }
        else{
            smallTitle = SubjectTitle
        }


        titleText.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            titleText.maxLines = 2
            titleText.textSize = 13f
            titleText.text = "$smallTitle"
        }

        subject.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            width = ConstraintLayout.LayoutParams.PARENT_ID
            height = subjectHeight
            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
            leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
            subject.setBackgroundColor(Color.LTGRAY)
            verticalBias = 0f
            topMargin = subjectMargin
            subject.setPadding(20,10,20,10)

            subject.setOnClickListener{
                val intentSubjectDetail = Intent (this@MainActivity, SubjectDetail::class.java)
                intentSubjectDetail.putExtra("SubjectTitle",SubjectTitle)
                intentSubjectDetail.putExtra("StartTimeText",StartTimeText)
                intentSubjectDetail.putExtra("EndTimeText",EndTimeText)
                intentSubjectDetail.putExtra("ContentText",ContentText)
                startActivityForResult(intentSubjectDetail,103)
            }

        }
        subject.addView(titleText)
        findViewById<ConstraintLayout>(DayFlag).addView(subject)


    } */

    override fun onCreateOptionsMenu(menu: Menu): Boolean { //Menu 추가 부분
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean { //Menu 목록 부분
        when (item.getItemId()) {
            R.id.home -> {
                //onBackPressed()
                return true
            }
            R.id.setting -> {
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }
}
    // 메모 Adapter 부분과 ViewHolder 부분
class MemoAdapter(val items :List<Memo>, private val clickListener: (memo:Memo) ->Unit) : RecyclerView.Adapter<MemoAdapter.MemoViewHolder>(){

    class MemoViewHolder(val binding: ItemNameBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_name,parent,false)
        val viewHolder = MemoViewHolder(ItemNameBinding.bind(view))
        view.setOnClickListener{
            clickListener.invoke(items[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
        holder.binding.memo = items[position]
    }

}

