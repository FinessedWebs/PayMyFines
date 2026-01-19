package com.example.paymyfinesstep

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.paymyfinesstep.api.ApiBackend
import com.example.paymyfinesstep.api.NotificationsApi
import com.example.paymyfinesstep.cart.CartManager
import com.example.paymyfinesstep.databinding.ActivityMainBinding
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNav: BottomNavigationView
    private var cartBadge: TextView? = null

    private val navController by lazy {
        (supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment_activity_main
        ) as NavHostFragment).navController
    }

    private val notificationsApi by lazy {
        ApiBackend.create(this, NotificationsApi::class.java)
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
        // NAV GRAPH START
        // ---------------------------
        val graph = navController.navInflater.inflate(R.navigation.nav_graph)
        graph.setStartDestination(R.id.loginFragment)
        navController.graph = graph

        // ---------------------------
        // DESTINATION UI CONTROL
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

            // Update cart badge only when visible
            if (showTopMenu) {
                updateCartBadge()
            }

            // ✅ Refresh notification badge when switching screens
            if (destination.id == R.id.notificationsFragment ||
                destination.id == R.id.homeFragment ||
                destination.id == R.id.aboutFragment
            ) {
                refreshNotificationsBadge()
            }
        }
    }

    // ---------------------------
    // MENU (TOP RIGHT)
    // ---------------------------
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_top, menu)

        val cartItem = menu.findItem(R.id.action_cart)
        val actionView = cartItem.actionView ?: return true

        cartBadge = actionView.findViewById(R.id.textCartBadge)

        actionView.setOnClickListener {
            onOptionsItemSelected(cartItem)
        }

        updateCartBadge()
        return true
    }

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
    // BADGES
    // ---------------------------
    override fun onResume() {
        super.onResume()

        // refresh both
        updateCartBadge()
        refreshNotificationsBadge()
    }

    fun refreshCartBadge() {
        updateCartBadge()
    }

    private fun updateCartBadge() {
        try {
            val count = CartManager.getCart(this).size

            if (count > 0) {
                cartBadge?.visibility = View.VISIBLE
                cartBadge?.text = count.toString()
            } else {
                cartBadge?.visibility = View.GONE
            }
        } catch (_: Exception) {
            cartBadge?.visibility = View.GONE
        }
    }

    // ✅ Bottom navigation notification badge
    fun refreshNotificationsBadge() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val resp = notificationsApi.getUnreadCount()

                if (!resp.isSuccessful || resp.body() == null) return@launch

                val unread = resp.body()!!.unread

                withContext(Dispatchers.Main) {
                    applyBottomNavBadge(unread)
                }

            } catch (_: Exception) {
                // if fails, hide badge (optional)
                withContext(Dispatchers.Main) {
                    applyBottomNavBadge(0)
                }
            }
        }
    }

    private fun applyBottomNavBadge(unread: Int) {
        val menuId = R.id.notificationsFragment

        if (unread <= 0) {
            bottomNav.removeBadge(menuId)
            return
        }

        val badge: BadgeDrawable = bottomNav.getOrCreateBadge(menuId)
        badge.isVisible = true
        badge.number = unread
        badge.maxCharacterCount = 3
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

        // remove badges
        bottomNav.removeBadge(R.id.notificationsFragment)
        cartBadge?.visibility = View.GONE

        navController.navigate(
            R.id.loginFragment,
            null,
            NavOptions.Builder()
                .setPopUpTo(R.id.nav_graph, true)
                .build()
        )
    }
}
