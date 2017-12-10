package com.transcodium.app

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Point
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDialog
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.alert
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.indeterminateProgressDialog
import org.json.JSONArray
import org.json.JSONObject


/**
 * Created by razzbee on 7/10/2017.
 */

//var sharedPrefConn: SharedPreferences? = null
/**
 * getSharedPref : returns the shared preference item
 * @param context  Context: the App Context
 * @param itemName String : the key of the item we wish to retrieve
 */
fun getSharedPref(context: Context, itemName: String?): Any?{

    //get the preference
    val sharedPrefConn = context.getSharedPreferences(sharedPrefDBName, Context.MODE_PRIVATE)


    //get all the items
    val dbDataSet = sharedPrefConn.all

    //if null or empty, we send all the db data
    if(itemName.isNullOrEmpty()){
        return dbDataSet
    }//end if

    //else lets get the data
    return dbDataSet[itemName]
}//end get sharedPref

/**
 * removeSharedPrefItem
 * @param activity
 * @param key
 */
fun removeSharedPrefItem(activity: Activity, itemKey: String):Boolean{

    //lets remove user account details and logout
    val pref: SharedPreferences = activity.getSharedPreferences(
            sharedPrefDBName,
            Context.MODE_PRIVATE
    )

    //remove user data
    pref.edit().remove(itemKey).commit()

    return true
}//end fun

/**
 * saveSharedPref
 * @param context
 * @param key
 * @param value
 */
fun saveSharedPref(context: Context,key: String,value: Any){

    //get the preference
    //if null create it
    val sharedPrefConn = context.getSharedPreferences(sharedPrefDBName, Context.MODE_PRIVATE)

    //editor
     val editor: SharedPreferences.Editor = sharedPrefConn.edit()

    //test and insert data
    when(value){
        is Int     -> editor.putInt(key,value)
        is Boolean -> editor.putBoolean(key,value)
        is String  -> editor.putString(key,value)
        is Float   -> editor.putFloat(key,value)
        is Long    -> editor.putLong(key,value)

        is JSONObject,
        is JSONArray -> {

            val gson:Gson = Gson()

            //save json
            editor.putString(key, gson.toJson(value))
        }//end

    }//end if

    //commit changes
    editor.commit()
}//edn save


/**
 * getUserAccounts
 * MutableMap<String,JSONObject>
 */
fun getUserAccounts(): Boolean{

    return true
}

/**
 * hideStatusBar
 * @param context
 */
fun hideStatusBar(activity: AppCompatActivity){

    //jellybean and lower
    if(Build.VERSION.SDK_INT < 16){
        activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }
    else{//else if greater than android 4

        //get decor view
        val decorView: View = activity.getWindow().decorView

        //fullscreen flag
        val uiOptions: Int = View.SYSTEM_UI_FLAG_FULLSCREEN

        //set ui visibility to full screen
        decorView.setSystemUiVisibility(uiOptions)
    }//end if

}//end function

/**
 * minimizeApp
 * @param activity
 */
fun minimizeApp(activity: Activity){
    val i = Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
        activity.startActivity(i);
    }//end minimize app


/**
 *   alertDialog
 *   @param context Context
 **/
fun  progressDialog(
        activity: Activity,
        text: Any = "Loading ..",
        isCancellable: Boolean = true): AppCompatDialog{


   val dialog = AppCompatDialog(activity)

    dialog.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)

    //root view
    val rootView = activity.window.decorView.rootView as ViewGroup

    val progressLayout = activity
                        .layoutInflater
                        .inflate(R.layout.indeterminate_progress_bar,
                                  rootView,false)

    val progressBar = progressLayout
                        .findViewById<ProgressBar>(R.id.progress_bar)

    val progressBarTextView = progressLayout
                            .findViewById<TextView>(R.id.progress_bar_text)

    if(text is Int){
        progressBarTextView.text = activity.getString(text)
    }else if(text is String){
        progressBarTextView.text = text
    }

    progressBar.isIndeterminate = true

    //progressBar.set

    dialog.setContentView(progressLayout)

    dialog.window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
    )

    dialog.setCancelable(isCancellable)

    return dialog
}//end alert Dialog



//run in bg mode
fun <T>  runInBg(
        activity: Activity,
        task: () -> T,
        beforeTask: (() -> Unit)? = null,
        afterTask: ((activity: Activity,result: T?) -> Unit)? = null,
        onError: ((error: Exception) -> Unit)? = null
        ){

    try {

        if (beforeTask != null) {
            beforeTask?.invoke()
        }

        async(UI) {

            //lets run in asyc
            val deferred: Deferred<T> = bg {

                //run task
                task()
            }//end bg mode

            //now lets update ui if possible
            afterTask?.invoke(activity,deferred.await())

        }//end async

    }catch(e: Exception){

        if (onError != null) {
            //execute on error
            onError?.invoke(e)

            e.printStackTrace()

            varDump(e)
        }

    }//end error catching

}//end runInBg

//Var Dump
 fun <T> varDump(obj: T): String {
    return GsonBuilder().setPrettyPrinting().create().toJson(obj);
}



/**
*startNewActivity
 **/
fun <T> startClassActivity(
        activity: Activity,
        ActivityClass: Class<T>,
        clearActivityStack: Boolean = false){

    //leave this intent to auth intent
    val i = Intent(activity,ActivityClass)

    //if clear activity Stack is true
    if(clearActivityStack) {
        i.flags = (
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                Intent.FLAG_ACTIVITY_NEW_TASK
                )
    }//end if

    //start activity
    activity.startActivity(i)
}//end fun

//set header info
fun setDrawerHeaderInfo(context: Context,headerView: View){

}//end fun


/**
 * a helper to hide system UI enabling better full screen
 */
fun enableFullScreen(activity: AppCompatActivity){

    activity
        .window
        .decorView
        .systemUiVisibility =   View.SYSTEM_UI_FLAG_LOW_PROFILE or
                                View.SYSTEM_UI_FLAG_FULLSCREEN or
                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    //lets hide status bar
    hideStatusBar(activity)

    //hide action bar
    activity.supportActionBar?.hide()
}//end fun

/**
 * exitAppAlert
 */
fun exitAppAlert(activity: AppCompatActivity) {

    activity.alert(
            titleResource = R.string.exit_app,
            messageResource = R.string.exit_app_msg
    ) {
        //options
        isCancelable = false
        positiveButton(R.string.exit, {
            android.os.Process.killProcess(android.os.Process.myPid())
        })
        negativeButton(R.string.cancel, { dialog ->
            dialog.dismiss()
        })

    }.show()
}//end show alert

   /**
    *getScreenSize
    */
    fun screenSize(activity: Activity): Point {

       val display: Display = activity.windowManager.defaultDisplay
       val size: Point = Point()
       display.getSize(size)

       return size
   }//end get ScreenSize

    /**
     * using hardcoded value to determine if screen size
     * is large or noot
     */
    fun isLargeScreen(activity: Activity, width: Int = 600): Boolean{

        val screenWidth = screenSize(activity).x

        if(screenWidth >= width){
            return true
        }

        return false
    }//end is Large Screen


/**
 * show progress spinner
 */
fun progressSpinner(
        activity: Activity,
        msg: Int = R.string.spinner_loading): ProgressDialog {

    val progress = activity.indeterminateProgressDialog(
            message = msg
    )

    progress.setCancelable(false)

    return progress
}//end progress Spinner


