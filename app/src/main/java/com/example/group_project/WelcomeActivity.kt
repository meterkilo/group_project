package com.example.group_project

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices



class WelcomeActivity : AppCompatActivity() {

    private lateinit var usernameInput: EditText
    private lateinit var continueButton: Button
    private lateinit var adView: AdView

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var pendingUsername: String? = null

    private val LOCATION_PERMISSION_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        //load animation
        val splashImage = findViewById<ImageView>(R.id.splashImage)
        val animation = AnimationUtils.loadAnimation(this, R.anim.bounce)
        splashImage.startAnimation(animation)

        // create banner ad
        MobileAds.initialize(this)
        adView = findViewById(R.id.adView)
        adView.loadAd(AdRequest.Builder().build())

        // location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // button references
        usernameInput = findViewById(R.id.username_input)
        continueButton = findViewById(R.id.continue_button)

        //textenter handler
        continueButton.setOnClickListener {
            val enteredUsername = usernameInput.text.toString().trim()
            if (enteredUsername.isEmpty()) {
                Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show()
            } else {
                checkIfUserExistsOrCreate(enteredUsername)
            }
        }
    }

    private fun checkIfUserExistsOrCreate(username: String) {
        FirebaseDB.getUser(username) { user ->
            if (user != null) {
                Log.d("WelcomeActivity", "User exists: $username")
                saveCurrentUsername(username)
                goToHomeActivity()
            } else {
                Log.d("WelcomeActivity", "User doesn't exist. Creating new user...")
                pendingUsername = username
                requestLocationPermission()
            }
        }
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            pendingUsername?.let { getLocationAndCreateUser(it) }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pendingUsername?.let { getLocationAndCreateUser(it) }
            } else {
                // Permission denied ➜ create with "Unknown"
                pendingUsername?.let { createNewUser(it, "Unknown") }
            }
        }
    }

    //gets country from geocaching
    private fun getLocationAndCreateUser(username: String) {
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        Thread {
                            val geocoder = Geocoder(this)
                            val country = try {
                                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                                if (!addresses.isNullOrEmpty()) {
                                    addresses[0].countryName ?: "Unknown"
                                } else {
                                    "Unknown"
                                }
                            } catch (e: Exception) {
                                "Unknown"
                            }

                            runOnUiThread {
                                createNewUser(username, country)
                            }
                        }.start()
                    } else {
                        createNewUser(username, "Unknown")
                    }
                }
                .addOnFailureListener {
                    createNewUser(username, "Unknown")
                }
        } catch (e: SecurityException) {
            createNewUser(username, "Unknown")
        }
    }




    private fun createNewUser(username: String, location: String) {
        val newUser = User(username = username, balance = 5000.0, location = location)
        FirebaseDB.setUser(newUser)
        saveCurrentUsername(username)
        Toast.makeText(this, "New user created!", Toast.LENGTH_SHORT).show()
        goToHomeActivity()
    }

    private fun saveCurrentUsername(username: String) {
        val prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        prefs.edit().putString("currentUsername", username).apply()
    }

    private fun goToHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
