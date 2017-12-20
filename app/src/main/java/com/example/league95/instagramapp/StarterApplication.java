package com.example.league95.instagramapp;



/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;


public class StarterApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        // Add your initialization code here
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                // Application id fetched from server.js file
                // From our AWS ssh host
                .applicationId("7cb00b29169287ed6f19f178056d89c542ea773c")
                // From same file
                .clientKey("096091415e7855ccf435b0e741653a776741d4c0")
                // Apparently you will get an error if you don't add a backslash
                // to the end of parse...
                .server("http://ec2-13-58-171-4.us-east-2.compute.amazonaws.com:80/parse/")
                .build()
        );
        //We don't need automatic users for our app
        //ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        // Optionally enable public read access.
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

    }
}

