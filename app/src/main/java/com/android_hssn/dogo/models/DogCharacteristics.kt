package com.android_hssn.dogo.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity
class DogCharacteristics : Serializable {

 

    @PrimaryKey
    @SerializedName("id")
    var id: Int = 0

    @SerializedName("name")
    var name: String = ""

    @SerializedName("physical")
    var physical: String = ""

    @SerializedName("personality")
    var personality: String = ""

    @SerializedName("img")
    var img: String = ""

    @SerializedName("is_favourite")
    var isFavourite = false
}