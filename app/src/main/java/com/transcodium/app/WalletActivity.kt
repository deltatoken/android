package com.transcodium.app

import android.os.Bundle


class WalletActivity : DrawerActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        //load main view
        setContentView(R.layout.activity_main)

        //Note this is important, The setContent View
        //must come before calling the parent onCreate since
        //we will use layout views from there
        super.onCreate(savedInstanceState)
    }

}
