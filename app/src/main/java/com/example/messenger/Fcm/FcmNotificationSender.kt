package com.example.messenger.Fcm

import android.app.Activity
import android.content.Context
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class FcmNotificationsSender(
    var userFcmToken: String,
    var title: String,
    var body: String,
   // var mContext: Context,
    var mActivity: Activity
) {
    private val postUrl = "https://fcm.googleapis.com/fcm/send"
    private val fcmServerKey =
        "AAAA3L3VhLo:APA91bHqHcsn2DVTTzuwt4gssM8-GBdhCBs3PUyFYcvhs-0fzjWhgzcH32fB0CM3BsguouBkKs4KK2i-HdW23QxK23iMMxfrgzkj0FRPQZbenfHXRvEcUdLpsXPWcy_qLzIILIGjyg-U"
    fun SendNotifications() {
        val requestQueue = Volley.newRequestQueue(mActivity)
        val mainObj = JSONObject()
        try {
            mainObj.put("to", userFcmToken)
            val notiObject = JSONObject()
            notiObject.put("title", title)
            notiObject.put("body", body)
            notiObject.put("icon", "icon") // enter icon that exists in drawable only
            mainObj.put("notification", notiObject)
            val request: JsonObjectRequest = object : JsonObjectRequest(
                Method.POST, postUrl, mainObj,
                Response.Listener { },
                Response.ErrorListener { error: VolleyError? -> }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val header: MutableMap<String, String> = HashMap()
                    header["content-type"] = "application/json"
                    header["authorization"] = "key=$fcmServerKey"
                    return header
                }
            }
            requestQueue.add(request)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}