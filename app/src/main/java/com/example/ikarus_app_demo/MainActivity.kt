package com.example.ikarus_app_demo

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.android.synthetic.main.activity_main.latitude
import kotlinx.android.synthetic.main.activity_main.longtitude

class MainActivity : AppCompatActivity() {

    lateinit var timer: Runnable
    private lateinit var listView: ListView
    private lateinit var btnSend: Button
    var x : Double
        get() {
            return this.x
        }
        set(value) {
            this.x = value
        }
    var y : Double
        get() {
            return this.y
        }
        set(value) {
            this.y = value
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView = findViewById(R.id.list_view)
        btnSend = findViewById(R.id.broadcast)


        //getMyChain()
        btnSend.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("latitude", latitude.text.toString())
            bundle.putString("longtitude", longtitude.text.toString())

            val send = Intent(this@MainActivity, MapsActivity::class.java)
            send.putExtras(bundle)
            startActivity(send)
        }
        //rawJSON()
        //get176()
    }

    private fun getMyChain(){
        val handler = Handler()
        var iterator = 1
        var listItems = mutableListOf<String>()
        val all_coords = mutableListOf<MutableList<Int>>()
        timer = Runnable() {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://192.168.2.154:8000")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(SimpleAPI::class.java)

            CoroutineScope(Dispatchers.IO).launch {

                // Do the GET request and get response
                val response = service.getChain()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val chainArray = response.body()?.chain?.transactions
                        var listItems = mutableListOf<String>()
                        if (chainArray != null) {
                            for (i in 0 until chainArray.size) {
                                var transaction = chainArray[i]?.coordinates
                                val regex = "[,\\s]+".toRegex()
                                val numbers = regex.split(transaction, 3).map{it.toInt()}
                                all_coords.add(numbers.toMutableList())
                                listItems.add(i, numbers.toString())
                            }
                        }
                    } else {
                        Log.e("RETROFIT_ERROR", response.code().toString())
                    }
                }
            }
            listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems)

            if (true) {
                iterator++
                handler.postDelayed(timer, 3000)
            }

        }
        handler.post(timer)
    }

    fun rawJSON() {
        // Create Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.2.176:8000")
            .build()

        // Create Service
        val service = retrofit.create(SimpleAPI::class.java)

        // Create JSON using JSONObject
        val jsonObject = JSONObject()
        jsonObject.put("x,y,z", "51.090488596594426, 71.398015177155")
        jsonObject.put("public key", "data2")
        jsonObject.put("name", "data3")

        // Convert JSONObject to String
        val jsonObjectString = jsonObject.toString()

        // Create RequestBody ( We're not using any converter, like GsonConverter, MoshiConverter e.t.c, that's why we use RequestBody )
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

        CoroutineScope(Dispatchers.IO).launch {
            // Do the POST request and get response
            val response = service.geotransaction(requestBody)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {

                    // Convert raw JSON to pretty JSON using GSON library
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(
                        JsonParser.parseString(
                            response.body()
                                ?.string() // About this thread blocking annotation : https://github.com/square/retrofit/issues/3255
                        )
                    )

                    Log.d("Pretty Printed JSON :", prettyJson)

                } else {

                    Log.e("RETROFIT_ERROR", response.code().toString())

                }
            }
        }
    }

    private fun get176(){
        val handler = Handler()
        var iterator = 1
        var listItems = mutableListOf<String>()
        val all_coords = mutableListOf<MutableList<Double>>()
        timer = Runnable() {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://192.168.2.176:8000")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(SimpleAPI::class.java)

            CoroutineScope(Dispatchers.IO).launch {

                // Do the GET request and get response
                val response = service.getChain176()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val chainArray = response.body()?.chain?.get(14)?.transactions
                        var listItems = mutableListOf<String>()
                        if (chainArray != null) {
                            for (i in 0 until chainArray.size) {
                                var transaction = chainArray[i]?.coordinates
                                val regex = "[,\\s]+".toRegex()
                                val numbers = regex.split(transaction, 3).map{it.toDouble()}
                                all_coords.add(numbers.toMutableList())
                                listItems.add(i, numbers.toString())
                                latitude.text=numbers[0].toString()
                                longtitude.text=numbers[1].toString()
                            }
                        }
                    } else {
                        Log.e("RETROFIT_ERROR", response.code().toString())
                    }
                }
            }
            listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems)

            if (true) {
                iterator++
                handler.postDelayed(timer, 3000)
            }

        }
        handler.post(timer)
    }
}