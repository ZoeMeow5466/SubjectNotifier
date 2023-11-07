package io.zoemeow.dutschedule.model.permissionrequest

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings

class PermissionList {
    companion object {
        val PERMISSION_NOTIFICATION = PermissionInfo(
            name = "Notifications",
            code = "android.permission.POST_NOTIFICATIONS",
            minSdk = 33,
            description = "Allow this app to send new announcements " +
                    "(news global and news subject) and other for you.",
            required = false
        )

        val PERMISSION_MANAGE_EXTERNAL_STORAGE = PermissionInfo(
            name = "Manage External Storage",
            code = "android.permission.MANAGE_EXTERNAL_STORAGE",
            minSdk = 30,
            description = "Allow this app to get your current launcher wallpaper.",
            required = false,
            extraAction = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Intent(
                    Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                    Uri.parse("package:${"io.zoemeow.dutschedule"}")
                )
            } else {
                Intent(
                    "android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS",
                    Uri.parse("package:${"io.zoemeow.dutschedule"}")
                )
            }
        )

        fun getAllRequiredPermissions(): List<PermissionInfo> {
            return listOf(
                PERMISSION_NOTIFICATION,
                PERMISSION_MANAGE_EXTERNAL_STORAGE
            )
        }
    }
}