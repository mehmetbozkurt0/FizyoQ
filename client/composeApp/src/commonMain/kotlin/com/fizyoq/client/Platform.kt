package com.fizyoq.client


interface FizyoPlatform {
    val name: String
}

expect fun getPlatform(): FizyoPlatform