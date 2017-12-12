package com.transcodium.app

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.find

/**
 * Created by dr_success on 8/12/2017.
 */
open class DrawerActivity: AppCompatActivity(){


    //lazy init vals
    private val toolbar: Toolbar by lazy{
        findViewById<Toolbar>(R.id.toolbar)
    }

    private val drawerLayout: DrawerLayout by lazy{
        findViewById<DrawerLayout>(R.id.drawer_layout)
    }

    private val navView: NavigationView by lazy{
        findViewById<NavigationView>(R.id.navView)
    }

    private  val contentView:View by lazy{
        findViewById<CoordinatorLayout>(R.id.contentView)
    }

    val context: Context by lazy {
        this
    }

    private lateinit var drawerToggle: ActionBarDrawerToggle

    val currentUser by lazy {
        FirebaseAuth.getInstance().currentUser
    }

    /**
     * check if user is logged in, since
     * this is almost inherited by most activities requiring login
     */
    override  fun onStart() {
        super.onStart()

        //go to login
        if(currentUser == null){
            startClassActivity(this,LoginActivity::class.java,true)
        }
    }//end on start

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //set toolbar as actionbar
        setSupportActionBar(toolbar)

        //remove elevation
        drawerLayout.drawerElevation = 0f


        //set Header Info
        setDrawerHeaderInfo()

        //set the scrim color (the dark overlay fading when the drawer is opened)
        drawerLayout.setScrimColor(Color.TRANSPARENT)


        //drawer options and events
        drawerToggle = object: ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        ){
            //events

            /**
             * onDrawerSlide
             */
            //push content on drawer open
            override fun onDrawerSlide(drawerView:View, offset: Float ){

                contentView.translationX = (offset * drawerView.width)

                super.onDrawerSlide(drawerView, offset)
            }//end


        }//end toggle  options and events


        //lets set drawer listener
       drawerLayout.addDrawerListener(drawerToggle)

        //lets set drawer item click listener
        navView.setNavigationItemSelectedListener({
            item: MenuItem ->
            onNavItemSelected(item)
        })


    }//end



    //on NavItemSelected
    fun onNavItemSelected(item: MenuItem): Boolean{

        val itemId = item.itemId

        val itemActivity = when(itemId){
            R.id.about ->  AboutActivity::class.java
           // R.id.contact_us -> ContactActivity::class.java
            else -> null
        }

       val  curActivityName = this.javaClass.name

        //is Same activity
        val isSameActivity = curActivityName == itemActivity?.name

        //if itemActivity is not null or
        //not the same activity, then open activity
        if(!(itemActivity == null || isSameActivity )){

            //Open activity
              startActivity(Intent(applicationContext,itemActivity))
        }


        //close drawer
        drawerLayout.closeDrawer(GravityCompat.START)

        return true
    }//end

    //onOptionItemSelected
    override fun onOptionsItemSelected(item: MenuItem):Boolean{

        when(item.itemId){
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }

        //if item
        if(drawerToggle.onOptionsItemSelected(item)){
            return true
        }

        return super.onOptionsItemSelected(item)
    }//end


    /**
     * onPostCreate
     */
    override fun onPostCreate(savedInstanceState: Bundle?){
        super.onPostCreate(savedInstanceState)

        //sync state of toggle
        drawerToggle.syncState()
    }//end


    /**
     * onconfiguration change tool.. we should
     * change the toggle
     */
    override fun onConfigurationChanged(newConfig: Configuration){
        super.onConfigurationChanged(newConfig)

        //onConfigurationChanged
        drawerToggle.onConfigurationChanged(newConfig)
    }//end


    //set Header info
    private fun setDrawerHeaderInfo(){

        //lets get the headerView of the drawer and set the neccessary vars
        val headerView: View = navView.getHeaderView(0)

        //lets get profile phot
        val profilePic = currentUser?.phoneNumber

        //if not empty lets update it
        if(!profilePic.isNullOrBlank()){

        }//end if


    }//end set header info



}//end class