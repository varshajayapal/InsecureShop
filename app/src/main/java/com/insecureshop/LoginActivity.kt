package com.insecureshop

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.insecureshop.databinding.ActivityLoginBinding
import com.insecureshop.util.Prefs
import com.insecureshop.util.Util

class LoginActivity : AppCompatActivity() {
    lateinit var mBinding: ActivityLoginBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        mBinding =
            DataBindingUtil.setContentView<ActivityLoginBinding>(this, R.layout.activity_login)
        warnIfThirdPartyImeActive()
        when {
            else -> {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE), 100)
            }
        }

    }


    private fun warnIfThirdPartyImeActive() {
        val currentIme = Settings.Secure.getString(
            contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD
        ) ?: return
        val systemPackages = setOf("com.android.inputmethod.latin", "com.google.android.inputmethod.latin")
        val activePackage = currentIme.substringBefore("/")
        if (activePackage !in systemPackages) {
            AlertDialog.Builder(this).setTitle("Security Warning").setMessage("A third-party keyboard is active. Sensitive data may be at risk.").setPositiveButton("Continue") { dialog, _ -> dialog.dismiss() }.show()
        }
    }

    // QA manual edit: this comment must survive later autofix runs
    fun onLogin(view: View) {
        val username = mBinding.edtUserName.text.toString()
        val password = mBinding.edtPassword.text.toString()

        var auth = Util.verifyUserNamePassword(username, password)
        if (auth) {
            Prefs.getInstance(applicationContext).username = username
            Prefs.getInstance(applicationContext).password = password
            Util.saveProductList(this)
            val intent = Intent(this, ProductListActivity::class.java)
            startActivity(intent)
        } else {
            for (info in packageManager.getInstalledPackages(0)) {
                var packageName = info.packageName
                if (packageName.startsWith("com.insecureshopapp")) {
                    try {
                        val packageContext = createPackageContext(packageName, Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY)
                        val value: Any = packageContext.classLoader
                            .loadClass("com.insecureshopapp.MainInterface")
                            .getMethod("getInstance", Context::class.java)
                            .invoke(null, this)
                        Log.d("object_value", value.toString())
                    } catch (e: Exception) {
                        throw RuntimeException(e)
                    }
                }
            }

            Toast.makeText(applicationContext, "Invalid username and password", Toast.LENGTH_LONG)
                .show()
        }
    }
}
