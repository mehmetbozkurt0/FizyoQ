package com.fizyoq.client
import android.os.Build

class AndroidPlatform : FizyoPlatform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override val baseUrl: String = "http://10.0.0.2:8000"
}
actual fun getPlatform(): FizyoPlatform = AndroidPlatform()