package com.simplemobiletools.launcher.extensions

import android.app.Activity
import android.app.SearchManager
import android.app.role.RoleManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.UserHandle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi

fun View.hideKeyboard() {
    this.clearFocus()
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}



@RequiresApi(Build.VERSION_CODES.Q)
fun Activity.showLauncherSelector(
    resultLauncher: ActivityResultLauncher<Intent>,
) {
    val roleManager = getSystemService(Context.ROLE_SERVICE) as RoleManager
    if (roleManager.isRoleAvailable(RoleManager.ROLE_HOME)) {
        val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_HOME)
        resultLauncher.launch(intent)
    } else
        resetDefaultLauncher()
}

fun Context.resetDefaultLauncher() {
    try {
        val componentName = ComponentName(this, FakeHomeActivity::class.java)
        packageManager.setComponentEnabledSetting(
            componentName,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
        val selector = Intent(Intent.ACTION_MAIN)
        selector.addCategory(Intent.CATEGORY_HOME)
//        startActivity(selector)
        packageManager.setComponentEnabledSetting(
            componentName,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Context.isDefaultLauncher(): Boolean {
    val launcherPackageName = getDefaultLauncherPackage(this)
    return this.packageName == launcherPackageName
}

fun Context.resetLauncherViaFakeActivity() {
    resetDefaultLauncher()
//    if (getDefaultLauncherPackage(this).contains("."))
//        startActivity(Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS))
}

fun Context.openSearch(query: String? = null) {
    val intent = Intent(Intent.ACTION_WEB_SEARCH)
    intent.putExtra(SearchManager.QUERY, query ?: "")
    startActivity(intent)
}


fun Context.searchOnPlayStore(query: String? = null): Boolean {
    return try {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/search?q=$query&c=apps")
            ).addFlags(
                Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            )
        )
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

fun Context.isPackageInstalled(
    packageName: String,
    userHandle: UserHandle = android.os.Process.myUserHandle(),
): Boolean {
    val launcher = getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
    val activityInfo = launcher.getActivityList(packageName, userHandle)
    return activityInfo.size > 0
}
