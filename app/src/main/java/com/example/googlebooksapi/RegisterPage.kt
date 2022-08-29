package com.example.googlebooksapi

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import com.example.googlebooksapi.ui.theme.GoogleBooksApiTheme
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import androidx.compose.material.Text as Text1

private const val TAG = "RegisterPage"
class RegisterPage:ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {

        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {

            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            Log.d("User", "onSignInResult: $user")
            navigateSearchScreen()
            // todo navigate to searchers screen
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            response?.let{resp->

                Log.e(TAG, "onSignInResult: ${resp?.error?.errorCode}", )
                // todo show compose error
            }?:kotlin.run {
                Log.e(TAG, "onSignInResult: user canceled ", )
            }
        }

    }

    private fun navigateSearchScreen() {
        val navigate = Intent(this,MainActivity::class.java)
        startActivity(navigate)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            Handler().postDelayed({
                "Load..."
            },2000)
            RegisterPageApp{
//                CircularProgressIndicator()

            }
        }
        //create the login provider
        // trigger the implicit intent for LoginUi
        // register the activity for result
        // handle the 2 scenario(Success, and failure)
        auth = Firebase.auth
        //this should be in the onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser == null){
            createFirebaseUILogon();
        }else{
            navigateSearchScreen()
        }
    }


    private  fun createFirebaseUILogon(){
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build()
           )

// Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
    }


    override fun onStop(){
        super.onStop()

//        AuthUI.getInstance()
//            .signOut(this)
//            .addOnCompleteListener {
//                it.result
//                Log.d("result", "onStop: ${it.result}")
//            }
    }
}
@Composable
fun RegisterPageApp(content:@Composable ()-> Unit){
    GoogleBooksApiTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            content()
        }
    }
}