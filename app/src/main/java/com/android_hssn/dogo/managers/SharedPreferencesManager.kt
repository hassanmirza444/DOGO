package com.android_hssn.dogo.managers

import android.content.SharedPreferences
import com.android_hssn.dogo.MainApplication

class SharedPreferencesManager {

    private var sharedPreferences: SharedPreferences? = null
    companion object instance{
        private var appPreferences: SharedPreferencesManager? = null

        public fun getInstance(): SharedPreferencesManager? {
            if (appPreferences == null) {
                appPreferences = SharedPreferencesManager()
                if (appPreferences!!.sharedPreferences == null) {
                    appPreferences!!.sharedPreferences = MainApplication.context.getSharedPreferences("my_app_shared_pref", 0)
                }
            }
            return appPreferences
        }
    }




    operator fun contains(str: String?): Boolean {
        return sharedPreferences!!.contains(str)
    }

    fun setString(str: String?, str2: String?) {
        sharedPreferences!!.edit().putString(str, str2).apply()
    }

    fun getString(str: String?): String? {
        return sharedPreferences!!.getString(str,"")
    }

    fun getString(str: String?, str2: String?): String? {
        return sharedPreferences!!.getString(str, str2)
    }

    fun setInt(str: String?, i: Int) {
        sharedPreferences!!.edit().putInt(str, i).apply()
    }

    fun getInt(str: String?): Int {
        return sharedPreferences!!.getInt(str, -1)
    }

    fun setLong(str: String?, j: Long) {
        sharedPreferences!!.edit().putLong(str, j).apply()
    }

    fun getLong(str: String?): Long {
        return sharedPreferences!!.getLong(str, -1)
    }

    fun setBoolean(str: String?, z: Boolean) {
        sharedPreferences!!.edit().putBoolean(str, z).apply()
    }

    fun getBoolean(str: String?): Boolean {
        return sharedPreferences!!.getBoolean(str, false)
    }

    fun getBoolean(str: String?, z: Boolean): Boolean {
        return sharedPreferences!!.getBoolean(str, z)
    }

    fun setFloat(str: String?, f: Float) {
        sharedPreferences!!.edit().putFloat(str, f).apply()
    }

    fun getFloat(str: String?): Float {
        return sharedPreferences!!.getFloat(str, 0.0f)
    }

}