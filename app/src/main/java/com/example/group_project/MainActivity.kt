package com.example.group_project

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var displayNameET : EditText
    private lateinit var balanceTV : TextView
    private lateinit var lightThemeButton : Button
    private lateinit var darkThemeButton : Button
    private lateinit var playButton : Button
    private lateinit var leaderboardButton : Button
    private lateinit var localGameHistoryLV : ListView
    private var username : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
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

        // listener for buttons
        var listener : Listener = Listener()
        playButton.setOnClickListener(listener)
        leaderboardButton.setOnClickListener(listener)

        // listener for username
        var textHandler : TextHandler = TextHandler()
        displayNameET.addTextChangedListener(textHandler)
    }

    fun updateUsername() {
        username = displayNameET.text.toString()
        Log.w("Main", "Username : $username")
    }

    inner class Listener : View.OnClickListener {
        override fun onClick(v: View?) {
            if (v == playButton) {
                var intent : Intent = Intent(this@MainActivity, Blackjack::class.java)
                startActivity(intent)
            } else if (v == leaderboardButton) {
                var intent : Intent = Intent(this@MainActivity, Leaderboard::class.java)
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
            this@MainActivity.updateUsername()
        }

    }
}