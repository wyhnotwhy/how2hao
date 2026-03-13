package data.repository

import android.content.Context
import android.content.SharedPreferences
import data.model.Task
import data.model.TaskRepeatType
import data.model.Bank
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable

actual fun loadTasksFromStorage(): List<Task> {
    val context = AppContext.get() ?: return emptyList()
    val prefs: SharedPreferences = context.getSharedPreferences("tasks", Context.MODE_PRIVATE)
    val tasksJson = prefs.getString("task_list", null) ?: return emptyList()
    
    return try {
        val taskDtos = Json.decodeFromString<List<TaskDto>>(tasksJson)
        taskDtos.map { it.toTask() }
    } catch (e: Exception) {
        emptyList()
    }
}

actual fun saveTasksToStorage(tasks: List<Task>) {
    val context = AppContext.get() ?: return
    val prefs: SharedPreferences = context.getSharedPreferences("tasks", Context.MODE_PRIVATE)
    
    val taskDtos = tasks.map { it.toDto() }
    val tasksJson = Json.encodeToString(taskDtos)
    
    prefs.edit().putString("task_list", tasksJson).apply()
}

@Serializable
data class TaskDto(
    val id: String,
    val title: String,
    val date: Long?,
    val repeatType: TaskRepeatTypeDto,
    val reminderTime: String?,
    val bankId: String?,
    val isCompleted: Boolean,
    val createdAt: Long
)

@Serializable
sealed class TaskRepeatTypeDto {
    @Serializable
    data class OneTime(val date: Long) : TaskRepeatTypeDto()
    
    @Serializable
    data class Simple(val type: SimpleType) : TaskRepeatTypeDto() {
        @Serializable
        enum class SimpleType { DAILY, WEEKLY, MONTHLY, YEARLY }
    }
    
    @Serializable
    data class AdvancedWeekly(val days: List<Int>) : TaskRepeatTypeDto()
    
    @Serializable
    data class AdvancedMonthly(val days: List<Int>) : TaskRepeatTypeDto()
    
    @Serializable
    data class AdvancedYearly(val months: List<Int>, val days: List<Int>) : TaskRepeatTypeDto()
}

fun Task.toDto(): TaskDto {
    return TaskDto(
        id = id,
        title = title,
        date = date,
        repeatType = repeatType.toDto(),
        reminderTime = reminderTime,
        bankId = bankTag?.id,
        isCompleted = isCompleted,
        createdAt = createdAt
    )
}

fun TaskDto.toTask(): Task {
    return Task(
        id = id,
        title = title,
        date = date,
        repeatType = repeatType.toModel(),
        reminderTime = reminderTime,
        bankTag = bankId?.let { BankRepository.getAllBanks().find { it.id == bankId } },
        isCompleted = isCompleted,
        createdAt = createdAt
    )
}

fun TaskRepeatType.toDto(): TaskRepeatTypeDto {
    return when (this) {
        is TaskRepeatType.OneTime -> TaskRepeatTypeDto.OneTime(date)
        is TaskRepeatType.Simple.Daily -> TaskRepeatTypeDto.Simple(TaskRepeatTypeDto.Simple.SimpleType.DAILY)
        is TaskRepeatType.Simple.Weekly -> TaskRepeatTypeDto.Simple(TaskRepeatTypeDto.Simple.SimpleType.WEEKLY)
        is TaskRepeatType.Simple.Monthly -> TaskRepeatTypeDto.Simple(TaskRepeatTypeDto.Simple.SimpleType.MONTHLY)
        is TaskRepeatType.Simple.Yearly -> TaskRepeatTypeDto.Simple(TaskRepeatTypeDto.Simple.SimpleType.YEARLY)
        is TaskRepeatType.AdvancedWeekly -> TaskRepeatTypeDto.AdvancedWeekly(daysOfWeek)
        is TaskRepeatType.AdvancedMonthly -> TaskRepeatTypeDto.AdvancedMonthly(daysOfMonth)
        is TaskRepeatType.AdvancedYearly -> TaskRepeatTypeDto.AdvancedYearly(months, daysOfMonth)
    }
}

fun TaskRepeatTypeDto.toModel(): TaskRepeatType {
    return when (this) {
        is TaskRepeatTypeDto.OneTime -> TaskRepeatType.OneTime(date)
        is TaskRepeatTypeDto.Simple -> when (type) {
            TaskRepeatTypeDto.Simple.SimpleType.DAILY -> TaskRepeatType.Simple.Daily
            TaskRepeatTypeDto.Simple.SimpleType.WEEKLY -> TaskRepeatType.Simple.Weekly
            TaskRepeatTypeDto.Simple.SimpleType.MONTHLY -> TaskRepeatType.Simple.Monthly
            TaskRepeatTypeDto.Simple.SimpleType.YEARLY -> TaskRepeatType.Simple.Yearly
        }
        is TaskRepeatTypeDto.AdvancedWeekly -> TaskRepeatType.AdvancedWeekly(days)
        is TaskRepeatTypeDto.AdvancedMonthly -> TaskRepeatType.AdvancedMonthly(days)
        is TaskRepeatTypeDto.AdvancedYearly -> TaskRepeatType.AdvancedYearly(months, days)
    }
}

object AppContext {
    private var context: Context? = null
    
    fun init(context: Context) {
        this.context = context.applicationContext
    }
    
    fun get(): Context? = context
}
