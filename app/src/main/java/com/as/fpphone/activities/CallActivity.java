package com.as.fpphone.activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.telecom.Call;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CallActivity extends AppCompatActivity {


    Context context;

    ImageView callerImage;

  TextView callerNameTV, callDurationTV, callStatusTV;

    Button btn0,btn1,btn2,btn3,btn4,btn5,btn6,btn7,btn8,btn9,btnHash,btnStar;
    BottomSheetDialog keypadDialog;
    String keypadDialogTVText ="";

    TextView incomingCallerPhoneNumberTV, incomingCallerNameTV, ringingStatusTV;

    EditText noteET;

    FloatingActionButton rejectIncomingCallBtn, acceptIncomingCallBtn;

    RelativeLayout inProgressCallRLView, incomingRLView;

    MaterialButton keypadBtn, holdBtn, addCallBtn, speakerBtn, endCallBtn, muteBtn;

    public static String PHONE_NUMBER, CALLER_NAME;

    public CallActivity(Context context) {
        this.context = context;
    }

    public CallActivity() {

    }


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

                if(action.equals("call_ended")){
                    finishAndRemoveTask();
                }
                else if (action.equals("call_answered")) {
                    inProgressCallRLView.setVisibility(View.VISIBLE);
                    incomingRLView.setVisibility(View.GONE);

                    if (CallListHelper.callList.get(CallManager.NUMBER_OF_CALLS -1).getDetails().hasProperty(Call.Details.PROPERTY_CONFERENCE)){
                        PHONE_NUMBER = "Conference";
                        CALLER_NAME = "Conference";
                    }
                    else{
                        PHONE_NUMBER = CallListHelper.callList.get(CallManager.NUMBER_OF_CALLS -1).getDetails().getHandle().getSchemeSpecificPart();
                        CALLER_NAME = "HEY";
                    }

                    callerNameTV.setText(PHONE_NUMBER);
                }
            }
        };

        IntentFilter intentFilter =new IntentFilter();
        intentFilter.addAction("call_ended");
        intentFilter.addAction("call_answered");
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

        if(callState == Call.STATE_CONNECTING || callState==Call.STATE_DIALING){

        }
        else if(callState == Call.STATE_ACTIVE || callState == Call.STATE_HOLDING){
            Intent broadcastIntent = new Intent("call_answered");
            sendBroadcast(broadcastIntent);
        }
        else if (callState == Call.STATE_RINGING) {
            inProgressCallRLView.setVisibility(View.GONE);
            incomingRLView.setVisibility(View.VISIBLE);
        }
    }

    public ImageView getCallerImage() {
        return callerImage;
    }

    public TextView getCallerNameTV() {
        return callerNameTV;
    }

    public TextView getCallDurationTV() {
        return callDurationTV;
    }

    public TextView getCallStatusTV() {
        return callStatusTV;
    }

    public BottomSheetDialog getKeypadDialog() {
        return keypadDialog;
    }

    public String getKeypadDialogTVText() {
        return keypadDialogTVText;
    }

    public TextView getIncomingCallerPhoneNumberTV() {
        return incomingCallerPhoneNumberTV;
    }

    public TextView getIncomingCallerNameTV() {
        return incomingCallerNameTV;
    }

    public TextView getRingingStatusTV() {
        return ringingStatusTV;
    }

    public EditText getNoteET() {
        return noteET;
    }

    public FloatingActionButton getRejectIncomingCallBtn() {
        return rejectIncomingCallBtn;
    }

    public FloatingActionButton getAcceptIncomingCallBtn() {
        return acceptIncomingCallBtn;
    }

    public RelativeLayout getInProgressCallRLView() {
        return inProgressCallRLView;
    }

    public RelativeLayout getIncomingRLView() {
        return incomingRLView;
    }

    public MaterialButton getKeypadBtn() {
        return keypadBtn;
    }

    public MaterialButton getHoldBtn() {
        return holdBtn;
    }

    public MaterialButton getAddCallBtn() {
        return addCallBtn;
    }

    public MaterialButton getSpeakerBtn() {
        return speakerBtn;
    }

    public MaterialButton getEndCallBtn() {
        return endCallBtn;
    }

    public MaterialButton getMuteBtn() {
        return muteBtn;
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