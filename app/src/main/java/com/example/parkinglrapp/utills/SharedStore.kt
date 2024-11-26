package com.example.parkinglrapp.utills

import android.content.Context
import android.preference.PreferenceManager
import com.example.parkinglrapp.Data.SearchData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedStore {

    val LOGINYN : String = "login_yn"
    val NICKNAME : String = "nickname"
    val PLACE : String = "place"
    val SPLASHYN : String = "splash_yn"
    val FIRSTYN : String = "first_yn"
    val MENUYN : String = "menu_yn"
    /**
     * string 값을 저장한다.
     *
     * @param p_context
     * @param p_keyName
     * @param p_strValue
     */
    fun putSharePrefrerenceStringData(
        p_context: Context?,
        p_keyName: String?,
        p_strValue: String?
    ) {
        val l_pref = PreferenceManager.getDefaultSharedPreferences(p_context)
        val l_editor = l_pref.edit()
        l_editor.putString(p_keyName, p_strValue)
        l_editor.commit()
    }

    /**
     * string 값을 반환한다.
     *
     * @param p_context
     * @param p_keyName
     * @return
     */
    fun getSharePrefrerenceStringData(p_context: Context?, p_keyName: String?): String? {
        var returnStr = ""
        val l_pref = PreferenceManager.getDefaultSharedPreferences(p_context)
        returnStr = l_pref.getString(p_keyName, "").toString()
        return returnStr
    }

    fun putSharePrefrerenceBooleanData(
        p_context: Context?,
        p_keyName: String?,
        p_strValue: Boolean
    ) {
        val l_pref = PreferenceManager.getDefaultSharedPreferences(p_context)
        val l_editor = l_pref.edit()
        l_editor.putBoolean(p_keyName, p_strValue)
        l_editor.commit()
    }

    fun getSharePrefrerenceBooleanData(p_context: Context?, p_keyName: String?): Boolean {
        var returnStr = false
        val l_pref = PreferenceManager.getDefaultSharedPreferences(p_context)
        returnStr = l_pref.getBoolean(p_keyName, false)
        return returnStr
    }

    fun putSharePrefrerenceIntData(p_context: Context?, p_keyName: String?, p_strValue: Int) {
        val l_pref = PreferenceManager.getDefaultSharedPreferences(p_context)
        val l_editor = l_pref.edit()
        l_editor.putInt(p_keyName, p_strValue)
        l_editor.commit()
    }

    fun getSharePrefrerenceIntData(p_context: Context?, p_keyName: String?): Int {
        var returnStr = 0
        val l_pref = PreferenceManager.getDefaultSharedPreferences(p_context)
        returnStr = l_pref.getInt(p_keyName, 0)
        return returnStr
    }
    fun putSharePrefrerenceListData(p_context: Context, p_keyName: String?, p_List: List<String>) {
        val prefs = p_context.getSharedPreferences(
            p_keyName,
            Context.MODE_PRIVATE
        )
        val editor = prefs.edit()
        val gson = Gson()
        val json = gson.toJson(p_List)
        editor.putString(p_keyName, json)
        editor.apply()
    }

    fun getSharePrefrerenceListData(p_context: Context, p_keyName: String?): List<String> {

        val prefs = p_context.getSharedPreferences(
            p_keyName,
            Context.MODE_PRIVATE
        )
        val gson = Gson()
        val json = prefs.getString(p_keyName, null)
        return if (json != null) {
            val type = object : TypeToken<List<String>>() {}.type // 타입 토큰 생성
            Gson().fromJson(json, type) // JSON 문자열을 리스트로 변환
        } else {
            emptyList() // 저장된 값이 없으면 빈 리스트 반환
        }
    }
    fun saveEventSearchResModel(context: Context, searchData: SearchData?) {
        val prefs = context.getSharedPreferences(
            "SEARCHDATA",
            Context.MODE_PRIVATE
        )
        val editor = prefs.edit()
        val gson = Gson()
        val json = gson.toJson(searchData)
        editor.putString("SearchData", json)
        editor.apply()
    }

    fun getEventSearchResModel(context: Context): SearchData? {
        val prefs = context.getSharedPreferences(
            "SEARCHDATA",
            Context.MODE_PRIVATE
        )
        val gson = Gson()
        val json = prefs.getString("SearchData", null)
        return gson.fromJson(json, SearchData::class.java)
    }
}