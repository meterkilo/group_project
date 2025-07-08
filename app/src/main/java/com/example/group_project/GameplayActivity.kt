package com.example.group_project

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GameplayActivity: AppCompatActivity() {

    private lateinit var dealerRV: RecyclerView
    private lateinit var playerRV: RecyclerView
    private lateinit var hitBTN: Button
    private lateinit var standBTN: Button
    private lateinit var backBTN: Button
    private lateinit var balanceTextView: TextView

    private val dealerAdapter = CardAdapter()
    private val playerAdapter  = CardAdapter()


    private val vm: GamePlayViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gameplay)

        dealerRV = findViewById(R.id.dealerRecycler)
        playerRV = findViewById(R.id.playerRecycler)
        hitBTN = findViewById(R.id.hit_button)
        standBTN =findViewById(R.id.stand_button)
        backBTN = findViewById(R.id.leave_game_button)
        balanceTextView = findViewById(R.id.game_balance)

        dealerRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        dealerRV.adapter = dealerAdapter

        playerRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        playerRV.adapter = playerAdapter

        vm.dealerr_cards.observe(this) {cards -> dealerAdapter.update(cards)  }
        vm.player_cards.observe(this) {cards -> playerAdapter.update(cards)  }
        vm.balance.observe(this) {bal -> balanceTextView.text = "$$bal"  }

        vm.result.observe(this){ result ->
            result?.let{
                val msg = when (it.outcome){
                    Outcome.PLAYER_BLACKJACK, Outcome.PLAYER_WIN -> "You Win! +${it.netChange}"
                    Outcome.DEALER_WIN , Outcome.PLAYER_BUST-> "Yoy Lose -${it.netChange}"
                    Outcome.PUSH -> "Push."
                }
                balanceTextView.text = "Balance: ${it.finalBalance}"
                hitBTN.isEnabled =false
                standBTN.isEnabled = false
            }
        }

        hitBTN.setOnClickListener {vm.hit() }
        standBTN.setOnClickListener { vm.stand() }
        backBTN.setOnClickListener { finish() }

        //need to enter a edit text or something here
        vm.startRound(bet = 50)

        hitBTN.isEnabled = true
        standBTN.isEnabled = true






    }
}