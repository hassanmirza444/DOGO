package com.android_hssn.dogo.temp

import androidx.lifecycle.LiveData
import androidx.room.*
import com.android_hssn.dogo.models.DogCharacteristics

@Dao
interface ExampleCharacteristicsDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDogCharacteristics(dogCharacteristics: Example?)


}