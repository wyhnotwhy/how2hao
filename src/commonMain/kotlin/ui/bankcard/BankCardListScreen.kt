package ui.bankcard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import data.model.BankCard
import data.model.BankCardType
import data.repository.BankCardRepository

/**
 * 银行卡列表页面
 * 顶部标题区右侧有添加按钮，左侧是返回按钮
 */
@Composable
fun BankCardListScreen(
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onCardClick: (BankCard) -> Unit = {}
) {
    val bankCards by BankCardRepository.bankCards.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("我的银行卡") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onAddClick) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "添加银行卡"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (bankCards.isEmpty()) {
                // 空状态
                EmptyBankCardState(onAddClick = onAddClick)
            } else {
                // 银行卡列表
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 统计信息
                    item {
                        BankCardStatistics(cards = bankCards)
                    }

                    // 银行卡列表
                    items(
                        items = bankCards.sortedByDescending { it.createdAt },
                        key = { it.id }
                    ) { card ->
                        BankCardItem(
                            card = card,
                            onClick = { onCardClick(card) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * 空状态展示
 */
@Composable
private fun EmptyBankCardState(onAddClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CreditCard,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colors.onSurface.copy(alpha = 0.3f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "还没有银行卡",
            style = MaterialTheme.typography.h6,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "点击右上角添加按钮添加您的第一张银行卡",
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.4f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onAddClick,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("添加银行卡")
        }
    }
}

/**
 * 银行卡统计信息
 */
@Composable
private fun BankCardStatistics(cards: List<BankCard>) {
    val creditCards = cards.filter { it.cardType == BankCardType.CREDIT }
    val debitCards = cards.filter { it.cardType == BankCardType.DEBIT }
    val totalCreditLimit = creditCards.sumOf { it.creditLimit ?: 0.0 }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 4.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatisticItem(
                icon = Icons.Default.CreditCard,
                value = cards.size.toString(),
                label = "总卡数"
            )
            StatisticItem(
                icon = Icons.Default.Money,
                value = creditCards.size.toString(),
                label = "信用卡"
            )
            StatisticItem(
                icon = Icons.Default.AccountBalance,
                value = debitCards.size.toString(),
                label = "借记卡"
            )
            if (totalCreditLimit > 0) {
                StatisticItem(
                    icon = Icons.Default.TrendingUp,
                    value = "${(totalCreditLimit / 10000).toInt()}万",
                    label = "总额度"
                )
            }
        }
    }
}

/**
 * 统计项
 */
@Composable
private fun StatisticItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colors.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
        )
    }
}

/**
 * 银行卡列表项
 */
@Composable
private fun BankCardItem(
    card: BankCard,
    onClick: () -> Unit
) {
    val cardColor = if (card.cardType == BankCardType.CREDIT) {
        Color(0xFF1E88E5) // 信用卡蓝色
    } else {
        Color(0xFF43A047) // 借记卡绿色
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = 4.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardColor)
                .padding(20.dp)
        ) {
            Column {
                // 银行名称和卡类型
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 银行图标占位
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = card.bankName.take(1),
                                color = Color.White,
                                style = MaterialTheme.typography.h6,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = card.bankName,
                                color = Color.White,
                                style = MaterialTheme.typography.subtitle1,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = BankCardRepository.getCardTypeDisplayName(card.cardType),
                                color = Color.White.copy(alpha = 0.8f),
                                style = MaterialTheme.typography.caption
                            )
                        }
                    }

                    // 卡类型标签
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = if (card.cardType == BankCardType.CREDIT) "💎" else "💳",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 卡号显示
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "**** **** **** ",
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.h6
                    )
                    Text(
                        text = card.cardLastFour,
                        color = Color.White,
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 归属地和额度信息
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 归属地
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${card.provinceName} ${card.cityName}",
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.caption
                        )
                    }

                    // 信用卡额度
                    if (card.cardType == BankCardType.CREDIT && card.creditLimit != null) {
                        Text(
                            text = "额度: ${card.creditLimit.toInt()}元",
                            color = Color.White.copy(alpha = 0.9f),
                            style = MaterialTheme.typography.caption
                        )
                    }
                }

                // 年费信息（仅信用卡显示）
                if (card.cardType == BankCardType.CREDIT && card.annualFee > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "年费: ${card.annualFee.toInt()}元",
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.caption
                    )
                }
            }
        }
    }
}
