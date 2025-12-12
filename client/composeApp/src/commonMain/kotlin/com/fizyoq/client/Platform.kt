package com.fizyoq.client

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform