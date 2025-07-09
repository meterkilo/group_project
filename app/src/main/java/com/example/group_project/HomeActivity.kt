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
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView


class HomeActivity : DrawerBaseActivity() {
    private lateinit var usernameTV : TextView
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
        username = sharedPref.getString("currentUsername", username).toString()
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

        setTitle("Home")

        // instantiating views and buttons
        usernameTV = findViewById(R.id.username)
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

        usernameTV.text = "Hi, $username!"
    }

    override fun onStart() {
        super.onStart()
        FirebaseDB.getUser(username) { user ->  balanceTV.text = "Balance: $" + user!!.balance }
        val bal = sharedPref.getInt("balance", 5000)
        balanceTV.text = "$$bal"
    }

    override fun onResume() {
        super.onResume()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, HomeActivity.getHistory())
        localGameHistoryLV.adapter = adapter
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
            if(net < 0){//if negative money
                gameHistory.add(0, result + " -$" + (net*-1).toString())
            }
            else{
                gameHistory.add(0, result + " +$" + (net).toString())
            }
        }

        fun getHistory(): MutableList<String>{
            return gameHistory
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