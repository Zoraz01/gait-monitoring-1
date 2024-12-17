package com.gaitmonitoring.navigationDrawer.uiChilds

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.gaitmonitoring.R
import com.gaitmonitoring.data.MyUser


@Composable
fun DrawerHeadUI(
    modifier: Modifier = Modifier,
    user: MyUser?,
    onProfileClicked: () -> Unit // Navigation action on profile click
) {

    val isDarkTheme = isSystemInDarkTheme()
    val logoImage = remember(isDarkTheme) {
        if (isDarkTheme) {
            R.drawable.gait_analysis_dark_just_logo
        } else {
            R.drawable.gait_analysis_just_logo // Use the light theme logo if different
        }
    }


    Column(
        modifier = modifier.padding(top = 4.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(id = logoImage),
            contentDescription = "Logo",
            modifier = Modifier
                .align(Alignment.Start)
                .size(48.dp) // Adjust the size as needed
        )

        Spacer(modifier = Modifier.height(2.dp)) // Space between logo and user name
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(text = user?.fullName ?: "", style = MaterialTheme.typography.bodyLarge)

            Surface(
                modifier = Modifier
                    .size(72.dp)
                    .clickable { onProfileClicked() },
                shape = CircleShape,
                border = BorderStroke(
                    width = Dp.Hairline,
                    color = MaterialTheme.colorScheme.primary
                )
            ) {
                SubcomposeAsyncImage(
                    model = user?.profilePicUriString ?: "",
                    contentDescription = stringResource(id = R.string.profile_picture_content_description),
                    modifier = Modifier.clip(CircleShape),
                    loading = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.scale(0.7f))
                        }
                    },
                    error = {
                        Image(
                            painter = painterResource(id = R.drawable.profile_icon_9),
                            contentDescription = stringResource(id = R.string.profile_picture_content_description),
                            modifier = Modifier
                                .clip(CircleShape) // This clips the image to a circle
                        )
                    }
                )
            }
        }
    }
}