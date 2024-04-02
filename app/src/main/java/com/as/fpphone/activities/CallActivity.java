package com.as.fpphone.activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.telecom.Call;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.as.fpphone.R;
import com.as.fpphone.helpers.CallListHelper;
import com.as.fpphone.helpers.CallManager;
import com.as.fpphone.helpers.ContactHelper;
import com.as.fpphone.helpers.RingtoneHelper;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class CallActivity extends AppCompatActivity {


    Context context;

    ImageView callerImage;

    TextView callerNameTV, callDurationTV, callStatusTV;

    Button btn0,btn1,btn2,btn3,btn4,btn5,btn6,btn7,btn8,btn9,btnHash,btnStar;
    BottomSheetDialog keypadDialog;
    String keypadDialogTVText ="";

    TextView incomingCallerPhoneNumberTV, incomingCallerNameTV, ringingStatusTV;

    EditText noteET;

    ImageButton rejectIncomingCallBtn, acceptIncomingCallBtn, endCallBtn;

    RelativeLayout inProgressCallRLView, incomingRLView;

    ImageButton keypadBtn, holdBtn, addCallBtn, speakerBtn, muteBtn;
    public static boolean isMuted, isSpeakerOn, isCallOnHold;
    public  static String muteBtnName = "Mute", speakerBtnName = "Speaker On";

    public static String PHONE_NUMBER, CALLER_NAME;

    public CallActivity(Context context) {
        this.context = context;
    }

    public CallActivity() {

    }


    RingtoneHelper ringtoneHelper = new RingtoneHelper();


    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_call);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initialize();
        addLockScreenFlags();


        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();

                assert action != null;
                switch (action) {
                    case "call_ended":
                        finishAndRemoveTask();
                        break;
                    case "call_answered":
                        //Stop ringtone and vibration when call answered
                        RingtoneHelper.stopRinging();
                        RingtoneHelper.stopVibration();
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
                        callStatusTV.setText("Connected");
                        callStatusTV.setTextColor(Color.GREEN);
                        break;

                    case "call_disconnecting":
                        ringingStatusTV.setText("Rejected");
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

        rejectIncomingCallBtn.setOnClickListener(v -> {
            CallManager.hangUpCall(CallListHelper.callList.get(CallManager.NUMBER_OF_CALLS-1));
        });

        acceptIncomingCallBtn.setOnClickListener(v -> {
            CallManager.answerCall(CallListHelper.callList.get(CallManager.NUMBER_OF_CALLS-1));
        });

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
        callStatusTV = findViewById(R.id.call_status_tv);
        callDurationTV = findViewById(R.id.call_time_tv);
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
}