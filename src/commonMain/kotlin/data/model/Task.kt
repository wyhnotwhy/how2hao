package data.model

/**
 * 任务频次类型
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
    val frequency: TaskFrequency,
    val reminderTime: String?,  // 提醒时间，格式如 "09:00"
    val bankTag: Bank?,         // 关联的银行
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

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
    val frequency: TaskFrequency,
    val reminderTime: String?,
    val bankId: String?
)
