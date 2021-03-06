package com.sjtu.naivegator

val canteenNameMap: Map<String, List<String>> = mapOf(
    "闵行第一餐厅" to listOf(
        "1F 餐厅",
        "2F 餐厅",
        "2F 清真餐厅"
    ),
    "闵行第二餐厅" to listOf(
        "1F 餐厅",
        "2F 教工餐厅",
        "2F 新疆餐厅",
        "3F 绿园餐厅",
    ),
    "闵行第三餐厅" to listOf(
        "1F 餐厅",
        "2F 外婆桥",
        "2F 清真餐厅"
    ),
    "闵行第四餐厅" to listOf(
        "1F 餐厅",
        "2F 餐厅"
    ),
    "闵行第五餐厅" to listOf(
        "1F 餐厅",
        "1F 东湖面馆",
        "2F 教工餐厅"
    ),
    "闵行第六餐厅" to listOf(
        "1F 餐厅",
        "2F 餐厅"
    ),
    "闵行第七餐厅" to listOf(
        "1F 学生餐厅"
    ),
    "闵行哈乐餐厅" to listOf(
        "1F 餐厅"
    ),
    "闵行玉兰苑" to listOf(
        "1F 统禾"
    ),
)


val canteenMap = mutableMapOf<Int, Triple<Pair<String, String>, Int, Int>>(
    // This is a map: canteenID -> ((canteenName, canteenName2), totalSeat, usedSeat)
    100 to Triple(Pair("闵行第一餐厅", ""), 1, 0),
    200 to Triple(Pair("闵行第二餐厅", ""), 1, 0),
    300 to Triple(Pair("闵行第三餐厅", ""), 1, 0),
    400 to Triple(Pair("闵行第四餐厅", ""), 1, 0),
    500 to Triple(Pair("闵行第五餐厅", ""), 1, 0),
    600 to Triple(Pair("闵行第六餐厅", ""), 1, 0),
    700 to Triple(Pair("闵行第七餐厅", ""), 1, 0),
    800 to Triple(Pair("闵行哈乐餐厅", ""), 1, 0),
    900 to Triple(Pair("闵行玉兰苑", ""), 1, 0),
    1 to Triple(Pair("闵行第一餐厅", "1F 餐厅"), 1, 0),
    2 to Triple(Pair("闵行第一餐厅", "2F 餐厅"), 1, 0),
    3 to Triple(Pair("闵行第一餐厅", "2F 清真餐厅"), 1, 0),
    5 to Triple(Pair("闵行第二餐厅", "1F 餐厅"), 1, 0),
    7 to Triple(Pair("闵行第二餐厅", "2F 教工餐厅"), 1, 0),
    8 to Triple(Pair("闵行第二餐厅", "2F 教工餐厅"), 1, 0),
    9 to Triple(Pair("闵行第二餐厅", "2F 新疆餐厅"), 1, 0),
    10 to Triple(Pair("闵行第二餐厅", "3F 绿园餐厅"), 1, 0),
    11 to Triple(Pair("闵行第三餐厅", "1F 餐厅"), 1, 0),
    12 to Triple(Pair("闵行第三餐厅", "2F 外婆桥"), 1, 0),
    13 to Triple(Pair("闵行第三餐厅", "2F 清真餐厅"), 1, 0),
    14 to Triple(Pair("闵行第四餐厅", "1F 餐厅"), 1, 0),
    15 to Triple(Pair("闵行第四餐厅", "2F 餐厅"), 1, 0),
    17 to Triple(Pair("闵行第五餐厅", "1F 餐厅"), 1, 0),
    18 to Triple(Pair("闵行第五餐厅", "1F 东湖面馆"), 1, 0),
    19 to Triple(Pair("闵行第五餐厅", "2F 教工餐厅"), 1, 0),
    21 to Triple(Pair("闵行第六餐厅", "1F 餐厅"), 1, 0),
    22 to Triple(Pair("闵行第六餐厅", "2F 餐厅"), 1, 0),
    23 to Triple(Pair("闵行第七餐厅", "1F 学生餐厅"), 1, 0),
    4 to Triple(Pair("闵行哈乐餐厅", "1F 餐厅"), 1, 0),
    36 to Triple(Pair("闵行玉兰苑", "1F 统禾"), 1, 0)
)

