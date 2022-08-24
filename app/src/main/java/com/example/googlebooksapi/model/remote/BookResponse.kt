package com.example.googlebooksapi.model.remote

data class BookResponse(
    val items: List<BookItem>
)

data class BookItem (val volumeInfo:BookVolumeInfo)

data class BookVolumeInfo (
    val title: String,
    val authors:List<String>,
    val imageLinks: BookImageLinks,
    val description: String,
    val publishedDate :String



    )
data class BookImageLinks(
    val smallThumbnail:String,
    val thumbnail:String
)

