package es.rudo.myapplication.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import es.rudo.myapplication.R
import es.rudo.myapplication.data.RefrigeratorDataSource
import es.rudo.myapplication.data.exceptions.CookingException
import es.rudo.myapplication.model.Cheese
import es.rudo.myapplication.model.Food
import es.rudo.myapplication.model.Hamburger
import es.rudo.myapplication.model.HamburgerBread

typealias NastyHamburgerBread = CookingException.NastyHamburgerBread
typealias NastyHamburger = CookingException.NastyHamburger
typealias NastyCheese = CookingException.NastyCheese

class MainViewModel : ViewModel() {
    val foodStack = mutableListOf<Food>()
    private val _isFoodDone = MutableLiveData<Boolean>()
    val isFoodDone: LiveData<Boolean> = _isFoodDone
    private val _breadError = MutableLiveData<Int>()
    val messageError: LiveData<Int> = _breadError

    fun addBread() {
        takeHamburgerBread().fold(
            ifLeft = {
                _breadError.value = R.string.error_bread
                foodStack.add(it)
            },
            ifRight = {
                foodStack.add(it)
            }
        )
    }

    fun addBurger() {
        takeHamburger().fold(
            ifLeft = {
                _breadError.value = R.string.error_burger
                foodStack.add(it)
            },
            ifRight = {
                foodStack.add(it)
            }
        )
    }

    fun addCheese() {
        takeCheese().fold(
            ifLeft = {
                _breadError.value = R.string.error_cheese
                foodStack.add(it)
            },
            ifRight = {
                foodStack.add(it)
            }
        )
    }

    fun takeHamburgerBread(): Either<NastyHamburgerBread, HamburgerBread> =
        if (RefrigeratorDataSource.getHamburgerBread().isExpired) {
            CookingException.NastyHamburgerBread.left()
        } else {
            RefrigeratorDataSource.getHamburgerBread().right()
        }

    fun takeHamburger(): Either<NastyHamburger, Hamburger> {
        val hamburger = RefrigeratorDataSource.getHamburger()
        return if (hamburger.isExpired) {
            Either.Left(CookingException.NastyHamburger)
        } else {
            hamburger.right()
        }
    }

    fun takeCheese(): Either<NastyCheese, Cheese> {
        val cheese = RefrigeratorDataSource.getCheese()
        return if (cheese.isExpired) {
            Either.Left(CookingException.NastyCheese)
        } else {
            Either.Right(cheese)
        }
    }

    fun checkIfEnoughIngredients(): Boolean {
        val checkBread: Boolean = foodStack.any {
            it is HamburgerBread || it is NastyHamburgerBread
        }
        val checkBurger: Boolean = foodStack.any {
            it is Hamburger || it is NastyHamburger
        }
        return checkBread and checkBurger
    }

    fun cookIngredients() {
        sortFoodStack()
        _isFoodDone.value = true
    }

    private fun sortFoodStack() {
        val bread = foodStack.first {
            it is HamburgerBread || it is NastyHamburgerBread
        }
        foodStack.removeAll {
            it is HamburgerBread || it is NastyHamburgerBread
        }
        foodStack.add(0, bread)
        foodStack.add(bread)
    }

    fun countAmountBread(): Int {
        return foodStack.count {
            it is HamburgerBread || it is NastyHamburgerBread
        }
    }

    fun countAmountBurger(): Int {
        return foodStack.count {
            it is Hamburger || it is NastyHamburger
        }
    }

    fun countAmountCheese(): Int {
        return foodStack.count {
            it is Cheese || it is NastyCheese
        }
    }
}