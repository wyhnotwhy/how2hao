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
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DatePickerDialog(
    onDismiss: () -> Unit,
    onDateSelected: (Long?, TaskRepeatMode) -> Unit,
    initialDate: Long? = null
) {
    var selectedMode by remember { mutableStateOf(DatePickerMode.ONE_TIME) }
    var selectedDate by remember { mutableStateOf(initialDate ?: getTodayStart()) }
    var simpleRepeatType by remember { mutableStateOf(SimpleRepeatType.DAILY) }
    
    var advancedWeekDays by remember { mutableStateOf<List<Int>>(emptyList()) }
    var advancedMonthDays by remember { mutableStateOf<List<Int>>(emptyList()) }
    var advancedMonths by remember { mutableStateOf<List<Int>>(emptyList()) }
    
    // 日历状态 - 使用Calendar处理
    var displayCalendar by remember { mutableStateOf(Calendar.getInstance().apply {
        if (initialDate != null) timeInMillis = initialDate
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择日期", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
            ) {
                ModeSelector(
                    selectedMode = selectedMode,
                    onModeSelected = { selectedMode = it }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))
                
                when (selectedMode) {
                    DatePickerMode.ONE_TIME -> {
                        CalendarView(
                            selectedDate = selectedDate,
                            displayCalendar = displayCalendar,
                            onDisplayCalendarChange = { displayCalendar = it },
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
                                advancedWeekDays.isNotEmpty() -> TaskRepeatMode.AdvancedWeekly(advancedWeekDays)
                                advancedMonthDays.isNotEmpty() && advancedMonths.isEmpty() -> TaskRepeatMode.AdvancedMonthly(advancedMonthDays)
                                advancedMonths.isNotEmpty() && advancedMonthDays.isNotEmpty() -> TaskRepeatMode.AdvancedYearly(advancedMonths, advancedMonthDays)
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

// 获取今天0点时间戳
private fun getTodayStart(): Long {
    return Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

// 比较两个日期是否同一天（忽略时间）
private fun isSameDay(time1: Long, time2: Long): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = time1 }
    val cal2 = Calendar.getInstance().apply { timeInMillis = time2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
           cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
           cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
}

@Composable
private fun CalendarView(
    selectedDate: Long,
    displayCalendar: Calendar,
    onDisplayCalendarChange: (Calendar) -> Unit,
    onDateSelected: (Long) -> Unit
) {
    val currentYear = displayCalendar.get(Calendar.YEAR)
    val currentMonth = displayCalendar.get(Calendar.MONTH)
    
    // 计算当月天数和偏移
    val tempCal = displayCalendar.clone() as Calendar
    tempCal.set(Calendar.DAY_OF_MONTH, 1)
    val firstDayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK)
    val daysInMonth = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH)
    
    // 计算需要显示的下月天数（补齐最后一行）
    val totalCells = firstDayOfWeek - 1 + daysInMonth
    val remainingCells = (7 - (totalCells % 7)) % 7
    
    Column {
        // 年月选择器
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                val newCal = displayCalendar.clone() as Calendar
                newCal.add(Calendar.MONTH, -1)
                onDisplayCalendarChange(newCal)
            }) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "上月")
            }
            Text(
                text = "${currentYear}年${currentMonth + 1}月",
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = {
                val newCal = displayCalendar.clone() as Calendar
                newCal.add(Calendar.MONTH, 1)
                onDisplayCalendarChange(newCal)
            }) {
                Icon(Icons.Default.ChevronRight, contentDescription = "下月")
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 星期标题
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("日", "一", "二", "三", "四", "五", "六").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.caption,
                    fontWeight = FontWeight.Bold,
                    color = if (day == "日" || day == "六") MaterialTheme.colors.error else MaterialTheme.colors.onSurface
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 日期网格 - 包含上月末尾、当月、下月开头
        val days = mutableListOf<CalendarDay>()
        
        // 上月末尾天数（灰色）
        val prevCal = displayCalendar.clone() as Calendar
        prevCal.add(Calendar.MONTH, -1)
        val daysInPrevMonth = prevCal.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (i in (daysInPrevMonth - firstDayOfWeek + 2)..daysInPrevMonth) {
            val cal = prevCal.clone() as Calendar
            cal.set(Calendar.DAY_OF_MONTH, i)
            days.add(CalendarDay(cal.timeInMillis, i, false, true))
        }
        
        // 当月天数
        for (i in 1..daysInMonth) {
            val cal = displayCalendar.clone() as Calendar
            cal.set(Calendar.DAY_OF_MONTH, i)
            days.add(CalendarDay(cal.timeInMillis, i, true, false))
        }
        
        // 下月开头天数（灰色，补齐）
        if (remainingCells > 0) {
            val nextCal = displayCalendar.clone() as Calendar
            nextCal.add(Calendar.MONTH, 1)
            for (i in 1..remainingCells) {
                val cal = nextCal.clone() as Calendar
                cal.set(Calendar.DAY_OF_MONTH, i)
                days.add(CalendarDay(cal.timeInMillis, i, false, true))
            }
        }
        
        val rows = days.chunked(7)
        rows.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEach { day ->
                    val isSelected = isSameDay(day.timeInMillis, selectedDate)
                    val isToday = isSameDay(day.timeInMillis, getTodayStart())
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isSelected -> MaterialTheme.colors.primary
                                    isToday && !day.isGray -> MaterialTheme.colors.primary.copy(alpha = 0.2f)
                                    else -> Color.Transparent
                                }
                            )
                            .then(
                                if (!day.isGray) Modifier.clickable { onDateSelected(day.timeInMillis) }
                                else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day.day.toString(),
                            color = when {
                                isSelected -> Color.White
                                day.isGray -> MaterialTheme.colors.onSurface.copy(alpha = 0.3f)
                                isToday -> MaterialTheme.colors.primary
                                else -> MaterialTheme.colors.onSurface
                            },
                            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

// 日历日期数据类
data class CalendarDay(
    val timeInMillis: Long,
    val day: Int,
    val isCurrentMonth: Boolean,
    val isGray: Boolean
)

@Composable
private fun ModeSelector(selectedMode: DatePickerMode, onModeSelected: (DatePickerMode) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        ModeTab("独立日期", selectedMode == DatePickerMode.ONE_TIME) { onModeSelected(DatePickerMode.ONE_TIME) }
        ModeTab("简单重复", selectedMode == DatePickerMode.SIMPLE_REPEAT) { onModeSelected(DatePickerMode.SIMPLE_REPEAT) }
        ModeTab("高级重复", selectedMode == DatePickerMode.ADVANCED_REPEAT) { onModeSelected(DatePickerMode.ADVANCED_REPEAT) }
    }
}

@Composable
private fun ModeTab(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onClick)) {
        Text(text, style = MaterialTheme.typography.body2, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface.copy(alpha = 0.6f))
        if (isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(modifier = Modifier.width(24.dp).height(2.dp).background(MaterialTheme.colors.primary))
        }
    }
}

