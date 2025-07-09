package com.example.group_project

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import com.example.group_project.BlackJackModel
class GamePlayViewModel(app: Application): AndroidViewModel(app) {

    private val prefs = app.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    private val startingBalance = prefs.getInt("balance",1000)
    private val game = BlackJackModel(app, startingBalance )

    private val _player_cards = MutableLiveData<List<Card>>(emptyList())
    val player_cards: LiveData<List<Card>> = _player_cards

    private val _dealer_cards = MutableLiveData<List<Card>>(emptyList())
    val dealer_cards: LiveData<List<Card>> = _dealer_cards


    private val _result = MutableLiveData<RoundResult?>()
    val result: LiveData<RoundResult?> = _result

    private val _balance = MutableLiveData<Int>(game.balance)
    val balance: LiveData<Int> = _balance

    fun startRound(bet:Int){
        Log.d("MainActivity","In view model.startround, balance =${game.balance}")
        game.startRound(bet)
        _result.value = null
        _player_cards.value = game.player.cards
        _dealer_cards.value = game.dealer.cards
        _balance.value = game.balance
    }

    fun hit(){
        _result.value = game.hit()
        _player_cards.value = game.player.cards
        _dealer_cards.value = game.dealer.cards
        _balance.value = game.balance
        prefs.edit().putInt("balance",game.balance).apply()

    }

    fun stand(){
        _result.value = game.stand()
        _player_cards.value = game.player.cards
        _dealer_cards.value = game.dealer.cards
        _balance.value = game.balance
        prefs.edit().putInt("balance",game.balance).apply()
    }


}