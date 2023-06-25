package com.example.model

import com.example.core.domain.model.Character

class CharacterFactory {

    fun create(hero: Hero) = when(hero) {
        Hero.ThreeDMan -> Character(
            "3-D Man",
            "https://i.annihil.us/u/prod/marvel/i/mg/c/e0/535fecbbb9784.jpg"
        )
        Hero.ABomb -> Character(
            "A-Bomb (HAS)",
            "https://i.annihil.us/u/prod/marvel/i/mg/c/e0/535fecbbb9716.jpg"
        )
    }

    sealed class Hero {
        object ThreeDMan : Hero()
        object ABomb : Hero()
    }
}