package com.example.group_project

//user object

//creating a new user example:
//val newUser = User(
//    username = "testUsername1",
//    balance = 5000,
//    location = "Maryland"

data class User(
    val username: String = "",
    val location: String = "",
    val balance: Double = 0.0
)
