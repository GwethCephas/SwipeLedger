package com.cephcoding.feature.transactions.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cephcoding.core.R
import com.cephcoding.core.ui.theme.DarkCharcoal
import com.cephcoding.core.ui.theme.DarkTealPrimary
import com.cephcoding.core.ui.theme.SteelBlue
import com.cephcoding.core.ui.theme.TextHighEmphasis
import com.cephcoding.core.ui.theme.TextMuted

@Composable
fun CustomSearch(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit
) {
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        DarkTealPrimary.copy(alpha = 0.6f),
                        DarkTealPrimary.copy(alpha = 0.4f),
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        DarkCharcoal,
                        SteelBlue,
                        DarkCharcoal
                    )
                )
            ),
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = "Search Transactions...",
                color = TextMuted.copy(0.5f),
                fontWeight = FontWeight.SemiBold
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            cursorColor = DarkTealPrimary,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        leadingIcon = {
            Icon(
                modifier = modifier.size(18.dp),
                painter = painterResource(
                    id = R.drawable.ic_search,
                ),
                contentDescription = "Search Icon",
                tint = TextMuted
            )
        },
        trailingIcon = {
            Row(
                modifier = modifier
                    .padding(10.dp)
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                DarkTealPrimary.copy(alpha = 0.6f),
                                TextHighEmphasis.copy(alpha = 0.4f)
                            )
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically

            ) {
                Icon(
                    modifier = modifier
                        .padding(top = 10.dp, start = 10.dp, bottom = 10.dp)
                        .size(18.dp),
                    painter = painterResource(
                        id = R.drawable.ic_filter,
                    ),
                    contentDescription = "Filter Icon",
                    tint = TextMuted
                )
                Text(
                    modifier = modifier.padding(top = 10.dp, end = 10.dp, bottom = 10.dp),
                    text = "Filter",
                    color = DarkTealPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }


        }

    )
}

@Preview(showBackground = true)
@Composable
fun CustomSearchPreview() {
    CustomSearch(query = "", onQueryChange = {})
}

