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
import data.model.Bank
import data.model.TaskFrequency
import data.repository.BankRepository

/**
 * 添加任务页面
 */
@Composable
fun AddTaskScreen(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedFrequency by remember { mutableStateOf(TaskFrequency.DAILY) }
    var reminderTime by remember { mutableStateOf("") }
    var selectedBank by remember { mutableStateOf<Bank?>(null) }
    var showBankPicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "添加任务",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    TextButton(
                        onClick = onSaveClick,
                        enabled = title.isNotBlank()
                    ) {
                        Text("保存")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // 任务标题
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("任务标题") },
                placeholder = { Text("请输入任务标题") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 频次选择
            Text(
                text = "执行频次",
                style = MaterialTheme.typography.subtitle2,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            FrequencySelector(
                selectedFrequency = selectedFrequency,
                onFrequencySelected = { selectedFrequency = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 提醒时间
            Text(
                text = "提醒时间",
                style = MaterialTheme.typography.subtitle2,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            TimeSelector(
                selectedTime = reminderTime,
                onTimeSelected = { reminderTime = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 银行标签
            Text(
                text = "关联银行（可选）",
                style = MaterialTheme.typography.subtitle2,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            BankSelector(
                selectedBank = selectedBank,
                onBankSelected = { selectedBank = it },
                onShowBankPicker = { showBankPicker = true }
            )
        }

        // 银行选择弹窗
        if (showBankPicker) {
            BankPickerDialog(
                onDismiss = { showBankPicker = false },
                onBankSelected = {
                    selectedBank = it
                    showBankPicker = false
                }
            )
        }
    }
}

/**
 * 频次选择器
 */
@Composable
fun FrequencySelector(
    selectedFrequency: TaskFrequency,
    onFrequencySelected: (TaskFrequency) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FrequencyOption(
            text = "每天一次",
            isSelected = selectedFrequency == TaskFrequency.DAILY,
            onClick = { onFrequencySelected(TaskFrequency.DAILY) },
            modifier = Modifier.weight(1f)
        )
        FrequencyOption(
            text = "每周一次",
            isSelected = selectedFrequency == TaskFrequency.WEEKLY,
            onClick = { onFrequencySelected(TaskFrequency.WEEKLY) },
            modifier = Modifier.weight(1f)
        )
        FrequencyOption(
            text = "每月一次",
            isSelected = selectedFrequency == TaskFrequency.MONTHLY,
            onClick = { onFrequencySelected(TaskFrequency.MONTHLY) },
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * 频次选项
 */
@Composable
fun FrequencyOption(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected)
            MaterialTheme.colors.primary.copy(alpha = 0.15f)
        else
            MaterialTheme.colors.surface,
        border = if (isSelected)
            null
        else
            androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
            ),
        elevation = if (isSelected) 2.dp else 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.body2,
                color = if (isSelected)
                    MaterialTheme.colors.primary
                else
                    MaterialTheme.colors.onSurface,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

/**
 * 时间选择器
 */
@Composable
fun TimeSelector(
    selectedTime: String,
    onTimeSelected: (String) -> Unit
) {
    val timeOptions = listOf("08:00", "09:00", "10:00", "14:00", "15:00", "18:00", "20:00", "21:00")

    Column {
        // 快速选择 - 使用两行显示
        Column(modifier = Modifier.fillMaxWidth()) {
            // 第一行
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                timeOptions.take(4).forEach { time ->
                    TimeChip(
                        time = time,
                        isSelected = selectedTime == time,
                        onClick = { onTimeSelected(time) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            // 第二行
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                timeOptions.drop(4).forEach { time ->
                    TimeChip(
                        time = time,
                        isSelected = selectedTime == time,
                        onClick = { onTimeSelected(time) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 自定义时间输入
        OutlinedTextField(
            value = selectedTime,
            onValueChange = { 
                // 简单的时间格式验证
                if (it.length <= 5) onTimeSelected(it)
            },
            label = { Text("自定义时间 (HH:mm)") },
            placeholder = { Text("如: 09:30") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}

/**
 * 时间芯片
 */
@Composable
fun TimeChip(
    time: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected)
            MaterialTheme.colors.primary
        else
            MaterialTheme.colors.surface,
        border = if (!isSelected)
            androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
            )
        else null,
        elevation = if (isSelected) 2.dp else 0.dp
    ) {
        Text(
            text = time,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.body2,
            color = if (isSelected) Color.White else MaterialTheme.colors.onSurface
        )
    }
}

/**
 * 银行选择器
 */
@Composable
fun BankSelector(
    selectedBank: Bank?,
    onBankSelected: (Bank?) -> Unit,
    onShowBankPicker: () -> Unit
) {
    if (selectedBank != null) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                elevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 银行图标占位
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colors.primary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = selectedBank.name.take(1),
                            style = MaterialTheme.typography.h6,
                            color = MaterialTheme.colors.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = selectedBank.name,
                            style = MaterialTheme.typography.body1,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "代码: ${selectedBank.code}",
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = { onBankSelected(null) }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "清除",
                    tint = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    } else {
        OutlinedButton(
            onClick = onShowBankPicker,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.AccountBalance,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("选择银行")
        }
    }
}

/**
 * 银行选择弹窗
 */
@Composable
fun BankPickerDialog(
    onDismiss: () -> Unit,
    onBankSelected: (Bank) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val banks = remember { BankRepository.getAllBanks() }
    val filteredBanks = remember(searchQuery) {
        if (searchQuery.isBlank()) banks else banks.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.code.contains(searchQuery, ignoreCase = true)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("选择银行", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("搜索银行...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    }
                )
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                items(filteredBanks) { bank ->
                    BankListItem(
                        bank = bank,
                        onClick = { onBankSelected(bank) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        },
        modifier = Modifier.fillMaxWidth(0.9f)
    )
}

/**
 * 银行列表项
 */
@Composable
fun BankListItem(
    bank: Bank,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 银行图标占位
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colors.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = bank.name.take(1),
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.primary
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = bank.name,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = bank.code,
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}


