package com.example.imaginarium.compose

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
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
import com.example.imaginarium.models.PhotoItem
import com.example.imaginarium.navgraphs.DetailsScreen
import com.example.imaginarium.viewmodels.PhotoViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun SearchView(photoViewModel: PhotoViewModel, navController: NavHostController, query: String) {
    ResultList(photoViewModel = photoViewModel, navController, query)
}


@Composable
fun ResultList(photoViewModel: PhotoViewModel, navController: NavHostController, query: String) {

    val results: LazyPagingItems<PhotoItem> =
        photoViewModel.pagingSearchPhotos(query).collectAsLazyPagingItems()

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(3.dp),
        state = rememberLazyListState(),
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 5.dp)
    ) {
        items(results.itemCount)
        { index ->
            results[index]?.let {
                ResultItem(photoViewModel, it) {
                    val id = results[index]?.id
                    id?.let {
                        photoViewModel.getSinglePhoto(id)
                        val profileImage = results[index]?.user?.profileImage?.small

                        if (profileImage != null) {
                            val encodedImage =
                                URLEncoder.encode(profileImage, StandardCharsets.UTF_8.toString())
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


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ResultItem(
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
                        overflow = TextOverflow.Ellipsis,
                        color = Color.White
                    )
                    Text(
                        text = "@${photoItem.user.username}",
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
