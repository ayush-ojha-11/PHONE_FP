package com.as.fpphone.activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.telecom.Call;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.as.fpphone.R;
import com.as.fpphone.fragments.BottomSheetFragment;
import com.as.fpphone.helpers.CallListHelper;
import com.as.fpphone.helpers.CallManager;
import com.as.fpphone.helpers.ContactHelper;
import com.as.fpphone.helpers.ProximitySensorManager;
import com.as.fpphone.helpers.RingtoneHelper;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class CallActivity extends AppCompatActivity {




    ImageView callerImage;

    TextView callerNameTV, callerPhoneNumberTV,callStatusTV;

    Chronometer callDurationCM;

    Button btn0,btn1,btn2,btn3,btn4,btn5,btn6,btn7,btn8,btn9,btnHash,btnStar;
    BottomSheetDialog keypadDialog;
    String keypadDialogTVText ="";

    TextView incomingCallerPhoneNumberTV, incomingCallerNameTV, ringingStatusTV;

    EditText noteET;

    ImageButton rejectIncomingCallBtn, acceptIncomingCallBtn, endCallBtn;

    RelativeLayout inProgressCallRLView, incomingRLView;

    ImageButton keypadBtn, holdBtn, addCallBtn, speakerBtn, muteBtn;
    public static boolean isMuted, isSpeakerOn, isCallOnHold = false;

    public static String PHONE_NUMBER, CALLER_NAME;

    ProximitySensorManager  proximitySensorManager;


    public CallActivity() {

    }




    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_call);


        // Automatically set the dark mode theme for the app, this is done
        // to achieve white status bar icon color
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initialize();
        addLockScreenFlags();
        onGoingCallButtonListeners();


        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                //Proximity sensor manager

                assert action != null;
                switch (action) {
                    case "call_ended":
                        //Unregister Listener
                        if(proximitySensorManager!=null) {
                            proximitySensorManager.unRegisterListener();
                        }
                        callDurationCM.stop();
                        finishAndRemoveTask();
                        break;
                    case "call_answered":
                        //Stop ringtone and vibration when call answered
                        RingtoneHelper.stopRinging();
                        RingtoneHelper.stopVibration();
                        //Register proximity sensor listener
                        proximitySensorManager = new ProximitySensorManager(CallActivity.this);
                        proximitySensorManager.registerListener();

                        inProgressCallRLView.setVisibility(View.VISIBLE);
                        incomingRLView.setVisibility(View.GONE);

                        if (CallListHelper.callList.get(CallManager.NUMBER_OF_CALLS - 1).getDetails().hasProperty(Call.Details.PROPERTY_CONFERENCE)) {
                            PHONE_NUMBER = "Conference";
                            CALLER_NAME = "Conference";
                        }
                        else {
                            PHONE_NUMBER = CallListHelper.callList.get(CallManager.NUMBER_OF_CALLS - 1).getDetails().getHandle().getSchemeSpecificPart();
                            CALLER_NAME = ContactHelper.getContactName(PHONE_NUMBER,CallActivity.this);
                        }
                        callerNameTV.setText(CALLER_NAME);
                        callerPhoneNumberTV.setText(PHONE_NUMBER);
                        callStatusTV.setText(R.string.connected);

                        // manage chronometer
                        callDurationCM.setBase(SystemClock.elapsedRealtime());
                        callDurationCM.start();
                        break;

                    case "call_disconnecting":
                        ringingStatusTV.setText(R.string.rejected);
                        ringingStatusTV.setTextColor(getColor(R.color.red));
                        break;
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("call_ended");
        intentFilter.addAction("call_answered");
        intentFilter.addAction("call_disconnecting");
        registerReceiver(broadcastReceiver,intentFilter,RECEIVER_EXPORTED);

        rejectIncomingCallBtn.setOnClickListener(v -> CallManager.hangUpCall(CallListHelper.callList.get(CallManager.NUMBER_OF_CALLS-1)));

        acceptIncomingCallBtn.setOnClickListener(v -> CallManager.answerCall(CallListHelper.callList.get(CallManager.NUMBER_OF_CALLS-1)));

    }

    @Override
    protected void onResume() {
        super.onResume();

        int callState = CallManager.FP_CALL_STATE;

        if(callState == Call.STATE_ACTIVE || callState == Call.STATE_HOLDING){
            Intent broadcastIntent = new Intent("call_answered");
            sendBroadcast(broadcastIntent);
        }
        else if (callState == Call.STATE_RINGING) {

            PHONE_NUMBER = CallListHelper.callList.get(CallManager.NUMBER_OF_CALLS - 1).getDetails().getHandle().getSchemeSpecificPart();
            CALLER_NAME = ContactHelper.getContactName(PHONE_NUMBER,this);

            inProgressCallRLView.setVisibility(View.GONE);
            incomingRLView.setVisibility(View.VISIBLE);

            ringingStatusTV.setText(R.string.incoming_call);
            incomingCallerNameTV.setText(CALLER_NAME);
            incomingCallerPhoneNumberTV.setText(PHONE_NUMBER);
        }
    }

    public void initialize(){

        incomingCallerNameTV = findViewById(R.id.incomingCallerName_tv);
        incomingCallerPhoneNumberTV = findViewById(R.id.incomingCallerNumber_tv);
        ringingStatusTV = findViewById(R.id.ringingStatus_tv);
        rejectIncomingCallBtn = findViewById(R.id.rejectIncomingCall_btn);
        acceptIncomingCallBtn = findViewById(R.id.acceptIncomingCall_btn);

        incomingRLView = findViewById(R.id.incoming_call_RL);
        inProgressCallRLView = findViewById(R.id.inProgressRL);

        callerNameTV = findViewById(R.id.caller_name_tv);
        callerPhoneNumberTV = findViewById(R.id.caller_phone_number_tv);
        callStatusTV = findViewById(R.id.call_status_tv);
        callDurationCM = findViewById(R.id.call_time_cm);
        callerImage = findViewById(R.id.caller_iv);


        keypadBtn = findViewById(R.id.keypad_btn);
        holdBtn = findViewById(R.id.hold_btn);
        addCallBtn = findViewById(R.id.add_call_btn);
        speakerBtn = findViewById(R.id.speaker_btn);
        endCallBtn = findViewById(R.id.end_call_btn);
        muteBtn = findViewById(R.id.mute_btn);

        noteET = findViewById(R.id.note_editText);

    }

    @SuppressLint("SetTextI18n")
    public void initBottomSheetButtonsAndDTMFTune(Call call, BottomSheetDialog keypadDialog, TextView keypadDialogTextView){

        btn0 = keypadDialog.findViewById(R.id.btn0);
        btn1 = keypadDialog.findViewById(R.id.btn01);
        btn2 = keypadDialog.findViewById(R.id.btn02);
        btn3 = keypadDialog.findViewById(R.id.btn03);
        btn4 = keypadDialog.findViewById(R.id.btn04);
        btn5 = keypadDialog.findViewById(R.id.btn05);
        btn6 = keypadDialog.findViewById(R.id.btn06);
        btn7 = keypadDialog.findViewById(R.id.btn07);
        btn8 = keypadDialog.findViewById(R.id.btn08);
        btn9 = keypadDialog.findViewById(R.id.btn09);
        btnHash = keypadDialog.findViewById(R.id.btnHash);
        btnStar = keypadDialog.findViewById(R.id.btnStar);

        btn0.setOnClickListener(v -> {
            CallManager.playDTMFTone(call,'0');
            keypadDialogTVText = keypadDialogTextView.getText().toString();
            keypadDialogTextView.setText(keypadDialogTVText+"0");
        });
        btn1.setOnClickListener(v -> {
            CallManager.playDTMFTone(call,'1');
            keypadDialogTVText = keypadDialogTextView.getText().toString();
            keypadDialogTextView.setText(keypadDialogTVText+"1");
        });
        btn2.setOnClickListener(v -> {
            CallManager.playDTMFTone(call,'2');
            keypadDialogTVText = keypadDialogTextView.getText().toString();
            keypadDialogTextView.setText(keypadDialogTVText+"2");
        });
        btn3.setOnClickListener(v -> {
            CallManager.playDTMFTone(call,'3');
            keypadDialogTVText = keypadDialogTextView.getText().toString();
            keypadDialogTextView.setText(keypadDialogTVText+"3");
        });
        btn4.setOnClickListener(v -> {
            CallManager.playDTMFTone(call,'4');
            keypadDialogTVText = keypadDialogTextView.getText().toString();
            keypadDialogTextView.setText(keypadDialogTVText+"4");
        });
        btn5.setOnClickListener(v -> {
            CallManager.playDTMFTone(call,'5');
            keypadDialogTVText = keypadDialogTextView.getText().toString();
            keypadDialogTextView.setText(keypadDialogTVText+"5");
        });
        btn6.setOnClickListener(v -> {
            CallManager.playDTMFTone(call,'6');
            keypadDialogTVText = keypadDialogTextView.getText().toString();
            keypadDialogTextView.setText(keypadDialogTVText+"6");
        });
        btn7.setOnClickListener(v -> {
            CallManager.playDTMFTone(call,'7');
            keypadDialogTVText = keypadDialogTextView.getText().toString();
            keypadDialogTextView.setText(keypadDialogTVText+"7");
        });
        btn8.setOnClickListener(v -> {
            CallManager.playDTMFTone(call,'8');
            keypadDialogTVText = keypadDialogTextView.getText().toString();
            keypadDialogTextView.setText(keypadDialogTVText+"8");
        });
        btn9.setOnClickListener(v -> {
            CallManager.playDTMFTone(call,'9');
            keypadDialogTVText = keypadDialogTextView.getText().toString();
            keypadDialogTextView.setText(keypadDialogTVText+"9");
        });
        btnHash.setOnClickListener(v -> {
            CallManager.playDTMFTone(call,'#');
            keypadDialogTVText = keypadDialogTextView.getText().toString();
            keypadDialogTextView.setText(keypadDialogTVText+"#");
        });
        btnStar.setOnClickListener(v -> {
            CallManager.playDTMFTone(call,'*');
            keypadDialogTVText = keypadDialogTextView.getText().toString();
            keypadDialogTextView.setText(keypadDialogTVText+"*");
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O_MR1)
    public void addLockScreenFlags(){
        setShowWhenLocked(true);
        setTurnScreenOn(true);
        getWindow().addFlags(
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        );
    }


    public void onGoingCallButtonListeners(){
        muteBtn.setOnClickListener(v -> {
            RingtoneHelper.slightVibration();

            if(isMuted){
                CallManager.muteCall(false);
                muteBtn.setBackgroundResource(R.drawable.round_button);
                muteBtn.setColorFilter(getColor(R.color.white), PorterDuff.Mode.SRC_IN);
                isMuted=false;
            }
            else {
                CallManager.muteCall(true);
                muteBtn.setBackgroundResource(R.drawable.round_button_pressed);
                muteBtn.setColorFilter(getColor(R.color.black), PorterDuff.Mode.SRC_IN);
                isMuted=true;
            }

        });


        speakerBtn.setOnClickListener(v -> {
            RingtoneHelper.slightVibration();

            if(isSpeakerOn){
                CallManager.speakerCall(false);
                //Changing button appearance when in on state or off state
                speakerBtn.setBackgroundResource(R.drawable.round_button);
                speakerBtn.setColorFilter(getColor(R.color.white), PorterDuff.Mode.SRC_IN);
                isSpeakerOn=false;
            }
            else {
                CallManager.speakerCall(true);
                speakerBtn.setBackgroundResource(R.drawable.round_button_pressed);
                speakerBtn.setColorFilter(getColor(R.color.black), PorterDuff.Mode.SRC_IN);
                isSpeakerOn=true;
            }
        });

        holdBtn.setOnClickListener(v -> {
            RingtoneHelper.slightVibration();
            if(isCallOnHold){
                CallManager.unHoldCall(CallListHelper.callList.get(CallManager.NUMBER_OF_CALLS-1));
                holdBtn.setBackgroundResource(R.drawable.round_button);
                holdBtn.setColorFilter(getColor(R.color.white), PorterDuff.Mode.SRC_IN);
                callStatusTV.setText(R.string.connected);
                callStatusTV.setTextColor(getColor(R.color.white));
                isCallOnHold = false;
            }
            else{
                CallManager.holdCall(CallListHelper.callList.get(CallManager.NUMBER_OF_CALLS-1));
                holdBtn.setBackgroundResource(R.drawable.round_button_pressed);
                holdBtn.setColorFilter(getColor(R.color.black), PorterDuff.Mode.SRC_IN);
                callStatusTV.setText(R.string.on_hold);
                callStatusTV.setTextColor(getColor(R.color.red));
                isCallOnHold = true;
            }
        });


        endCallBtn.setOnClickListener(v -> {
            RingtoneHelper.slightVibration();
            CallManager.hangUpCall(CallListHelper.callList.get(CallManager.NUMBER_OF_CALLS-1));
        });


        keypadBtn.setOnClickListener(v -> {
            BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
            bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unregister proximity sensor listener when activity enters onStop()
        if (proximitySensorManager != null) {
            proximitySensorManager.unRegisterListener();
        }
    }
}