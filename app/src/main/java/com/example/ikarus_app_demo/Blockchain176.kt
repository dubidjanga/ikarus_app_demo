package com.example.ikarus_app_demo

import com.google.gson.annotations.SerializedName

data class Chain176(
    val index: Int,
    val transactions: MutableList<MyTransaction176>,
    val timestamp: String,
    val previous_hash: String,
    val nonce: Int,
    val hash: String
)

data class MyTransaction176(
    @SerializedName("x,y,z")
    val coordinates : String,
    @SerializedName("public key")
    val public_key : String,
    val name: String,
    val timestamp: String
)

data class Blockchain176 (
    val length: Int,
    val chain: MutableList<Chain176>
)