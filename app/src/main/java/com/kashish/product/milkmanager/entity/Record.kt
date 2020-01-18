package com.kashish.product.milkmanager.entity

import org.json.JSONException
import org.json.JSONObject
import java.util.*

class Record(
    var tookMilk: Boolean,
    var quantity: Float,
    var notes: String,
    var rate: Float,
    var date: Date
) {

    fun getJSONObject(): JSONObject {
        val obj = JSONObject()
        try {
            obj.put("TookMilk", this.tookMilk)
            obj.put("Quantity", this.quantity)
            obj.put("Notes", this.notes)
            obj.put("Rate", this.rate)
            obj.put("Date", this.date)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return obj
    }
}