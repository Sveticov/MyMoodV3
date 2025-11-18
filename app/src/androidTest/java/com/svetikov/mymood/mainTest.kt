package com.svetikov.mymood

fun main() {
    val list = listOf(
        ActionLogTest(id = 85, actionType = "🥰", timestamp = 1763369774463),
        ActionLogTest(id = 84, actionType = "😐", timestamp = 1763368663673),
        ActionLogTest(id = 83, actionType = "🥰", timestamp = 1763364246658),
        ActionLogTest(id = 82, actionType = "😐", timestamp = 1763119162776),
        ActionLogTest(id = 81, actionType = "🥰", timestamp = 1763118942920),
        ActionLogTest(id = 80, actionType = "🥰", timestamp = 1763118090715),
        ActionLogTest(id = 79, actionType = "😢", timestamp = 1763117647603),
        ActionLogTest(id = 78, actionType = "😀", timestamp = 1763109413079),
        ActionLogTest(id = 77, actionType = "😡", timestamp = 1763108655981),
        ActionLogTest(id = 76, actionType = "😢", timestamp = 1763108468628),
        ActionLogTest(id = 75, actionType = "😀", timestamp = 1763107347942),
        ActionLogTest(id = 74, actionType = "😡", timestamp = 1763106388695),
        ActionLogTest(id = 73, actionType = "😡", timestamp = 1763105504936),
        ActionLogTest(id = 72, actionType = "😀", timestamp = 1763105164175),
        ActionLogTest(id = 71, actionType = "😴", timestamp = 1763099785947),
        ActionLogTest(id = 70, actionType = "😵", timestamp = 1763053956054),
        ActionLogTest(id = 69, actionType = "😴", timestamp = 1763053529113),
        ActionLogTest(id = 67, actionType = "😢", timestamp = 1763053199130),
        ActionLogTest(id = 66, actionType = "😡", timestamp = 1763053187982)
    )
/*calculatePercentEmoji(list, targetDate = "2025-11-17")
    .forEach { println(it) }*/

}
/*

fun calculatePercentEmoji(list:List<ActionLogTest>,targetDate:String="2025-11-14"):List<Pair<String,Float>>{
    val listSortByTime = list.filter {
        sortedTime(timestamp = it.timestamp, targetDateStr = targetDate)
    }
        .groupBy { it.actionType }
        .map {
            Pair(it.key, it.value.count())
        }
    if (listSortByTime.isNotEmpty()){
    val sumEmoji =listSortByTime
        .map { it.second.toInt() }
        .reduce { acc, i -> acc+i }
   return listSortByTime.map {
        Pair(it.first,it.second*(100.0/sumEmoji).toFloat())
    }}
    else
        return emptyList()

}

fun sortedTime(
    timestamp: Long,
    targetDateStr: String = "2025-11-17",
    zoneId: ZoneId = ZoneId.systemDefault()
): Boolean {
    val instant = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), zoneId)
    val targetDate = LocalDate.parse(targetDateStr)
    return instant.toLocalDate() == targetDate

}

*/

data class ActionLogTest(
    val id: Int = 0,
    val actionType: String,//Button A and B
    val timestamp: Long = System.currentTimeMillis()
)
