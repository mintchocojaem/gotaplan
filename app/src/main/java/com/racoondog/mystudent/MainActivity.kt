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
import me.grantland.widget.AutofitTextView
import android.content.DialogInterface
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.create_subject.*
import kotlinx.android.synthetic.main.lesson_detail.*


class MainActivity: AppCompatActivity() {

    val memo = arrayListOf<Memo>()
    var day = listOf<String>()
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

                    LoadSchedule(dayflag, start_timer, end_timer)
                    intentStartTime = start_timer
                    intentEndTime = end_timer
                    intentflag = dayflag
                    initSubjectLine()
                }
                101 -> {
                    memo.add(Memo(data!!.getStringExtra("memo").toString() + ",  "))
                }
                102 -> {
                    val SubjectStartTime = data!!.getIntExtra("SubjectStartTime", 0)
                    val SubjectEndTime = data!!.getIntExtra("SubjectEndTime", 0)
                    val DayFlag = data!!.getIntExtra("DayFlag", 0)
                    val SubjectTitle = data!!.getStringExtra("SubjectTitle")
                    val StartTimeText = data!!.getStringExtra("StartTimeText")
                    val EndTimeText = data!!.getStringExtra("EndTimeText")
                    createSubjectLine(SubjectStartTime, SubjectEndTime, DayFlag, SubjectTitle,StartTimeText,EndTimeText)

                }
                103->{

                }
            }
        }
    }

    // 시간표를 그리는 함수
    fun LoadSchedule(day_flag: Int, start_time: Int, end_time: Int) {

        // 마지막 요일의 선택에 따라 배열이 추가됨

        var period =
            mutableListOf<String>() //원래는 교시부분이었으나 기획자의 변경에 따라 시간으로 표시되는 배열 ex -> 오후 1시 2시 3시

        // var time = mutableListOf<String>() //원래는 시간을 나타내는 부분이었으나 기획자의 변경에 따라 period로 치환됨

        //var subject = listOf("화1","화2") // 임시적 과목 시간

        //var content = listOf("태경이삼촌과 레슨") // 임시적 과목 내용


        if (day_flag == 1) { //마지막 요일을 선택하고 그에 따라 day_flag 값을 반환 하고 day 배열에 추가
            day = listOf("월", "화", "수", "목", "금")
        } else if (day_flag == 2) {
            day = listOf("월", "화", "수", "목", "금", "토")
        } else if (day_flag == 3) {
            day = listOf("월", "화", "수", "목", "금", "토", "일")
        } // day_line 레이아웃 문제로 띄어쓰기함 늘리거나 줄일수록 위에 날짜 사이즈 변함

        var AMPM_flag = 0 //마지막 요일 구분을 위한 flag 선언

        for (i in start_time until end_time) {  // 24시 형식을 오전과 오후를 구분 하기위한 논리연산
            if (i == start_time && i < 10) {
                period.add("$i\n 오전 ")
            } else if (i == start_time && i < 12 && i >= 10) {
                period.add("$i\n 오전 ")
            } else if (i == start_time && i > 12) {
                period.add("${i - 12}\n 오후 ")
                AMPM_flag = 1
            } else if (i == 13) {
                period.add("${i - 12}\n 오후 ")
                AMPM_flag = 1
            } else if (AMPM_flag == 1) {
                period.add("${i - 12}")
            } else {
                period.add("$i")
            }
        }

        /*
        for (i in 1..end_time - start_time) {

            period.add("$i")
        }*/
        //교시 ex ) 1,2,3

        /*

        var day_flag = 0

        for (i in start_time..end_time){
            if (i == start_time && i < 10) {
                time.add(" 오전\n $i" + ":00 ")
            }
            else if (i == start_time && i < 12 && i >= 10) {
                time.add("  오전\n$i" + ":00 ")
            }
            else if (i == start_time && i > 12) {
                time.add(" 오후\n ${i-12}" + ":00 ")
                day_flag =1
            }
            else if(i ==13){
                time.add(" 오후\n ${i-12}" + ":00 ")
                day_flag = 1
            }
            else if (day_flag == 1){
                time.add(" ${i-12}" + ":00 ")
            }
            else {
                time.add(" $i" + ":00 ")
            }
        }*/
        //period 밑에 작은 오전/오후 시간 표시 논리연산 부분 ex)오후 1:00 -> time 으로 정의 밑에 레이아웃도 세팅해야함

        val layout = TableLayout(this)  //전체 TableRow를 담기위한 Tablelayout

        val dayrow = TableRow(this) //initday를 담기위한 TableRow

        layout.layoutParams = TableLayout.LayoutParams(
            TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT
        ).apply {

        }

        dayrow.layoutParams = TableLayout.LayoutParams(
            TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT
        ).apply {

        }

        val initday = TextView(this)  // timetext의 textsize에 의한 간격차를 매꾸기 위해 수동으로 공백을 추가하는 부분
        initday.setBackgroundResource(R.color.colorAccent)// 색깔을 변경해서 얼마나 띄워지는지 확인 가능
        initday.layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.WRAP_CONTENT,
            TableRow.LayoutParams.WRAP_CONTENT
        ).apply {

            if (day_flag == 1) {
                initday.text = "   "
            }
            if (day_flag == 2) {
                initday.text = "    "
            }
            if (day_flag == 3) {
                initday.text = "     "
            }
            weight = 1f //시간표 위에 날짜별 공백 줄을 맞추기 위한 부분
        }

        dayrow.addView(initday)


        for (i in 0 until day.size) {

            val daytxt = TextView(this) // 요일을 나타내는 부분 ex -> 월 화 수 목
            daytxt.gravity = Gravity.CENTER
            daytxt.setBackgroundResource(R.color.Actionbar_bg)
            daytxt.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
            ).apply {
                daytxt.text = day[i]
                weight = 3f

            }

            dayrow.addView(daytxt)
        }

        Day_Line.addView(dayrow)

        for (i in 0 until period.size) {


            val timerow = TableRow(this) // const_init을 담기위한 TableRow
            timerow.layoutParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT
            ).apply {
                weight = 1f
            }
            timerow.setBackgroundResource(R.color.whitegray_bg)

            val const_init = ConstraintLayout(this) //initperiod를 담기위한 Constraintlayout 부분
            const_init.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT
            ).apply {
                weight - 1f
            }

            val initperiod = TextView(this) // 원래는 교시였으나 기획자의 변경사항에 따라 시간으로 치환된 부분 ex-> 1 2 3 4
            initperiod.gravity = Gravity.CENTER
            initperiod.layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                initperiod.text = period[i]
                initperiod.textSize = 13f
                topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
                leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
                //verticalBias = 0.2f
                //time 사용시 period 레이아웃 영역 활성화
            }
            /*
            val inittime = TextView(this) // 이 부분은 원래 시간이 부분이었으나 기확자의 지시에 따라 initperiod가 시간으로 대체됨 ex-> 오전 8:00시 9:00시
            inittime.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT).apply {
                inittime.text = time[i]
                inittime.textSize = 10f
                topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
                leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
                verticalBias = 0.8f
            }

            const_init.addView(inittime)
             */ //time 레이아웃 세팅 영역 ()

            const_init.addView(initperiod)
            timerow.addView(const_init)

            for (j in 0 until day.size) {

                val timetxt = AutofitTextView(this) // 각 시간표 일정이 들어가는 공백 부분
                val tag: String = day[j] + i
                timetxt.tag = tag
                timetxt.setBackgroundResource(R.drawable.cell_shape)

                timetxt.setMinTextSize(10)


                /* // 임시적으로 만든 과목 부분
                for (k in 0 until subject.size) {
                    if (timetxt.tag == subject[k]) {
                        timetxt.setBackgroundColor(Color.LTGRAY)
                    }
                }
                if(timetxt.tag == subject[0]){
                    timetxt.text = content[0]
                }*/


                // timetxt lyaout 설정부분
                timetxt.layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                ).apply {
                    height = 150
                    width = 0
                    weight = 3f

                }

                timerow.addView(timetxt)

            }



            layout.addView(timerow)
        }

        scheduleview.addView(layout) //activity_main 의 스크롤 뷰에 추가

    }

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

    fun createSubjectLine(StartTime:Int,EndTime:Int,DayFlag:Int,SubjectTitle:String,StartTimeText:String,EndTimeText:String){

        val subjectHeight = (EndTime - StartTime) * 150
        val subjectMargin = (StartTime - intentStartTime) * 150
        val subject = ImageView(this)

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
            subject.setBackgroundColor(Color.BLUE)
            verticalBias = 0f
            topMargin = subjectMargin

            subject.setOnClickListener{
                val intentLessonDetail = Intent (this@MainActivity, LessonDetail::class.java)
                intentLessonDetail.putExtra("LessonTitle",SubjectTitle)
                intentLessonDetail.putExtra("StartTimeText",StartTimeText)
                intentLessonDetail.putExtra("EndTimeText",EndTimeText)
                startActivityForResult(intentLessonDetail,103)
            }


        }
        findViewById<ConstraintLayout>(DayFlag).addView(subject)


    }

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

