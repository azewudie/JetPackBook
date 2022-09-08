package com.example.googlebooksapi.viewModel

import com.example.googlebooksapi.model.Failure
import com.example.googlebooksapi.model.Repository
import com.example.googlebooksapi.model.UIState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class RepositoryTest:Repository {
    override fun getBookByName(
        bookTitle: String,
        bookSize: String,
        printType: String
    ): Flow<UIState> {
       return flowOf(Failure("Test Failure"))
    }
}