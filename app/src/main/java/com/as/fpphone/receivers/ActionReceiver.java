package com.as.fpphone.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.as.fpphone.helpers.CallListHelper;
import com.as.fpphone.helpers.CallManager;

public class ActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle intentExtras = intent.getExtras();
        assert intentExtras != null;

        if(intentExtras.containsKey("pickUpCall")){
            String action = intent.getStringExtra("pickUpCall");
            assert action != null;

            if(action.equalsIgnoreCase("YES")){
                CallManager.answerCall(CallListHelper.callList.get(CallManager.NUMBER_OF_CALLS-1));
            }

            else if(action.equalsIgnoreCase("NO")){
                CallManager.hangUpCall(CallListHelper.callList.get(CallManager.NUMBER_OF_CALLS - 1));
            }
        }

        if(intentExtras.containsKey("cancelCall")){
            String action = intent.getStringExtra("cancelCall");

            assert action != null;
            if(action.equalsIgnoreCase("YES"))
                CallManager.hangUpCall(CallListHelper.callList.get(CallManager.NUMBER_OF_CALLS - 1));
        }

        if(intentExtras.containsKey("endCall")){

            String action = intent.getStringExtra("endCall");

            assert action != null;
            if(action.equalsIgnoreCase("YES"))
                CallManager.hangUpCall(CallListHelper.callList.get(CallManager.NUMBER_OF_CALLS - 1));

        }

    }
}
