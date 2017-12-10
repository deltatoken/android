package com.transcodium.app

import android.accounts.Account
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.alert
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.db.MapRowParser
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.parseList
import org.jetbrains.anko.db.select
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask

class EntryActivity : AppCompatActivity() {

    private lateinit var nextActivityClass: Class<*>
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        //initilizd firebase
      // FirebaseApp.initializeApp(this as Context)

        //auth
      //  mAuth = FirebaseAuth.getInstance()

        ///is intro completed ?
        val introCompleted :Any? = getSharedPref(this, "intro_completed") as Boolean?

        //if user has finished intro, go to
        //to app
        if(introCompleted != true) {

            nextActivityClass = AppIntroActivity::class.java

        }
         //if user has no account, send to login to create
        else if(false == false){

            nextActivityClass = LoginActivity::class.java

        }else{

            //else send to main activity
            nextActivityClass = MainActivity::class.java
        }//end if else

        //start the activity clearing the activity stack
        startClassActivity(this,nextActivityClass,true)

        //finish
        this.finish()

        super.onCreate(savedInstanceState)
    }

    /**
     * onBackPressed show Exit modal
     */
    override fun onBackPressed() {
        //show exit app dialog
        exitAppAlert(this)
    }//end event


}//end class
