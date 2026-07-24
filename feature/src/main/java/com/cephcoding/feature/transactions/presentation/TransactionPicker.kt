package com.cephcoding.feature.transactions.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cephcoding.core.ui.theme.BrightCyanAccent
import com.cephcoding.core.ui.theme.DarkCharcoal
import com.cephcoding.core.ui.theme.SteelBlue
import com.cephcoding.core.ui.theme.TextMuted

@Composable
fun TransactionPicker(
    modifier: Modifier = Modifier,
    selectedIndex: Int = 0,
    onIndexSelected: (Int) -> Unit = {}
) {
    val transactionTypes = listOf("All", "Income", "Expense")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        DarkCharcoal,
                        SteelBlue,
                        DarkCharcoal
                    )
                )
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        transactionTypes.forEachIndexed { index, transactionType ->
            val isSelected = index == selectedIndex
            Box(
                modifier = modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) BrightCyanAccent else Color.Transparent)
                    .clickable {
                        onIndexSelected(index)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier.padding(10.dp),
                    text = transactionType,
                    color = if (isSelected) Color.Black else TextMuted,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun TransactionPickerPreview() {
    TransactionPicker()

}