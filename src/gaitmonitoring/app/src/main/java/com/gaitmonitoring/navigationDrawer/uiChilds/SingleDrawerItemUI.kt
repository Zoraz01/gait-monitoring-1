package com.gaitmonitoring.navigationDrawer.uiChilds

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gaitmonitoring.navigationDrawer.models.DrawerItem

@Composable
fun SingleDrawerItemUI(
    modifier: Modifier = Modifier,
    item: DrawerItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
        label = ""
    )

    Surface(
        modifier = modifier.clickable { onClick() },
        color = backgroundColor,
        shape = RoundedCornerShape(24.dp)
    ) {

        Text(
            text = stringResource(id = item.textRes),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }

}