package com.example.group_project

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LeaderboardActivity : DrawerBaseActivity() {
    private lateinit var leaderboardContainer: LinearLayout
    private lateinit var container : FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle("Leaderboard")

        container = findViewById(R.id.activityContainer)

        layoutInflater.inflate(R.layout.activity_leaderboard, container, true)
        //setContentView(R.layout.activity_leaderboard)

        leaderboardContainer = findViewById(R.id.leaderboardContainer)

        loadLeaderboard()
    }

     fun loadLeaderboard() {
        FirebaseDB.getLeaderboard { users ->
            displayLeaderboard(users)
        }
    }

     fun displayLeaderboard(users: List<User>) {
         //refreshes lb every time activity is open
        leaderboardContainer.removeAllViews()

        if (users.isEmpty()) {
            val emptyView = TextView(this)
            emptyView.text = "No players found."
            emptyView.textSize = 18f
            leaderboardContainer.addView(emptyView)
            return
        }

        for ((index, user) in users.withIndex()) {
            val itemView = layoutInflater.inflate(R.layout.item_leaderboard_user, leaderboardContainer, false)

            val rankView = itemView.findViewById<TextView>(R.id.textRank)
            val usernameView = itemView.findViewById<TextView>(R.id.textUsername)
            val balanceView = itemView.findViewById<TextView>(R.id.textBalance)
            val locationView = itemView.findViewById<TextView>(R.id.textLocation)

            val rankNumber = index + 1
            val medalEmoji = when (rankNumber) {
                1 -> "\uD83E\uDD47"
                2 -> "\uD83E\uDD48"
                3 -> "\uD83E\uDD49"
                else -> ""
            }
            rankView.text = "$medalEmoji $rankNumber"
            usernameView.text = user.username
            balanceView.text = "Balance: \uD83D\uDCB2${user.balance}"
            locationView.text = user.location

            leaderboardContainer.addView(itemView)
        }
    }
}
