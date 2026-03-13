package ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import data.model.Bank
import data.repository.BankLogoRepository

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
        BankSvgImage(
            svgFileName = svgFile,
            modifier = modifier.size(size.dp),
            contentDescription = bank.name
        )
    } else {
        BankPlaceholder(
            bankName = bank.name,
            modifier = modifier.size(size.dp)
        )
    }
}

@Composable
expect fun BankSvgImage(
    svgFileName: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
)

@Composable
fun BankPlaceholder(
    bankName: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(MaterialTheme.colors.primary.copy(alpha = 0.2f), shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = bankName.take(1),
            style = MaterialTheme.typography.h6,
            color = MaterialTheme.colors.primary,
            fontWeight = FontWeight.Bold
        )
    }
}
