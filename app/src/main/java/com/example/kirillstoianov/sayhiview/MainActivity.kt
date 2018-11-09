package com.example.kirillstoianov.sayhiview

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sayHiView = SayHiView(this@MainActivity)
//        val layoutParams =ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
//        sayHiView.layoutParams = layoutParams
//        sayHiView.layoutParams.width = 664
//        sayHiView.layoutParams.height = 930
        container.addView(sayHiView)

        sayHiView.post {
            sayHiView.startAnimate()
        }
    }
}
