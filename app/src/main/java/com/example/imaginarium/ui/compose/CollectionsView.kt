package com.example.imaginarium.compose

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.imaginarium.R
import com.example.imaginarium.models.PhotoCollection
import com.example.imaginarium.navgraphs.DetailsScreen
import com.example.imaginarium.viewmodels.PhotoViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun CollectionsView(photoViewModel: PhotoViewModel, navController: NavHostController) {
    val collections: LazyPagingItems<PhotoCollection> =
        photoViewModel.pagingCollections().collectAsLazyPagingItems()

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(3.dp),
        state = rememberLazyListState(),
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 5.dp)
    ) {
        items(collections.itemCount)
        { index ->
            collections[index]?.let {
                SingleCollectionItem(it) {
                    val id = collections[index]?.id
                    id?.let {
                        photoViewModel.getSingleCollection(id)
                        val username = collections[index]?.user?.username
                        val profileImage = collections[index]?.user?.profileImage?.small

                        if (username != null) {
                            val encodedImage =
                                URLEncoder.encode(profileImage, StandardCharsets.UTF_8.toString())
                            navController.navigate(
                                DetailsScreen.SingleCollection.route + "/$username/$encodedImage"
                            ) {
                            }
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalGlideComposeApi::class, ExperimentalComposeUiApi::class)
@Composable
fun SingleCollectionItem(
    collection: PhotoCollection,
    onOpenCollection: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        GlideImage(
            model = collection.coverPhoto.urls.regular, contentDescription = null,
            modifier = Modifier
                .fillMaxHeight()
                .clickable(onClick = onOpenCollection),
            contentScale = ContentScale.Crop,
        )
        Row(

            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp),

//            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier
                    .padding(start = 5.dp)
            ) {
                val count = collection.totalPhotos.toString().takeLast(1).toInt()
                val countString = pluralStringResource(id = R.plurals.photo_plurals, count = count)
                Text(
                    text = collection.totalPhotos.toString() + " " + countString,
                    fontSize = 20.sp,
                    color = Color.White,
                    style = androidx.compose.ui.text.TextStyle(
                        shadow = Shadow(
                            color = Color.DarkGray,
                            offset = Offset(2.0f, 2.0f),
                            blurRadius = 10.0f
                        )
                    )
                )
                Text(
                    text = collection.title,
                    fontSize = 35.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    style = androidx.compose.ui.text.TextStyle(
                        shadow = Shadow(
                            color = Color.DarkGray,
                            offset = Offset(2.0f, 2.0f),
                            blurRadius = 10.0f
                        )
                    )
                )
            }


        }
        Row(

            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Row() {
                GlideImage(
                    model = collection.user.profileImage.small, contentDescription = null,
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
                        text = collection.user.name,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                    Text(
                        text = "@${collection.user.username}",
                        fontSize = 10.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

