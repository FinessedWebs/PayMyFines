package com.example.paymyfinesstep.cart

import java.io.Serializable

data class CartItem(
    val noticeNumber: String,
    val description: String,
    val amountInCents: Int
) : Serializable
