package io.zoemeow.dutapp.android.utils

import android.Manifest
import android.app.Activity
import android.app.WallpaperManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class GetCurrentHomeWallpaper {
    companion object {
        private fun checkPermission(context: Context): Boolean {
            return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }

        fun getCurrentWallpaper(activity: Activity): Drawable? {
            return if (checkPermission(activity)) {
                val wallpaperManager = WallpaperManager.getInstance(activity)
                wallpaperManager.drawable
            } else {
                ActivityCompat.requestPermissions(
                    activity,
                    listOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ).toTypedArray(),
                    0
                )
                if (checkPermission(activity)) getCurrentWallpaper(activity) else null
            }
        }
    }
}