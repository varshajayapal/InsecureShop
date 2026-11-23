package com.insecureshop.util

import android.content.Context
import java.io.BufferedReader
import java.io.FileReader
import java.net.InetSocketAddress
import java.net.Socket

object HookingDetector {
    private val HOOK_PACKAGES = listOf(
        "de.robv.android.xposed.installer",
        "com.saurik.substrate"
    )
    private val MAPS_MARKERS = listOf("XposedBridge.jar", "frida-agent", "frida-gadget")

    fun isMapsContaminated(): Boolean = try {
        BufferedReader(FileReader("/proc/${android.os.Process.myPid()}/maps")).useLines { lines ->
            lines.any { line -> MAPS_MARKERS.any { marker -> line.contains(marker, ignoreCase = true) } }
        }
    } catch (e: Exception) {
        false
    }

    fun isHookingPackageInstalled(context: Context): Boolean =
        HOOK_PACKAGES.any { pkg ->
            try {
                context.packageManager.getApplicationInfo(pkg, 0)
                true
            } catch (e: Exception) {
                false
            }
        }

    fun isFridaServerListening(): Boolean = try {
        Socket().use { it.connect(InetSocketAddress("127.0.0.1", 27042), 50) }
        true
    } catch (e: Exception) {
        false
    }

    fun isHooked(context: Context): Boolean =
        isMapsContaminated() || isHookingPackageInstalled(context) || isFridaServerListening()
}
