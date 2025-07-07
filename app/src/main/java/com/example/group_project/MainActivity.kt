package com.example.group_project

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var localGameHistoryLV : ListView
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        //Retrieve Color Theme from preferences before creation
        sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        if(!sharedPref.contains("colorTheme")){
            sharedPref.edit().putString("colorTheme", "light").apply()
        }
        when {
            sharedPref.getString("colorTheme","light")!! == "light" -> setTheme(R.style.Theme_lightTheme)
            sharedPref.getString("colorTheme","light")!! == "dark" -> setTheme(R.style.Theme_darkTheme)
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }
        setupThemeButtons()
        localGameHistoryLV = findViewById(R.id.local_history)
    }

    private fun setupThemeButtons() {
        findViewById<AppCompatButton>(R.id.light_theme_button).setOnClickListener {
            sharedPref.edit().putString("colorTheme", "light").apply()
            recreate() // this will trigger onCreate and apply the theme there
        }

        findViewById<AppCompatButton>(R.id.dark_theme_button).setOnClickListener {
            sharedPref.edit().putString("colorTheme", "dark").apply()
            recreate()
        }
    }

}