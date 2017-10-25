package com.SDH3.VCA;

import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by Cian on 19/10/2017.
 */

public class CallListener extends PhoneStateListener{

    private boolean calling = false;
    private String log = "Ringing";


    @Override
    public void onCallStateChanged(int state, String number)
    {
        if(TelephonyManager.CALL_STATE_RINGING == state)
        {
            Log.i(log, number);
        }
        else if(TelephonyManager.CALL_STATE_OFFHOOK == state)
        {
            calling = true;
        }
        else if(TelephonyManager.CALL_STATE_IDLE ==state)
        {
            calling = false;
        }
    }
}
