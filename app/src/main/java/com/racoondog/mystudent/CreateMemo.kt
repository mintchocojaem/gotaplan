package com.racoondog.mystudent

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.memo_layout.*

class CreateMemo : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.memo_layout)

        CreateMemo_button.setOnClickListener {

            val Memo = Memo_text.text.toString()

            if (Memo != "") {
                val intent = Intent()
                intent.putExtra("memo", Memo_text.text.toString())
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            else {
                Toast.makeText(this, "메모 사항을 입력하세요.", Toast.LENGTH_SHORT).show()
            }

        }
    }
}