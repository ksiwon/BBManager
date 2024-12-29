package com.example.bbmanager.ui.contact

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.json.JSONArray
import org.json.JSONObject
import android.util.Log
import com.example.bbmanager.Contact
import com.example.bbmanager.ContactGroup


//data class Contact(val name: String, val phone: String, val img: String)

class ContactViewModel : ViewModel() {
    private val _contactGroups = MutableLiveData<List<ContactGroup>>()
    val contactGroups: LiveData<List<ContactGroup>> = _contactGroups

    fun loadContacts(context: Context) {
        try {
            // JSON 파일 읽기
            val inputStream = context.assets.open("contacts.json")
            val json = inputStream.bufferedReader().use { it.readText() }
            Log.d("ContactViewModel", "JSON File Content: $json")

            // JSON 데이터 파싱
            val jsonArray = JSONArray(json)
            Log.d("ContactViewModel", "JSONArray length: ${jsonArray.length()}")
            val groupList = mutableListOf<ContactGroup>()



            for (i in 0 until jsonArray.length()) {
                val groupObject = jsonArray.getJSONObject(i)
                val groupName = groupObject.keys().next() // 그룹 이름 가져오기
                val contactsArray = groupObject.getJSONArray(groupName)

                val contacts = mutableListOf<Contact>()
                for (j in 0 until contactsArray.length()) {
                    val contactObject = contactsArray.getJSONObject(j)
                    val name = contactObject.getString("name")
                    val phone = contactObject.getString("phone")
                    val img = contactObject.getString("img")
                    contacts.add(Contact(name, phone, img))
                }
                groupList.add(ContactGroup(groupName, contacts))
            }

            _contactGroups.value = groupList
            // LiveData에 데이터 설정
            //_contacts.value = contactList
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("ContactFragment", "Error reading JSON: ${e.message}")
            _contactGroups.value = emptyList()
        }
    }
}


