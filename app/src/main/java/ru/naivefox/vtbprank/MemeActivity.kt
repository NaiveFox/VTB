package ru.naivefox.vtbprank

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView

class MemeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meme)

        val memeImage = findViewById<ImageView>(R.id.meme_image)
        memeImage.setImageResource(R.drawable.meme)
    }
}
