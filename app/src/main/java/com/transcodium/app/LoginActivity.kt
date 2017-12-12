package com.transcodium.app

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import android.content.Intent
import android.util.Log
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterAuthClient
import org.jetbrains.anko.longToast
import com.google.firebase.auth.TwitterAuthProvider




/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class LoginActivity : AppCompatActivity() {

    //progress loader
    private val spinner by lazy {
        progressSpinner(this,R.string.signing_in)
    }

    //activity
    private val mActivity by lazy{
        this
    }

    //firebase auth
    private val mAuth:FirebaseAuth by lazy{

        //initialize firebase
       FirebaseAuth.getInstance()
    }

    //facebook callback manager
    private val fbCallbackManager by lazy{
        CallbackManager.Factory.create();
    }

    //we will use intent key to detect google signin result
    private val GOOGLE_SIGNIN = 777

    //we wil use boolean for the other login results
    private var IS_FACEBOOK_SIGNIN = false
    private var IS_TWITTER_SIGNIN = false

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
            "facebook" -> signInFacebook()
            //"twitter" -> simpleAuth.connectTwitter(processLoginAuth(social))
        }//end when

    }//end proecss Login


    /**
     * request google login
     */
    private fun signInGoogle(){

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
     * handle GoogleSingin Result
     */
    private fun handleGoogleSignInResult(data: Intent?){

        try{
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            val acct = task.getResult(ApiException::class.java)

            //get credentials
            val credential = GoogleAuthProvider
                    .getCredential(acct.idToken,null)

            //signin
            firebaseSignIn(credential)

        }catch(e: ApiException){

            longToast(R.string.auth_failed)

            Log.e("Google Auth Failed: ",e.message)
            e.printStackTrace()
        }
    }//end handle google signin result


    /**
     * signInFacebook
     */
   private fun signInFacebook(){

        //set IS_FACEBOOK_SIGNIN to true
        IS_FACEBOOK_SIGNIN = true

        //get instance of the login manager
        val loginManager = LoginManager.getInstance()

        //lets set the permissions or scopes
        loginManager.logInWithReadPermissions(this,listOf("email","public_profile"))

        //lets now start the auth and listen to the call back
        loginManager.registerCallback(
                fbCallbackManager,object: FacebookCallback<LoginResult>{

            //listen to success callback
            override fun onSuccess(result: LoginResult) {

                //access token
                val accessToken = result.accessToken

                //lets get the credentials from firebase
                val credential = FacebookAuthProvider.getCredential(
                                accessToken.token
                )

                //now sign into app using firebase
                firebaseSignIn(credential)
            }//end on success

            //listen to error
            override fun onError(error: FacebookException) {

                //show auth failed
                longToast(R.string.auth_failed)

                Log.e("Facebook Auth Error: ",error.message)

                error.printStackTrace()
            }//end error

            //if cancelled
            override fun onCancel() {
                longToast(R.string.request_aborted_by_user)
            }//end

        })//end callback listener

    }//end sigin to facebook


    //sigin twitter
    fun signinTwitter(){

        //set signal to true
        IS_TWITTER_SIGNIN = true

        //init twitter
        val config = TwitterConfig.Builder(this)
                .logger(DefaultLogger(Log.ERROR))
                .twitterAuthConfig(TwitterAuthConfig(
                        getString(R.string.twitter_consumer_key),
                        getString(R.string.twitter_consumer_secret)
                ))
                .debug(true)
                .build()
        Twitter.initialize(config)

        //twitter Client
        val twitterAuthClient = TwitterAuthClient()

        //authorize request
        twitterAuthClient.authorize(this, object: Callback<TwitterSession>(){

            //on success
            override fun success(result: Result<TwitterSession>) {

                //session
                val session = result.data

                //get crednetials
                val credential = TwitterAuthProvider.getCredential(
                        session.authToken.token,
                        session.authToken.secret)

                //login
                firebaseSignIn(credential)
            }//end success


            //if failed
            override fun failure(exception: TwitterException) {

                //auth failed
                longToast(R.string.auth_failed)

                //print stacktrace
                exception.printStackTrace()

                Log.e("Twitter Auth Failed:",exception.message)
            }//end if failed

        })//end autheorize

    }//end sign in twitter

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

                        //get error
                        val err = task.exception

                        err?.printStackTrace()

                        //log error
                        Log.e("Firebase Auth Failed",err?.message)
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
        //if is facebook
        else if(IS_FACEBOOK_SIGNIN){
            //let fb handle the result
            fbCallbackManager.onActivityResult(requestCode,resultCode,data)
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
