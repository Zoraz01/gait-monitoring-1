package com.gaitmonitoring.screens.signup

import android.graphics.Bitmap
import android.text.format.Formatter
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.gaitmonitoring.R
import com.gaitmonitoring.ui.common.ProgressBarAnimated
import java.io.ByteArrayOutputStream

@Composable
fun PickProfilePictureRow(
    onPickFromCamera: () -> Unit,
    onPickFromGallery: () -> Unit,
    pickedBitmap: Bitmap?,
    onClearBitmap: () -> Unit,
    uploadProfilePictureProgress: Float?,
    modifier: Modifier = Modifier,
) {

    val context = LocalContext.current
    var bitmapSize by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(key1 = pickedBitmap, block = {
        pickedBitmap?.let {
            val size = getBitmapBytes(it)
            val short = Formatter.formatShortFileSize(context, size)
            bitmapSize = short
        }
    })

    Surface(
        modifier = modifier.animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            color = MaterialTheme.colorScheme.onSurface,
            width = 0.5.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = stringResource(id = R.string.add_profile_pic_text),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    color = MaterialTheme.colorScheme.primary
                )

                IconButton(onClick = onPickFromCamera) {
                    Icon(
                        painter = painterResource(id = R.drawable.photo_camera_icon),
                        contentDescription = stringResource(id = R.string.add_profile_pic_from_camera_text),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(onClick = onPickFromGallery) {
                    Icon(
                        painter = painterResource(id = R.drawable.browse_gallery_icon),
                        contentDescription = stringResource(id = R.string.add_profile_pic_from_gallery_text),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

            }

            SelectedBitmapInfo(
                onClearBitmap = onClearBitmap,
                pickedBitmap = pickedBitmap,
                uploadProfilePictureProgress = uploadProfilePictureProgress,
                bitmapSize = bitmapSize,
                modifier = Modifier.fillMaxWidth()
            )

        }

    }
}

private fun getBitmapBytes(bitmap: Bitmap): Long {
    val stream = ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
    val imageInByte = stream.toByteArray();
    val length = imageInByte.size;
    return length.toLong()
}


// show notice for the user to enable him to clear the profile picture ..
@Composable
private fun SelectedBitmapInfo(
    modifier: Modifier = Modifier,
    onClearBitmap: () -> Unit,
    pickedBitmap: Bitmap?,
    uploadProfilePictureProgress: Float?,
    bitmapSize: String
) {
    Crossfade(
        targetState = pickedBitmap != null,
        label = "",
        modifier = modifier
    ) {
        if (it) {
            Column(modifier = Modifier.fillMaxWidth()) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.selected_pic_text),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.primary,
                        fontStyle = FontStyle.Italic
                    )
                    Text(
                        text = bitmapSize,
                        color = MaterialTheme.colorScheme.primary,
                        fontStyle = FontStyle.Italic,
                        style = MaterialTheme.typography.bodySmall,
                    )

                    IconButton(
                        onClick = onClearBitmap,
                        modifier = Modifier.scale(0.5f),
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = null,
                        )
                    }
                }

                uploadProfilePictureProgress?.let { value ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        ProgressBarAnimated(
                            portion = value,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )

                        Text(
                            text = stringResource(
                                id = R.string.float_progress_value,
                                value * 100f
                            ),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

    }

}
