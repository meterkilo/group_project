package com.example.group_project

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

open class DrawerBaseActivity : AppCompatActivity() {
    private lateinit var drawerLayout : DrawerLayout
    private lateinit var toolBar : Toolbar
    private lateinit var navigationView : NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_drawer_base)
        drawerLayout = findViewById(R.id.drawer_layout)
        toolBar = findViewById(R.id.tool_bar)

        navigationView = findViewById(R.id.navigation_view)
        var navListener : NavListener = NavListener()
        navigationView.setNavigationItemSelectedListener(navListener)

        var toggle : ActionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolBar, R.string.open_drawer, R.string.close_drawer)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    fun selectActivity(item: String) {
        if (item == "Home") {
            var intent : Intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        } else if (item == "Play") {
            var intent : Intent = Intent(this, GameplayActivity::class.java)
            startActivity(intent)
        } else if (item == "Leaderboard") {
            var intent : Intent = Intent(this, LeaderboardActivity::class.java)
            startActivity(intent)
        }

    }

    fun setTitle(title : String) {
        if (supportActionBar != null) {
            supportActionBar!!.title = title
        }
    }

    inner class NavListener : NavigationView.OnNavigationItemSelectedListener {
        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            this@DrawerBaseActivity.selectActivity(item.title.toString())
            drawerLayout.closeDrawers()
            return true
        }
    }
}
