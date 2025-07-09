package com.example.group_project

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.app.AlertDialog

class GameplayActivity: AppCompatActivity() {

    private lateinit var dealerRV: RecyclerView
    private lateinit var playerRV: RecyclerView
    private lateinit var hitBTN: Button
    private lateinit var standBTN: Button
    private lateinit var backBTN: Button
    private lateinit var balanceTextView: TextView
    private lateinit var betInput: EditText
    private lateinit var dealButton: Button


    private val dealerAdapter = CardAdapter()
    private val playerAdapter  = CardAdapter()


    private val vm: GamePlayViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPref:SharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        when {
            sharedPref.getString("colorTheme","light")!! == "light" -> setTheme(R.style.Theme_lightTheme)
            sharedPref.getString("colorTheme","light")!! == "dark" -> setTheme(R.style.Theme_darkTheme)
        }

        super.onCreate(savedInstanceState)
        Log.d("MainActivity","entered oncreate in gameplayactivity")
        setContentView(R.layout.activity_gameplay)

        dealerRV = findViewById(R.id.dealerRecycler)
        dealerRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        dealerRV.adapter = dealerAdapter
        Log.d("MainActivity","dealerRv.adapter = ${dealerRV.adapter != null}")

        playerRV= findViewById(R.id.playerRecycler)
        playerRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        playerRV.adapter = playerAdapter


        hitBTN = findViewById(R.id.hit_button)
        standBTN =findViewById(R.id.stand_button)
        backBTN = findViewById(R.id.leave_game_button)
        balanceTextView = findViewById(R.id.game_balance)
        betInput = findViewById(R.id.bet_input)
        dealButton = findViewById(R.id.DealBtn)


        hitBTN.isEnabled =false
        standBTN.isEnabled = false
        //

        vm.dealer_cards.observe(this) {cards ->
            Log.d("MainActivity","Dealer cards list size = ${cards.size}")
            dealerAdapter.update(cards)  }
        vm.player_cards.observe(this) {cards ->
            Log.d("MainActivity","Player cards list size = ${cards.size}")
            playerAdapter.update(cards)  }
        vm.balance.observe(this) {bal -> balanceTextView.text = "$$bal"  }

        vm.result.observe(this){ result ->
            result?.let{ round->
                val msg = when (round.outcome){
                    Outcome.PLAYER_BLACKJACK, Outcome.PLAYER_WIN -> "You Win! +${round.netChange}"
                    Outcome.DEALER_WIN , Outcome.PLAYER_BUST-> "You Lose -${round.netChange}"
                    Outcome.PUSH -> "Push."
                }+ "\n Balance = ${round.finalBalance}"

                AlertDialog.Builder(this)
                    .setTitle("RoundOver")
                    .setTitle(msg)
                    .setCancelable(false)
                    .setPositiveButton("Ok") { dialog, _ ->
                        dialog.dismiss()
                        dealButton.isEnabled
                    }
                    .show()

                balanceTextView.text = "Balance: ${round.finalBalance}"
                sharedPref.edit().putInt("balance",round.finalBalance)
                    .apply()
                hitBTN.isEnabled =false
                standBTN.isEnabled = false
            }
        }

        dealButton.setOnClickListener {
            val bet = betInput.text.toString().toIntOrNull()
            if(bet == null|| bet<= 0){
                Toast.makeText(this, "Enter a valid bet", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (bet > (vm.balance.value?:0)){
                Toast.makeText(this, "Bet cannot exceed balance", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            vm.startRound(bet)

            hitBTN.isEnabled = true
            standBTN.isEnabled = true
        }
        hitBTN.setOnClickListener {vm.hit() }
        standBTN.setOnClickListener { vm.stand() }
        backBTN.setOnClickListener { finish() }









    }
}