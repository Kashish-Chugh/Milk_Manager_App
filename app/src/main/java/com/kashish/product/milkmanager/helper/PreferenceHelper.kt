package com.kashish.product.milkmanager.helper

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONException
import android.R.attr.name
import android.R.id
import org.json.JSONObject
import com.google.gson.Gson
import android.preference.PreferenceManager
import com.google.gson.reflect.TypeToken
import com.kashish.product.milkmanager.entity.Record


class PreferenceHelper(val context: Context) {
    val PREFS_NAME = "OrganicPreference"
    var preferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, 0)
    public var set = HashSet<String>()

    fun saveValueAsString(key: String, value: String) {
        val editor = preferences.edit()
        editor.putString(key, value)
        editor.apply()
    }


    fun RemoveValue(key: String) {
        val editor = preferences.edit()
        editor.remove(key)
        editor.apply()
    }

    fun getQuantity():Float{
        return preferences.getFloat(AppConstants.EventsKeys().QUANTITY,2.5F)
    }

    fun getRate():Float{
        return preferences.getFloat(AppConstants.EventsKeys().RATE,55.0F)
    }

    fun addItemInSet(record: Record): HashSet<String> {
        set.add(record.getJSONObject().toString())
        return set
    }

    fun saveStringSet(key: String, set: HashSet<String>) {
        val editor = preferences.edit()
        val sett = preferences.getStringSet(key, set)
        sett!!.add(set.elementAt(0))
        editor.putStringSet(key, sett)
        editor.apply()
    }

    fun getStringSet(key: String): MutableSet<String>? {
        return preferences.getStringSet(key, null)
    }

    fun removeItem(key: String, item: String) {
        val editor = preferences.edit()
        val sett = preferences.getStringSet(key, set)
        val iterator = sett!!.iterator()
        while (iterator.hasNext()) {
            val i = iterator.next()
            if (item == i) {
                iterator.remove()
                break
            }
        }
        editor.putStringSet(key, sett)
        editor.apply()
    }

    /*fun getCheckedAddressId():String?{
        return preferences.getString(AppConstants.UserKeys().CHECKEDADDRESS,"")
    }

    fun saveAddressList(key: String,addressList: MutableList<String>) {
        val editor = preferences.edit()
        val gson = Gson()
        val json = gson.toJson(addressList)
        editor.putString(key, json)
        editor.apply()
    }*/

    fun getRecordArray():MutableList<String>{
        val gson = Gson()
        val json = preferences.getString(AppConstants().RECORDS,"")
        val type = object : TypeToken<List<String>>() {

        }.type
        if (json!="") {
            val list = gson.fromJson<MutableList<String>>(json ,type)
            return (list)
        }else
            return mutableListOf()
    }

    /*fun getMobileNumber():String?{
        return preferences.getString(AppConstants.UserKeys().VERIFYMOBILE.toString(),"")
    }

    fun getName():String?{
        return preferences.getString(AppConstants.UserKeys().VERIFYNAME,"")
    }*/
}


