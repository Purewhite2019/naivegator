package com.sjtu.naivegator

val canteenNameMap : Map<String, List<String>> = mapOf(
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
    100 to Triple(Pair("闵行第一餐厅", ""),1,1),
    200 to Triple(Pair("闵行第二餐厅", ""),1,1),
    300 to Triple(Pair("闵行第三餐厅", ""),1,1),
    400 to Triple(Pair("闵行第四餐厅", ""),1,1),
    500 to Triple(Pair("闵行第五餐厅", ""),1,1),
    600 to Triple(Pair("闵行第六餐厅", ""),1,1),
    700 to Triple(Pair("闵行第七餐厅", ""),1,1),
    800 to Triple(Pair("闵行哈乐餐厅", ""),1,1),
    900 to Triple(Pair("闵行玉兰苑", "") ,1,1),
    1 to Triple(Pair("闵行第一餐厅", "1F 餐厅") ,1,1),
    2 to Triple(Pair("闵行第一餐厅", "2F 餐厅") ,1,1),
    3 to Triple(Pair("闵行第一餐厅", "2F 清真餐厅") ,1,1),
    5 to Triple(Pair("闵行第二餐厅", "1F 餐厅") ,1,1),
    7 to Triple(Pair("闵行第二餐厅", "2F 教工餐厅") ,1,1),
    8 to Triple(Pair("闵行第二餐厅", "2F 教工餐厅") ,1,1),
    9 to Triple(Pair("闵行第二餐厅", "2F 新疆餐厅") ,1,1),
    10 to Triple(Pair("闵行第二餐厅", "3F 绿园餐厅") ,1,1),
    11 to Triple(Pair("闵行第三餐厅", "1F 餐厅") ,1,1),
    12 to Triple(Pair("闵行第三餐厅", "2F 外婆桥") ,1,1),
    13 to Triple(Pair("闵行第三餐厅", "2F 清真餐厅") ,1,1),
    14 to Triple(Pair("闵行第四餐厅", "1F 餐厅") ,1,1),
    15 to Triple(Pair("闵行第四餐厅", "2F 餐厅") ,1,1),
    17 to Triple(Pair("闵行第五餐厅", "1F 餐厅") ,1,1),
    18 to Triple(Pair("闵行第五餐厅", "1F 东湖面馆") ,1,1),
    19 to Triple(Pair("闵行第五餐厅", "2F 教工餐厅") ,1,1),
    21 to Triple(Pair("闵行第六餐厅", "1F 餐厅") ,1,1),
    22 to Triple(Pair("闵行第六餐厅", "2F 餐厅") ,1,1),
    23 to Triple(Pair("闵行第七餐厅", "1F 学生餐厅") ,1,1),
    4 to Triple(Pair("闵行哈乐餐厅", "1F 餐厅") ,1,1),
    36 to Triple(Pair("闵行玉兰苑", "1F 统禾") ,1,1)
)

