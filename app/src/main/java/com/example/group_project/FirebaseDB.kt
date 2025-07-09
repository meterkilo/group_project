package com.example.group_project
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query


//object that handles all db functions
object FirebaseDB {
    var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    var ref: CollectionReference = db.collection("users")

    fun setUser(user: User) {
        ref.document(user.username).set(user)
    }

    fun getUser(username: String, onResult: (User?) -> Unit) {
        ref.document(username).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onResult(document.toObject(User::class.java))
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    //returns a sorted list of users by balance

    fun getLeaderboard(onResult: (List<User>) -> Unit) {
        ref
            .orderBy("balance", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val users = result.documents.mapNotNull { it.toObject(User::class.java) }
                onResult(users)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }
}





