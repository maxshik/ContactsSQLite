package com.example.contacts.db

import android.provider.BaseColumns

object MyDBNameClass : BaseColumns {
    const val TABLE_CONTACTS = "CONTACTS"
    const val COLUMN_CONTACT_NAME = "name"
    const val COLUMN_CONTACT_TELEPHONE = "telephone"
    const val COLUMN_CONTACT_FIRST_LETTER = "first_latter"
    const val COLUMN_CONTACT_ISIMPORTANT = "isImportant"

    const val TABLE_TAGS = "TAGS"
    const val COLUMN_TAG_NAME = "tag"
    const val COLUMN_CONTACT_ID = "contact_id"

    const val TABLE_PREV_CALLS = "PREV_CALLS"
    const val COLUMN_CALL_NAME = "name"
    const val COLUMN_CALL_TELEPHONE = "telephone"
    const val COLUMN_CALL_DATE = "date"
    const val COLUMN_CALL_TIME = "time"

    const val DATABASE_VERSION = 1
    const val DATABASE_NAME = "Contacts.db"

    const val SQL_CREATE_CONTACTS = """
    CREATE TABLE IF NOT EXISTS $TABLE_CONTACTS (
        ${BaseColumns._ID} INTEGER PRIMARY KEY,
        $COLUMN_CONTACT_NAME TEXT,
        $COLUMN_CONTACT_TELEPHONE TEXT,
        $COLUMN_CONTACT_FIRST_LETTER TEXT,
        $COLUMN_CONTACT_ISIMPORTANT INTEGER
    )
    """
    //ALTER TABLE contacts ADD COLUMN isImportant INTEGER;


    const val SQL_CREATE_TAGS = """
        CREATE TABLE IF NOT EXISTS $TABLE_TAGS (
            ${BaseColumns._ID} INTEGER PRIMARY KEY,
            $COLUMN_TAG_NAME TEXT,
            $COLUMN_CONTACT_ID INTEGER,
            FOREIGN KEY($COLUMN_CONTACT_ID) REFERENCES $TABLE_CONTACTS(${BaseColumns._ID})
        )
    """

    const val SQL_CREATE_PREV_CALLS = """
        CREATE TABLE $TABLE_PREV_CALLS (
            ${BaseColumns._ID} INTEGER PRIMARY KEY,
            $COLUMN_CALL_NAME TEXT,
            $COLUMN_CALL_TELEPHONE TEXT,
            $COLUMN_CALL_DATE DATE,
            $COLUMN_CALL_TIME TIME
        )
    """

    const val SQL_DELETE_CONTACTS = "DROP TABLE IF EXISTS $TABLE_CONTACTS"
    const val SQL_DELETE_TAGS = "DROP TABLE IF EXISTS $TABLE_TAGS"
    const val SQL_DELETE_PREV_CALLS = "DROP TABLE IF EXISTS $TABLE_PREV_CALLS"
}
