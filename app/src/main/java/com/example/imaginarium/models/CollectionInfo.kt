package com.example.imaginarium.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CollectionInfo(
val id: String?,
val title: String?,
val description: String?,
@Json(name = "total_photos")
val totalPhotos: Long,
)




