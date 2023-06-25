package com.example.core.useCase

import androidx.paging.PagingConfig
import com.example.core.data.repository.CharactersRepository
import com.example.core.domain.model.Character
import com.example.core.usecase.GetCharactersUseCase
import com.example.core.usecase.GetCharactersUseCaseImpl
import com.example.model.CharacterFactory
import com.example.paginsource.PagingSourceFactory
import com.example.testing.MainCoroutineRule
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class GetCharactersUseCaseTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    lateinit var repository: CharactersRepository

    private lateinit var getCharactersUseCase: GetCharactersUseCase

    private val hero = CharacterFactory().create(CharacterFactory.Hero.ThreeDMan)

    private val fakePagingSource = PagingSourceFactory().create(listOf(hero))

    @Before
    fun setup(){
        getCharactersUseCase = GetCharactersUseCaseImpl(repository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `should validate flow paging data creation when invoke from use case is called`() =
        runTest{
        whenever(repository.getCharacters("")).thenReturn(fakePagingSource)
        val result = getCharactersUseCase.invoke(
            GetCharactersUseCase.GetCharactersParams("",
                PagingConfig(20)
            ))
        verify(repository).getCharacters("")
        assertNotNull(result.first())
    }
}