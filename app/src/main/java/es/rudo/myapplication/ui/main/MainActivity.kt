package es.rudo.myapplication.ui.main

import android.animation.Animator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import es.rudo.myapplication.R
import es.rudo.myapplication.databinding.ActivityMainBinding
import es.rudo.myapplication.model.Cheese
import es.rudo.myapplication.model.Hamburger
import es.rudo.myapplication.model.HamburgerBread
import es.rudo.myapplication.utils.Constants

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MainViewModel

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setContentView(binding.root)
        initUi()
        initListeners()
        initObservers()
    }

    private fun initUi() = with(binding) {
        buttonRemoveBread.visibility = View.GONE
        buttonRemoveBurger.visibility = View.GONE
        buttonRemoveCheese.visibility = View.GONE

        textAmountBread.text = getString(R.string.default_amount)
        textAmountBurger.text = getString(R.string.default_amount)
        textAmountCheese.text = getString(R.string.default_amount)

        gridLayoutHamburger.removeAllViews()
        gridLayoutHamburger.visibility = View.GONE

        lottieLoader.visibility = View.GONE
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initListeners() = with(binding) {
        buttonAddBread.setOnClickListener {
            if (viewModel.countAmountBread() < Constants.MAX_AMOUNT_BREAD ) {
                viewModel.addBread()
                updateAmountBread()
            }
        }
        buttonAddBurger.setOnClickListener {
            if (viewModel.countAmountBurger() < Constants.MAX_AMOUNT_BURGER ) {
                viewModel.addBurger()
                updateAmountBurger()
            }
        }
        buttonAddCheese.setOnClickListener {
            if (viewModel.countAmountCheese() < Constants.MAX_AMOUNT_CHEESE ) {
                viewModel.addCheese()
                updateAmountCheese()
            }
        }
        buttonRemoveBread.setOnClickListener {
            viewModel.foodStack.removeIf {
                it is HamburgerBread || it is NastyHamburgerBread
            }
            updateAmountBread()
        }
        buttonRemoveBurger.setOnClickListener {
            viewModel.foodStack.removeIf {
                it is Hamburger || it is NastyHamburger
            }
            updateAmountBurger()
        }
        buttonRemoveCheese.setOnClickListener {
            viewModel.foodStack.removeIf {
                it is Cheese || it is NastyCheese
            }
            updateAmountCheese()
        }
        buttonToCook.setOnClickListener {
            if (viewModel.checkIfEnoughIngredients()) {
                showLoader()
            } else {
                hideLoader()
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.amount_insufficient),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        binding.lottieLoader.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {
            }

            override fun onAnimationEnd(p0: Animator) {
                viewModel.cookIngredients()
            }

            override fun onAnimationCancel(p0: Animator) {
            }

            override fun onAnimationRepeat(p0: Animator) {
            }
        })
    }

    private fun initObservers() {
        viewModel.isFoodDone.observe(this) { isFoodDone ->
            binding.gridLayoutHamburger.removeAllViews()
            if (isFoodDone) {
                viewModel.foodStack.forEachIndexed { index, food ->
                    val imageView = ImageView(this@MainActivity)
                    imageView.layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE

                    when (food) {
                        is HamburgerBread,
                        is NastyHamburgerBread -> {
                            if (index == 0) {
                                imageView.setImageResource(R.drawable.pan_superior)
                            } else {
                                imageView.setImageResource(R.drawable.pan_inferior)
                            }
                        }
                        is Hamburger -> imageView.setImageResource(R.drawable.hamburger)
                        is NastyHamburger -> imageView.setImageResource(R.drawable.hamburger_mala)
                        is Cheese -> imageView.setImageResource(R.drawable.queso)
                        is NastyCheese -> imageView.setImageResource(R.drawable.queso_malo)
                    }
                    binding.gridLayoutHamburger.addView(imageView)
                }
                hideLoader()
            }
        }

        viewModel.messageError.observe(this) { id ->
            if (id != 0) {
                Toast.makeText(this@MainActivity, getString(id), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showLoader() = with(binding) {
        gridLayoutHamburger.visibility = View.GONE
        lottieLoader.visibility = View.VISIBLE
        lottieLoader.playAnimation()
    }

    private fun hideLoader() = with(binding) {
        gridLayoutHamburger.visibility = View.VISIBLE
        lottieLoader.visibility = View.GONE
    }

    private fun updateAmountBread() {
        val count = viewModel.countAmountBread()
        binding.textAmountBread.text = getString(R.string.amount_format, count.toString())
        binding.buttonRemoveBread.visibility = if (count == 0) View.GONE else View.VISIBLE
    }

    private fun updateAmountBurger() {
        val count = viewModel.countAmountBurger()
        binding.textAmountBurger.text = getString(R.string.amount_format, count.toString())
        binding.buttonRemoveBurger.visibility = if (count == 0) View.GONE else View.VISIBLE
    }

    private fun updateAmountCheese() {
        val count = viewModel.countAmountCheese()
        binding.textAmountCheese.text = getString(R.string.amount_format, count.toString())
        binding.buttonRemoveCheese.visibility = if (count == 0) View.GONE else View.VISIBLE
    }
}