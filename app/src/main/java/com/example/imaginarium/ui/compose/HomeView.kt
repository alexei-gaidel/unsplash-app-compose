package com.example.imaginarium.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.imaginarium.R
import com.example.imaginarium.models.PhotoNetworkEntity
import com.example.imaginarium.navgraphs.DetailsScreen
import com.example.imaginarium.viewmodels.PhotoViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


@Composable
fun HomeView(photoViewModel: PhotoViewModel, navController: NavHostController) {
    PhotoList(photoViewModel, navController)
}

@Composable
fun PhotoList(photoViewModel: PhotoViewModel, navController: NavHostController) {

    val photos: LazyPagingItems<PhotoNetworkEntity> =
        photoViewModel.pagingPhotos().collectAsLazyPagingItems()

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(3.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        state = rememberLazyGridState(),
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 5.dp)
    ) {
        items(photos.itemCount)
        { index ->
            photos[index]?.let {
                SinglePhotoItem(photoViewModel, it) {
                    val id = photos[index]?.id
                    id?.let {
                        photoViewModel.getSinglePhoto(id)


                        val profileImage = photos[index]?.profileImage

                        if (profileImage != null) {
                            val encodedImage =
                                URLEncoder.encode(profileImage, StandardCharsets.UTF_8.toString())
                            navController.navigate(
                                DetailsScreen.SinglePhoto.route + "/$encodedImage"
                            )
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SinglePhotoItem(
    photoViewModel: PhotoViewModel,
    photoItem: PhotoNetworkEntity,
    onOpenSinglePhoto: () -> Unit
) {

    var likedByUser by rememberSaveable { mutableStateOf(photoItem.likedByUser) }
    var likes by rememberSaveable { mutableStateOf(photoItem.likes) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(350.dp, 450.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        GlideImage(
            model = photoItem.uri, contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(350.dp, 450.dp)
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
                    model = photoItem.profileImage, contentDescription = null,
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
                        text = photoItem.user,
                        fontSize = 15.sp,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.White
                    )
                    Text(
                        text = "@${photoItem.username}",
                        fontSize = 10.sp,
                        overflow = TextOverflow.Ellipsis,
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

