package com.transcodium.app

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

/**
 * Created by razzbee on 12/7/2017.
 * the sqliteDbName is in AppConfig.kt file
 */
class DBHelper(ctx: Context)
    : ManagedSQLiteOpenHelper(ctx,sqliteDbName,null,1){


    /**
     * singleton
     */
    companion object {

        //setting initial instance to null
        private var instance:DBHelper? = null

        /**
         * getInstance
         * public fun for getting instance
         */
        @Synchronized
        fun getInstance(ctx:Context):DBHelper {

            //create the instance if its still null
            if (instance == null) {
                instance = DBHelper(ctx.applicationContext)
            }//end if

            return instance!!
        }//end getInstance

    }//end companion class

    /**
     * onUpgrade
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Here you can upgrade tables, as usual
        db.dropTable("User", true)
    }


/**
     * onCreate
     * this creates the database if query cannot not find it
     */
    override fun onCreate(db: SQLiteDatabase){

        //tables

        //accounts
        db.createTable("accounts",true,
                "id" to INTEGER + PRIMARY_KEY + UNIQUE + NOT_NULL,
                "name" to TEXT,
                "email" to TEXT,
                "photo_url" to TEXT,
                "is_default" to INTEGER + DEFAULT("0"),
                "provider_name" to TEXT, //social network name
                "provider_user_id" to TEXT,
                "provider_username" to TEXT,
                "access_token" to TEXT,
                "created_at" to TEXT,
                "updated_at" to TEXT
        )//end create table

    }//end on create


}//end class


// Access property for Context
val Context.database : DBHelper
    get() = DBHelper.getInstance(getApplicationContext())