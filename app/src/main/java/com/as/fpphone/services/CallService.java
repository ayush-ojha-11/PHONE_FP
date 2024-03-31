package com.as.fpphone.services;

import android.content.Intent;
import android.os.Build;
import android.telecom.Call;
import android.telecom.InCallService;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.as.fpphone.activities.CallActivity;
import com.as.fpphone.helpers.CallListHelper;
import com.as.fpphone.helpers.CallManager;

public class CallService extends InCallService {

    int call_state;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);

        if(call.getDetails().hasProperty(Call.Details.PROPERTY_CONFERENCE)){
            CallActivity callActivity = new CallActivity(this);
            callActivity.getCallerNameTV().setText("Conference Call");
            callActivity.getAddCallBtn().setEnabled(true);
            callActivity.getAddCallBtn().setClickable(true);

            callActivity.getHoldBtn().setClickable(true);
            callActivity.getHoldBtn().setEnabled(true);

        }

        CallListHelper.callList.add(call);
        CallManager.inCallService = this;
        CallManager.NUMBER_OF_CALLS = CallListHelper.callList.size();

        call.registerCallback(CallManager.callback);
        call_state = call.getDetails().getState();

        if(call_state ==Call.STATE_RINGING){
            Intent intent = new Intent(this,CallActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            Toast.makeText(this, "Incoming call from " + call.getDetails().getHandle().getSchemeSpecificPart(), Toast.LENGTH_SHORT).show();
        }

        else if (call_state == Call.STATE_CONNECTING || call_state == Call.STATE_DIALING){

            Intent intent = new Intent(this, CallActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            Toast.makeText(this, "Dialing to " + call.getDetails().getHandle().getSchemeSpecificPart(), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);

        Toast.makeText(this,"Call Ended",Toast.LENGTH_SHORT).show();
        if(CallListHelper.callList.size()>0){

        }
    }
}
