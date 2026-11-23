package com.insecureshop.util

import android.content.Context
import android.os.Build
import android.provider.Settings

object SecurityUtils {

    fun isAdbEnabled(context: Context): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 &&
            Settings.Global.getInt(context.contentResolver, Settings.Global.ADB_ENABLED, 0) == 1
}
