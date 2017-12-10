package com.transcodium.app

import android.accounts.Account
import android.content.Context
import android.util.Log
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.parseList
import org.jetbrains.anko.db.select
import org.jetbrains.anko.doAsync


/**
 * Created by dr_success on 12/7/2017.
 */
class Accounts(){

    /**
     * innerClass for the cols
     * class row Parser
     */
    class AccountCols(
            val id: Int,
            val name: String,
            val email: String,
            val photo_url: String,
            val is_default: Int,
            val provider_name: String,
            val provider_user_id: String,
            val provider_username: String,
            val access_token: String,
            val created_at: String,
            val updated_at: String
    )//end inner class


    /**
     * object
     */
    companion object {

        /**
         * hasAccount - checks if at least 1 account Exists
         */
        fun hasAccount(database: DBHelper): Boolean{

            val rowParser = classParser<AccountCols>()

            //make query
            val acct =  database
                        .readableDatabase
                         .select("accounts","id")
                        .limit(1)
                        .exec {
                            parseList(rowParser)
                        }//end query

            if(acct.isEmpty()){
                return false
            }

            return true

        }//end has hasAccount


        /**
         * all - fetch all accounts
         */
        fun all(database: DBHelper): List<AccountCols> {

            val rowParser = classParser<AccountCols>()

            //make query
            return  database
                    .readableDatabase
                    .select("accounts")
                    .exec {
                        parseList(rowParser)
                    }//end query

        }//end fun

   }//end companion object

}//end main class