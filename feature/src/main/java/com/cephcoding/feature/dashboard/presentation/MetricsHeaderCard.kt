package com.cephcoding.feature.dashboard.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cephcoding.core.ui.theme.BrightCyanAccent
import com.cephcoding.core.ui.theme.CoralDestructive
import com.cephcoding.core.ui.theme.DarkCharcoal
import com.cephcoding.core.ui.theme.DarkTealPrimary
import com.cephcoding.core.ui.theme.SteelBlue
import com.cephcoding.core.ui.theme.TextHighEmphasis
import com.cephcoding.core.ui.theme.TextMediumEmphasis


@Composable
fun MetricsHeaderCard(
    netCashFlow: Double,
    income: Double,
    expenses: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        DarkTealPrimary.copy(alpha = 0.6f),
                        TextHighEmphasis.copy(alpha = 0.4f)
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            DarkCharcoal,
                            SteelBlue,
                            DarkCharcoal
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Text(
                text = "Net Cash Flow",
                style = MaterialTheme.typography.labelMedium,
                color = TextMediumEmphasis
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Ksh %,.2f".format(netCashFlow),
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = if (netCashFlow >= 0) TextHighEmphasis else CoralDestructive
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            DarkTealPrimary.copy(alpha = 0.05f),
                            RoundedCornerShape(16.dp)
                        )
                        .border(
                            1.dp,
                            DarkTealPrimary.copy(alpha = 0.2f),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Total Income",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextMediumEmphasis
                    )
                    Text(
                        text = "Ksh %,.2f".format(income),
                        style = MaterialTheme.typography.titleLarge,
                        color = BrightCyanAccent
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            CoralDestructive.copy(alpha = 0.05f),
                            RoundedCornerShape(16.dp)
                        )
                        .border(
                            1.dp,
                            CoralDestructive.copy(alpha = 0.2f),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Total Expenses",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextMediumEmphasis
                    )
                    Text(
                        text = "Ksh %,.2f".format(expenses),
                        style = MaterialTheme.typography.titleLarge,
                        color = CoralDestructive
                    )
                }
            }
        }
    }
}
