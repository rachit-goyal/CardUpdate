package com.example.cardupdate

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.cardupdate.databinding.ActivityMainBinding
import com.example.cardupdate.databinding.ActivityPermissionActiivtyBinding

class PermissionActiivty : AppCompatActivity() {
    var permissionGranted: Boolean = false
    lateinit var binding: ActivityPermissionActiivtyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionActiivtyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.orange)
        val prefs = getSharedPreferences("MyPref", MODE_PRIVATE)
        if(prefs!=null){
            val time = prefs.getBoolean("permissionAdded", false)
            if(time){
                val mainIntent = Intent(this, MainActivity::class.java)
                this.startActivity(mainIntent)
                finish()
            }
        }
        binding.btn.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestSmsPermission();
            }
        }
    }

    private fun requestSmsPermission() {
        val permission: String = Manifest.permission.RECEIVE_SMS
        val grant = ContextCompat.checkSelfPermission(this, permission)
        if (grant != PackageManager.PERMISSION_GRANTED) {
            val permission_list = arrayOfNulls<String>(1)
            permission_list[0] = permission
            ActivityCompat.requestPermissions(this, permission_list, 1)
        } else {
            permissionGranted = true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.size > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            val editor = getSharedPreferences("MyPref", MODE_PRIVATE).edit()
            editor.putBoolean("permissionAdded", true)
            editor.apply()
            val mainIntent = Intent(this, MainActivity::class.java)
            this.startActivity(mainIntent)
            finish()


        } else {
            Toast.makeText(this, "Permission is mandatory to proceed", Toast.LENGTH_SHORT)
                .show()
        }
    }
}