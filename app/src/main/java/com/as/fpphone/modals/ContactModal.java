package com.as.fpphone.modals;

import android.net.Uri;

import java.util.List;

public class ContactModal {
    private String displayName;
    private List<String> phoneNumber;
    private Uri photoUri;

    public ContactModal(String displayName, List<String> phoneNumber, Uri photoUri) {
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


    public List<String> getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(List<String> phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Uri getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(Uri photoUri) {
        this.photoUri = photoUri;
    }
}
