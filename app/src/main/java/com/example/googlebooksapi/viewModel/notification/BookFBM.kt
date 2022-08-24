package com.example.googlebooksapi.viewModel.notification


import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

private const val TAG = "BookFBM"
class BookFBM:FirebaseMessagingService() {

    /**
     * here is the token is created, either the user
     * clean app data or reinstall
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("testMESSAGE", "onNewToken: $token")
    }

    /**
     * When a message is send in the campaign
     */

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG, "onMessageReceived: $message")
    }
}