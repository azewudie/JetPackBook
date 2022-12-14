package com.example.googlebooksapi.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.googlebooksapi.model.remote.BookItem

@Composable
fun BookDetails(bookItem: BookItem){

    // todo implemennt remember

    Column(modifier = Modifier.fillMaxWidth()) {
        BookDetailHeader(bookItem  )
        Spacer(modifier = Modifier.fillMaxWidth(0.5f))
        BookDetailBody(bookItem)
    }

}

@OptIn(ExperimentalUnitApi::class)
@Composable
fun BookDetailHeader(bookItem: BookItem) {
    Card(shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(3.dp, Color.DarkGray),
        elevation = 0.dp
    ){
        Row{


        Column {
            Text(text = bookItem.volumeInfo?.title!!,
                fontSize = TextUnit(value = 20f, type = TextUnitType.Sp),
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(5.dp)

            )

Spacer(modifier = Modifier.width(20.dp))
            Text(text = bookItem.volumeInfo.publishedDate,


                fontWeight = FontWeight.ExtraLight,
                modifier = Modifier.padding(5.dp)

            )
        }

        AsyncImage(model = ImageRequest.Builder(LocalContext.current).
        data(bookItem.volumeInfo?.imageLinks?.thumbnail!!).
        crossfade(true).build(),
            contentDescription = "Hello image ")
    }
    }

}

@Composable
fun BookDetailBody(bookItem: BookItem) {
    Card(shape = RoundedCornerShape(corner = CornerSize(8.dp)),
    border = BorderStroke(3.dp, Color.DarkGray),
        elevation = 0.dp
    ){
        Column() {
            Text(text = bookItem.volumeInfo.description.toString(),
                modifier = Modifier
                    .border(BorderStroke(3.dp, Color.DarkGray))
                    .padding(5.dp))
            Text(text = bookItem.volumeInfo.authors.toString(),
                modifier = Modifier
                    .border(BorderStroke(3.dp, Color.DarkGray))
                    .padding(5.dp))
        }

    }

}

