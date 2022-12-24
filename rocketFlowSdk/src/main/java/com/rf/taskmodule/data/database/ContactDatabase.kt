package com.rf.taskmodule.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rf.taskmodule.data.database.dao.ContactsDataDao
import com.rf.taskmodule.ui.addbuddy.Contact


@Database(entities = [Contact::class], version = 1 ,exportSchema = false)
abstract class ContactDatabase : RoomDatabase() {
    abstract fun contactsDataDao(): ContactsDataDao

}