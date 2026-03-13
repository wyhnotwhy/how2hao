package ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * 日期选择器组件
 * 支持：独立日期、简单重复、高级重复
 */
@Composable
fun DatePickerDialog(
    onDismiss: () -> Unit,
    onDateSelected: (Long?, TaskRepeatMode) -> Unit,
    initialDate: Long? = null
) {
    var selectedMode by remember { mutableStateOf(DatePickerMode.ONE_TIME) }
    var selectedDate by remember { mutableStateOf(initialDate ?: System.currentTimeMillis()) }
    var simpleRepeatType by remember { mutableStateOf(SimpleRepeatType.DAILY) }
    
    // 高级重复状态
    var advancedWeekDays by remember { mutableStateOf<List<Int>>(emptyList()) }
    var advancedMonthDays by remember { mutableStateOf<List<Int>>(emptyList()) }
    var advancedMonths by remember { mutableStateOf<List<Int>>(emptyList()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择日期", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
            ) {
                // 模式选择标签
                ModeSelector(
                    selectedMode = selectedMode,
                    onModeSelected = { selectedMode = it }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Divider()
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 根据模式显示不同内容
                when (selectedMode) {
                    DatePickerMode.ONE_TIME -> {
                        OneTimeDatePicker(
                            selectedDate = selectedDate,
                            onDateSelected = { selectedDate = it }
                        )
                    }
                    DatePickerMode.SIMPLE_REPEAT -> {
                        SimpleRepeatPicker(
                            selectedType = simpleRepeatType,
                            onTypeSelected = { simpleRepeatType = it }
                        )
                    }
                    DatePickerMode.ADVANCED_REPEAT -> {
                        AdvancedRepeatPicker(
                            weekDays = advancedWeekDays,
                            onWeekDaysChange = { advancedWeekDays = it },
                            monthDays = advancedMonthDays,
                            onMonthDaysChange = { advancedMonthDays = it },
                            months = advancedMonths,
                            onMonthsChange = { advancedMonths = it }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val repeatMode = when (selectedMode) {
                        DatePickerMode.ONE_TIME -> TaskRepeatMode.OneTime
                        DatePickerMode.SIMPLE_REPEAT -> TaskRepeatMode.Simple(simpleRepeatType)
                        DatePickerMode.ADVANCED_REPEAT -> {
                            when {
                                advancedWeekDays.isNotEmpty() -> 
                                    TaskRepeatMode.AdvancedWeekly(advancedWeekDays)
                                advancedMonthDays.isNotEmpty() && advancedMonths.isEmpty() ->
                                    TaskRepeatMode.AdvancedMonthly(advancedMonthDays)
                                advancedMonths.isNotEmpty() && advancedMonthDays.isNotEmpty() ->
                                    TaskRepeatMode.AdvancedYearly(advancedMonths, advancedMonthDays)
                                else -> TaskRepeatMode.OneTime
                            }
                        }
                    }
                    val finalDate = if (selectedMode == DatePickerMode.ONE_TIME) selectedDate else null
                    onDateSelected(finalDate, repeatMode)
                }
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        },
        modifier = Modifier.fillMaxWidth(0.95f)
    )
}

/**
 * 模式选择器
 */
@Composable
private fun ModeSelector(
    selectedMode: DatePickerMode,
    onModeSelected: (DatePickerMode) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ModeTab(
            text = "独立日期",
            isSelected = selectedMode == DatePickerMode.ONE_TIME,
            onClick = { onModeSelected(DatePickerMode.ONE_TIME) }
        )
        ModeTab(
            text = "简单重复",
            isSelected = selectedMode == DatePickerMode.SIMPLE_REPEAT,
            onClick = { onModeSelected(DatePickerMode.SIMPLE_REPEAT) }
        )
        ModeTab(
            text = "高级重复",
            isSelected = selectedMode == DatePickerMode.ADVANCED_REPEAT,
            onClick = { onModeSelected(DatePickerMode.ADVANCED_REPEAT) }
        )
    }
}

/**
 * 模式标签
 */
@Composable
private fun ModeTab(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.body2,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
        )
        if (isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(24.dp)
                    .height(2.dp)
                    .background(MaterialTheme.colors.primary)
            )
        }
    }
}

