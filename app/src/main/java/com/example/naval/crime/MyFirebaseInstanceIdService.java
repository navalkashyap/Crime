package com.example.naval.crime;

import android.util.Log;


import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by sweth on 2/4/2017.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    private static final String REG_TOKEN="REG_TOKEN";
    @Override
    //Each time a new token is created in the system, this method is called to get the most recent version of registeration token
    public void onTokenRefresh() {

        String recent_token = FirebaseInstanceId.getInstance().getToken();
        Log.d(REG_TOKEN, recent_token);
    }
}