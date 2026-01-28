package com.example.paymyfine

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform