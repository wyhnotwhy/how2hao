package ui.location

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
import androidx.compose.ui.unit.dp
import data.model.City
import data.model.Province
import data.model.UserLocation
import data.repository.LocationRepository
import ui.components.CenteredTopBar

/**
 * 地区选择页面
 * 包含省份列表、城市列表、字母索引
 */
@Composable
fun LocationPickerScreen(
    currentLocation: UserLocation,
    onLocationSelected: (UserLocation) -> Unit,
    onBackClick: () -> Unit
) {
    var selectedProvince by remember { mutableStateOf<Province?>(null) }
    var selectedLetter by remember { mutableStateOf<Char?>(null) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            CenteredTopBar(
                title = if (selectedProvince == null) "选择省份" else "选择城市",
                onBackClick = if (selectedProvince != null) {
                    { selectedProvince = null }
                } else onBackClick
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // 定位权限按钮
                Button(
                    onClick = { showPermissionDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.secondary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("获取当前定位")
                }
                
                // 内容区域
                if (selectedProvince == null) {
                    // 省份列表
                    ProvinceList(
                        selectedLetter = selectedLetter,
                        onProvinceSelected = { selectedProvince = it },
                        onLetterSelected = { selectedLetter = it }
                    )
                } else {
                    // 城市列表
                    CityList(
                        province = selectedProvince!!,
                        onCitySelected = { city ->
                            onLocationSelected(
                                UserLocation(
                                    province = selectedProvince!!.name,
                                    city = city.name
                                )
                            )
                        }
                    )
                }
            }
            
            // 字母索引（仅在省份列表显示）
            if (selectedProvince == null) {
                AlphabetIndexBar(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    selectedLetter = selectedLetter,
                    onLetterSelected = { selectedLetter = it }
                )
            }
        }
    }
    
    // 定位权限对话框
    if (showPermissionDialog) {
        LocationPermissionDialog(
            onConfirm = {
                showPermissionDialog = false
                // 模拟获取定位
                onLocationSelected(UserLocation("上海市", "上海市"))
            },
            onDismiss = { showPermissionDialog = false }
        )
    }
}

/**
 * 省份列表
 */
@Composable
fun ProvinceList(
    selectedLetter: Char?,
    onProvinceSelected: (Province) -> Unit,
    onLetterSelected: (Char?) -> Unit
) {
    val provinces = if (selectedLetter != null) {
        LocationRepository.getProvincesByLetter(selectedLetter)
    } else {
        LocationRepository.getAllProvinces()
    }
    
    LazyColumn {
        if (selectedLetter != null) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "字母 $selectedLetter",
                        style = MaterialTheme.typography.subtitle2,
                        color = MaterialTheme.colors.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = { onLetterSelected(null) }) {
                        Text("显示全部")
                    }
                }
            }
        }
        
        items(provinces) { province ->
            LocationListItem(
                name = province.name,
                onClick = { onProvinceSelected(province) }
            )
        }
    }
}

/**
 * 城市列表
 */
@Composable
fun CityList(
    province: Province,
    onCitySelected: (City) -> Unit
) {
    LazyColumn {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.primary.copy(alpha = 0.1f))
                    .padding(16.dp)
            ) {
                Text(
                    text = province.name,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.primary
                )
            }
        }
        
        items(province.cities) { city ->
            LocationListItem(
                name = city.name,
                onClick = { onCitySelected(city) }
            )
        }
    }
}

/**
 * 字母索引条
 */
@Composable
fun AlphabetIndexBar(
    modifier: Modifier = Modifier,
    selectedLetter: Char?,
    onLetterSelected: (Char?) -> Unit
) {
    val letters = LocationRepository.getIndexLetters()
    
    Column(
        modifier = modifier
            .padding(end = 8.dp)
            .background(
                color = MaterialTheme.colors.surface,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(vertical = 8.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        letters.forEach { letter ->
            val isSelected = letter == selectedLetter
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) MaterialTheme.colors.primary
                        else Color.Transparent
                    )
                    .clickable { 
                        onLetterSelected(if (isSelected) null else letter)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = letter.toString(),
                    style = MaterialTheme.typography.caption,
                    color = if (isSelected) MaterialTheme.colors.onPrimary
                           else MaterialTheme.colors.primary,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

/**
 * 地区列表项
 */
@Composable
fun LocationListItem(
    name: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colors.onSurface.copy(alpha = 0.3f)
        )
    }
    
    Divider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
    )
}

/**
 * 定位权限对话框
 */
@Composable
fun LocationPermissionDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("申请定位权限") },
        text = { Text("需要获取您的位置信息以提供精准服务。是否允许？") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("允许")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("拒绝")
            }
        }
    )
}
