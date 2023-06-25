package com.example.marvelapp.framework.paging

import androidx.paging.PagingSource
import com.example.core.data.repository.CharactersRemoteDataSource
import com.example.core.domain.model.Character
import com.example.marvelapp.factoryresponse.DataWrapperResponseFactory
import com.example.marvelapp.framework.network.response.DataWrapperResponse
import com.example.model.CharacterFactory
import com.example.testing.MainCoroutineRule
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class CharactersPagingSourceTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    lateinit var remoteDataSource: CharactersRemoteDataSource<DataWrapperResponse>

    private val dataWrapperResponseFactory = DataWrapperResponseFactory()

    private lateinit var charactersPagingSource: CharactersPagingSource

    private val characterFactory = CharacterFactory()

    @Before
    fun setup() {
        charactersPagingSource = CharactersPagingSource(remoteDataSource, "")
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should return a success load result when load is called`() = runTest {

        whenever(remoteDataSource.fetchCharacters(any())).thenReturn(dataWrapperResponseFactory.create())

        val result = charactersPagingSource.load(
            PagingSource.LoadParams.Refresh(
                null,
                loadSize = 2,
                false
            )
        )
        val expected = listOf(
            characterFactory.create(CharacterFactory.Hero.ThreeDMan),
            characterFactory.create(CharacterFactory.Hero.ABomb)
        )
        assertEquals(
            PagingSource.LoadResult.Page(
                data = expected,
                prevKey = null,
                nextKey = 20
            ),
            result
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should return a error load result when load is called`() = runTest {
        val exception = RuntimeException()
        whenever(remoteDataSource.fetchCharacters(any())).thenThrow(exception)

      val result =  charactersPagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 2,
                placeholdersEnabled = false
            )
        )
        assertEquals(
            PagingSource.LoadResult.Error<Int,Character>(exception),
            result
        )
    }
}