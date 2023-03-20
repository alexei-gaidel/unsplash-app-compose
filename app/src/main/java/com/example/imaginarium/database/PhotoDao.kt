package com.example.imaginarium.database

import androidx.paging.PagingSource
import androidx.room.*


@Dao
interface PhotoDao {

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun save(photos: List<PhotoDBEntity>)

        @Query("SELECT * FROM photo_database")
        fun getPagingSource(): PagingSource<Int, PhotoDBEntity>

        @Query("DELETE FROM photo_database")
        fun delete()

}