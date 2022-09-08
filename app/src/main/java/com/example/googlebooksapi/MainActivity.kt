package com.example.googlebooksapi

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.twotone.Check
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
import androidx.navigation.NavArgumentBuilder
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.googlebooksapi.model.*
import com.example.googlebooksapi.model.remote.BookApi
import com.example.googlebooksapi.model.remote.BookItem
import com.example.googlebooksapi.model.remote.BookResponse
import com.example.googlebooksapi.ui.screens.BookDetails
import com.example.googlebooksapi.ui.theme.GoogleBooksApiTheme
import com.example.googlebooksapi.util.*
import com.example.googlebooksapi.viewModel.BookViewModel
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Error

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContent {
            MainApp {
                //It will be a main container in your activity
                // used to rendr ToolBar/ BottomApp Bar
                //allong bwith the content
                val viewModel: BookViewModel by viewModels()

                val navController = rememberNavController()

                NavHost(navController = navController,
                    startDestination = SEARCH_SCREEN){
                    composable(
                        route = SEARCH_SCREEN
                    ){
                        SearchScreenStateFull(viewModel = viewModel, navigation =navController ) {
                            logOutCurrentUser()

                        }
                    }
                    composable(
                        route = navigateDetailScreen,
                        arguments = listOf(
                            navArgument(ARG_BOOK){
                                type = NavType.ParcelableType<BookItem>(BookItem::class.java)
                            }
                        )
                    ){ backStackEntry->
                        BookDetails(bookItem = backStackEntry.
                        arguments?.getParcelable<BookItem>(ARG_BOOK)?:throw Exception("Data is not ready")
                        )
                    }

                }



//                Scaffold( topBar = {
//                    MainTopBar{
//                        logOutCurrentUser()
//                    }
//                }) {
//                    it.toString()
//                    val viewModel: BookViewModel by viewModels()
//                    {
//                        object : ViewModelProvider.Factory{
//                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
//                                //return super.create(modelClass)
//                                return BookViewModel(RepositoryImpl(BookApi().api)) as T
//                            }
//                        }
//                    }

//                    val uiState =  viewModel.uiState.collectAsState().value
//                    SearchScreen(viewModel)
//                    when(uiState){
//                        is Response -> {
//                            BookResponseScreen(uiState.data){
//
//                            }
//                        }
//                        is Failure -> {
//                            ErrorScreen(uiState.reason)
//                        }
//                        is Loading -> {
//                            LoadingScreen(uiState.isLoading)
//                        }
//                        is Empty ->{}
//                    }
//                }




            }

        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast

            val msg = "fcm token : $token"
            Log.d("Token", msg)
            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        })
    }

    fun logOutCurrentUser(){
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener{
                Log.d(TAG, "LogOutCurrentUser: ${it.result}")
            }
    }
}

