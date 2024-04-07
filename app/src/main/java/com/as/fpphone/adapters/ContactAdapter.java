package com.as.fpphone.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.as.fpphone.R;
import com.as.fpphone.modals.ContactModal;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private final List<ContactModal> contacts;

    public ContactAdapter(List<ContactModal> contacts) {
        this.contacts = contacts;
    }

    @NonNull
    @Override
    public ContactAdapter.ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item,parent,false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactAdapter.ContactViewHolder holder, int position) {

        ContactModal contact = contacts.get(position);
        holder.bind(contact);

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        CircleImageView contactPhoto;
        TextView displayNameTV;
        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            contactPhoto = itemView.findViewById(R.id.contact_CIV);
            displayNameTV = itemView.findViewById(R.id.displayName_TV);
        }

        public void bind(ContactModal contact){

            displayNameTV.setText(contact.getDisplayName());
            if(contact.getPhotoUri() != null){
                Glide.with(itemView.getContext())
                        .load(contact.getPhotoUri())
                        .placeholder(R.drawable.ic_person)
                        .into(contactPhoto);
            }
            else {
                contactPhoto.setImageResource(R.drawable.ic_person);
            }
        }
    }
}
