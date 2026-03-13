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
import data.model.*
import data.repository.TaskRepository

@Composable
fun TaskScreen(onAddTaskClick: () -> Unit) {
    // 使用TaskRepository的真实数据
    val repository = remember { TaskRepository.getInstance() }
    val tasks by repository.tasks.collectAsState()
    
    // 按日期分组
    val taskGroups = remember(tasks) { groupTasksByDate(tasks) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { Text("任务", fontWeight = FontWeight.Bold) } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTaskClick, backgroundColor = MaterialTheme.colors.primary) {
                Icon(Icons.Default.Add, contentDescription = "添加任务", tint = Color.White)
            }
        }
    ) { paddingValues ->
        if (tasks.isEmpty()) {
            // 空状态
            EmptyTaskState(onAddTaskClick = onAddTaskClick)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }
                items(taskGroups) { group -> TaskGroupSection(group = group) }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun EmptyTaskState(onAddTaskClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colors.onSurface.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("还没有任务", style = MaterialTheme.typography.h6, color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f))
        Spacer(modifier = Modifier.height(8.dp))
        Text("点击右下角按钮添加您的第一个任务", style = MaterialTheme.typography.body2, color = MaterialTheme.colors.onSurface.copy(alpha = 0.4f))
    }
}

@Composable
fun TaskGroupSection(group: TaskGroup) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(group.date, style = MaterialTheme.typography.subtitle1, fontWeight = FontWeight.Bold, color = MaterialTheme.colors.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Divider(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            Text("${group.tasks.size}个任务", style = MaterialTheme.typography.caption, color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f))
        }
        Spacer(modifier = Modifier.height(8.dp))
        group.tasks.forEach { task ->
            TaskItem(task = task)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun TaskItem(task: Task) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { },
        elevation = 2.dp,
        shape = RoundedCornerShape(12.dp),
        backgroundColor = if (task.isCompleted) MaterialTheme.colors.surface.copy(alpha = 0.7f) else MaterialTheme.colors.surface
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(24.dp).clip(CircleShape).background(if (task.isCompleted) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    task.title,
                    style = MaterialTheme.typography.body1,
                    fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.Medium,
                    textDecoration = if (task.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else androidx.compose.ui.text.style.TextDecoration.None,
                    color = if (task.isCompleted) MaterialTheme.colors.onSurface.copy(alpha = 0.5f) else MaterialTheme.colors.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(task.getRepeatDescription(), style = MaterialTheme.typography.caption, color = MaterialTheme.colors.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    task.reminderTime?.let {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colors.onSurface.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(it, style = MaterialTheme.typography.caption, color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f))
                        }
                    }
                }
            }
            task.bankTag?.let { bank ->
                Spacer(modifier = Modifier.width(8.dp))
                BankTag(bank = bank)
            }
        }
    }
}

@Composable
fun BankTag(bank: Bank) {
    Surface(shape = RoundedCornerShape(4.dp), color = MaterialTheme.colors.primary.copy(alpha = 0.1f)) {
        Text(bank.name.take(4), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.caption, color = MaterialTheme.colors.primary)
    }
}

private fun groupTasksByDate(tasks: List<Task>): List<TaskGroup> {
    if (tasks.isEmpty()) return emptyList()
    
    val today = java.text.SimpleDateFormat("MM-dd", java.util.Locale.CHINA).format(java.util.Date())
    val completed = tasks.filter { it.isCompleted }
    val uncompleted = tasks.filter { !it.isCompleted }
    
    return when {
        tasks.size <= 5 -> listOf(TaskGroup("今天", tasks))
        else -> listOf(
            TaskGroup("今天", uncompleted.take(3) + completed.take(2)),
            TaskGroup("明天", uncompleted.drop(3).take(2)),
            TaskGroup("后天", uncompleted.drop(5).take(2))
        )
    }
}
