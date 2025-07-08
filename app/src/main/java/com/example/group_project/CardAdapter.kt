package com.example.group_project

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputBinding
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class CardAdapter (
    private var cards: List<Card> = emptyList()
): RecyclerView.Adapter<CardAdapter.CardViewHolder>(){

    inner class CardViewHolder(itemView:View): RecyclerView.ViewHolder(itemView){
        val cardImage: ImageView = itemView.findViewById(R.id.cardImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card,parent,false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cards[position]
        Log.d("CardAdapter","Binding ${card.rank} of  ${card.suit} -> resid = ${card.image_resID}")

        holder.cardImage.setImageResource(card.image_resID)
    }
    override fun getItemCount(): Int = cards.size

    fun update(newCards:List<Card>){
        cards = newCards
        notifyDataSetChanged()
    }


}