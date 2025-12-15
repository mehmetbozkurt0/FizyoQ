package com.fizyoq.client
import platform.UIKit.UIDevice

class IOSPlatform : FizyoPlatform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}
actual fun getPlatform(): FizyoPlatform = IOSPlatform()