package com.example.imaginarium.compose

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
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

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun SingleCollectionView(
    username: String?,
    navController: NavHostController,
    photoViewModel: PhotoViewModel
) {
    val collectionInfo = photoViewModel.collectionInfoFlow.collectAsState(null)
    val collectionId = collectionInfo.value?.id?.let { it }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = collectionInfo.value?.title ?: "",
            modifier = Modifier.padding(7.dp),
            overflow = TextOverflow.Ellipsis,
            fontSize = 22.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = collectionInfo.value?.description ?: "",
            modifier = Modifier.padding(7.dp),
            overflow = TextOverflow.Ellipsis,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
        val count = collectionInfo.value?.totalPhotos?.toInt()
        val countString = pluralStringResource(id = R.plurals.photo_plurals, count = count ?: 0)
        Text(
            text = "${collectionInfo.value?.totalPhotos.toString()} $countString ${
                stringResource(R.string.by)
            } @$username",
            modifier = Modifier.padding(7.dp),
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End
        )
        Spacer(Modifier.height(5.dp))

        if (collectionId != null) {
            val photos: LazyPagingItems<PhotoItem> =
                photoViewModel.pagingCollectionsPhotos(collectionId).collectAsLazyPagingItems()


            val cellConfiguration =
                if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    StaggeredGridCells.Adaptive(minSize = 250.dp)
                } else StaggeredGridCells.Fixed(2)

            LazyVerticalStaggeredGrid(
                columns = cellConfiguration,
                verticalArrangement = Arrangement.spacedBy(3.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                state = rememberLazyStaggeredGridState(),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 5.dp)
            ) {
                items(photos.itemCount)
                { index ->
                    photos[index]?.let {
                        PhotoItem(photoViewModel, it) {
                            val id = photos[index]?.id
                            Log.d("eee", "id in homeview $id")
                            id?.let {
                                photoViewModel.getSinglePhoto(id)

                                val profileImage = photos[index]?.user?.profileImage?.small

                                if (profileImage != null) {
                                    val encodedImage =
                                        URLEncoder.encode(
                                            profileImage,
                                            StandardCharsets.UTF_8.toString()
                                        )
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
    }
}

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun PhotoItem(
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
//
                            }
                    )

                }

            }
        }


    }


