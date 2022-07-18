package com.android_hssn.dogo.database.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import com.android_hssn.dogo.MainApplication
import com.android_hssn.dogo.database.DogsDatabase
import com.android_hssn.dogo.database.dao.DogCharacteristicsDao
import com.android_hssn.dogo.models.DogCharacteristics
import com.android_hssn.dogo.temp.Example
import com.android_hssn.dogo.temp.ExampleCharacteristicsDao

class DogCharacteristicsRepository(application: Context) {


    private var dogsCharacteristicsDao: DogCharacteristicsDao =
        DogsDatabase.getInstance(application).dogCharacteristicsDao()
    private var exampleCharacteristicsDao: ExampleCharacteristicsDao =
        DogsDatabase.getInstance(application).exampleCharacteristicsDao()

    companion object {
        private var instance: DogCharacteristicsRepository? = null

        fun getInstance(): DogCharacteristicsRepository? {
            if (instance == null)
                instance = DogCharacteristicsRepository(MainApplication.context)
            return instance
        }
    }


    fun getAllDogs(): LiveData<List<DogCharacteristics?>?> {
        return dogsCharacteristicsDao.getAllDogs()
    }

    fun getAllFavouriteDogs(): LiveData<List<DogCharacteristics?>?> {
        return dogsCharacteristicsDao.getAllFavouriteDogs()
    }

    fun insertDog(dogCharacteristics: DogCharacteristics?) {
        dogsCharacteristicsDao.insertDogCharacteristics(dogCharacteristics)
    }

    fun insertExample(dogCharacteristics: Example?) {
        exampleCharacteristicsDao.insertDogCharacteristics(dogCharacteristics)
    }

    fun deleteDog(dogCharacteristics: DogCharacteristics?) {
        dogsCharacteristicsDao.deleteDog(dogCharacteristics)
    }

    fun updateDog(dogCharacteristics: DogCharacteristics?) {
        dogsCharacteristicsDao.updateDogCharacteristics(dogCharacteristics)
    }
}