@Composable
private fun SimpleRepeatPicker(selectedType: SimpleRepeatType, onTypeSelected: (SimpleRepeatType) -> Unit) {
    val options = listOf(Pair(SimpleRepeatType.DAILY, "每天"), Pair(SimpleRepeatType.WEEKLY, "每周"), Pair(SimpleRepeatType.MONTHLY, "每月"), Pair(SimpleRepeatType.YEARLY, "每年"))
    Column {
        Text("重复频率", style = MaterialTheme.typography.caption, color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f))
        Spacer(modifier = Modifier.height(8.dp))
        options.forEach { (type, label) ->
            val isSelected = type == selectedType
            Row(modifier = Modifier.fillMaxWidth().clickable { onTypeSelected(type) }.padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = isSelected, onClick = { onTypeSelected(type) })
                Spacer(modifier = Modifier.width(8.dp))
                Text(label, style = MaterialTheme.typography.body1)
            }
        }
    }
}

@Composable
private fun AdvancedRepeatPicker(weekDays: List<Int>, onWeekDaysChange: (List<Int>) -> Unit, monthDays: List<Int>, onMonthDaysChange: (List<Int>) -> Unit, months: List<Int>, onMonthsChange: (List<Int>) -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf("每周", "每月", "每年").forEachIndexed { index, label ->
                TextButton(onClick = { selectedTab = index }, colors = ButtonDefaults.textButtonColors(contentColor = if (selectedTab == index) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface.copy(alpha = 0.6f))) {
                    Text(label, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal)
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        when (selectedTab) {
            0 -> WeekDayPicker(selectedDays = weekDays, onDaysChange = onWeekDaysChange)
            1 -> MonthDayPicker(selectedDays = monthDays, onDaysChange = onMonthDaysChange)
            2 -> YearMonthPicker(selectedMonths = months, onMonthsChange = onMonthsChange, selectedDays = monthDays, onDaysChange = onMonthDaysChange)
        }
    }
}

@Composable
private fun WeekDayPicker(selectedDays: List<Int>, onDaysChange: (List<Int>) -> Unit) {
    val weekDays = listOf(Pair(1, "一"), Pair(2, "二"), Pair(3, "三"), Pair(4, "四"), Pair(5, "五"), Pair(6, "六"), Pair(7, "日"))
    Column {
        Text("选择每周的哪一天（可多选）", style = MaterialTheme.typography.caption, color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f))
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            weekDays.forEach { (day, label) ->
                val isSelected = selectedDays.contains(day)
                DayChip(label = label, isSelected = isSelected, onClick = { onDaysChange(if (isSelected) selectedDays - day else (selectedDays + day).sorted()) })
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { onDaysChange(listOf(1, 2, 3, 4, 5)) }, modifier = Modifier.weight(1f)) { Text("工作日") }
            OutlinedButton(onClick = { onDaysChange(listOf(6, 7)) }, modifier = Modifier.weight(1f)) { Text("周末") }
            OutlinedButton(onClick = { onDaysChange(listOf(1, 2, 3, 4, 5, 6, 7)) }, modifier = Modifier.weight(1f)) { Text("每天") }
        }
    }
}

