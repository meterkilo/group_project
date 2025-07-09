package com.example.group_project

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.graphics.Color
import android.widget.Button
import android.widget.LinearLayout


class HomeActivity : AppCompatActivity() {

    private lateinit var balanceTV : TextView
    private lateinit var themeToggleButton: Button
    private lateinit var playButton : Button
    private lateinit var leaderboardButton : Button
    private lateinit var sharedPref: SharedPreferences
    private lateinit var usernameTV : TextView
    private lateinit var historyContainer: LinearLayout



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
        usernameTV = findViewById(R.id.username_tv)
        balanceTV = findViewById(R.id.balance_tv)
        themeToggleButton = findViewById(R.id.theme_toggle_button)
        playButton = findViewById(R.id.play_button)
        leaderboardButton = findViewById(R.id.leaderboard_button)
        historyContainer = findViewById(R.id.history_container)

        setupThemeButtons()

        // listener for buttons
        var listener : Listener = Listener()
        playButton.setOnClickListener(listener)
        leaderboardButton.setOnClickListener(listener)

    }

    override fun onResume() {
        super.onResume()

        val currentTheme = sharedPref.getString("colorTheme", "light")
        themeToggleButton.text = if (currentTheme == "light") "Switch to Dark Mode" else "Switch to Light Mode"

        val currentUser = sharedPref.getString("currentUsername", null)
        if (currentUser != null) {
            usernameTV.text = "Hi, $currentUser"

            FirebaseDB.getUser(currentUser) { user ->
                if (user != null) {
                    runOnUiThread {
                        balanceTV.text = "Balance: \uD83D\uDCB2${user.balance}"
                    }
                } else {
                    runOnUiThread {
                        balanceTV.text = "Balance: Unknown"
                    }
                }
            }
        } else {
            usernameTV.text = "Username: Unknown"
            balanceTV.text = "Balance: Unknown"
        }

        populateHistory()

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
    //companion object for game history
    companion object {
        private val gameHistory: MutableList<String> = mutableListOf<String>()

        fun addHistory(net: Int, result: String){
            val outcome = if (net < 0) "Lost" else "Won"
            val entry = "$result - $outcome: $${kotlin.math.abs(net)}"
            gameHistory.add(0, entry)
        }


        fun getHistory(): MutableList<String>{
            return gameHistory
        }

    }

    private fun setupThemeButtons() {
        themeToggleButton.setOnClickListener {
            val currentTheme = sharedPref.getString("colorTheme", "light") ?: "light"
            val newTheme = if (currentTheme == "light") "dark" else "light"
            sharedPref.edit().putString("colorTheme", newTheme).apply()
            recreate()
        }
    }

    private fun populateHistory() {
        historyContainer.removeAllViews()

        val currentTheme = sharedPref.getString("colorTheme", "light") ?: "light"
        val historyList = HomeActivity.getHistory()

        for (entry in historyList) {
            val textView = TextView(this)
            textView.text = entry
            textView.textSize = 16f
            textView.setPadding(8, 8, 8, 8)

            // Apply theme colors
            if (currentTheme == "dark") {
                textView.setBackgroundColor(Color.parseColor("#333333"))
                textView.setTextColor(Color.parseColor("#FFFFFF"))
            } else {
                textView.setBackgroundColor(Color.parseColor("#EEEEEE"))
                textView.setTextColor(Color.parseColor("#000000"))
            }

            // Add to container
            historyContainer.addView(textView)
        }
    }



}