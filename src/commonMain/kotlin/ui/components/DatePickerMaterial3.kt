package ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// Material3 DatePicker 封装
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialogMaterial3(
    onDismiss: () -> Unit,
    onDateSelected: (Long?, TaskRepeatModeM3) -> Unit,
    initialDate: Long? = null
) {
    var selectedMode by remember { mutableStateOf(DatePickerModeM3.ONE_TIME) }
    var selectedDate by remember { mutableStateOf(initialDate ?: System.currentTimeMillis()) }
    var simpleRepeatType by remember { mutableStateOf(SimpleRepeatTypeM3.DAILY) }
    
    // Material3 DatePicker 状态
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择日期", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 600.dp)
            ) {
                // 模式选择标签
                ModeSelectorM3(
                    selectedMode = selectedMode,
                    onModeSelected = { selectedMode = it }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                HorizontalDivider()
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 根据模式显示不同内容
                when (selectedMode) {
                    DatePickerModeM3.ONE_TIME -> {
                        // 使用 Material3 DatePicker
                        DatePicker(
                            state = datePickerState,
                            modifier = Modifier.fillMaxWidth(),
                            title = null,
                            headline = null,
                            showModeToggle = false
                        )
                    }
                    DatePickerModeM3.SIMPLE_REPEAT -> {
                        SimpleRepeatPickerM3(
                            selectedType = simpleRepeatType,
                            onTypeSelected = { simpleRepeatType = it }
                        )
                    }
                    DatePickerModeM3.ADVANCED_REPEAT -> {
                        Text("高级重复功能待实现", modifier = Modifier.padding(16.dp))
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when (selectedMode) {
                        DatePickerModeM3.ONE_TIME -> {
                            datePickerState.selectedDateMillis?.let {
                                selectedDate = it
                            }
                            onDateSelected(selectedDate, TaskRepeatModeM3.OneTime)
                        }
                        DatePickerModeM3.SIMPLE_REPEAT -> {
                            onDateSelected(null, TaskRepeatModeM3.Simple(simpleRepeatType))
                        }
                        else -> onDateSelected(null, TaskRepeatModeM3.OneTime)
                    }
                }
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
private fun ModeSelectorM3(
    selectedMode: DatePickerModeM3,
    onModeSelected: (DatePickerModeM3) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ModeTabM3(
            text = "独立日期",
            isSelected = selectedMode == DatePickerModeM3.ONE_TIME,
            onClick = { onModeSelected(DatePickerModeM3.ONE_TIME) }
        )
        ModeTabM3(
            text = "简单重复",
            isSelected = selectedMode == DatePickerModeM3.SIMPLE_REPEAT,
            onClick = { onModeSelected(DatePickerModeM3.SIMPLE_REPEAT) }
        )
        ModeTabM3(
            text = "高级重复",
            isSelected = selectedMode == DatePickerModeM3.ADVANCED_REPEAT,
            onClick = { onModeSelected(DatePickerModeM3.ADVANCED_REPEAT) }
        )
    }
}

@Composable
private fun ModeTabM3(
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
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        if (isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(24.dp)
                    .height(2.dp)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
private fun SimpleRepeatPickerM3(
    selectedType: SimpleRepeatTypeM3,
    onTypeSelected: (SimpleRepeatTypeM3) -> Unit
) {
    val options = listOf(
        Pair(SimpleRepeatTypeM3.DAILY, "每天"),
        Pair(SimpleRepeatTypeM3.WEEKLY, "每周"),
        Pair(SimpleRepeatTypeM3.MONTHLY, "每月"),
        Pair(SimpleRepeatTypeM3.YEARLY, "每年")
    )
    
    Column {
        Text(
            text = "重复频率",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
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
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

// 数据类
enum class DatePickerModeM3 {
    ONE_TIME,
    SIMPLE_REPEAT,
    ADVANCED_REPEAT
}

enum class SimpleRepeatTypeM3 {
    DAILY, WEEKLY, MONTHLY, YEARLY
}

sealed class TaskRepeatModeM3 {
    object OneTime : TaskRepeatModeM3()
    data class Simple(val type: SimpleRepeatTypeM3) : TaskRepeatModeM3()
    data class AdvancedWeekly(val days: List<Int>) : TaskRepeatModeM3()
    data class AdvancedMonthly(val days: List<Int>) : TaskRepeatModeM3()
    data class AdvancedYearly(val months: List<Int>, val days: List<Int>) : TaskRepeatModeM3()
}
