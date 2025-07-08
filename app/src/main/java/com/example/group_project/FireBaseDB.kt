package com.example.group_project
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.CollectionReference


//object that handles all db functions
object FireBaseDB{
    var db : FirebaseFirestore = FirebaseFirestore.getInstance()
    var ref : CollectionReference = db.collection("users")

    fun setUser(user: User){
        ref.document(user.username).set(user)
    }

    fun getUser(username: String){
        ref.document(username).get()
    }

    fun setBal(username: String, newBal : Double){
        ref.document(username).update("balance", newBal)
    }

    fun setLocation(username: String, newLoc : String){
        ref.document(username).update("location", newLoc)
    }


}