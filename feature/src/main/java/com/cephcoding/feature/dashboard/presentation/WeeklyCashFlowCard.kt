package com.cephcoding.feature.dashboard.presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cephcoding.core.ui.theme.BrightCyanAccent
import com.cephcoding.core.ui.theme.DarkCharcoal
import com.cephcoding.core.ui.theme.DarkTealPrimary
import com.cephcoding.core.ui.theme.SlateGray
import com.cephcoding.core.ui.theme.TextHighEmphasis
import com.cephcoding.core.ui.theme.TextMuted
import com.cephcoding.feature.dashboard.model.DailyFlow

@Composable
fun WeeklyCashFlowCard(
    modifier: Modifier = Modifier,
    data: List<DailyFlow>
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        DarkTealPrimary.copy(alpha = 0.6f),
                        TextHighEmphasis.copy(alpha = 0.15f)
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
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            DarkCharcoal,
                            SlateGray
                        )
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "WEEKLY CASH FLOW",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                letterSpacing = 1.5.sp
            )
            SmoothLineCanvas(
                data = data,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun SmoothLineCanvas(
    data: List<DailyFlow>,
    modifier: Modifier = Modifier
) {
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(1f, animationSpec = tween(1500))
    }

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            if (data.isEmpty()) return@Canvas

            val width = size.width
            val height = size.height
            val maxAmount = data.maxOf { it.amount }.coerceAtLeast(1f)
            val minAmount = data.minOf { it.amount }.coerceAtMost(0f)
            val range = (maxAmount - minAmount).coerceAtLeast(1f)

            val points = data.mapIndexed { index, flow ->
                val x = if (data.size > 1) index * (width / (data.size - 1)) else width / 2f
                val y = height - ((flow.amount - minAmount) / range) * height
                x to y
            }

            val strokePath = Path().apply {
                if (points.isNotEmpty()) {
                    moveTo(points.first().first, points.first().second)
                    for (i in 1 until points.size) {
                        val prev = points[i - 1]
                        val curr = points[i]
                        val cp1X = prev.first + (curr.first - prev.first) / 2f
                        val cp2X = prev.first + (curr.first - prev.first) / 2f
                        cubicTo(
                            cp1X, prev.second,
                            cp2X, curr.second,
                            curr.first, curr.second
                        )
                    }
                }
            }

            val fillPath = Path().apply {
                addPath(strokePath)
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }

            clipRect(right = width * animationProgress.value) {
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            BrightCyanAccent.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    )
                )

                drawPath(
                    path = strokePath,
                    color = BrightCyanAccent,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            data.forEach {
                Text(
                    text = it.day,
                    fontSize = 10.sp,
                    color = TextMuted,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
