package com.example.paymyfinesstep

import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.paymyfinesstep.cart.CartManager
import com.example.paymyfinesstep.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val cartPrefs by lazy {
        getSharedPreferences(CartManager.PREF_NAME, MODE_PRIVATE)
    }

    private val cartListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == CartManager.cartKey(this)) {
                updateCartBadge()
            }
        }


    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNav: BottomNavigationView
    private var cartBadge: TextView? = null

    private val navController by lazy {
        (supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment_activity_main
        ) as NavHostFragment).navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ---------------------------
        // TOOLBAR
        // ---------------------------
        setSupportActionBar(binding.homeToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // ---------------------------
        // APP BAR CONFIGURATION
        // ---------------------------
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.aboutFragment,
                R.id.notificationsFragment
            )
        )

        binding.homeToolbar.setupWithNavController(navController, appBarConfiguration)

        // ---------------------------
        // BOTTOM NAVIGATION
        // ---------------------------
        bottomNav = binding.bottomNav
        bottomNav.setupWithNavController(navController)

        // ---------------------------
        // NAV GRAPH
        // ---------------------------
        val graph = navController.navInflater.inflate(R.navigation.nav_graph)
        graph.setStartDestination(R.id.loginFragment)
        navController.graph = graph

        // ---------------------------
        // DESTINATION-BASED UI CONTROL
        // ---------------------------
        navController.addOnDestinationChangedListener { _, destination, _ ->

            val showTopMenu = destination.id == R.id.homeFragment ||
                    destination.id == R.id.aboutFragment ||
                    destination.id == R.id.notificationsFragment

            when (destination.id) {
                R.id.loginFragment,
                R.id.signupFragment -> {
                    binding.homeToolbar.visibility = View.GONE
                    binding.bottomNav.visibility = View.GONE
                }
                else -> {
                    binding.homeToolbar.visibility = View.VISIBLE
                    binding.bottomNav.visibility =
                        if (showTopMenu) View.VISIBLE else View.GONE
                }
            }

            // Control toolbar menu items
            binding.homeToolbar.menu.apply {
                findItem(R.id.action_cart)?.isVisible = showTopMenu
                findItem(R.id.action_apply)?.isVisible = showTopMenu
                findItem(R.id.action_settings)?.isVisible = showTopMenu
                findItem(R.id.action_logout)?.isVisible = showTopMenu
            }

            // ✅ Update badge ONLY when toolbar menu is visible
            if (showTopMenu) {
                updateCartBadge()
            }
        }

    }

    // ---------------------------
    // MENU INFLATION (ONCE)
    // ---------------------------
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_top, menu)

        val cartItem = menu.findItem(R.id.action_cart)
        val actionView = cartItem.actionView ?: return true

        cartBadge = actionView.findViewById(R.id.textCartBadge)

        // Forward click from custom actionView
        actionView.setOnClickListener {
            onOptionsItemSelected(cartItem)
        }

        updateCartBadge()
        return true
    }

    // ---------------------------
    // MENU ACTIONS
    // ---------------------------
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.action_cart -> {
                if (navController.currentDestination?.id != R.id.cartFragment) {
                    navController.navigate(R.id.cartFragment)
                }
                true
            }

            R.id.action_settings -> {
                navController.navigate(R.id.settingsFragment)
                true
            }

            R.id.action_logout -> {
                logoutUser()
                true
            }

            R.id.action_apply_reduction -> {
                navController.navigate(R.id.applyReductionFragment)
                true
            }

            R.id.action_apply_redirection -> {
                navController.navigate(R.id.applyRedirectionFragment)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    // ---------------------------
    // BADGE UPDATES
    // ---------------------------

    override fun onStart() {
        super.onStart()
        cartPrefs.registerOnSharedPreferenceChangeListener(cartListener)
    }


    override fun onResume() {
        super.onResume()
        updateCartBadge()
    }

    override fun onStop() {
        super.onStop()
        cartPrefs.unregisterOnSharedPreferenceChangeListener(cartListener)
    }


    /** Call this from fragments after add/remove cart actions */
    fun refreshCartBadge() {
        updateCartBadge()
    }

    private fun updateCartBadge() {
        val count = CartManager.getCart(this).size

        if (count > 0) {
            cartBadge?.visibility = View.VISIBLE
            cartBadge?.text = count.toString()
        } else {
            cartBadge?.visibility = View.GONE
        }
    }

    // ---------------------------
    // LOGOUT
    // ---------------------------
    private fun logoutUser() {
        val prefs = getSharedPreferences("paymyfines_prefs", MODE_PRIVATE)

        prefs.edit()
            .remove("jwt_token")
            .remove("fullName")
            .remove("email")
            .remove("idNumber")
            .apply()

        navController.navigate(
            R.id.loginFragment,
            null,
            NavOptions.Builder()
                .setPopUpTo(R.id.nav_graph, true)
                .build()
        )
    }

}
