package com.as.fpphone.fragments;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.as.fpphone.R;
import com.as.fpphone.adapters.ContactAdapter;
import com.as.fpphone.modals.ContactModal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ContactsFragment extends Fragment {

    List<ContactModal> contactsList;



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        contactsList = new ArrayList<>();

        RecyclerView recyclerView = view.findViewById(R.id.contact_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }


    public void queryContacts(){
        ContentResolver contentResolver = requireContext().getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
        null, null,null, ContactsContract.Contacts.DISPLAY_NAME + " ASC" );

        if(cursor!= null && cursor.getCount() >0){

            while(cursor.moveToNext()){
                @SuppressLint("Range")
                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                @SuppressLint("Range")
                String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));


                //Receive PhoneNumbers associated with each contact

                List<String> phoneNumbers = new ArrayList<>();
                Cursor phoneCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        new String[]{contactId},
                        null
                );
                if (phoneCursor != null) {
                    while (phoneCursor.moveToNext()) {
                        @SuppressLint("Range")
                        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneNumbers.add(phoneNumber);
                    }
                    phoneCursor.close();
                }

                // Receive photoUri
                Uri photoUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI,String.valueOf(contactId));
                ContactModal contact = new ContactModal(displayName,phoneNumbers,photoUri);
                contactsList.add(contact);
            }
            cursor.close();
        }
    }
}