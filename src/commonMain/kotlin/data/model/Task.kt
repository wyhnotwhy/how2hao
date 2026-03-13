package data.model

/**
 * 任务重复类型
 */
sealed class TaskRepeatType {
    // 独立日期 - 不重复
    data class OneTime(val date: Long) : TaskRepeatType()
    
    // 简单重复
    sealed class Simple : TaskRepeatType() {
        object Daily : Simple()      // 每天
        object Weekly : Simple()     // 每周
        object Monthly : Simple()    // 每月
        object Yearly : Simple()     // 每年
    }
    
    // 高级重复 - 支持多选
    data class AdvancedWeekly(
        val daysOfWeek: List<Int>  // 1=周一, 7=周日
    ) : TaskRepeatType()
    
    data class AdvancedMonthly(
        val daysOfMonth: List<Int>  // 1-31
    ) : TaskRepeatType()
    
    data class AdvancedYearly(
        val months: List<Int>,      // 1-12
        val daysOfMonth: List<Int>  // 1-31
    ) : TaskRepeatType()
}

/**
 * 任务频次类型（保留用于兼容）
 */
enum class TaskFrequency {
    DAILY,      // 每天一次
    WEEKLY,     // 每周一次
    MONTHLY     // 每月一次
}

/**
 * 任务数据模型
 */
data class Task(
    val id: String,
    val title: String,
    val date: Long?,                    // 执行日期（时间戳）
    val repeatType: TaskRepeatType,     // 重复类型
    val reminderTime: String?,          // 提醒时间，格式如 "09:00"
    val bankTag: Bank?,                 // 关联的银行
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    // 获取下次执行日期
    fun getNextDate(): Long? {
        return when (repeatType) {
            is TaskRepeatType.OneTime -> repeatType.date
            else -> null // 重复任务需要计算下次日期
        }
    }
    
    // 获取重复描述
    fun getRepeatDescription(): String {
        return when (repeatType) {
            is TaskRepeatType.OneTime -> "单次"
            is TaskRepeatType.Simple.Daily -> "每天"
            is TaskRepeatType.Simple.Weekly -> "每周"
            is TaskRepeatType.Simple.Monthly -> "每月"
            is TaskRepeatType.Simple.Yearly -> "每年"
            is TaskRepeatType.AdvancedWeekly -> {
                val days = repeatType.daysOfWeek.sorted()
                when {
                    days.size == 7 -> "每天"
                    days == listOf(1, 2, 3, 4, 5) -> "工作日"
                    days == listOf(6, 7) -> "周末"
                    else -> "每周${days.joinToString(",") { getWeekDayName(it) }}"
                }
            }
            is TaskRepeatType.AdvancedMonthly -> {
                "每月${repeatType.daysOfMonth.sorted().joinToString(",")}日"
            }
            is TaskRepeatType.AdvancedYearly -> {
                val months = repeatType.months.sorted().joinToString(",") { "${it}月" }
                val days = repeatType.daysOfMonth.sorted().joinToString(",") { "${it}日" }
                "每年${months}的${days}"
            }
        }
    }
    
    private fun getWeekDayName(day: Int): String {
        return when (day) {
            1 -> "一"
            2 -> "二"
            3 -> "三"
            4 -> "四"
            5 -> "五"
            6 -> "六"
            7 -> "日"
            else -> ""
        }
    }
}

/**
 * 按日期分组的任务
 */
data class TaskGroup(
    val date: String,  // 格式: "今天", "明天", "后天" 或具体日期 "03-15"
    val tasks: List<Task>
)

/**
 * 银行数据模型
 */
data class Bank(
    val id: String,
    val name: String,
    val code: String,           // 银行代码
    val iconUrl: String? = null // 图标URL
)

/**
 * 添加任务请求
 */
data class AddTaskRequest(
    val title: String,
    val date: Long?,
    val repeatType: TaskRepeatType,
    val reminderTime: String?,
    val bankId: String?
)
