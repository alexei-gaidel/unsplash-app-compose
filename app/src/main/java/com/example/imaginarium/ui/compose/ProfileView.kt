package com.example.imaginarium.compose

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.imaginarium.R
import com.example.imaginarium.models.PhotoItem
import com.example.imaginarium.models.UnsplashUser
import com.example.imaginarium.navgraphs.DetailsScreen
import com.example.imaginarium.viewmodels.PhotoViewModel
import com.example.imaginarium.viewmodels.UserInfoViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProfileView(
    photoViewModel: PhotoViewModel,
    userInfoViewModel: UserInfoViewModel,
    navController: NavHostController
) {
    userInfoViewModel.loadUserInfo()
    val user by userInfoViewModel.userInfoFlow.collectAsState(null)
    val username = user?.username

    val results: LazyPagingItems<PhotoItem>? =
        username?.let { photoViewModel.pagingLikedPhotos(it).collectAsLazyPagingItems() }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(3.dp),
        state = rememberLazyListState(),
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 5.dp)
    ) {

        if (user != null) {

            item { Header(user = user) }
            if (results != null) {
                items(results.itemCount)
                { index ->

                    results[index]?.let {
                        LikedtItem(photoViewModel, it) {
                            val id = results[index]?.id
                            id?.let {
                                photoViewModel.getSinglePhoto(id)

                                val profileImage = results[index]?.user?.profileImage?.small
                                if (profileImage != null) {
                                    val encodedImage =
                                        URLEncoder.encode(
                                            profileImage,
                                            StandardCharsets.UTF_8.toString()
                                        )
                                    navController.navigate(
                                        DetailsScreen.SinglePhoto.route + "/$encodedImage"
                                    ) {

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun Header(user: UnsplashUser?) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .padding(10.dp)

    ) {
        GlideImage(
            model = user?.profileImage?.large,
            contentDescription = null,
            modifier = Modifier
                .width(100.dp)
                .height(100.dp)
                .padding(7.dp)
                .clip(
                    RoundedCornerShape(50.dp)
                )
        )
        Column(modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)) {
            Text(
                text = "${user?.name}",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "@${user?.username}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Light

            )

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "${user?.bio}",
                fontSize = 21.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(12.dp))
            Column(modifier = Modifier.alpha(0.7f)) {
                TextWithLeadingImage(R.drawable.location_icon, user?.location) {
                    if (user?.location != null) {
                        val location = user?.location
                        val gmmIntentUri = Uri.parse("geo:0, 0?q=" + Uri.encode(location))
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        ContextCompat.startActivity(context, mapIntent, null)

                    } else Toast.makeText(
                        context,
                        R.string.no_location_data_found,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                TextWithLeadingImage(R.drawable.mail_icon, user?.email) {}
                TextWithLeadingImage(
                    R.drawable.download_icon,
                    user?.downloads.toString()
                ) {}

            }

        }
    }
    Row(
        modifier = Modifier

            .fillMaxWidth()
            .background(color = Color.LightGray)
    ) {
        Text(
            color = Color.Black,
            modifier = Modifier.padding(10.dp),
            text = (stringResource(R.string.liked)),
            fontSize = 15.sp,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center
        )
    }
}


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun LikedtItem(
    photoViewModel: PhotoViewModel,
    photoItem: PhotoItem,
    onOpenSinglePhoto: () -> Unit
) {

    var likedByUser by rememberSaveable { mutableStateOf(photoItem.likedByUser) }
    var likes by rememberSaveable { mutableStateOf(photoItem.likes) }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomStart
    ) {
        GlideImage(
            model = photoItem.urls.regular, contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(150.dp)
                .clickable(onClick = onOpenSinglePhoto),
            contentScale = ContentScale.Crop,
        )
        Row(

            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Row() {
                GlideImage(
                    model = photoItem.user.profileImage.small, contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.Bottom)
                        .padding(4.dp)
                        .width(22.dp)
                        .height(22.dp)
                        .clip(RoundedCornerShape(11.dp))
                )
                Column(
                    modifier = Modifier
                        .padding(3.dp)
                ) {
                    Text(
                        text = photoItem.user.name,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                    Text(
                        text = "@${photoItem.user.username}",
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
                    text = likes.toString(),
                    fontSize = 11.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Image(
                    painterResource(id = if (likedByUser) R.drawable.favorite_icon_liked else R.drawable.favorite_icon_not_liked),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(5.dp)
                        .width(15.dp)
                        .height(15.dp)
                        .align(Alignment.CenterVertically)
                        .clickable {
                            if (likedByUser) {
                                photoViewModel.getPhotoUnliked(photoItem.id)
                                likedByUser = false
                                likes -= 1
                            } else {
                                photoViewModel.getPhotoLiked(photoItem.id)
                                likes += 1
                                likedByUser = true
                            }
                        }
                )
            }
        }
    }
}

@Composable
fun TextWithLeadingImage(imageId: Int?, text: String?, onClick: () -> Unit) {
    Row(modifier = Modifier.padding(2.dp))
    {
        Image(
            painterResource(imageId ?: -1), null,
            modifier = Modifier
                .width(20.dp)
                .height(20.dp)
                .padding(end = 5.dp)
                .clickable {
                    onClick()
                },
            alignment = Alignment.Center,
            contentScale = ContentScale.FillWidth

        )
        Text(
            text = text ?: "No data",
            fontSize = 15.sp,
            fontWeight = FontWeight.Light

        )
    }

}

