package com.example.group_project
import android.content.Context
import kotlin.repeat
class BlackJackModel(private val context: Context, startBalance:Int) {

    private val deck = Deck(context)
    private var currBet = 0

    val player = Hand()
    val dealer = Hand()

    var balance:Int = startBalance
    fun startRound(bet:Int){
        require(bet in 1..balance){"bet must be btwn 1 and curr balance"}
        currBet=bet
        deck.reset(context)
        player.clear()
        dealer.clear()
        repeat(2){
            player.add(deck.draw())
            dealer.add(deck.draw())
        }
    }

    fun hit():RoundResult? {
        player.add(deck.draw())
        return if (player.value()>21) finishRound() else null
    }

    fun stand(): RoundResult = finishRound()

    private fun finishRound(): RoundResult{
        while(dealer.value()< 17){
            dealer.add(deck.draw())

        }
        val pv = player.value()
        val dv = dealer.value()
        val outcome = when {
            pv >21 -> Outcome.PLAYER_BUST
            pv==21 && player.cards.size == 21 -> Outcome.PLAYER_BLACKJACK
            dv> 21 || pv > dv -> Outcome.PLAYER_WIN
            dv> pv -> Outcome.DEALER_WIN
            else -> Outcome.PUSH
        }

        val net = when(outcome){
            Outcome.PLAYER_BLACKJACK -> (currBet * 3)/2
            Outcome.PLAYER_WIN -> currBet
            Outcome.DEALER_WIN -> -currBet
            Outcome.PLAYER_BUST -> -currBet
            Outcome.PUSH -> 0
        }
        //send result to history
        when(outcome){
            Outcome.PLAYER_BLACKJACK -> HomeActivity.addHistory(net,"Player Blackjack")
            Outcome.PLAYER_WIN -> HomeActivity.addHistory(net,"Player Won")
            Outcome.DEALER_WIN -> HomeActivity.addHistory(net,"Dealer Won")
            Outcome.PLAYER_BUST -> HomeActivity.addHistory(net,"Player Busted")
            Outcome.PUSH -> HomeActivity.addHistory(net,"Push")
        }
        balance+= net
        return RoundResult(outcome,net,balance)
    }
}

enum class Outcome{
    PLAYER_BLACKJACK ,PLAYER_WIN, DEALER_WIN, PLAYER_BUST, PUSH
    // ace +10, p > d but p=<21 or d >21, same but with d winning, p>21, tie
}
data class RoundResult (
    val outcome : Outcome, val netChange : Int, val finalBalance :  Int
)
private class Deck(private val context: Context){
    private val cards = mutableListOf<Card>()
    init {reset(context)}

    fun reset(context: Context){

        cards.clear()
        for (s in Suit.values()){
            for (r in Rank.values()){
                val resName = "${r.name.lowercase()}_of_${s.name.lowercase()}"
                // ace_of_spades etc
                val id = context.resources.getIdentifier(resName,"drawable", context.packageName)
                cards += Card(s,r,id)
            }
        }
        shuffle()

    }
    fun shuffle()= cards.shuffle()
    fun draw():Card = cards.removeAt(0)
}
 class Hand{
    private val m_cards = mutableListOf<Card>()
    val cards: List<Card> get() = m_cards

    fun add(card: Card){   m_cards+= card   }
    fun clear()        {   m_cards.clear()  }

    fun value():Int{

        var total = m_cards.sumOf { it.rank.value }
        val aces = m_cards.count{it.rank == Rank.ACE}

        repeat (aces){ if (total>21) total -= 10 }
        return total
    }
}
enum class Suit {
    CLUBS,
    DIAMONDS,
    HEARTS,
    SPADES
}

enum class Rank (val value: Int){
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    TEN(10),
    JACK(10),
    QUEEN(10),
    KING(10),
    ACE(11) // Note ace can be 1 or 11
}
data class Card (
    val suit: Suit,
    val rank: Rank,
    val image_resID: Int
)