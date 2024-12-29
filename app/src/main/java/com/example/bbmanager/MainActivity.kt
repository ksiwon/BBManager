package com.example.bbmanager

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.bbmanager.databinding.ActivityMainBinding
import android.util.Log
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate: MainActivity started")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        // Set up the Floating Action Button
        val fabAdd = findViewById<FloatingActionButton>(R.id.fab_add)
        fabAdd.setOnClickListener {
            Toast.makeText(this, "FAB clicked!", Toast.LENGTH_SHORT).show()
            // Add your desired action here, such as navigating to a new fragment
        }


        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        // Log NavController destination changes
        navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.d("MainActivity", "Navigating to: ${destination.label}")
        }
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_contact, R.id.navigation_gallery, R.id.navigation_broadcast
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}