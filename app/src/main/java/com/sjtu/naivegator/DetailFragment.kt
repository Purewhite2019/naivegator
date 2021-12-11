package com.sjtu.naivegator

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi

private const val ARG_tmpId = "tmpId"
private const val ARG_canteenName = "canteenName"


class DetailFragment : Fragment() {
    private var tmpId: Int? = null
    private var canteenName: String? = null
    private val canteenIntros: Array<String> = arrayOf(
        "一餐临近上、中、下院教学楼、包玉刚图书馆和西区学生宿舍，地理位置优越，对于西区上课和住宿的同学来说是便利的就餐选择，" +
                "所以平时人流量大，经常在饭点会出现“爆座”现象。\n" +
                "一餐有二层，一楼主要有特色快餐（一般口味的同学都能满足）、川味窗口(喜欢辣的同学可以冲！)、" +
                "西安风味（经典西安面食，如biangbiang面、臊子面、肉夹馍）、面食肠粉窗口、掉渣饼、瓦锅饭铁板烧窗口、五芳斋江南点心等。",
        "二餐位于东区，临近东上、中、下院教学楼和东区宿舍，菜品丰富，好评率高，对于在此" +
                "上课和居住的同学来说是一个非常好的选择。\n" +
                " 二餐一共有三层，一楼有面食、咖喱饭、木桶饭、韩式拌饭窗口、麻辣干锅、酸菜" +
                "鱼等各种口味的餐品，同时还有一个西餐厅。二楼有一个大众餐厅和一个可供自选的教工餐厅，" +
                "排队自助取餐，统一结账，提供更多选择，物美价廉，平时人较少，可以错开一楼的高峰；此外新疆" +
                "餐厅是风味餐厅也位于二楼，欢迎各民族同学前往。三楼是物美价廉的绿园餐厅，也很不错哦。",
        "三餐分为上下两楼。一楼有特色木桶饭窗口、快餐窗口、F+牛肉饭窗口、水饺窗口、" +
                "面食窗口；二楼有干锅火锅黄焖鸡、石锅拌饭、特色风味快餐、泰国铁板烧等。\n" +
                "\n" +
                "地址：涵泽湖旁边，东三区同学出门即是食堂说的就是三餐。夏天可以吃完午饭买杯咖" +
                "啡驻足湖边赏荷，秋天可以绕着西区步道跑完四公里再吃晚饭。",
        " 四餐分上下两层。一楼主要是风味餐厅,有扬州炒饭、石锅拌饭、铁板饭、热干面、馄饨" +
                "水饺、兰州拉面、自助小菜、麻辣香锅、特色火锅、培根滑蛋饭等等" +
                "；二楼为自选餐厅，有特色农家菜、盖浇饭、铁板烧各种特色窗口。同时也有吉姆利德西餐厅" +
                "。四餐是当之无愧的交大菜品种类最丰富的食堂之一。\n" +
                "地址：位于霍英东体育馆附近，李政道图书馆正对面，靠近研究生宿舍。" +
                "是东川路800号人气最高的食堂。但凡有访客来校，都想尝尝四餐。四餐地处闵行校区西北角，深受资深交大人的喜爱。",
        "五餐之所以被不少老饕津津乐道，主要靠中西两座餐馆。\n" +
                " 中——东湖面馆，和它的名字风格相同，东湖面馆主推的就是苏式面食。\n" +
                " 西——吉姆利德西餐厅，五餐西餐厅与四餐的菜单有略微不同，但相同的是披萨饼" +
                "上满满的料，喜欢芝士的同学一定不能忘记去那里拔草哦。\n" +
                "除了这两个餐厅，五餐还有大众餐厅、学生餐厅（含特色小吃）分别在一楼和" +
                "二楼。另外二楼还有一家火锅店，想吃火锅又不想出校门的同学可以享口福啦！",
        "六餐最近经过了一次彻底的翻新，新装修好的六餐十分大气美观，但是六餐相对其他餐厅而言" +
                "偏小，里面菜品种类也比较少，以面条和米饭为主。但是价格实惠，味道也让人满意。\n" +
                "地址：六餐地理位置位于木兰楼对面，靠近庙门，生物实验楼。经过改扩建后，六餐面积比七餐、" +
                "哈乐餐厅、玉兰苑都要大。",
        " 七餐享有绿植环绕的全景玻璃房，分为两部分：\n" +
                "靠东边的常规餐厅中规中矩，提供自选菜和面食。\n" +
                "隔着一条帘幕则是西边的西餐厅。和二餐的西餐厅大有不同，这里讲究的便是环境和品味。炸鱼，羊排，惠灵顿牛排" +
                "，彰显着七餐西餐厅对菜品的高调选择。在刀叉摩挲盘面的慢生活格调中，交大褪去了它理科院校严肃的面纱，更多了一分情调。\n" +
                "地址：第七餐饮大楼位于校园东北面，在转化医学大楼后。地理位置的偏僻使得七餐厅人流一般不大，但却拥有其他餐厅无可比拟的优质环境。",
        "以“性价比高，量大实惠”闻名的哈乐之家可谓麻雀虽小五脏俱全。哈乐餐厅以中餐为主，" +
                "提供各色各样的美食、客饭、面食、粉丝、小馄饨等等。\n" +
                "地址：哈乐之家身为只有一层的食堂，坐落于西区宿舍群中，隐蔽性极强。从中院朝着南体和教超的方向走就可以找到他。",
        "玉兰苑内，是号称交大八餐的统禾餐厅，木桶饭，麻辣烫，鱼粉，铁板烧，" +
                "东北水饺，各个都能让你吃的心满意足。玉兰苑外围有串府、巴比馒头、" +
                "鸡蛋灌饼、还有任何一位交大人都无法拒绝的小眷村，哪管你队伍排多" +
                "长，俩字儿，“盘它”。\n" +
                "地址：玉兰苑位于思源西路边，在电工力学楼对面，一餐北面，处于西区宿舍中央" +
                "，它是校内最热门的餐厅之一，如果是在高峰时段前往用餐，就请准备好排队吧。",
        "中院1-2层开放时间为7:30-22:30，中院114、115为通宵自习室",
        "东中院3号楼3-4层开放时间为7:30-22:30，东中院3号楼1-2层开放时间为7:30-24:00，3-105、3-106四间教室为24小时通宵自习教室",
        "本教学楼一般不开放自习"
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tmpId = it.getInt(ARG_tmpId)
            canteenName = it.getString(ARG_canteenName)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_detail, container, false)
        tmpId?.let { view.findViewById<ImageView>(R.id.detail_img).setImageResource(it) }
        var idx: Int = 0
        if (canteenName == "一餐"){
            idx = 0
        }
        else if(canteenName == "二餐")
        {
            idx = 1
        }
        else if(canteenName == "三餐")
        {
            idx = 2
        }
        else if(canteenName == "四餐")
        {
            idx = 3
        }
        else if(canteenName == "五餐")
        {
            idx = 4
        }
        else if(canteenName == "六餐")
        {
            idx = 5
        }
        else if(canteenName == "七餐")
        {
            idx = 6
        }
        else if(canteenName == "哈乐")
        {
            idx = 7
        }
        else if(canteenName == "玉兰苑")
        {
            idx = 8
        }
        else if(canteenName == "中院")
        {
            idx = 9
        }
        else if(canteenName == "东中院")
        {
            idx = 10
        }
        else
        {
            idx = 11
        }
        view.findViewById<TextView>(R.id.detail_intro).text =
                    "Name: $canteenName\n\n"+
                    canteenIntros[idx]
        Log.e("tmpid", tmpId.toString())
        view.findViewById<Button>(R.id.btn_exit).setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
            val frags = requireActivity().supportFragmentManager.fragments

        }
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(tmpId: Int, canteenName: String) =
            DetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_tmpId, tmpId)
                    putString(ARG_canteenName, canteenName)
                }
            }
    }

}