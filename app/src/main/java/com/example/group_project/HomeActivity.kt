package com.example.group_project

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.Button

class HomeActivity : AppCompatActivity() {
    private lateinit var displayNameET : EditText
    private lateinit var balanceTV : TextView
    private lateinit var lightThemeButton : Button
    private lateinit var darkThemeButton : Button
    private lateinit var playButton : Button
    private lateinit var leaderboardButton : Button
    private lateinit var localGameHistoryLV : ListView
    private var username : String = ""
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
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // instantiating views and buttons
        displayNameET = findViewById(R.id.username)
        balanceTV = findViewById(R.id.balance_tv)
        lightThemeButton = findViewById(R.id.light_theme_button)
        darkThemeButton = findViewById(R.id.dark_theme_button)
        playButton = findViewById(R.id.play_button)
        leaderboardButton = findViewById(R.id.leaderboard_button)
        localGameHistoryLV = findViewById(R.id.local_history)

        setupThemeButtons()

        // listener for buttons
        var listener : Listener = Listener()
        playButton.setOnClickListener(listener)
        leaderboardButton.setOnClickListener(listener)

        // listener for username
        var textHandler : TextHandler = TextHandler()
        displayNameET.addTextChangedListener(textHandler)
    }

    override fun onResume() {
        super.onResume()
        val bal = sharedPref.getInt("balance",1000)
        balanceTV.text = "$$bal"
    }

    fun updateUsername() {
        username = displayNameET.text.toString()
        Log.w("Main", "Username : $username")
    }

    inner class Listener : View.OnClickListener {
        override fun onClick(v: View?) {
            if (v == playButton) {
                var intent : Intent = Intent(this@HomeActivity, GameplayActivity::class.java)
                startActivity(intent)
            } else if (v == leaderboardButton) {
                var intent : Intent = Intent(this@HomeActivity, LeaderboardActivity::class.java)
                startActivity(intent)
            }
        }
    }

    inner class TextHandler : TextWatcher {
        override fun beforeTextChanged(
            s: CharSequence?,
            start: Int,
            count: Int,
            after: Int
        ) {
            // does nothing
        }

        override fun onTextChanged(
            s: CharSequence?,
            start: Int,
            before: Int,
            count: Int
        ) {
            // does nothing
        }

        override fun afterTextChanged(s: Editable?) {
            this@HomeActivity.updateUsername()
        }


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