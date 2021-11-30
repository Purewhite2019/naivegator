package com.sjtu.naivegator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.sjtu.naivegator.filter.FilterFragment


class MainActivity : AppCompatActivity() {

    var fragmentManager: FragmentManager? = null
    var fragmentTransaction: FragmentTransaction? = null

    var filterFragment : FilterFragment ?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fragmentManager= supportFragmentManager;
        fragmentTransaction = fragmentManager!!.beginTransaction()
        filterFragment = FilterFragment()
        invoke_filter()

    }


    private fun invoke_filter(){
        fragmentTransaction!!.add(R.id.content, filterFragment!!)
        fragmentTransaction!!.commit()
    }

}






