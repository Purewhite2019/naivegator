package com.sjtu.naivegator

val studyroomNameMap : MutableMap<String, MutableSet<String>> = mutableMapOf(
    "上院" to mutableSetOf<String>(),
    "中院" to mutableSetOf<String>(),
    "下院" to mutableSetOf<String>(),
    "东上院" to mutableSetOf<String>(),
    "东中院" to mutableSetOf<String>(),
    "东下院" to mutableSetOf<String>()
)

val studyroomMap = mutableMapOf(
    // This is a map: (教学楼名, 教室名) -> (总座位, 实际人数, (温度, 是否有课（true为有课）))
    Pair("东中院", "1-101") to Triple("0", 0, Pair("23", true))
)

