package ru.naivefox.vtbprank

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val phoneInput = findViewById<EditText>(R.id.phone_input)
        val continueBtn = findViewById<Button>(R.id.continue_button)

        continueBtn.setOnClickListener {
            val intent = Intent(this, MemeActivity::class.java)
            startActivity(intent)
        }
    }
}
