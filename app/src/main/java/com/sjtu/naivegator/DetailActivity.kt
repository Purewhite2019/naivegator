package com.sjtu.naivegator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast


class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        val pic = this.findViewById<ImageView>(R.id.detail_img)
//        Toast.makeText(this, intent.extras?.getString("resid"), Toast.LENGTH_LONG).show()
        pic.setImageResource(intent.extras?.getString("resid")!!.toInt())
        var str_intro:String= "canteen_name:"+intent.extras?.getString("canteen_name")+"\n\n"
        str_intro += "Here are some details about the canteen.\n\n"

        this.findViewById<TextView>(R.id.detail_intro).setText(str_intro)
    }
}