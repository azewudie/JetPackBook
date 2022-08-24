package com.example.googlebooksapi

import android.content.ContentValues.TAG
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.googlebooksapi.model.*
import com.example.googlebooksapi.model.remote.BookApi
import com.example.googlebooksapi.model.remote.BookItem
import com.example.googlebooksapi.model.remote.BookResponse
import com.example.googlebooksapi.ui.theme.GoogleBooksApiTheme
import com.example.googlebooksapi.viewModel.BookViewModel
import java.lang.Error

private const val TAG = "MainActivity"
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainApp {
                val viewModel: BookViewModel by viewModels(){
                    object : ViewModelProvider.Factory{
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return super.create(modelClass)
                            return BookViewModel(RepositoryImpl(BookApi().api)) as T
                        }
                    }
                }

                val uiState =  viewModel.uiState.collectAsState().value
                 SearchScreen(viewModel)
                when(uiState){
                    is Response -> {
                        BookResponseScreen(uiState.data)
                    }
                    is Failure -> {
                        ErrorScreen(uiState.reason)
                    }
                    is Loading -> {
                        LoadingScreen(uiState.isLoading)
                    }
                    is Empty ->{}
                }



            }

        }
    }
}

@Composable
fun BookResponseScreen(data:BookResponse){

    LazyColumn{
        items( data.items.size){position->
            Text(text = "item")
            BookItemStateLess(data.items[position])


        }

    }

}
//Image(
//painter = rememberImagePainter(data = cardImage.url,
//builder = {
//    crossfade(true)
//}),
//contentDescription = stringResource(id = R.string.image_description),
//Modifier.size(cardImage.size.width.dp)//, height = cardImage.size.height.dp)
//)

//Image(
//painter = rememberImagePainter(data = image),
//contentDescription = "Movie Image"
//)
//implementation "io.coil-kt:coil-compose:1.4.0"
@Composable
fun BookItemStateLess(book:BookItem){
    Box(modifier = Modifier
        .padding(
            start = 10.dp, top = 2.dp,
            bottom = 2.dp, end = 5.dp
        )
        .fillMaxWidth()
    ){
        Row{
            AsyncImage(model = ImageRequest.Builder(LocalContext.current).
            data(book.volumeInfo.imageLinks.smallThumbnail)
                .crossfade(true)
                .build(),
                contentScale = ContentScale.Inside,
                modifier = Modifier.clip(CircleShape),
                contentDescription = stringResource(id = R.string.book_cover),
            placeholder = painterResource(id = R.drawable.ic_baseline_menu_book_24))

            Spacer(modifier = Modifier.width(10.dp))
            Column() {
                Text(text = book.volumeInfo.title)
                Row(){
                    Text(text = book.volumeInfo.authors.toString())
                    Text(text=book.volumeInfo.publishedDate)
                }
                Text(text =book.volumeInfo.description)




            }
        }




    }

}
@Composable
fun LoadingScreen(isLoading: Boolean){
    Log.d(TAG, "LoadingScreen: $isLoading")
}
@Composable
fun ErrorScreen(reason:Error){
    Log.d("resason", "LoadingScreen: $reason")
}
@Composable
fun MainApp(context: @Composable () -> Unit){
    GoogleBooksApiTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            context()
        }
    }
}



@Composable
fun SearchScreen(viewModel:BookViewModel){


    var bookQuery:String by remember {
        mutableStateOf("")
    }


    var bookQuerySize by remember {
        mutableStateOf("")
    }
    var selectedPrintType by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current

    Surface(modifier = Modifier
        .fillMaxWidth()
        .padding(50.dp),
        shape = RoundedCornerShape(corner = CornerSize(20.dp)),
        elevation = 14.dp){
        Column {
            OutlinedTextField(
                value = bookQuery, onValueChange = { bookQuery = it },
                label = {
                    Text(text = "Search by Name")
                }
            )
            TextField(value = bookQuerySize, onValueChange ={
                bookQuerySize = it
            } )

            BookPrintType(){ printType ->
               selectedPrintType =printType

            }


            Button(onClick = {

                    Toast.makeText(context, "Searching", Toast.LENGTH_LONG).show()
                viewModel.searchBook(bookQuery,bookQuerySize,selectedPrintType)

                }

                ) {
                Text(text = "Search")
            }
        }

    }

}

@Composable
fun BookPrintType(selected :(String)->Unit) {
    var isExpanded by remember {
        mutableStateOf(true)
    }
    var selection by remember {
        mutableStateOf("")
    }
    val dropDownIcon = if(isExpanded) Icons.Filled.KeyboardArrowDown
    else Icons.Filled.KeyboardArrowUp


    Column(modifier = Modifier
        .padding(all = 2.dp)
        .fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()){


            Icon(imageVector = dropDownIcon,contentDescription ="A dropdown icon ",
                modifier = Modifier.clickable {
                    isExpanded = !isExpanded
                }
            )
            Spacer(modifier =Modifier.width(30.dp))
            Text(text="$selection", modifier = Modifier.fillMaxWidth())
        }



    }
    DropdownMenu(expanded = isExpanded,
        onDismissRequest = {
            isExpanded  = false
        }) {
        val  context = LocalContext.current
        context.resources.getStringArray(R.array.book_print_type).forEach { printType->

            DropdownMenuItem(onClick = {
                isExpanded = false
                selected(printType)
                selection = printType
            }) {
                Text(text=printType)
            }
        }

    }
}



@Preview(showBackground = false)
@Composable
fun SearchScreenPreview(){
    val viewModel:BookViewModel  = BookViewModel()
    MainApp {
        SearchScreen(viewModel = viewModel)
    }




}
