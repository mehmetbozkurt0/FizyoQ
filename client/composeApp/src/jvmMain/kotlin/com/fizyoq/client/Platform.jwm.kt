package com.fizyoq.client

class JVMPlatform : FizyoPlatform {
    override val name: String = "Java ${System.getProperty("java.version")}"
    override val baseUrl: String = "http://localhost:8000"
}

actual fun getPlatform(): FizyoPlatform = JVMPlatform()