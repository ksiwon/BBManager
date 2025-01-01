package com.example.bbmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bbmanager.ui.contact.ContactFragment

data class ContactGroup (
    val groupName: String,
    val contacts: List<Contact>
)

data class Contact(
    val name: String,
    val phone: String,
    val img: String,
    val website: String
)

