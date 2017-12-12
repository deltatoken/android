package com.transcodium.app

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import org.jetbrains.anko.longToast


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class LoginActivity : AppCompatActivity() {

    val spinner by lazy {
        progressSpinner(this,R.string.signing_in)
    }

    val mActivity by lazy{
        this
    }

    //firebase
    val mAuth:FirebaseAuth by lazy{

        FirebaseApp.initializeApp(this as Context)

        //initialize firebase
       FirebaseAuth.getInstance()
    }

    //we will use intent key to detect google signin result
    val GOOGLE_SIGNIN = 777

    //we wil use boolean for the other login results
    val ISFACEBOOK = false
    val ISTWITTER = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)


    }//end

    /**
     * onStart
     */
    override public fun onStart()
    {
        super.onStart()

      val currentUser = mAuth.currentUser
    }//end

        /**
     * processLogin
     */
    fun processLogin(v: View){

        //spinner.show()

        //lets get the login type
        val social = v.tag.toString()

        when(social){
            "google" -> signInGoogle()
            //"facebook" -> simpleAuth.connectFacebook(processLoginAuth(social))
            //"twitter" -> simpleAuth.connectTwitter(processLoginAuth(social))
        }//end when


    }//end proecss Login


    /**
     * request google login
     */
    fun signInGoogle(){

        //init google sigin
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_webclient_id))
                .requestEmail()
                .build()

        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = mGoogleSignInClient.getSignInIntent()


        //start activity
        startActivityForResult(signInIntent, GOOGLE_SIGNIN)


    }//end request google login


    /**
     * signInFacebook
     */
    fun signInFacebook(){

    }//end login fb


    /**
     * handle GoogleSingin Result
     */
    fun handleGoogleSignInResult(data: Intent?){

        try{
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            val acct = task.getResult(ApiException::class.java)

            //get credentials
            val credential = GoogleAuthProvider.getCredential(acct.idToken,null)

            //signin
            firebaseSignIn(credential)

        }catch(e: ApiException){
            e.printStackTrace()
        }
    }//end


    /**
     *firebase signIn
     */
    fun firebaseSignIn(credential: AuthCredential){

        //spin spinner
        spinner.show()

        //signin into firebase
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener{
                    task ->

                    //if successful, we move to next
                    if(task.isSuccessful){

                        //to prevent data leak, cancel spinner before leaving
                        //window
                        spinner.cancel()

                        //login to app
                        startClassActivity(mActivity,WalletActivity::class.java)
                    }else{

                        //auth failed
                        longToast(R.string.auth_failed)
                    }//end if

                    //if we are here then means it wasnt success
                    spinner.hide()

                }//end onComplete

    }//end sign in to firebase


    /**
     * onActivityResult
     */
    override  fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        //if the returned results is from google signin request
        if(requestCode == GOOGLE_SIGNIN){
            handleGoogleSignInResult(data)
        }

    }///end event fun



    /**
* onBackPressed show Exit modal
     */
    override fun onBackPressed() {
        //show exit app dialog
        exitAppAlert(this)
    }//end event


}
