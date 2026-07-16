package com.cephcoding.feature.dashboard.presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cephcoding.core.domain.model.ExpenseCategory
import com.cephcoding.core.ui.theme.*

@Composable
fun ExpenseBreakdownSection(
    breakdown: Map<ExpenseCategory, Double>,
    totalExpenses: Double
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
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Expense Allocation",
                style = MaterialTheme.typography.titleLarge,
                color = TextHighEmphasis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ExpenseDonutChart(
                    breakdown = breakdown,
                    totalExpenses = totalExpenses
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    breakdown.forEach { (category, amount) ->
                        val ratio =
                            if (totalExpenses > 0) (amount / totalExpenses).toFloat() else 0f
                        val percentage = (ratio * 100).toInt()
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.size(10.dp)
                                    .clip(CircleShape)
                                    .background(category.color())
                            )
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.labelMedium,
                                color = TextMediumEmphasis,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = "$percentage%",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextHighEmphasis,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }


                }

            }

        }

    }
}


@Composable
fun ExpenseDonutChart(
    modifier: Modifier = Modifier,
    breakdown: Map<ExpenseCategory, Double>,
    totalExpenses: Double
) {
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(breakdown) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1200)
        )
    }

    Canvas(
        modifier = modifier
            .size(100.dp)
            .padding(10.dp)
    ) {
        var startAngle = -90f

        breakdown.forEach { (category, amount) ->
            val percentage = if (totalExpenses > 0) (amount / totalExpenses).toFloat() else 0f
            val sweep = percentage * 360

            drawArc(
                color = category.color(),
                startAngle = startAngle,
                sweepAngle = sweep * animationProgress.value,
                useCenter = false,
                style = Stroke(
                    width = 40f,
                    cap = StrokeCap.Round
                )
            )
            startAngle += sweep
        }
    }
}

fun ExpenseCategory.color(): Color =
    when (this) {
        ExpenseCategory.INVENTORY -> TextMuted
        ExpenseCategory.TRANSPORT -> CoralDestructive
        ExpenseCategory.UTILITIES -> TextMediumEmphasis
        ExpenseCategory.MARKETING -> DarkTealPrimary
        ExpenseCategory.SOFTWARE_SAAS -> TextHighEmphasis
        ExpenseCategory.UNCATEGORIZED -> BrightCyanAccent

    }
