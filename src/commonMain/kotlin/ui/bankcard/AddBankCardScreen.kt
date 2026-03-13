package ui.bankcard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import data.model.AddBankCardRequest
import data.model.Bank
import data.model.BankCardType
import data.model.City
import data.model.Province
import data.repository.BankCardRepository
import data.repository.BankRepository
import data.repository.LocationRepository

/**
 * 添加银行卡页面
 */
@Composable
fun AddBankCardScreen(
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    var selectedBank by remember { mutableStateOf<Bank?>(null) }
    var selectedCardType by remember { mutableStateOf<BankCardType?>(null) }
    var selectedProvince by remember { mutableStateOf<Province?>(null) }
    var selectedCity by remember { mutableStateOf<City?>(null) }
    var cardLastFour by remember { mutableStateOf("") }
    var creditLimit by remember { mutableStateOf("") }
    var annualFee by remember { mutableStateOf("") }

    var showBankPicker by remember { mutableStateOf(false) }
    var showCardTypePicker by remember { mutableStateOf(false) }
    var showProvincePicker by remember { mutableStateOf(false) }
    var showCityPicker by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    // 验证表单
    fun validateForm(): Boolean {
        return when {
            selectedBank == null -> {
                errorMessage = "请选择所属银行"
                false
            }
            selectedCardType == null -> {
                errorMessage = "请选择卡片类型"
                false
            }
            selectedProvince == null || selectedCity == null -> {
                errorMessage = "请选择归属地"
                false
            }
            cardLastFour.length != 4 || !cardLastFour.all { it.isDigit() } -> {
                errorMessage = "请输入正确的卡号后4位"
                false
            }
            selectedCardType == BankCardType.CREDIT && creditLimit.isBlank() -> {
                errorMessage = "请输入信用卡额度"
                false
            }
            else -> true
        }
    }

    // 保存银行卡
    fun saveBankCard() {
        if (!validateForm()) return

        val request = AddBankCardRequest(
            bankId = selectedBank!!.id,
            bankName = selectedBank!!.name,
            cardType = selectedCardType!!,
            provinceCode = selectedProvince!!.code,
            provinceName = selectedProvince!!.name,
            cityCode = selectedCity!!.code,
            cityName = selectedCity!!.name,
            cardLastFour = cardLastFour,
            creditLimit = if (selectedCardType == BankCardType.CREDIT) {
                creditLimit.toDoubleOrNull()
            } else null,
            annualFee = if (selectedCardType == BankCardType.CREDIT) {
                annualFee.toDoubleOrNull() ?: 0.0
            } else 0.0
        )

        BankCardRepository.addBankCard(request)
        onSaveSuccess()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("添加银行卡") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                elevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { saveBankCard() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(48.dp)
                ) {
                    Text("保存")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 所属银行
            FormField(
                label = "所属银行",
                value = selectedBank?.name ?: "请选择",
                isSelected = selectedBank != null,
                onClick = { showBankPicker = true },
                leadingIcon = Icons.Default.AccountBalance
            )

            // 卡片类型
            FormField(
                label = "卡片类型",
                value = selectedCardType?.let { 
                    when(it) {
                        BankCardType.DEBIT -> "借记卡"
                        BankCardType.CREDIT -> "信用卡"
                    }
                } ?: "请选择",
                isSelected = selectedCardType != null,
                onClick = { showCardTypePicker = true },
                leadingIcon = Icons.Default.CreditCard
            )

            // 归属地
            FormField(
                label = "归属地",
                value = if (selectedProvince != null && selectedCity != null) {
                    "${selectedProvince!!.name} ${selectedCity!!.name}"
                } else "请选择",
                isSelected = selectedProvince != null && selectedCity != null,
                onClick = { showProvincePicker = true },
                leadingIcon = Icons.Default.LocationOn
            )

            // 卡号后4位
            OutlinedTextField(
                value = cardLastFour,
                onValueChange = { 
                    if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                        cardLastFour = it
                    }
                },
                label = { Text("卡号后4位") },
                leadingIcon = {
                    Icon(Icons.Default.Numbers, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 信用卡特有字段
            if (selectedCardType == BankCardType.CREDIT) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 2.dp,
                    backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.05f)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "信用卡信息",
                            style = MaterialTheme.typography.subtitle2,
                            color = MaterialTheme.colors.primary,
                            fontWeight = FontWeight.Bold
                        )

                        // 额度
                        OutlinedTextField(
                            value = creditLimit,
                            onValueChange = { creditLimit = it },
                            label = { Text("额度（元）") },
                            leadingIcon = {
                                Icon(Icons.Default.Money, contentDescription = null)
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Next
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        // 年费
                        OutlinedTextField(
                            value = annualFee,
                            onValueChange = { annualFee = it },
                            label = { Text("年费（元，默认0）") },
                            leadingIcon = {
                                Icon(Icons.Default.Payment, contentDescription = null)
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Done
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // 银行选择对话框
    if (showBankPicker) {
        BankPickerDialog(
            onDismiss = { showBankPicker = false },
            onBankSelected = { 
                selectedBank = it
                showBankPicker = false
            }
        )
    }

    // 卡片类型选择对话框
    if (showCardTypePicker) {
        CardTypePickerDialog(
            onDismiss = { showCardTypePicker = false },
            onCardTypeSelected = { 
                selectedCardType = it
                showCardTypePicker = false
            }
        )
    }

    // 省份选择对话框
    if (showProvincePicker) {
        ProvincePickerDialog(
            onDismiss = { showProvincePicker = false },
            onProvinceSelected = { province ->
                selectedProvince = province
                selectedCity = null
                showProvincePicker = false
                showCityPicker = true
            }
        )
    }

    // 城市选择对话框
    if (showCityPicker && selectedProvince != null) {
        CityPickerDialog(
            province = selectedProvince!!,
            onDismiss = { showCityPicker = false },
            onCitySelected = { city ->
                selectedCity = city
                showCityPicker = false
            }
        )
    }

    // 错误提示
    errorMessage?.let { message ->
        LaunchedEffect(message) {
            // 可以在这里显示Toast或Snackbar
            errorMessage = null
        }
    }
}

/**
 * 表单字段
 */
@Composable
private fun FormField(
    label: String,
    value: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = if (isSelected) 
                    MaterialTheme.colors.primary 
                else 
                    MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
            ),
            elevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = if (isSelected) 
                        MaterialTheme.colors.primary 
                    else 
                        MaterialTheme.colors.onSurface.copy(alpha = 0.4f)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = value,
                    style = MaterialTheme.typography.body1,
                    color = if (isSelected) 
                        MaterialTheme.colors.onSurface 
                    else 
                        MaterialTheme.colors.onSurface.copy(alpha = 0.4f),
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colors.onSurface.copy(alpha = 0.3f)
                )
            }
        }
    }
}

/**
 * 银行选择对话框
 */
@Composable
private fun BankPickerDialog(
    onDismiss: () -> Unit,
    onBankSelected: (Bank) -> Unit
) {
    val banks = remember { BankRepository.getAllBanks() }
    var searchQuery by remember { mutableStateOf("") }
    val filteredBanks = remember(searchQuery) {
        if (searchQuery.isBlank()) banks
        else banks.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择银行") },
        text = {
            Column {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("搜索银行") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.heightIn(max = 300.dp)
                ) {
                    items(filteredBanks) { bank ->
                        BankPickerItem(
                            bank = bank,
                            onClick = { onBankSelected(bank) }
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

/**
 * 银行选择项
 */
@Composable
private fun BankPickerItem(
    bank: Bank,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 银行首字母图标
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colors.primary.copy(alpha = 0.1f),
            modifier = Modifier.size(40.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = bank.name.take(1),
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = bank.name,
                style = MaterialTheme.typography.body1
            )
            Text(
                text = bank.code,
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

/**
 * 卡片类型选择对话框
 */
@Composable
private fun CardTypePickerDialog(
    onDismiss: () -> Unit,
    onCardTypeSelected: (BankCardType) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择卡片类型") },
        text = {
            Column {
                CardTypePickerItem(
                    icon = "💳",
                    title = "借记卡",
                    subtitle = "储蓄卡、工资卡等",
                    onClick = { onCardTypeSelected(BankCardType.DEBIT) }
                )

                Divider()

                CardTypePickerItem(
                    icon = "💎",
                    title = "信用卡",
                    subtitle = "可透支消费",
                    onClick = { onCardTypeSelected(BankCardType.CREDIT) }
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

/**
 * 卡片类型选择项
 */
@Composable
private fun CardTypePickerItem(
    icon: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.h5,
            modifier = Modifier.size(40.dp),
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

/**
 * 省份选择对话框
 */
@Composable
private fun ProvincePickerDialog(
    onDismiss: () -> Unit,
    onProvinceSelected: (Province) -> Unit
) {
    val provinces = remember { LocationRepository.getAllProvinces() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择省份") },
        text = {
            LazyColumn(
                modifier = Modifier.heightIn(max = 400.dp)
            ) {
                items(provinces) { province ->
                    ProvincePickerItem(
                        province = province,
                        onClick = { onProvinceSelected(province) }
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

/**
 * 省份选择项
 */
@Composable
private fun ProvincePickerItem(
    province: Province,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = province.name,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colors.onSurface.copy(alpha = 0.3f)
        )
    }
}

/**
 * 城市选择对话框
 */
@Composable
private fun CityPickerDialog(
    province: Province,
    onDismiss: () -> Unit,
    onCitySelected: (City) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择城市 - ${province.name}") },
        text = {
            LazyColumn(
                modifier = Modifier.heightIn(max = 400.dp)
            ) {
                items(province.cities) { city ->
                    CityPickerItem(
                        city = city,
                        onClick = { onCitySelected(city) }
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

/**
 * 城市选择项
 */
@Composable
private fun CityPickerItem(
    city: City,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = city.name,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colors.onSurface.copy(alpha = 0.3f)
        )
    }
}
