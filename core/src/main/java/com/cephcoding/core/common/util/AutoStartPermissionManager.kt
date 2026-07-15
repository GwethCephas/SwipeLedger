package com.cephcoding.core.common.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build

object AutoStartPermissionManager {
    private data class OemIntent(val brand: String, val intent: Intent)

    private val OEM_INTENTS = listOf(
        OemIntent(
            brand = Constants.XIAOMI,
            intent = Intent().setComponent(
                ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity"
                )
            )
        ),
        OemIntent(
            brand = Constants.HUAWEI,
            intent = Intent().setComponent(
                ComponentName(
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity"
                )
            )
        ),
        OemIntent(
            brand = Constants.OPPO,
            intent = Intent().setComponent(
                ComponentName(
                    "com.coloros.safecenter",
                    "com.coloros.safecenter.permission.startup.StartupAppListActivity"
                )
            )
        ),
        OemIntent(
            brand = Constants.VIVO,
            intent = Intent().setComponent(
                ComponentName(
                    "com.vivo.permissionmanager",
                    "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
                )
            )
        )
    )

    fun isRestrictedDevice(): Boolean {
        val manufacturer = Build.MANUFACTURER.lowercase()
        val brand = Build.BRAND.lowercase()
        return OEM_INTENTS.any {
            it.brand.equals(manufacturer, ignoreCase = true) || it.brand.equals(
                brand,
                ignoreCase = true
            )
        }
    }

    fun openAutostartSettings(context: Context): Boolean {
        val manufacturer = Build.MANUFACTURER.lowercase()
        val brand = Build.BRAND.lowercase()

        val matchingOem = OEM_INTENTS.firstOrNull {
            it.brand.equals(manufacturer, ignoreCase = true) || it.brand.equals(
                brand,
                ignoreCase = true
            )
        }

        return if (matchingOem != null) {
            try {
                // Ensure we flag the activity as a new task when starting from a non-Activity context
                matchingOem.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(matchingOem.intent)
                true
            } catch (e: Exception) {
                // Fallback: If their customized OS changed the component path, open standard App Settings
                false
            }
        } else {
            false
        }
    }
}