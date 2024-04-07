package com.as.fpphone.modals;

import android.net.Uri;

public class ContactModal {
    private String displayName;
    private String phoneNumber;
    private Uri photoUri;


    public ContactModal(String displayName, String phoneNumber, Uri photoUri) {
        this.displayName = displayName;
        this.phoneNumber = phoneNumber;
        this.photoUri = photoUri;
    }
    public ContactModal (String displayName, Uri photoUri){
        this.displayName = displayName;
        this.photoUri = photoUri;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Uri getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(Uri photoUri) {
        this.photoUri = photoUri;
    }
}
