package com.example.kirillstoianov.sayhiview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sayHiView = SayHiView(this@MainActivity)
        container.addView(sayHiView)

        sayHiView.post {
            sayHiView.startAnimate()
        }
    }
}
