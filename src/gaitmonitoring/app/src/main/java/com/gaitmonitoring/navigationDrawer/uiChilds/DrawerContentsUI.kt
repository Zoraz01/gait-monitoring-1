package com.gaitmonitoring.navigationDrawer.uiChilds

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.gaitmonitoring.data.MyUser
import com.gaitmonitoring.navigationDrawer.models.DrawerItem
import com.gaitmonitoring.ui.common.Space

@Composable
fun DrawerContentsUI(
    modifier: Modifier = Modifier,
    user:MyUser?,
    drawerItems: List<DrawerItem>,
    currentNavigationRoute:String?,
    onItemClicked: (DrawerItem) -> Unit,
    onProfileClicked: () -> Unit
) {


    ModalDrawerSheet(modifier = modifier) {
        Column {
            Space(height = 32.dp)
            DrawerHeadUI(
                user = user,
                modifier = Modifier.fillMaxWidth(), // Pass the resource ID
                onProfileClicked = onProfileClicked
            )
            Space(height = 32.dp)
            drawerItems.fastForEach { item ->
                SingleDrawerItemUI(
                    item = item,
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(vertical = 16.dp),
                    isSelected = item.navigationDestination?.route == currentNavigationRoute,
                    onClick = {
                        onItemClicked(item)
                    }
                )
            }
        }
    }

}