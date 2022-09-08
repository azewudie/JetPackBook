package com.example.googlebooksapi.viewModel

import com.example.googlebooksapi.model.Empty
import com.example.googlebooksapi.model.Failure
import com.example.googlebooksapi.model.Repository
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test

class BookViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    private var mockRepository = mockk<Repository>(relaxed = false)

    private lateinit var subject: BookViewModel
    private lateinit var subject2: BookViewModel


    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        subject = BookViewModel(RepositoryTest(), testDispatcher)
        subject2 = BookViewModel(mockRepository, testDispatcher)

    }

    @Test
    fun `repository test return failure`() {
        //when ->arrange
        val expected = Failure("Test Failure")

        //given ->act
        subject.searchBook("", "", "")
        //then -> assert
        assertEquals(expected, subject.uiState.value)
    }
    //assertEquals(Empty, subject.uiState.value)

    @Test
    fun `initial flow value is empty`() {
        // arrange
       val expected = coEvery {
            mockRepository.getBookByName(
                "", "", ""
            )
        }.returns(flowOf(Empty))
        //act
        subject2.searchBook("", "", "")

        //assert

        assertEquals(subject2.uiState.value, Empty)
        verify {
            mockRepository.getBookByName(
                "", "", ""
            )
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()

    }

}