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

    val GOOGLE_SIGNIN = 100
    val FACEBOOK_SIGNIN = 200
    val TWITTER_SIGNIN = 300

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
                .requestIdToken(googleWebClientId)
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

    }

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

        //signin into firebase
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener{
                    task ->

                    //if successful, we move to next
                    if(task.isSuccessful){
                        //login to app
                        startClassActivity(mActivity,MainActivity::class.java)
                    }else{

                        //auth failed
                        longToast(R.string.auth_failed)
                    }//end if

                }//end onComplete

    }//end sign in to firebase


    /**
     * onActivityResult
     */
    override  fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        //lets check if its our login request
        when(resultCode){

            //if google
            GOOGLE_SIGNIN -> handleGoogleSignInResult(data)
        }
    }///end



    /**
* onBackPressed show Exit modal
     */
    override fun onBackPressed() {
        //show exit app dialog
        exitAppAlert(this)
    }//end event


}
