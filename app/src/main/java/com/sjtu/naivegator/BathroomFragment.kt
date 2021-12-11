package com.sjtu.naivegator

import android.os.*
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sjtu.naivegator.R

class BathroomFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var ratingSeekBarDistance: Int = 50
        val view = inflater.inflate(R.layout.fragment_bathroom, container, false)
        val start_time = view.findViewById<EditText>(R.id.text_input_start_time)
        val end_time = view.findViewById<EditText>(R.id.text_input_end_time)    //输出时间
        val set_btn = view.findViewById<Button>(R.id.set_time)                //设置按钮
        val ratingSeekBar = view.findViewById<SeekBar>(R.id.bathroom_seekbar)
        val crowd_text = view.findViewById<TextView>(R.id.crowd_text)
        val clk_info = view.findViewById<TextView>(R.id.clock_name)
        ratingSeekBar.progress = ratingSeekBarDistance
        ratingSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                crowd_text.text =
                    "Choosing the ideal congestion in the bathroom: ${progress}/${100 - progress}"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                ratingSeekBarDistance = seekBar!!.progress
            }
        })
        set_btn.setOnClickListener {
            clk_info.text = "闹钟时间为："+start_time.text?.toString() +"至"+ end_time.text?.toString() + ",理想拥挤程度为"+ratingSeekBar.progress?.toString() + "/" + (100 - ratingSeekBar.progress)
        }
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
    }


}
