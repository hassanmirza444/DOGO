package com.android_hssn.dogo.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.android_hssn.dogo.models.DogCharacteristics

@Dao
interface DogCharacteristicsDao {

    @Query("SELECT * FROM DogCharacteristics")
    fun getAllDogs(): LiveData<List<DogCharacteristics?>?>

    @Query("SELECT * FROM DogCharacteristics where isFavourite=1")
    fun getAllFavouriteDogs(): LiveData<List<DogCharacteristics?>?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDogCharacteristics(dogCharacteristics: DogCharacteristics?)

    @Delete
    fun deleteDog(dogCharacteristics: DogCharacteristics?)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateDogCharacteristics(dogCharacteristics: DogCharacteristics?)
}