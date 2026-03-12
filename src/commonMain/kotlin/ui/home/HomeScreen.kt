package ui.home

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
import data.model.UserLocation
import ui.components.CenteredTopBar

@Composable
fun HomeScreen(
    currentLocation: UserLocation,
    onLocationClick: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Scaffold(
        topBar = {
            HomeTopBar(
                location = currentLocation,
                onLocationClick = onLocationClick
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item { FinancialOverviewCard() }
            item { TodoSection() }
            stickyHeader { PostListHeader() }
            items(getMockPosts()) { post -> PostCard(post = post) }
        }
    }
}

@Composable
fun HomeTopBar(location: UserLocation, onLocationClick: () -> Unit) {
    TopAppBar(
        title = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(text = "首页", fontWeight = FontWeight.Bold)
            }
        },
        navigationIcon = {
            Surface(
                modifier = Modifier.padding(start = 8.dp).clickable(onClick = onLocationClick),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colors.primary.copy(alpha = 0.1f),
                elevation = 0.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "定位",
                        tint = MaterialTheme.colors.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = location.city.take(3),
                        color = MaterialTheme.colors.primary,
                        style = MaterialTheme.typography.caption,
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }
            }
        }
    )
}

@Composable
fun FinancialOverviewCard() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp).heightIn(min = 180.dp, max = 220.dp),
        elevation = 4.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(text = "财务概览", style = MaterialTheme.typography.h6, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                FinancialCategory(title = "支出", amount = "¥2,580.00", color = Color(0xFF4CAF50),
                    categories = listOf("年费", "活动", "损耗", "手续费"))
                Divider(modifier = Modifier.height(80.dp).width(1.dp), color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f))
                FinancialCategory(title = "收入", amount = "¥5,280.00", color = Color(0xFFE53935),
                    categories = listOf("卡券出售", "帮充代充", "支付立减", "立减金"))
            }
        }
    }
}

@Composable
fun FinancialCategory(title: String, amount: String, color: Color, categories: List<String>) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, style = MaterialTheme.typography.caption, color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = amount, style = MaterialTheme.typography.h6, fontWeight = FontWeight.Bold, color = color)
        Spacer(modifier = Modifier.height(8.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            categories.forEach { CategoryChip(text = it) }
        }
    }
}

@Composable
fun CategoryChip(text: String) {
    Surface(modifier = Modifier.padding(2.dp), shape = RoundedCornerShape(12.dp), color = MaterialTheme.colors.surface, elevation = 1.dp) {
        Text(text = text, style = MaterialTheme.typography.caption, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
    }
}

@Composable
fun TodoSection() {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).heightIn(min = 180.dp, max = 220.dp), elevation = 2.dp, shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "待办事项", style = MaterialTheme.typography.h6, fontWeight = FontWeight.Bold)
                Row {
                    TodoTab(text = "今天", isSelected = true)
                    Spacer(modifier = Modifier.width(16.dp))
                    TodoTab(text = "明天", isSelected = false)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            listOf("完成项目文档编写", "团队周会 14:00", "代码审查", "客户沟通").forEach { TodoItem(text = it) }
        }
    }
}

@Composable
fun TodoTab(text: String, isSelected: Boolean) {
    Text(text = text, style = MaterialTheme.typography.body2, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
        color = if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface.copy(alpha = 0.5f))
}

@Composable
fun TodoItem(text: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = false, onCheckedChange = {}, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, style = MaterialTheme.typography.body2, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun PostListHeader() {
    Surface(color = MaterialTheme.colors.background, elevation = 4.dp) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = "社区动态", style = MaterialTheme.typography.h6, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            TextButton(onClick = { }) { Text("查看更多") }
        }
    }
}

@Composable
fun PostCard(post: Post) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), elevation = 2.dp, shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(text = post.title, style = MaterialTheme.typography.subtitle1, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.width(8.dp))
                Surface(shape = RoundedCornerShape(4.dp), color = MaterialTheme.colors.primary.copy(alpha = 0.1f)) {
                    Text(text = post.tag, style = MaterialTheme.typography.caption, color = MaterialTheme.colors.primary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = post.content, style = MaterialTheme.typography.body2, color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f), maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(MaterialTheme.colors.primary)) {
                    Text(text = post.author.take(1), color = Color.White, fontSize = 12.sp, modifier = Modifier.align(Alignment.Center))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = post.author, style = MaterialTheme.typography.caption, color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f))
                Spacer(modifier = Modifier.width(16.dp))
                Icon(imageVector = Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colors.onSurface.copy(alpha = 0.4f))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = post.time, style = MaterialTheme.typography.caption, color = MaterialTheme.colors.onSurface.copy(alpha = 0.4f))
                Spacer(modifier = Modifier.weight(1f))
                Icon(imageVector = Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFE53935))
                Spacer(modifier = Modifier.width(2.dp))
                Text(text = post.likes.toString(), style = MaterialTheme.typography.caption, color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f))
                Spacer(modifier = Modifier.width(12.dp))
                Icon(imageVector = Icons.Default.Comment, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colors.onSurface.copy(alpha = 0.4f))
                Spacer(modifier = Modifier.width(2.dp))
                Text(text = post.comments.toString(), style = MaterialTheme.typography.caption, color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f))
            }
        }
    }
}

data class Post(val id: String, val title: String, val content: String, val author: String, val time: String, val tag: String, val likes: Int, val comments: Int)

fun getMockPosts(): List<Post> = listOf(
    Post("1", "分享一个省钱小技巧", "最近发现通过帮充代充可以省下不少手续费...", "省钱达人", "10分钟前", "经验分享", 128, 32),
    Post("2", "支付立减活动汇总", "整理了近期各大平台的支付立减活动...", "活动搜集官", "30分钟前", "活动", 256, 45),
    Post("3", "卡券出售注意事项", "在出售卡券时一定要注意安全...", "安全小助手", "1小时前", "安全", 89, 18),
    Post("4", "立减金使用攻略", "很多人拿到了立减金却不知道如何使用...", "攻略大师", "2小时前", "攻略", 167, 28),
    Post("5", "年费会员值得开吗？", "最近很多平台都在推年费会员...", "理性消费者", "3小时前", "讨论", 234, 67),
    Post("6", "损耗怎么降到最低？", "在做帮充代充的时候，总会遇到一些损耗...", "效率专家", "5小时前", "技巧", 145, 41)
)