/**
 * 独立日期选择器 - 简化版
 */
@Composable
private fun OneTimeDatePicker(
    selectedDate: Long,
    onDateSelected: (Long) -> Unit
) {
    // 简化的日期选择 - 使用预设选项
    val today = System.currentTimeMillis()
    val tomorrow = today + 24 * 60 * 60 * 1000
    val nextWeek = today + 7 * 24 * 60 * 60 * 1000
    
    val presets = listOf(
        Pair("今天", today),
        Pair("明天", tomorrow),
        Pair("一周后", nextWeek)
    )
    
    Column {
        Text(
            text = "快速选择",
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            presets.forEach { (label, date) ->
                val isSelected = date == selectedDate
                OutlinedButton(
                    onClick = { onDateSelected(date) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        backgroundColor = if (isSelected) 
                            MaterialTheme.colors.primary.copy(alpha = 0.1f) 
                        else 
                            MaterialTheme.colors.surface
                    ),
                    border = if (isSelected)
                        ButtonDefaults.outlinedBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colors.primary)
                        )
                    else
                        ButtonDefaults.outlinedBorder
                ) {
                    Text(label)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 显示当前选择的日期
        val dateText = java.text.SimpleDateFormat("yyyy年MM月dd日", java.util.Locale.CHINA)
            .format(java.util.Date(selectedDate))
        Text(
            text = "已选择: $dateText",
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

/**
 * 简单重复选择器
 */
@Composable
private fun SimpleRepeatPicker(
    selectedType: SimpleRepeatType,
    onTypeSelected: (SimpleRepeatType) -> Unit
) {
    val options = listOf(
        Pair(SimpleRepeatType.DAILY, "每天"),
        Pair(SimpleRepeatType.WEEKLY, "每周"),
        Pair(SimpleRepeatType.MONTHLY, "每月"),
        Pair(SimpleRepeatType.YEARLY, "每年")
    )
    
    Column {
        Text(
            text = "重复频率",
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        options.forEach { (type, label) ->
            val isSelected = type == selectedType
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onTypeSelected(type) }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = { onTypeSelected(type) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.body1
                )
            }
        }
    }
}

/**
 * 高级重复选择器
 */
@Composable
private fun AdvancedRepeatPicker(
    weekDays: List<Int>,
    onWeekDaysChange: (List<Int>) -> Unit,
    monthDays: List<Int>,
    onMonthDaysChange: (List<Int>) -> Unit,
    months: List<Int>,
    onMonthsChange: (List<Int>) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    
    Column {
        // 子标签切换
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("每周", "每月", "每年").forEachIndexed { index, label ->
                val isSelected = selectedTab == index
                TextButton(
                    onClick = { selectedTab = index },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (isSelected) 
                            MaterialTheme.colors.primary 
                        else 
                            MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                    )
                ) {
                    Text(
                        text = label,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        when (selectedTab) {
            0 -> WeekDayPicker(
                selectedDays = weekDays,
                onDaysChange = onWeekDaysChange
            )
            1 -> MonthDayPicker(
                selectedDays = monthDays,
                onDaysChange = onMonthDaysChange
            )
            2 -> YearMonthPicker(
                selectedMonths = months,
                onMonthsChange = onMonthsChange,
                selectedDays = monthDays,
                onDaysChange = onMonthDaysChange
            )
        }
    }
}

/**
 * 星期选择器
 */
@Composable
private fun WeekDayPicker(
    selectedDays: List<Int>,
    onDaysChange: (List<Int>) -> Unit
) {
    val weekDays = listOf(
        Pair(1, "一"), Pair(2, "二"), Pair(3, "三"),
        Pair(4, "四"), Pair(5, "五"), Pair(6, "六"), Pair(7, "日")
    )
    
    Column {
        Text(
            text = "选择每周的哪一天（可多选）",
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            weekDays.forEach { (day, label) ->
                val isSelected = selectedDays.contains(day)
                DayChip(
                    label = label,
                    isSelected = isSelected,
                    onClick = {
                        val newDays = if (isSelected) {
                            selectedDays - day
                        } else {
                            selectedDays + day
                        }
                        onDaysChange(newDays.sorted())
                    }
                )
            }
        }
        
        // 快捷选择
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { onDaysChange(listOf(1, 2, 3, 4, 5)) },
                modifier = Modifier.weight(1f)
            ) {
                Text("工作日")
            }
            OutlinedButton(
                onClick = { onDaysChange(listOf(6, 7)) },
                modifier = Modifier.weight(1f)
            ) {
                Text("周末")
            }
            OutlinedButton(
                onClick = { onDaysChange(listOf(1, 2, 3, 4, 5, 6, 7)) },
                modifier = Modifier.weight(1f)
            ) {
                Text("每天")
            }
        }
    }
}

/**
 * 日期选择器（每月几号）
 */
@Composable
private fun MonthDayPicker(
    selectedDays: List<Int>,
    onDaysChange: (List<Int>) -> Unit
) {
    Column {
        Text(
            text = "选择每月的哪一天（可多选）",
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 使用网格显示1-31
        val rows = (1..31).chunked(7)
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { day ->
                    val isSelected = selectedDays.contains(day)
                    DayChip(
                        label = day.toString(),
                        isSelected = isSelected,
                        onClick = {
                            val newDays = if (isSelected) {
                                selectedDays - day
                            } else {
                                selectedDays + day
                            }
                            onDaysChange(newDays.sorted())
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                // 填充剩余空间
                repeat(7 - row.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

/**
 * 年月选择器
 */
@Composable
private fun YearMonthPicker(
    selectedMonths: List<Int>,
    onMonthsChange: (List<Int>) -> Unit,
    selectedDays: List<Int>,
    onDaysChange: (List<Int>) -> Unit
) {
    val months = listOf(
        Pair(1, "1月"), Pair(2, "2月"), Pair(3, "3月"),
        Pair(4, "4月"), Pair(5, "5月"), Pair(6, "6月"),
        Pair(7, "7月"), Pair(8, "8月"), Pair(9, "9月"),
        Pair(10, "10月"), Pair(11, "11月"), Pair(12, "12月")
    )
    
    Column {
        Text(
            text = "选择月份（可多选）",
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 月份网格
        val rows = months.chunked(4)
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { (month, label) ->
                    val isSelected = selectedMonths.contains(month)
                    DayChip(
                        label = label,
                        isSelected = isSelected,
                        onClick = {
                            val newMonths = if (isSelected) {
                                selectedMonths - month
                            } else {
                                selectedMonths + month
                            }
                            onMonthsChange(newMonths.sorted())
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        Spacer(modifier = Modifier.height(16.dp))
        
        // 日期选择
        Text(
            text = "选择日期",
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 简化的日期选择
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(1, 15).forEach { day ->
                val isSelected = selectedDays.contains(day)
                OutlinedButton(
                    onClick = {
                        val newDays = if (isSelected) {
                            selectedDays - day
                        } else {
                            selectedDays + day
                        }
                        onDaysChange(newDays.sorted())
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        backgroundColor = if (isSelected) 
                            MaterialTheme.colors.primary.copy(alpha = 0.1f) 
                        else 
                            MaterialTheme.colors.surface
                    )
                ) {
                    Text("${day}日")
                }
            }
        }
    }
}

/**
 * 日期芯片
 */
@Composable
private fun DayChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(
                if (isSelected) 
                    MaterialTheme.colors.primary 
                else 
                    MaterialTheme.colors.surface
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else MaterialTheme.colors.onSurface,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            style = MaterialTheme.typography.body2
        )
    }
}

// 数据类定义
enum class DatePickerMode {
    ONE_TIME,       // 独立日期
    SIMPLE_REPEAT,  // 简单重复
    ADVANCED_REPEAT // 高级重复
}

enum class SimpleRepeatType {
    DAILY,      // 每天
    WEEKLY,     // 每周
    MONTHLY,    // 每月
    YEARLY      // 每年
}

sealed class TaskRepeatMode {
    object OneTime : TaskRepeatMode()
    data class Simple(val type: SimpleRepeatType) : TaskRepeatMode()
    data class AdvancedWeekly(val days: List<Int>) : TaskRepeatMode()
    data class AdvancedMonthly(val days: List<Int>) : TaskRepeatMode()
    data class AdvancedYearly(val months: List<Int>, val days: List<Int>) : TaskRepeatMode()
}
