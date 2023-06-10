package com.example.marvelapp.framework.network.response

import androidx.compose.ui.unit.DpOffset

data class DataContainerResponse(
    val offset: Int,
    val total: Int,
    val results : List<CharacterResponse>
)
