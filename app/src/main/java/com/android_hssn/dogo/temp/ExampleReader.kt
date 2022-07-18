package com.android_hssn.dogo.temp

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ExampleReader : Serializable {
    @SerializedName("layers")
    var list: ArrayList<Example>? = null
}