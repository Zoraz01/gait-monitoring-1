package com.gaitmonitoring.navigationDrawer.uiChilds

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gaitmonitoring.ui.common.Space

@Composable
fun MyTopAppBar(
    title: String = "",
    onMenuClicked: () -> Unit
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = onMenuClicked) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = null,
                    modifier = Modifier.size(62.dp)
                )
            }

            if (title.isNotEmpty()) {
                Space(width = 16.dp)
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

}