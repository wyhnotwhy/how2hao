package ui.task

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.model.*
import data.repository.BankRepository

/**
 * 任务页面
 * 按日期展示每日任务，支持添加新任务
 */
@Composable
fun TaskScreen(
    onAddTaskClick: () -> Unit
) {
    val tasks = remember { generateMockTasks() }
    val taskGroups = remember { groupTasksByDate(tasks) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "任务",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTaskClick,
                backgroundColor = MaterialTheme.colors.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "添加任务",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp)
        ) {
            taskGroups.forEach { group ->
                item {
                    TaskGroupHeader(dateLabel = group.date)
                }
                items(group.tasks) { task ->
                    TaskItemCard(
                        task = task,
                        onClick = { }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

/**
 * 任务分组头部
 */
@Composable
fun TaskGroupHeader(dateLabel: String) {
    val badgeColor = when (dateLabel) {
        "今天" -> MaterialTheme.colors.primary
        "明天" -> MaterialTheme.colors.secondary
        else -> MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = badgeColor.copy(alpha = 0.15f)
        ) {
            Text(
                text = dateLabel,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.body2,
                color = badgeColor,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Divider(modifier = Modifier.weight(1f))
    }
}

/**
 * 任务卡片
 */
@Composable
fun TaskItemCard(
    task: Task,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 完成状态圆圈
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        if (task.isCompleted)
                            MaterialTheme.colors.primary
                        else
                            MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
                    )
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 任务内容
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = if (task.isCompleted) 
                        androidx.compose.ui.text.style.TextDecoration.LineThrough 
                    else null,
                    color = if (task.isCompleted) 
                        MaterialTheme.colors.onSurface.copy(alpha = 0.5f) 
                    else MaterialTheme.colors.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                // 频次和提醒时间
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FrequencyChip(frequency = task.frequency)
                    Spacer(modifier = Modifier.width(8.dp))
                    task.reminderTime?.let {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = it,
                                style = MaterialTheme.typography.caption,
                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }

            // 银行标签
            task.bankTag?.let { bank ->
                Spacer(modifier = Modifier.width(8.dp))
                BankTag(bank = bank)
            }
        }
    }
}

/**
 * 频次标签
 */
@Composable
fun FrequencyChip(frequency: TaskFrequency) {
    val (text, color) = when (frequency) {
        TaskFrequency.DAILY -> "每天" to Color(0xFF4CAF50)
        TaskFrequency.WEEKLY -> "每周" to Color(0xFF2196F3)
        TaskFrequency.MONTHLY -> "每月" to Color(0xFFFF9800)
    }

    Surface(
        shape = RoundedCornerShape(4.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.caption,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * 银行标签
 */
@Composable
fun BankTag(bank: Bank) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = MaterialTheme.colors.primary.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 银行图标占位
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colors.primary.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = bank.name.take(1),
                    style = MaterialTheme.typography.caption,
                    fontSize = 8.sp,
                    color = MaterialTheme.colors.primary
                )
            }
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = bank.name.take(4),
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ============ 模拟数据 ============

private fun generateMockTasks(): List<Task> {
    val banks = BankRepository.getAllBanks()
    return listOf(
        Task(
            id = "1",
            title = "工商银行信用卡还款",
            frequency = TaskFrequency.MONTHLY,
            reminderTime = "09:00",
            bankTag = banks.find { it.id == "1" },
            isCompleted = false
        ),
        Task(
            id = "2",
            title = "查看招商银行优惠活动",
            frequency = TaskFrequency.DAILY,
            reminderTime = "10:00",
            bankTag = banks.find { it.id == "6" },
            isCompleted = true
        ),
        Task(
            id = "3",
            title = "建设银行积分兑换",
            frequency = TaskFrequency.WEEKLY,
            reminderTime = "14:00",
            bankTag = banks.find { it.id == "2" },
            isCompleted = false
        ),
        Task(
            id = "4",
            title = "农业银行账单查询",
            frequency = TaskFrequency.MONTHLY,
            reminderTime = "09:30",
            bankTag = banks.find { it.id == "3" },
            isCompleted = false
        ),
        Task(
            id = "5",
            title = "查看各银行立减金活动",
            frequency = TaskFrequency.DAILY,
            reminderTime = "08:00",
            bankTag = null,
            isCompleted = false
        ),
        Task(
            id = "6",
            title = "交通银行信用卡还款",
            frequency = TaskFrequency.MONTHLY,
            reminderTime = "09:00",
            bankTag = banks.find { it.id == "5" },
            isCompleted = true
        ),
        Task(
            id = "7",
            title = "浦发银行活动报名",
            frequency = TaskFrequency.WEEKLY,
            reminderTime = "10:00",
            bankTag = banks.find { it.id == "9" },
            isCompleted = false
        ),
        Task(
            id = "8",
            title = "中信银行积分查询",
            frequency = TaskFrequency.MONTHLY,
            reminderTime = "15:00",
            bankTag = banks.find { it.id == "10" },
            isCompleted = false
        )
    )
}

private fun groupTasksByDate(tasks: List<Task>): List<TaskGroup> {
    // 简单分组：已完成放今天，未完成按序号分配到不同日期
    val todayTasks = tasks.filter { !it.isCompleted }.take(4)
    val tomorrowTasks = tasks.filter { !it.isCompleted }.drop(4)
    val completedTasks = tasks.filter { it.isCompleted }

    return buildList {
        if (todayTasks.isNotEmpty()) {
            add(TaskGroup("今天", todayTasks))
        }
        if (tomorrowTasks.isNotEmpty()) {
            add(TaskGroup("明天", tomorrowTasks))
        }
        if (completedTasks.isNotEmpty()) {
            add(TaskGroup("已完成", completedTasks))
        }
    }
}
