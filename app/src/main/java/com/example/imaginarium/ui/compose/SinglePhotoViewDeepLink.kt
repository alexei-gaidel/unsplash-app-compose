package com.example.imaginarium.compose

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.imaginarium.R
import com.example.imaginarium.download.AndroidDownloader
import com.example.imaginarium.models.PhotoDetails
import com.example.imaginarium.netutils.Network
import com.example.imaginarium.viewmodels.PhotoViewModel


@SuppressLint(
    "UnusedMaterialScaffoldPaddingParameter", "Range",
    "StateFlowValueCalledInComposition", "CoroutineCreationDuringComposition"
)
@Composable
fun SinglePhotoViewDeepLink(
    photoViewModel: PhotoViewModel,
    id: String?
) {
    if (id != null) {
        photoViewModel.getSinglePhoto(id)
    } else Toast.makeText(
        LocalContext.current,
        stringResource(R.string.no_data_found),
        Toast.LENGTH_SHORT
    ).show()

    val photo = photoViewModel.photoDetailsFlow.collectAsState(null)

    SinglePhotoDeepLink(photoViewModel = photoViewModel, photo = photo.value)
}

@OptIn(ExperimentalGlideComposeApi::class)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun SinglePhotoDeepLink(
    photoViewModel: PhotoViewModel,
    photo: PhotoDetails?,
) {

    val context = LocalContext.current
    val downloader = AndroidDownloader(context)
    photoViewModel.startSnackbar.value = false
    val permissions = listOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    ).toTypedArray()

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { isGranted ->
            if (isGranted.values.all { it }) {
                Toast.makeText(context, R.string.download_starting, Toast.LENGTH_SHORT).show()
                photo?.urls?.raw?.let {
                    downloader.downLoadFile(it)
                }
            } else {
                Toast.makeText(
                    context,
                    R.string.permission_needed_to_download,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    val scaffoldState = rememberScaffoldState()
    val message = stringResource(R.string.download_finished)
    val actionLabel = stringResource(R.string.open_image)
    LaunchedEffect(scaffoldState.snackbarHostState) {
        photoViewModel.startSnackbar.collect {
            when (photoViewModel.startSnackbar.value) {
                true -> {

                    val result = scaffoldState.snackbarHostState.showSnackbar(
                        message = message,
                        actionLabel = actionLabel
                    )
                    when (result) {
                        SnackbarResult.Dismissed -> {
                            photoViewModel.startSnackbar.value = false
                        }
                        SnackbarResult.ActionPerformed -> {
                            photoViewModel.startSnackbar.value = false

                            val uriString = photoViewModel.uriFlow.value
                            val uriparsed = Uri.parse(uriString)
                            var imageUri: Uri? = null

                            context.contentResolver.query(uriparsed, null, null, null, null)
                                ?.use { cursor ->

                                    cursor.moveToFirst()
                                    imageUri =
                                        ContentUris
                                            .withAppendedId(
                                                MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                                                cursor.getLong(
                                                    cursor.getColumnIndex(
                                                        MediaStore.Images.ImageColumns._ID
                                                    )
                                                )
                                            )
                                }
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.setDataAndType(imageUri, "image/*")
                            context.startActivity(intent)

                        }
                    }
                }
            }
        }
    }


    if (photo != null) {

        var likedByUser by rememberSaveable { mutableStateOf(photo.likedByUser) }
        var dlikes by rememberSaveable { mutableStateOf(photo.likes) }

        Scaffold(
            modifier = Modifier,
            scaffoldState = scaffoldState
        ) {

            Column(
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .verticalScroll(state = rememberScrollState(), enabled = true)
                    .fillMaxSize()
            ) {

                Row(verticalAlignment = Alignment.Top) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .padding(5.dp)
                            .padding(top = 20.dp)
                            .padding(bottom = 10.dp),
                        contentAlignment = Alignment.TopCenter,

                        ) {
                        GlideImage(
                            model = photo?.urls?.regular, contentDescription = null,
                            modifier = Modifier
                                .height(250.dp),
                            contentScale = ContentScale.Crop
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Row() {

                                Column(
                                    modifier = Modifier
                                        .padding(3.dp)
                                ) {

                                    Text(
                                        text = "${photo?.user?.name}",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "@${photo?.user?.username}",
                                        fontSize = 10.sp,
                                        color = Color.White
                                    )
                                }
                            }


                            Row(
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.padding(end = 5.dp),
                                verticalAlignment = Alignment.Bottom
                            ) {

                                Text(
                                    text = dlikes.toString(),
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.align(Alignment.CenterVertically)

                                )
                                Image(
                                    painterResource(id = if (likedByUser == true) R.drawable.favorite_icon_liked else R.drawable.favorite_icon_not_liked),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .width(15.dp)
                                        .height(15.dp)
                                        .align(Alignment.CenterVertically)
                                        .clickable {
                                            if (Network.isNetworkAvailable(context)) {
                                                if (likedByUser == true) {
                                                    photoViewModel.getPhotoUnliked(photo!!.id!!)
                                                    dlikes = dlikes?.minus(1)
                                                    likedByUser = false

                                                } else {
                                                    photoViewModel.getPhotoLiked(photo!!.id!!)
                                                    dlikes = dlikes?.plus(1)
                                                    likedByUser = true
                                                }
                                            }
                                        },
                                )
                            }
                        }
                    }
                }
                Row(modifier = Modifier.padding(start = 5.dp)) {
                    Image(
                        painterResource(id = R.drawable.location_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(5.dp)
                            .width(21.dp)
                            .height(21.dp)
                            .align(Alignment.CenterVertically)
                            .clickable {
                                if (photo.location?.position != null) {
                                    val latitude = photo.location.position.latitude.toString()
                                    val longitude = photo.location.position.longitude.toString()
                                    val city = photo.location.city
                                    val gmmIntentUri = Uri.parse("geo:$latitude, $longitude")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                    mapIntent.setPackage("com.google.android.apps.maps")
                                    startActivity(context, mapIntent, null)

                                } else Toast
                                    .makeText(
                                        context,
                                        R.string.no_location_data_found,
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                            }
                    )
                    Text(
                        text = photo.location?.city ?: "",

                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    val tags = photo?.tags?.map { it?.title }?.joinToString(" #")
                    Text(
                        overflow = TextOverflow.Ellipsis,
                        text = "#$tags" ?: ""
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp)
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(start = 5.dp)
                            .weight(1f)
                    ) {
                        Text(
                            text = ((stringResource(R.string.made_with) + (photo?.exif?.make
                                ?: "")))
                        )
                        Text(
                            text = ((stringResource(R.string.model) + (photo?.exif?.model ?: "")))
                        )
                        Text(
                            text = ((stringResource(R.string.exposure) + (photo?.exif?.exposureTime
                                ?: "")))
                        )
                        Text(
                            text = ((stringResource(R.string.aperture) + (photo?.exif?.aperture
                                ?: "")))
                        )
                        Text(
                            text = ((stringResource(R.string.focal_length) + (photo?.exif?.focalLength
                                ?: ""))),
                        )
                        Text(
                            text = ((stringResource(R.string.iso) + (photo?.exif?.iso ?: "")))
                        )
                    }
                    Column(
                        modifier = Modifier
                            .padding(start = 5.dp)
                            .weight(1f)
                    ) {
                        Text(
                            text = (stringResource(R.string.about) + photo?.user?.username)
                                ?: "",
                        )
                        Text(
                            text = (photo?.user?.bio ?: ""),
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(7.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.downloads),

                        modifier = Modifier

                    )

                    Text(
                        text = "(${photo?.downloads.toString()})",
                        modifier = Modifier
                            .padding(6.dp)
                    )
                    Image(
                        painterResource(id = R.drawable.download_icon_photo_details),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(5.dp)
                            .width(21.dp)
                            .height(21.dp)
                            .align(Alignment.CenterVertically)
                            .clickable {
                                if (permissions.all { permission ->
                                        ContextCompat.checkSelfPermission(
                                            context,
                                            permission
                                        ) == PackageManager.PERMISSION_GRANTED
                                    }
                                ) {
                                    photo.urls?.raw?.let {
                                        downloader.downLoadFile(it)
                                        Toast.makeText(context, R.string.download_starting, Toast.LENGTH_SHORT).show()
                                    }

                                } else {
                                    launcher.launch(permissions)
                                }
                            }
,                        )
                    Image(
                        painterResource(id = R.drawable.share_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(10.dp)
                            .width(21.dp)
                            .height(21.dp)
                            .align(Alignment.CenterVertically)
                            .clickable {
                                val type = "text/plain"
                                val extraText = "https://unsplash.com/photos/${photo.id}"
                                val shareWith = buildString { (R.string.share_with) }
                                val intent = Intent(Intent.ACTION_SEND)
                                intent.type = type
                                intent.putExtra(Intent.EXTRA_TEXT, extraText)

                                ContextCompat.startActivity(
                                    context,
                                    Intent.createChooser(intent, shareWith),
                                    null
                                )
                            },

                        )
                }
            }
        }
    }
}




