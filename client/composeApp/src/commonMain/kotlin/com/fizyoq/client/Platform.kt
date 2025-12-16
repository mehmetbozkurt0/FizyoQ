package com.fizyoq.client


interface FizyoPlatform {
    val name: String
    val baseUrl: String
}

expect fun getPlatform(): FizyoPlatform