package com.sjtu.naivegator

import android.os.*
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment

import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sjtu.naivegator.BathroomAlarmService.Companion.startActionSetAlarm
import java.text.ParseException
import java.text.SimpleDateFormat
import com.sjtu.naivegator.R
import kotlin.concurrent.thread

class BathroomFragment : Fragment() {
    companion object {
        const val STATUS_FINISH_UPDATE = 0
        const val TOTAL = "total"
        const val USED = "used"
    }

    val handler: Handler = Handler(Looper.getMainLooper()) { msg ->
        when (msg.what) {
            STATUS_FINISH_UPDATE -> {
                val total = msg.data.getInt(TOTAL)
                val used = msg.data.getInt(USED)
                curPeople?.text =
                    if (total > 0) "当前楼栋浴室人数/总可用隔间: ${used}/${total}"
                    else "浴室信息获取中..."
//                println(cur_people?.text)
            }
        }
        true
    }

    var curPeople: TextView? = null

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

        val areaEditText = view.findViewById<EditText>(R.id.east_or_west)
        val numEditText = view.findViewById<EditText>(R.id.number_of_building)

        curPeople = view.findViewById<TextView>(R.id.bathroom_cur_people)

        areaEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                curPeople?.text = "浴室信息获取中..."
            }
        })
        numEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                curPeople?.text = "浴室信息获取中..."
            }
        })

        ratingSeekBar.progress = ratingSeekBarDistance
        ratingSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                crowd_text.text =
                    "选择你期望的浴室拥挤程度: ${progress}/100"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                ratingSeekBarDistance = seekBar!!.progress
            }
        })
        set_btn.setOnClickListener {
            val format = "yyyy-MM-dd HH:mm:ss"
            val curTime =
                SimpleDateFormat(format).format(System.currentTimeMillis()).substring(0, 11)
            var startTimeStamp = 0L
            var endTimeStamp = 0L
            var area = ""
            if (numEditText.text.toString() == "") {
                Toast.makeText(requireContext(), "请输入相关信息", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val num = numEditText.text.toString().toInt()

            try {
                startTimeStamp =
                    SimpleDateFormat(format).parse(curTime + start_time.text + ":00").time
                endTimeStamp = SimpleDateFormat(format).parse(curTime + end_time.text + ":00").time
                area = when (areaEditText.text.toString()) {
                    "x", "X", "西" -> "西"
                    "d", "D", "东" -> "东"
                    else -> {
                        throw ParseException("area parse error", 0)
                    }
                }
                if (num < 0 || (area == "西" && num > 34) || (area == "东" && num > 34))
                    throw ParseException("num parse error", 0)
            } catch (e: ParseException) {
                Toast.makeText(context, "数据解析失败，请按正确的格式输入数据", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            startActionSetAlarm(
                requireContext(),
                startTimeStamp.toString(),
                endTimeStamp.toString(),
                area + num,
                ratingSeekBar.progress.toString()
            )
            clk_info.text =
                "闹钟时间为：" + start_time.text?.toString() + "至" + end_time.text?.toString() + ",理想拥挤程度为" + ratingSeekBar.progress?.toString() + "/" + (100 - ratingSeekBar.progress)
        }
        thread {
            while (true) {
                val area = areaEditText.text
                val num =
                    if (numEditText.text.toString() == "") 0
                    else numEditText.text.toString().toInt()
                var dormName = ""
                if (area.length > 0) {
                    when (area[0]) {
                        'x', 'X', '西' -> {
                            if (num >= 0 && num <= 34) {
                                dormName = "西$num"
                                Network.getBathroomData('西', num)
                            }

                        }
                        'd', 'D', '东' -> {
                            if (num >= 0 && num <= 34) {
                                dormName = "东$num"
                                Network.getBathroomData('东', num)
                            }
                        }
                    }
                }
                Thread.sleep(5000)
                val msg = Message.obtain()
                msg.what = STATUS_FINISH_UPDATE
                msg.data = Bundle().apply {
                    bathroomInfo[dormName]?.let {
                        putInt(TOTAL, it.first)
                        putInt(USED, it.second)
                    }
                }
                handler.sendMessage(msg)
            }
        }

        return view
    }
}

