package com.example.googlebooksapi.model.remote

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class BookResponse(
    val items: List<BookItem>
)
@Parcelize
data class BookItem (val volumeInfo:BookVolumeInfo):Parcelable
@Parcelize
data class BookVolumeInfo (
    val title: String?=null,
    val authors:List<String>,
    val imageLinks: BookImageLinks?=null ,
    val description: String,
    val publishedDate :String



    ):Parcelable
@Parcelize
data class BookImageLinks(
    val smallThumbnail:String?=null,
    val thumbnail:String?=null
):Parcelable

