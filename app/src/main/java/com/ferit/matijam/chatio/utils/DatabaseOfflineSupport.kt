package com.ferit.matijam.chatio.utils

import com.google.firebase.database.FirebaseDatabase




class DatabaseOfflineSupport {
    companion object{
        private var database: FirebaseDatabase? = null

        fun getDatabase(): FirebaseDatabase? {
            if (database == null) {
                database = FirebaseDatabase.getInstance("https://chatio-3e74b-default-rtdb.europe-west1.firebasedatabase.app")
                database!!.setPersistenceEnabled(true)
            }
            return database
        }

    }
}