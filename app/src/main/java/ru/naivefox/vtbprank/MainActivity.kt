package ru.naivefox.vtbprank

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val phoneInput = findViewById<TextInputEditText>(R.id.phoneInput)
        val btn = findViewById<MaterialButton>(R.id.continueBtn)

        btn.setOnClickListener {
            // тут мог бы быть реальный логин, но у нас пранк :)
            startActivity(Intent(this, MemeActivity::class.java))
        }
    }
}
