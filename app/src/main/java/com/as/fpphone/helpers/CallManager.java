package com.as.fpphone.helpers;

import android.content.Intent;
import android.telecom.Call;
import android.telecom.CallAudioState;
import android.telecom.InCallService;
import android.telecom.VideoProfile;
import android.widget.Toast;

import com.as.fpphone.services.CallService;

public class CallManager {

    public static int NUMBER_OF_CALLS = 0;

    public static InCallService inCallService;

    public static int FP_CALL_STATE = 0;

    public  static Call.Callback callback = new Call.Callback() {
        @Override
        public void onStateChanged(Call call, int newState) {

            super.onStateChanged(call, newState);
            FP_CALL_STATE = newState;

            if(newState == Call.STATE_ACTIVE){
                Intent broadcastIntent = new Intent("call_answered");
                inCallService.sendBroadcast(broadcastIntent);
            }
            else if(newState == Call.STATE_DISCONNECTING){
                Intent intent = new Intent("call_disconnecting");
                inCallService.sendBroadcast(intent);
            }
            else if(newState == Call.STATE_DISCONNECTED){

                call.unregisterCallback(callback);
                Intent intent = new Intent("call_ended");
                inCallService.sendBroadcast(intent);

                CallListHelper.callList.remove(NUMBER_OF_CALLS - 1);

                NUMBER_OF_CALLS = NUMBER_OF_CALLS - 1;

            }
            else if(newState == Call.STATE_HOLDING){

            }
        }
    };

    public static void answerCall(Call mCall){
        mCall.answer(VideoProfile.STATE_AUDIO_ONLY);
    }
    public static void hangUpCall(Call mCall){
        mCall.disconnect();
    }
    public static void playDTMFTone(Call call, char c ){
        call.playDtmfTone(c);
        call.stopDtmfTone();
    }
    public static void holdCall(Call mCall){
        mCall.hold();
        Toast.makeText(inCallService, "Call on hold", Toast.LENGTH_SHORT).show();
    }

    public static void unHoldCall(Call mCall){
        mCall.unhold();
        Toast.makeText(inCallService,"Call unhold",Toast.LENGTH_SHORT).show();
    }

    public static void muteCall(boolean isMuted){

        inCallService.setMuted(isMuted);

        if(isMuted){
            Toast.makeText(inCallService,"Call muted",Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(inCallService,"Call unmuted",Toast.LENGTH_SHORT).show();
        }

    }

    public static void speakerCall(boolean isSpeakerOn){
        if(isSpeakerOn){
            inCallService.setAudioRoute(CallAudioState.ROUTE_SPEAKER);
            Toast.makeText(inCallService,"Speaker on",Toast.LENGTH_SHORT).show();
        }
        else{
            inCallService.setAudioRoute(CallAudioState.ROUTE_EARPIECE);
            Toast.makeText(inCallService,"Speaker Off",Toast.LENGTH_SHORT).show();
        }
    }
}
