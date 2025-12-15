package com.fizyoq.client

class JVMPlatform : FizyoPlatform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): FizyoPlatform = JVMPlatform()