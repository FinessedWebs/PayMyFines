package com.example.paymyfinesstep

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
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

        // ------------------------------------------------------
        // 1. TOOLBAR (NO TITLE)
        // ------------------------------------------------------
        setSupportActionBar(binding.homeToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // ------------------------------------------------------
        // 2. NAV HOST & CONTROLLER
        // ------------------------------------------------------
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment

        val navController = navHostFragment.navController

        // ------------------------------------------------------
        // 3. APP BAR CONFIG (TOP-LEVEL DESTINATIONS)
        // ------------------------------------------------------
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.aboutFragment,
                R.id.vehicleFragment,
                R.id.notificationsFragment
            )
        )

        binding.homeToolbar.setupWithNavController(navController, appBarConfiguration)

        // ------------------------------------------------------
        // 4. BOTTOM NAV
        // ------------------------------------------------------
        bottomNav = binding.bottomNav
        bottomNav.setupWithNavController(navController)



        // ------------------------------------------------------
        // 5. NAV GRAPH
        // ------------------------------------------------------
        val graph = navController.navInflater.inflate(R.navigation.nav_graph)
        graph.setStartDestination(R.id.loginFragment)
        navController.graph = graph

        // ------------------------------------------------------
        // 6. SHOW / HIDE TOOLBAR & BOTTOM NAV
        // ------------------------------------------------------
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {

                R.id.loginFragment,
                R.id.signupFragment -> {
                    binding.homeToolbar.visibility = View.GONE
                    binding.bottomNav.visibility = View.GONE
                }

                R.id.homeFragment,
                R.id.aboutFragment,
                R.id.vehicleFragment,
                R.id.notificationsFragment -> {
                    binding.homeToolbar.visibility = View.VISIBLE
                    binding.bottomNav.visibility = View.VISIBLE
                }

                else -> {
                    binding.homeToolbar.visibility = View.VISIBLE
                    binding.bottomNav.visibility = View.GONE
                }
            }
        }
    }
}
