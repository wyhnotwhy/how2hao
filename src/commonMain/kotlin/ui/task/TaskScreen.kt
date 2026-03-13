package ui.task

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import data.model.*
import data.repository.TaskRepository
import data.repository.BankRepository
import java.util.*

@Composable
fun TaskScreen(onAddTaskClick: () -> Unit) {
    val repository = remember { TaskRepository.getInstance() }
    val myTasks by repository.tasks.collectAsState()
    
    var isTomorrowExpanded by remember { mutableStateOf(false) }
    val plazaTasks = remember { generatePlazaTasks() }

    // 获取当前时间
    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val currentMinute = Calendar.getInstance().get(Calendar.MINUTE)
    val currentTime = currentHour * 60 + currentMinute

    // 分类今天任务
    val todayTasks = myTasks.filter { isToday(it.date) }
    val expiredTasks = todayTasks.filter { isExpired(it.reminderTime, currentTime) && !it.isCompleted }
    val pendingTasks = todayTasks.filter { !isExpired(it.reminderTime, currentTime) && !it.isCompleted }
    val completedTasks = todayTasks.filter { it.isCompleted }

    // 明天任务
    val tomorrowTasks = myTasks.filter { isTomorrow(it.date) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { 
                        Text("任务", fontWeight = FontWeight.Bold) 
                    } 
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTaskClick, backgroundColor = MaterialTheme.colors.primary) {
                Icon(Icons.Default.Add, contentDescription = "添加任务", tint = Color.White)
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 今天的任务
            item { 
                TodayTasksSection(
                    expiredTasks = expiredTasks,
                    pendingTasks = pendingTasks,
                    completedTasks = completedTasks
                ) 
            }
            
            // 明天的任务
            item {
                TomorrowTasksSection(
                    tasks = tomorrowTasks,
                    isExpanded = isTomorrowExpanded,
                    onExpandToggle = { isTomorrowExpanded = !isTomorrowExpanded }
                )
            }
            
            // 任务广场
            item { PlazaHeader() }
            
            items(plazaTasks) { task ->
                PlazaTaskItem(
                    task = task,
                    onAddClick = { 
                        repository.addTask(
                            AddTaskRequest(
                                title = task.title,
                                date = task.date,
                                repeatType = task.repeatType,
                                reminderTime = task.reminderTime,
                                bankId = task.bankTag?.id
                            )
                        )
                    }
                )
            }
            
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

// 判断是否是今天
private fun isToday(date: Long?): Boolean {
    if (date == null) return false
    val taskCal = Calendar.getInstance().apply { timeInMillis = date }
    val todayCal = Calendar.getInstance()
    return taskCal.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR) &&
           taskCal.get(Calendar.DAY_OF_YEAR) == todayCal.get(Calendar.DAY_OF_YEAR)
}

// 判断是否是明天
private fun isTomorrow(date: Long?): Boolean {
    if (date == null) return false
    val taskCal = Calendar.getInstance().apply { timeInMillis = date }
    val tomorrowCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
    return taskCal.get(Calendar.YEAR) == tomorrowCal.get(Calendar.YEAR) &&
           taskCal.get(Calendar.DAY_OF_YEAR) == tomorrowCal.get(Calendar.DAY_OF_YEAR)
}

// 判断是否已过期
private fun isExpired(reminderTime: String?, currentTime: Int): Boolean {
    if (reminderTime == null) return false
    val parts = reminderTime.split(":")
    if (parts.size != 2) return false
    val taskHour = parts[0].toIntOrNull() ?: 0
    val taskMinute = parts[1].toIntOrNull() ?: 0
    val taskTime = taskHour * 60 + taskMinute
    return taskTime < currentTime
}

@Composable
private fun TodayTasksSection(
    expiredTasks: List<Task>,
    pendingTasks: List<Task>,
    completedTasks: List<Task>
) {
    val totalCount = expiredTasks.size + pendingTasks.size + completedTasks.size
    
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        elevation = 4.dp,
        shape = RoundedCornerShape(16.dp),
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 标题栏
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colors.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Today,
                            contentDescription = null,
                            tint = MaterialTheme.colors.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            "今天",
                            style = MaterialTheme.typography.h6,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${totalCount}个任务 · ${pendingTasks.size}待完成",
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
                
                // 完成进度
                if (totalCount > 0) {
                    val progress = completedTasks.size.toFloat() / totalCount
                    CircularProgressIndicator(
                        progress = progress,
                        modifier = Modifier.size(36.dp),
                        strokeWidth = 3.dp,
                        color = MaterialTheme.colors.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (totalCount == 0) {
                EmptyTodayState()
            } else {
                // 已过期任务
                if (expiredTasks.isNotEmpty()) {
                    TaskStatusHeader("已过期", Color(0xFFE53935), expiredTasks.size)
                    Spacer(modifier = Modifier.height(8.dp))
                    expiredTasks.forEach { task ->
                        TaskItemWithStatus(task = task, statusColor = Color(0xFFE53935), statusText = "已过期")
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                // 待完成任务
                if (pendingTasks.isNotEmpty()) {
                    TaskStatusHeader("待完成", Color(0xFF1976D2), pendingTasks.size)
                    Spacer(modifier = Modifier.height(8.dp))
                    pendingTasks.forEach { task ->
                        TaskItemWithStatus(task = task, statusColor = Color(0xFF1976D2), statusText = "待完成")
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                // 已完成任务
                if (completedTasks.isNotEmpty()) {
                    TaskStatusHeader("已完成", Color(0xFF43A047), completedTasks.size)
                    Spacer(modifier = Modifier.height(8.dp))
                    completedTasks.forEach { task ->
                        TaskItemWithStatus(task = task, statusColor = Color(0xFF43A047), statusText = "已完成")
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskStatusHeader(status: String, color: Color, count: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            status,
            style = MaterialTheme.typography.caption,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            "($count)",
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
        )
    }
}

@Composable
private fun TaskItemWithStatus(task: Task, statusColor: Color, statusText: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 1.dp,
        shape = RoundedCornerShape(12.dp),
        backgroundColor = when (statusText) {
            "已过期" -> Color(0xFFFFEBEE)
            "待完成" -> Color(0xFFE3F2FD)
            "已完成" -> Color(0xFFE8F5E9)
            else -> MaterialTheme.colors.surface
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 状态指示器
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        when (statusText) {
                            "已完成" -> Color(0xFF43A047)
                            else -> statusColor.copy(alpha = 0.2f)
                        }
                    )
                    .clickable { /* 切换状态 */ },
                contentAlignment = Alignment.Center
            ) {
                when (statusText) {
                    "已完成" -> Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    "已过期" -> Icon(Icons.Default.Schedule, contentDescription = null, tint = statusColor, modifier = Modifier.size(16.dp))
                    else -> Icon(Icons.Default.RadioButtonUnchecked, contentDescription = null, tint = statusColor, modifier = Modifier.size(16.dp))
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    task.title,
                    style = MaterialTheme.typography.body1,
                    fontWeight = if (statusText == "已完成") FontWeight.Normal else FontWeight.Medium,
                    textDecoration = if (statusText == "已完成") 
                        androidx.compose.ui.text.style.TextDecoration.LineThrough 
                    else 
                        androidx.compose.ui.text.style.TextDecoration.None,
                    color = if (statusText == "已完成") 
                        MaterialTheme.colors.onSurface.copy(alpha = 0.5f) 
                    else 
                        MaterialTheme.colors.onSurface
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    task.reminderTime?.let {
                        Text(
                            it,
                            style = MaterialTheme.typography.caption,
                            color = statusColor,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        "· ${task.getRepeatDescription()}",
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
            
            task.bankTag?.let { bank ->
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colors.primary.copy(alpha = 0.1f)
                ) {
                    Text(
                        bank.name.take(4),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyTodayState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.BeachAccess,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colors.primary.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "今天没有任务",
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
        )
        Text(
            "去添加一个吧！",
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.4f)
        )
    }
}

@Composable
private fun TomorrowTasksSection(tasks: List<Task>, isExpanded: Boolean, onExpandToggle: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        OutlinedButton(
            onClick = onExpandToggle,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                backgroundColor = MaterialTheme.colors.surface
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colors.secondary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colors.secondary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "明天",
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colors.secondary.copy(alpha = 0.1f)
                    ) {
                        Text(
                            "${tasks.size}",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.secondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                val rotation by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f)
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "收起" else "展开",
                    modifier = Modifier.rotate(rotation),
                    tint = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
                )
            }
        }
        
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp),
                backgroundColor = MaterialTheme.colors.surface
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    if (tasks.isEmpty()) {
                        Text(
                            "明天没有任务",
                            style = MaterialTheme.typography.body2,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
                            modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp)
                        )
                    } else {
                        tasks.forEach { task ->
                            TomorrowTaskItem(task = task)
                            if (task != tasks.last()) {
                                Divider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.05f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TomorrowTaskItem(task: Task) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colors.secondary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                task.reminderTime?.substring(0, 2) ?: "--",
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.secondary,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                task.title,
                style = MaterialTheme.typography.body2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                task.getRepeatDescription(),
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
            )
        }
        
        task.bankTag?.let { bank ->
            Text(
                bank.name.take(4),
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
private fun PlazaHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colors.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Group,
                contentDescription = null,
                tint = MaterialTheme.colors.primary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                "任务广场",
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold
            )
            Text(
                "发现更多实用任务",
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun PlazaTaskItem(task: Task, onAddClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = 2.dp,
        shape = RoundedCornerShape(12.dp),
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧图标
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colors.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // 任务信息
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    task.title,
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colors.primary.copy(alpha = 0.1f)
                    ) {
                        Text(
                            task.getRepeatDescription(),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.primary
                        )
                    }
                    task.bankTag?.let { bank ->
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "· ${bank.name}",
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // 添加按钮
            IconButton(
                onClick = onAddClick,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colors.primary)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "添加任务",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

private fun generatePlazaTasks(): List<Task> {
    val banks = BankRepository.getAllBanks()
    return listOf(
        Task("p1", "每日签到领积分", null, TaskRepeatType.Simple.Daily, "09:00", banks.find { it.id == "1" }, false),
        Task("p2", "查看信用卡优惠活动", null, TaskRepeatType.Simple.Weekly, "10:00", banks.find { it.id == "6" }, false),
        Task("p3", "还款日前三天提醒", null, TaskRepeatType.Simple.Monthly, "09:00", banks.find { it.id == "2" }, false),
        Task("p4", "生日月积分翻倍", null, TaskRepeatType.Simple.Yearly, "00:00", banks.find { it.id == "3" }, false),
        Task("p5", "周五美食半价", null, TaskRepeatType.AdvancedWeekly(listOf(5)), "11:00", banks.find { it.id == "5" }, false),
        Task("p6", "每月1号抢优惠券", null, TaskRepeatType.AdvancedMonthly(listOf(1)), "10:00", banks.find { it.id == "9" }, false),
        Task("p7", "季度账单分析", null, TaskRepeatType.AdvancedYearly(listOf(3, 6, 9, 12), listOf(1)), "09:00", banks.find { it.id == "10" }, false),
        Task("p8", "每周三加油优惠", null, TaskRepeatType.AdvancedWeekly(listOf(3)), "14:00", banks.find { it.id == "4" }, false)
    )
}
