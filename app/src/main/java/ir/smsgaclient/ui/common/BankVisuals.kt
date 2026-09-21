// app/src/main/java/ir/smsgaclient/ui/common/BankVisuals.kt
package ir.smsgaclient.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.smsgaclient.ui.theme.BankAyandeh
import ir.smsgaclient.ui.theme.BankBlu
import ir.smsgaclient.ui.theme.BankDefault
import ir.smsgaclient.ui.theme.BankKeshavarzi
import ir.smsgaclient.ui.theme.BankMellat
import ir.smsgaclient.ui.theme.BankMelli
import ir.smsgaclient.ui.theme.BankParsian
import ir.smsgaclient.ui.theme.BankPasargad
import ir.smsgaclient.ui.theme.BankRefah
import ir.smsgaclient.ui.theme.BankSaderat
import ir.smsgaclient.ui.theme.BankSaman
import ir.smsgaclient.ui.theme.BankSepah
import ir.smsgaclient.ui.theme.BankShahr
import ir.smsgaclient.ui.theme.BankTejarat
import ir.smsgaclient.ui.theme.CardNumberStyle
import ir.smsgaclient.ui.theme.CockpitTypography
import ir.smsgaclient.ui.theme.StatusErrorContainer
import ir.smsgaclient.ui.theme.StatusErrorOnContainer
import ir.smsgaclient.ui.theme.StatusForwardingContainer
import ir.smsgaclient.ui.theme.StatusForwardingOnContainer
import ir.smsgaclient.ui.theme.StatusNeutralContainer
import ir.smsgaclient.ui.theme.StatusNeutralOnContainer
import ir.smsgaclient.ui.theme.StatusQueuedContainer
import ir.smsgaclient.ui.theme.StatusQueuedOnContainer
import ir.smsgaclient.ui.theme.TextPrimary
import ir.smsgaclient.util.PersianNumberFormatter

data class BankInfo(
    val nameFa: String,
    val shortNameFa: String,
    val primaryColor: Color,
    val secondaryColor: Color
)

fun getBankInfo(bankId: String): BankInfo {
    return when (bankId.lowercase().trim()) {
        "blu" -> BankInfo("بلوبانک", "بلو", BankBlu, Color(0xFF0055A5))
        "pasargad" -> BankInfo("بانک پاسارگاد", "پاس", BankPasargad, Color(0xFF1E1E1E))
        "saman" -> BankInfo("بانک سامان", "سام", BankSaman, Color(0xFF0D47A1))
        "mellat" -> BankInfo("بانک ملت", "ملت", BankMellat, Color(0xFF880E4F))
        "melli" -> BankInfo("بانک ملی ایران", "ملی", BankMelli, Color(0xFF003865))
        "sepah" -> BankInfo("بانک سپه", "سپه", BankSepah, Color(0xFF78350F))
        "tejarat" -> BankInfo("بانک تجارت", "تجار", BankTejarat, Color(0xFF01579B))
        "keshavarzi" -> BankInfo("بانک کشاورزی", "کشو", BankKeshavarzi, Color(0xFF14532D))
        "parsian" -> BankInfo("بانک پارسیان", "پارس", BankParsian, Color(0xFF4A044E))
        "refah" -> BankInfo("بانک رفاه کارگران", "رفاه", BankRefah, Color(0xFF064E3B))
        "shahr" -> BankInfo("بانک شهر", "شهر", BankShahr, Color(0xFF991B1B))
        "saderat" -> BankInfo("بانک صادرات", "صادر", BankSaderat, Color(0xFF1E3A8A))
        "ayandeh" -> BankInfo("بانک آینده", "آیند", BankAyandeh, Color(0xFF581C87))
        else -> BankInfo("بانک ${bankId.ifEmpty { "نامشخص" }}", "بانک", BankDefault, Color(0xFF1E293B))
    }
}

/**
 * High-contrast Bank Avatar Badge
 */
@Composable
fun BankAvatar(
    bankId: String,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp
) {
    val bankInfo = getBankInfo(bankId)
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    listOf(bankInfo.primaryColor, bankInfo.secondaryColor)
                )
            )
            .border(1.dp, Color(0x33FFFFFF), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = bankInfo.shortNameFa,
            style = CockpitTypography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

/**
 * Status Pill with accessible contrast tokens from ui-ux-pro-max
 */
@Composable
fun StatusPill(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (status.uppercase()) {
        "SENT", "SUCCESS", "CONFIRMED" -> Triple(StatusForwardingContainer, StatusForwardingOnContainer, "موفق")
        "PENDING", "RECEIVED", "PARSED", "QUEUED" -> Triple(StatusQueuedContainer, StatusQueuedOnContainer, "در صف")
        "FAILED", "ERROR" -> Triple(StatusErrorContainer, StatusErrorOnContainer, "خطا")
        else -> Triple(StatusNeutralContainer, StatusNeutralOnContainer, status)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, textColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = CockpitTypography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
    }
}

/**
 * EMV Chip simulation graphic for bank cards
 */
@Composable
fun EmvChipGraphic(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(width = 38.dp, height = 28.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFFE2B857))
            .border(1.dp, Color(0xFFC59B27), RoundedCornerShape(6.dp))
    ) {
        Box(
            modifier = Modifier
                .size(width = 24.dp, height = 18.dp)
                .align(Alignment.Center)
                .border(1.dp, Color(0xFF9E7B1D), RoundedCornerShape(3.dp))
        )
    }
}

/**
 * Realistic Iranian Bank Card presentation
 */
@Composable
fun IranianBankCard(
    bankId: String,
    cardLast4: String,
    holderName: String,
    modifier: Modifier = Modifier
) {
    val bankInfo = getBankInfo(bankId)
    val maskedDisplay = "•••• •••• •••• ${PersianNumberFormatter.toPersianDigits(cardLast4)}"

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        bankInfo.primaryColor,
                        bankInfo.secondaryColor,
                        Color(0xFF0A0F1D)
                    )
                )
            )
            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.matchParentSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header: Bank Name and EMV Chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = bankInfo.nameFa,
                    style = CockpitTypography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                EmvChipGraphic()
            }

            // Card Number centered (LTR formatted)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = maskedDisplay,
                    style = CardNumberStyle.copy(fontSize = 18.sp),
                    color = Color.White
                )
            }

            // Footer: Cardholder name & Persian banking label
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "صاحب کارت",
                        style = CockpitTypography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Text(
                        text = holderName.ifEmpty { "پذیرنده smsGA" },
                        style = CockpitTypography.titleSmall,
                        color = Color.White
                    )
                }
                Text(
                    text = "شتاب",
                    style = CockpitTypography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}
