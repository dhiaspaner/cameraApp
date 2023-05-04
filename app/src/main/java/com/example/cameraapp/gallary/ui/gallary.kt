package com.example.cameraapp.gallary.ui

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.cameraapp.gallary.domain.getMedia

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MediaScreen(context: Context) {
    val context2 = LocalContext.current
    val mediaList = remember {
        mutableStateOf<List<Uri>>(emptyList())
    }
    val modifier = Modifier.fillMaxSize()
    mediaList.value = getMedia(context)
    println(mediaList.value)

    LazyVerticalGrid(columns = GridCells.Adaptive(80.dp)){
        items(mediaList.value){ uri ->
            val thumbnail: Bitmap =
                context.contentResolver.loadThumbnail(
                    uri,
                    Size(128 , 128),
                    null
                )
            Image(bitmap = thumbnail.asImageBitmap(),
                contentDescription = null, contentScale = ContentScale.FillWidth
            )
        }
    }


}
@Composable
fun ImageFromUri(uri: Uri, modifier: Modifier = Modifier) {
    Image(
        painter = rememberImagePainter(
            data = uri,
            builder = {
                crossfade(true)
            }
        ),
        contentDescription = null,
        modifier = modifier
    )
}


