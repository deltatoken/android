package com.transcodium.app

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.Window
import com.github.paolorotolo.appintro.AppIntro
import com.github.paolorotolo.appintro.AppIntroFragment
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask

/**
 * by razzbee
 * full screen app intro
 */
class AppIntroActivity:AppIntro() {

    override fun onCreate(savedInstanceState: Bundle?) {

        //hide title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)

        //call parent
        super.onCreate(savedInstanceState)

        setSlideOverAnimation()

        val slideOneTitle = getString(R.string.multi_coin_wallet) as String
        val slideOneDes = getString(R.string.multi_coin_wallet_desc) as String
        val slideOneImage: Int =  R.drawable.mobile_wallet
        val slideOneBg = ContextCompat.getColor(this, R.color.colorPrimary)

        //add slide 1
        addSlide(AppIntroFragment.newInstance(slideOneTitle , slideOneDes,slideOneImage ,slideOneBg))

        //slide 2
        val slideTwoTitle = getString(R.string.visual_media_studio) as String
        val slideTwoDes = getString(R.string.visual_media_studio_desc) as String
        val slidTwoImage: Int =  R.drawable.tablet
        val slideTwoBg = ContextCompat.getColor(this, R.color.pink)

        //add slide 2
        addSlide(AppIntroFragment.newInstance(slideTwoTitle , slideTwoDes,slidTwoImage ,slideTwoBg));

        //slide 3
        val slide3Title = getString(R.string.mobile_miner) as String
        val slide3Des = getString(R.string.mobile_miner_desc) as String
        val slide3Image: Int =  R.drawable.gold
        val slide3Bg = ContextCompat.getColor(this, R.color.purple)

        //add slide 3
        addSlide(AppIntroFragment.newInstance(slide3Title , slide3Des,slide3Image ,slide3Bg));

        //slide 4
        val slide4Title = getString(R.string.builtin_exchange) as String
        val slide4Des = getString(R.string.builtin_exchange_desc) as String
        val slide4Image: Int =  R.drawable.trade
        val slide4Bg = ContextCompat.getColor(this, R.color.orange)

        //add slide 4
        addSlide(AppIntroFragment.newInstance(slide4Title , slide4Des,slide4Image ,slide4Bg));

        //slide 5
        val slide5Title = getString(R.string.builtin_security) as String
        val slide5Des = getString(R.string.builtin_security_desc) as String
        val slide5Image: Int =  R.drawable.security
        val slide5Bg = ContextCompat.getColor(this, R.color.green)

        //add slide 5
        addSlide(AppIntroFragment.newInstance(slide5Title , slide5Des,slide5Image ,slide5Bg));

        setVibrate(true)
        setVibrateIntensity(40);
    }//end

    override fun onResume() {
        val introCompleted :Any? = getSharedPref(this, "intro_completed")

        //if the intro has been completed already dont show
        //just skip to next activity
        if(introCompleted == true){
            closeIntro(false)
        }

        //if we are here then it means, the intro was not completed
        super.onResume()

        //enable full screen
        enableFullScreen(this)

    }//end on resume

    /**
     * onBackPressed
     */
    override fun onBackPressed(){
        //minimize the app
        minimizeApp(this)
    }//end on back pressed


    /**
     * onSkipPressed
     * @currentFragment
     */
    override  fun  onSkipPressed(currentFargment: Fragment){
        super.onSkipPressed(currentFargment)

        //close the intro
        closeIntro()
    }//end onskipPressed


    /**
     * onDonePressed
     * @param currentFragment
     */
    override fun onDonePressed(currentFragment: Fragment){
        super.onDonePressed(currentFragment)

        //close the intro
        closeIntro()
    }//end fun

    /**
     * openLoginActivity
     */
    private fun closeIntro(updateDB: Boolean = true){

        if(updateDB) {
            //lets update status that user has finished intro
            saveSharedPref(this, "intro_completed", true)
        }

        //open activity AuthActivity
        startActivity(
                intentFor<MainActivity>()
                        .clearTop()
                        .newTask()
        )

        //finish the activity
        this.finish()
    }//end open login activity

}//end class
