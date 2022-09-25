package es.rudo.myapplication.data.exceptions

import es.rudo.myapplication.model.Food

sealed class CookingException : Food {
    object NastyHamburgerBread: CookingException()
    object NastyHamburger: CookingException()
    object NastyCheese: CookingException()
}
