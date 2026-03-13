package ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import data.model.Bank
import data.repository.BankLogoRepository

/**
 * 银行Logo组件
 * 优先显示SVG图标，如果找不到则显示银行名称首字母
 */
@Composable
fun BankLogo(
    bank: Bank,
    modifier: Modifier = Modifier,
    size: Int = 40
) {
    val svgFile = remember(bank.id) {
        BankLogoRepository.getLogoByBankId(bank.id)
            ?: BankLogoRepository.getLogoByBankName(bank.name)
    }
    
    if (svgFile != null) {
        // 显示SVG图标
        BankSvgImage(
            svgFileName = svgFile,
            modifier = modifier.size(size.dp),
            contentDescription = bank.name
        )
    } else {
        // 显示银行首字母
        BankPlaceholder(
            bankName = bank.name,
            modifier = modifier.size(size.dp)
        )
    }
}

/**
 * SVG图片组件（跨平台实现）
 */
@Composable
expect fun BankSvgImage(
    svgFileName: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
)

/**
 * 银行占位符（显示首字母）
 */
@Composable
fun BankPlaceholder(
    bankName: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.background(
            MaterialTheme.colors.primary.copy(alpha = 0.2f),
            shape = androidx.compose.foundation.shape.CircleShape
        )
        Text(
            text = bankName.take(1),
            style = MaterialTheme.typography.h6,
            color = MaterialTheme.colors.primary,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
    }
}
