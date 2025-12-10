package com.example.paymyfinesstep

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.paymyfinesstep.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set toolbar
        setSupportActionBar(binding.homeToolbar)

        // Read token
        val prefs = getSharedPreferences("paymyfines_prefs", MODE_PRIVATE)
        val token = prefs.getString("jwt_token", null)

        // Nav host
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment

        val navController = navHostFragment.navController

        bottomNav = findViewById(R.id.bottom_nav)
        bottomNav.setupWithNavController(navController)

        // Inflate graph
        val graph = navController.navInflater.inflate(R.navigation.nav_graph)

        // Always start at login
        graph.setStartDestination(R.id.loginFragment)
        navController.graph = graph

        // Connect bottom nav
        binding.bottomNav.setupWithNavController(navController)

        // HIDE / SHOW BOTH TOP & BOTTOM NAV
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {

                // Hide BOTH toolbar and bottom nav
                R.id.loginFragment,
                R.id.signupFragment -> {
                    binding.homeToolbar.visibility = View.GONE
                    binding.bottomNav.visibility = View.GONE
                }

                // Show both on main pages
                R.id.homeFragment,
                R.id.familyFragment,
                R.id.vehicleFragment,
                R.id.notificationsFragment -> {
                    binding.homeToolbar.visibility = View.VISIBLE
                    binding.bottomNav.visibility = View.VISIBLE
                }

                // Default: hide bottom nav, show toolbar
                else -> {
                    binding.homeToolbar.visibility = View.VISIBLE
                    binding.bottomNav.visibility = View.GONE
                }
            }
        }

        println("TOOLBAR VIEW = " + binding.homeToolbar)
    }
}
