package com.example.ikarus_app_demo

import com.google.gson.annotations.SerializedName

data class Chain(
    val index: Int,
    val transactions: MutableList<MyTransaction>,
    val timestamp: String,
    val previous_hash: String,
    val nonce: Int,
    val hash: String
)

data class MyTransaction(
    @SerializedName("x,y,z")
    val coordinates : String,
    @SerializedName("public key")
    val public_key : String,
    val name: String,
    val timestamp: String
)

data class Blockchain (
    val length: Int,
    val chain: Chain
)