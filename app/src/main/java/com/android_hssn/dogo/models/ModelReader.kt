package com.android_hssn.dogo.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelReader : Serializable {
    @SerializedName("layers")
    var list: ArrayList<DogCharacteristics>? = null
}