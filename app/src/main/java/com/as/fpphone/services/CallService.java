package com.as.fpphone.services;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.os.Build;
import android.telecom.Call;
import android.telecom.InCallService;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.as.fpphone.activities.CallActivity;
import com.as.fpphone.helpers.CallListHelper;
import com.as.fpphone.helpers.CallManager;
import com.as.fpphone.helpers.RingtoneHelper;

public class CallService extends InCallService {

    RingtoneHelper ringtoneHelper = new RingtoneHelper();

    int call_state;
    static Ringtone ringtone;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);

        CallListHelper.callList.add(call);
        CallManager.inCallService = this;

        CallManager.NUMBER_OF_CALLS = CallListHelper.callList.size();

        call.registerCallback(CallManager.callback);
        call_state = call.getDetails().getState();

        //Updating call state
        CallManager.FP_CALL_STATE = call_state;

        if(call_state == Call.STATE_RINGING){
            //Play the ringtone during incoming call
            RingtoneHelper.playRingtone(this);

            //Checking if the device is on lockscreen or home-screen
            KeyguardManager keyguardManager =(KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            boolean isDeviceLocked = keyguardManager!=null && keyguardManager.isKeyguardLocked();

            if(isDeviceLocked){
                Intent intent = new Intent(this,CallActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Toast.makeText(this, "Incoming call from " + call.getDetails().getHandle().getSchemeSpecificPart(), Toast.LENGTH_SHORT).show();
            }
            else {
                //When on home-screen
            }
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
        //Stop ringing or vibration when call ended
        RingtoneHelper.stopRinging();
        RingtoneHelper.stopVibration();
        Toast.makeText(this,"Call Ended",Toast.LENGTH_SHORT).show();
    }
}
