package ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onTimeSelected: (String) -> Unit,
    initialHour: Int = 9,
    initialMinute: Int = 0
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择时间") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TimePicker(
                    state = timePickerState,
                    modifier = Modifier.padding(16.dp)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val hour = timePickerState.hour.toString().padStart(2, '0')
                    val minute = timePickerState.minute.toString().padStart(2, '0')
                    onTimeSelected("$hour:$minute")
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
fun TimeSelectorButton(
    selectedTime: String,
    onTimeSelected: (String) -> Unit,
    label: String = "提醒时间"
) {
    var showTimePicker by remember { mutableStateOf(false) }
    
    val (initialHour, initialMinute) = remember(selectedTime) {
        if (selectedTime.isNotBlank() && selectedTime.contains(":")) {
            val parts = selectedTime.split(":")
            Pair(parts[0].toIntOrNull() ?: 9, parts[1].toIntOrNull() ?: 0)
        } else {
            Pair(9, 0)
        }
    }
    
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedButton(
            onClick = { showTimePicker = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (selectedTime.isNotBlank()) selectedTime else "选择时间",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
    
    if (showTimePicker) {
        TimePickerDialog(
            onDismiss = { showTimePicker = false },
            onTimeSelected = { time ->
                onTimeSelected(time)
                showTimePicker = false
            },
            initialHour = initialHour,
            initialMinute = initialMinute
        )
    }
}
