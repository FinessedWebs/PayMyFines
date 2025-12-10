package com.example.paymyfinesstep.cart

data class CartItemRequest(
    val noticeNumber: String,
    val description: String,
    val amountInCents: Int
)

data class CartItemResponse(
    val noticeNumber: String,
    val description: String,
    val amountInCents: Int
)
