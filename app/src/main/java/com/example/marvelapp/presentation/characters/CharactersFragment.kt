package com.example.marvelapp.presentation.characters

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import com.example.core.domain.model.Character
import com.example.marvelapp.R
import com.example.marvelapp.databinding.FragmentCharactersBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CharactersFragment : Fragment() {

    private lateinit var binding: FragmentCharactersBinding
    private lateinit var charactersAdapter: CharactersAdapter
    private val viewModel: CharactersViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentCharactersBinding.inflate(inflater, container, false).apply {
        binding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCharacters()
        observeInitialLoadState()

        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.characterPagingData("").collect { pagingData ->
                    charactersAdapter.submitData(pagingData)
                }
            }
        }
    }
    private fun initCharacters(){
        charactersAdapter = CharactersAdapter()
        binding.recyclerCharacters.run{
            scrollToPosition(0)
            setHasFixedSize(true)
            adapter = charactersAdapter.withLoadStateFooter(
                footer = CharactersLoadingStateAdapter(
                    charactersAdapter::retry
                )
            )

        }
    }
    private fun observeInitialLoadState(){
        lifecycleScope.launch {
            charactersAdapter.loadStateFlow.collectLatest { loadState ->
               binding.flipperCharacters.displayedChild =  when (loadState.refresh){
                    is LoadState.Loading -> {
                        setShimmerVisibility(true)
                        FLIPPER_CHILD_LOADING
                    }
                   is LoadState.NotLoading -> {
                       setShimmerVisibility(false)
                       FLIPPER_CHILD_CHARACTERS
                   }
                   is LoadState.Error -> {
                       setShimmerVisibility(false)
                       binding.includeViewCharactersErrorState.buttonRetry.setOnClickListener {
                           charactersAdapter.refresh()
                       }
                       FLIPPER_CHILD_ERROR
                   }
                }
            }
        }
    }

    private fun setShimmerVisibility(visibility: Boolean){
        binding.includeViewCharactersLoadingState.shimmerCharacters.run {
            isVisible = visibility
            if(visibility){
                startShimmer()
            } else stopShimmer()
        }
    }

    companion object {
        private const val FLIPPER_CHILD_LOADING = 0
        private const val FLIPPER_CHILD_CHARACTERS = 1
        private const val FLIPPER_CHILD_ERROR = 2
    }
}