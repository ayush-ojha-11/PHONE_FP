package com.as.fpphone.helpers;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

public class ContactHelper {
    public static String getContactName(String phoneNumber, Context context){

        String contactName = "";

        if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(phoneNumber));

            String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

            Cursor cursor = context.getContentResolver().query(uri,projection,null,null,null);

            if(cursor!=null){
                if(cursor.moveToFirst()){
                    contactName = cursor.getString(0);
                }
                cursor.close();
            }

            if (contactName.isEmpty()){
                contactName ="Unknown";
            }
        }
        else {
            Toast.makeText(context, "Please grant contacts permission", Toast.LENGTH_SHORT).show();
        }

        return contactName;

    }
}
