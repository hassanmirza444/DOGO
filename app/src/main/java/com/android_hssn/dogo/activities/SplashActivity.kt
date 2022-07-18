package com.android_hssn.dogo.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.android_hssn.dogo.temp.ExampleReader
import com.android_hssn.dogo.R
import com.android_hssn.dogo.database.repositories.DogCharacteristicsRepository
import com.android_hssn.dogo.models.ModelReader

import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_favourites.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        DogCharacteristicsRepository.getInstance()!!
            .getAllDogs().observe(this, {
                if (it!!.isEmpty()) {
                    CoroutineScope(Dispatchers.IO).launch {

                        val myJson =
                            inputStreamToString(
                                this@SplashActivity.getResources().openRawResource(R.raw.dogs)
                            )
                        val modelReader: ModelReader =
                            Gson().fromJson(myJson, ModelReader::class.java)
                        for (dog in modelReader.list!!) {
                            DogCharacteristicsRepository.getInstance()!!.insertDog(dog)
                        }
                    }
                }
            })

        CoroutineScope(Dispatchers.IO).launch {

            val myJson =
                inputStreamToString(
                    this@SplashActivity.getResources().openRawResource(R.raw.success)
                )
            val modelReader: ExampleReader = Gson().fromJson(myJson, ExampleReader::class.java)
            for (dog in modelReader.list!!) {
                DogCharacteristicsRepository.getInstance()!!.insertExample(dog)
            }
        }
        startNextScreen()

    }

    fun startNextScreen() {
        Handler().postDelayed({
            val mainIntent = Intent(this@SplashActivity, MainActivity::class.java)
            this@SplashActivity.startActivity(mainIntent)
            this@SplashActivity.finish()
        }, 2000)
    }

    fun inputStreamToString(inputStream: InputStream): String? {
        return try {
            val bytes = ByteArray(inputStream.available())
            inputStream.read(bytes, 0, bytes.size)
            String(bytes)
        } catch (e: IOException) {
            null
        }
    }

}