@Composable
fun SearchScreenStateFull(viewModel:BookViewModel,navigation:NavController,logOutCurrentUser: () -> Unit){
    //It will be a main container in your activity
    // used to rendr ToolBar/ BottomApp Bar
    //allong bwith the content
    Scaffold( topBar = {
        MainTopBar{
            logOutCurrentUser()
        }
    }) {
        it.toString()
      // val viewModel: BookViewModel by viewModels()
//                    {
//                        object : ViewModelProvider.Factory{
//                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
//                                //return super.create(modelClass)
//                                return BookViewModel(RepositoryImpl(BookApi().api)) as T
//                            }
//                        }
//                    }

        val uiState =  viewModel.uiState.collectAsState().value

        Column() {




        SearchScreen(viewModel)

        when(uiState){
            is Response -> {
                BookResponseScreen(uiState.data){bookItem->
                    navigation.navigate("$DETAIL_SCREEN$bookItem")

                }
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

@Composable
fun MainTopBar(logOutCurrentUser: ()->Unit ={}) {
    var menuLogOutIsExpanded: Boolean by remember {
        mutableStateOf(false)
    }
    Log.d(
        TAG,
        "MainTopBar:PhotoURl= ${FirebaseAuth.getInstance().currentUser?.photoUrl} " +
                "${FirebaseAuth.getInstance().currentUser?.displayName}")
    FirebaseAuth.getInstance().currentUser?.photoUrl
    FirebaseAuth.getInstance().currentUser?.displayName


    TopAppBar(
        title = {
            LocalContext.current.getString(R.string.app_name)
        },
        actions = {
            Icon(
                imageVector = Icons.Filled.Menu,
                contentDescription = LocalContext.current.getString(R.string.menu),
                modifier = Modifier.clickable { menuLogOutIsExpanded = true }
            )
            DropdownMenu(
                expanded = menuLogOutIsExpanded,
                onDismissRequest = { menuLogOutIsExpanded = false }
            ) {
                DropdownMenuItem(
                    onClick = {
                        menuLogOutIsExpanded = false
                        logOutCurrentUser
                    }
                ) {
                    Text(text = LocalContext
                        .current
                        .getString(R.string.menu))
                }
            }
        }
    )
}



@Composable
fun BookResponseScreen(data:BookResponse, openDetails:(BookItem)->Unit){

    LazyColumn{
        items( data.items.size){position->
            Text(text = "item")
            BookItemStateLess(data.items[position],openDetails)


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
fun BookItemStateLess(book:BookItem,openDetails:(BookItem)->Unit){
    Log.d("TESTBOOK", "BookItemStateLess: $book")
    Box(modifier = Modifier
        .padding(
            start = 10.dp, top = 2.dp,
            bottom = 2.dp, end = 5.dp
        )
        .fillMaxWidth()
    ){
        Row{
            AsyncImage(model = ImageRequest.Builder(LocalContext.current).
            data(book.volumeInfo.imageLinks?.smallThumbnail?:"")
                .crossfade(true)
                .build(),
                contentScale = ContentScale.Inside,
                modifier = Modifier.clip(CircleShape),
                contentDescription = stringResource(id = R.string.book_cover),
            placeholder = painterResource(id = R.drawable.ic_baseline_menu_book_24),
                error = painterResource(id = R.drawable.ic_baseline_menu_book_24)
            )

            Spacer(modifier = Modifier.width(10.dp))
            Column() {
                Text(text = book.volumeInfo?.title!!,
                modifier = Modifier.clickable {
                    openDetails(book)
                })
                Row(){
                    Text(text = book.volumeInfo?.authors.toString())
                    Text(text=book.volumeInfo.publishedDate)
                }
                Text(text = book.volumeInfo.description?:"hello")




            }
        }




    }

}
@Composable
fun LoadingScreen(isLoading: Boolean){
    Log.d(TAG, "LoadingScreen: $isLoading")
}
@Composable
fun ErrorScreen(reason:String){
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



@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchScreen(viewModel:BookViewModel){
    val ebookSate = ChipState.C_Ebooks()



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
            Spacer(modifier = Modifier.height(3.dp))
            Chip(onClick = {
                ebookSate.isChecked = true
            }) {

                Row(horizontalArrangement = Arrangement.SpaceBetween){


                Text(text = "Search")
                AnimatedVisibility(visible =   ebookSate.isChecked) {

                }

                Text(text = "Book")

              Image(imageVector = Icons.TwoTone.Check, contentDescription ="Felter opetion Ebook"
           )
                }
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

sealed class ChipState{
    data class C_Ebooks(var isChecked: Boolean = false):ChipState()
    data class C_Free(var isChecked: Boolean = false):ChipState()
    data class C_Full(var isChecked: Boolean = false):ChipState()
}

//enum class  ChipsState(val isChecked:Boolean){
//    C1_C(true),
//    C1_NC(false)
//
//}

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


//
//@Preview(showBackground = false)
//@Composable
//fun SearchScreenPreview(){
//    val viewModel:BookViewModel  = BookViewModel()
//    MainApp {
//        SearchScreen(viewModel = viewModel)
//    }
//
//
//}
