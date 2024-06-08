package com.as.fpphone.activities;

import static android.telecom.TelecomManager.ACTION_CHANGE_DEFAULT_DIALER;

import android.app.role.RoleManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.as.fpphone.R;

public class DefaultActivity extends AppCompatActivity {

    public static final int REQ_CODE_FOR_DEFAULT_APP = 1000;

    Button setDefaultButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_default);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Automatically set the dark mode theme for the app, this was done
        // to achieve white status bar icon color
      //  AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        //initializing

        setDefaultButton = findViewById(R.id.setDefaultBtn);

        if(isDefaultPhoneApp(this)){
            startActivity(new Intent(DefaultActivity.this, MainActivity.class));
            finish();
        }
        else {
              setDefaultButton.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {

                      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                          RoleManager roleManager = getApplicationContext().getSystemService(RoleManager.class);
                          Intent roleRequestIntent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER);
                          startActivityForResult(roleRequestIntent,REQ_CODE_FOR_DEFAULT_APP);
                      }

                  }
              });
        }
    }

    public boolean isDefaultPhoneApp(Context context){
        TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
        String defaultDialerPackage = telecomManager.getDefaultDialerPackage();
        String myPackageName = context.getPackageName();
        return myPackageName.equals(defaultDialerPackage);
    }

    public void requestToChangeDefaultPhoneApp(Context context){
        Intent intent = new Intent(ACTION_CHANGE_DEFAULT_DIALER);
        intent.putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, context.getPackageName());
        context.startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQ_CODE_FOR_DEFAULT_APP){
            if(resultCode == RESULT_OK){
                startActivity(new Intent(DefaultActivity.this, MainActivity.class));
                finish();
            }
            else {
                Toast.makeText(this,"APP needs to be set as default, to use it!",Toast.LENGTH_SHORT).show();

            }
        }
    }
}