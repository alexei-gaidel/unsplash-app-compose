package com.example.imaginarium.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "photo_database")
data class PhotoDBEntity (
@PrimaryKey
@ColumnInfo(name = "id")
val id: String,
@ColumnInfo(name = "likes")
val likes: Long,
@ColumnInfo(name = "liked_by_user")
val likedByUser: Boolean,
@ColumnInfo(name = "photo")
var uri: String,
@ColumnInfo(name = "username")
val username: String,
@ColumnInfo(name = "user")
val user: String,
@ColumnInfo(name = "profile_image")
val profileImage: String

)