@Composable
private fun MonthDayPicker(selectedDays: List<Int>, onDaysChange: (List<Int>) -> Unit) {
    Column {
        Text("选择每月的哪一天（可多选）", style = MaterialTheme.typography.caption, color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f))
        Spacer(modifier = Modifier.height(8.dp))
        val rows = (1..31).chunked(7)
        rows.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                row.forEach { day ->
                    val isSelected = selectedDays.contains(day)
                    DayChip(label = day.toString(), isSelected = isSelected, onClick = { onDaysChange(if (isSelected) selectedDays - day else (selectedDays + day).sorted()) }, modifier = Modifier.weight(1f))
                }
                repeat(7 - row.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun YearMonthPicker(selectedMonths: List<Int>, onMonthsChange: (List<Int>) -> Unit, selectedDays: List<Int>, onDaysChange: (List<Int>) -> Unit) {
    val months = (1..12).map { it to "${it}月" }
    Column {
        Text("选择月份（可多选）", style = MaterialTheme.typography.caption, color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f))
        Spacer(modifier = Modifier.height(8.dp))
        val rows = months.chunked(4)
        rows.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { (month, label) ->
                    val isSelected = selectedMonths.contains(month)
                    DayChip(label = label, isSelected = isSelected, onClick = { onMonthsChange(if (isSelected) selectedMonths - month else (selectedMonths + month).sorted()) }, modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        Spacer(modifier = Modifier.height(16.dp))
        Text("选择日期", style = MaterialTheme.typography.caption, color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f))
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(1, 15).forEach { day ->
                val isSelected = selectedDays.contains(day)
                OutlinedButton(onClick = { onDaysChange(if (isSelected) selectedDays - day else (selectedDays + day).sorted()) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.outlinedButtonColors(backgroundColor = if (isSelected) MaterialTheme.colors.primary.copy(alpha = 0.1f) else MaterialTheme.colors.surface)) {
                    Text("${day}日")
                }
            }
        }
    }
}

@Composable
private fun DayChip(label: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(40.dp).clip(CircleShape).background(if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.surface).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Text(text = label, color = if (isSelected) Color.White else MaterialTheme.colors.onSurface, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, style = MaterialTheme.typography.body2)
    }
}

enum class DatePickerMode { ONE_TIME, SIMPLE_REPEAT, ADVANCED_REPEAT }

enum class SimpleRepeatType { DAILY, WEEKLY, MONTHLY, YEARLY }

sealed class TaskRepeatMode {
    object OneTime : TaskRepeatMode()
    data class Simple(val type: SimpleRepeatType) : TaskRepeatMode()
    data class AdvancedWeekly(val days: List<Int>) : TaskRepeatMode()
    data class AdvancedMonthly(val days: List<Int>) : TaskRepeatMode()
    data class AdvancedYearly(val months: List<Int>, val days: List<Int>) : TaskRepeatMode()
}
