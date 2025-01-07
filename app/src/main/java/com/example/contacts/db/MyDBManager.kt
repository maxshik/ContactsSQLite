package com.example.contacts.db

import DatabaseHelper
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import android.util.Log
import com.example.contacts.models.Contact
import com.example.contacts.models.PrevCalls
import java.sql.Date
import java.sql.Time

class MyDBManager(context: Context) {
    val myDBHelper = DatabaseHelper(context)
    var db: SQLiteDatabase? = null

    fun openDb() {
        db = myDBHelper.writableDatabase
    }

    fun saveContact(contact: Contact) {
        Log.i("Test", contact.isImportant.toString())
        val contactValues = ContentValues().apply {
            put(MyDBNameClass.COLUMN_CONTACT_NAME, contact.name)
            put(MyDBNameClass.COLUMN_CONTACT_TELEPHONE, contact.telephone)
            put(MyDBNameClass.COLUMN_CONTACT_FIRST_LETTER, contact.firstLatter)
            put(MyDBNameClass.COLUMN_CONTACT_ISIMPORTANT, contact.isImportant)
        }
        val contactId = db?.insert(MyDBNameClass.TABLE_CONTACTS, null, contactValues)
        contact?.tags?.forEach { tag ->
            val tagValues = ContentValues().apply {
                put(MyDBNameClass.COLUMN_TAG_NAME, tag)
                put(MyDBNameClass.COLUMN_CONTACT_ID, contactId)
            }
            db?.insert(MyDBNameClass.TABLE_TAGS, null, tagValues)
        }
    }

    fun savePrevCall(prevCall: PrevCalls) {
        val callValues = ContentValues().apply {
            put(MyDBNameClass.COLUMN_CALL_NAME, prevCall.name)
            put(MyDBNameClass.COLUMN_CALL_TELEPHONE, prevCall.telephone)
            put(MyDBNameClass.COLUMN_CALL_DATE, prevCall.date.time)
            put(MyDBNameClass.COLUMN_CALL_TIME, prevCall.time.time)
        }
        db?.insert(MyDBNameClass.TABLE_PREV_CALLS, null, callValues)
    }

    fun getContacts(onlyImportant: Boolean = false): MutableList<Contact> {
        val contacts = mutableListOf<Contact>()
        val selection = if (onlyImportant) "${MyDBNameClass.COLUMN_CONTACT_ISIMPORTANT} = 1" else null

        val contactCursor: Cursor? = db?.query(
            MyDBNameClass.TABLE_CONTACTS,
            null, selection, null, null, null, null
        )

        contactCursor?.use { cursor ->
            while (cursor.moveToNext()) {
                val contactId = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(MyDBNameClass.COLUMN_CONTACT_NAME))
                val telephone = cursor.getString(cursor.getColumnIndexOrThrow(MyDBNameClass.COLUMN_CONTACT_TELEPHONE))
                val firstLetter = cursor.getString(cursor.getColumnIndexOrThrow(MyDBNameClass.COLUMN_CONTACT_FIRST_LETTER))
                val isImportant = cursor.getInt(cursor.getColumnIndexOrThrow(MyDBNameClass.COLUMN_CONTACT_ISIMPORTANT))

                val tags = mutableListOf<String>()
                val tagCursor: Cursor? = db?.query(
                    MyDBNameClass.TABLE_TAGS,
                    arrayOf(MyDBNameClass.COLUMN_TAG_NAME),
                    "${MyDBNameClass.COLUMN_CONTACT_ID} = ?",
                    arrayOf(contactId.toString()),
                    null, null, null
                )

                tagCursor?.use { tagCur ->
                    while (tagCur.moveToNext()) {
                        val tag = tagCur.getString(tagCur.getColumnIndexOrThrow(MyDBNameClass.COLUMN_TAG_NAME))
                        tags.add(tag)
                    }
                }
                Log.i("Test", isImportant.toString())
                contacts.add(Contact(name, firstLetter, telephone, tags.toTypedArray(), isImportant))
            }
        }

        return contacts
    }

    fun updateContactImportance(contactName: String, isImportant: Int) {
        val values = ContentValues().apply {
            put(MyDBNameClass.COLUMN_CONTACT_ISIMPORTANT, isImportant)
        }
        db?.update(
            MyDBNameClass.TABLE_CONTACTS,
            values,
            "${MyDBNameClass.COLUMN_CONTACT_NAME} = ?",
            arrayOf(contactName)
        )
    }

    fun getContactNameByPhoneNumber(phoneNumber: String): String? {
        val contactCursor: Cursor? = db?.query(
            MyDBNameClass.TABLE_CONTACTS,
            arrayOf(MyDBNameClass.COLUMN_CONTACT_NAME),
            "${MyDBNameClass.COLUMN_CONTACT_TELEPHONE} = ?",
            arrayOf(phoneNumber),
            null,
            null,
            null
        )
        contactCursor?.use { cursor ->
            if (cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndexOrThrow(MyDBNameClass.COLUMN_CONTACT_NAME))
            }
        }
        return null
    }

    fun getPrevCalls(): List<PrevCalls> {
        val prevCallsList = mutableListOf<PrevCalls>()
        val prevCallsCursor: Cursor? = db?.query(
            MyDBNameClass.TABLE_PREV_CALLS,
            null,
            null,
            null,
            null,
            null,
            null
        )
        prevCallsCursor?.use { cursor ->
            while (cursor.moveToNext()) {
                val name = cursor.getString(cursor.getColumnIndexOrThrow(MyDBNameClass.COLUMN_CALL_NAME))
                val telephone = cursor.getString(cursor.getColumnIndexOrThrow(MyDBNameClass.COLUMN_CALL_TELEPHONE))
                val date = Date(cursor.getLong(cursor.getColumnIndexOrThrow(MyDBNameClass.COLUMN_CALL_DATE)))
                val time = Time(cursor.getLong(cursor.getColumnIndexOrThrow(MyDBNameClass.COLUMN_CALL_TIME)))
                val prevCall = PrevCalls(name, telephone, date, time)
                prevCallsList.add(prevCall)
            }
        }
        return prevCallsList
    }

    fun deleteContact(contactName: String) {
        db?.delete(
            MyDBNameClass.TABLE_TAGS,
            "${MyDBNameClass.COLUMN_CONTACT_ID} IN (SELECT ${BaseColumns._ID} FROM ${MyDBNameClass.TABLE_CONTACTS} WHERE ${MyDBNameClass.COLUMN_CONTACT_NAME} = ?)",
            arrayOf(contactName)
        )

        db?.delete(
            MyDBNameClass.TABLE_CONTACTS,
            "${MyDBNameClass.COLUMN_CONTACT_NAME} = ?",
            arrayOf(contactName)
        )
    }

    fun closeDb() {
        db?.close()
    }
}
