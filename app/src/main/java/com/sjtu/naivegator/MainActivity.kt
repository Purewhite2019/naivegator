package com.sjtu.naivegator

import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.sjtu.naivegator.filter.FilterFragment
import android.location.LocationManager
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*





class MainActivity : AppCompatActivity() {

    var fragmentManager: FragmentManager? = null

    var filterFragment : FilterFragment ?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fragmentManager= supportFragmentManager;
        filterFragment = FilterFragment()
        test_filter_button.setOnClickListener{
            if (filterFragment!=null&&filterFragment!!.isVisible){
                supportFragmentManager.beginTransaction().remove(filterFragment!!).commit();
            }else{
                supportFragmentManager.beginTransaction().add(R.id.content, filterFragment!!).commit()
            }

        }
    }

}






