package es.rudo.myapplication.data

import es.rudo.myapplication.model.Cheese
import es.rudo.myapplication.model.Hamburger
import es.rudo.myapplication.model.HamburgerBread

object RefrigeratorDataSource {
    fun getHamburgerBread(): HamburgerBread {
        val isExpired = false
        return HamburgerBread(isExpired = isExpired)
    }
    fun getHamburger(): Hamburger {
        val isExpired = (0..1).random() == 0
        return Hamburger(isExpired = isExpired)
    }
    fun getCheese(): Cheese {
        val isExpired = (0..1).random() == 0
        return Cheese(isExpired = isExpired)
    }
}