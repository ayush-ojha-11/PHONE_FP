package com.as.fpphone.activities;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsetsController;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.as.fpphone.R;
import com.as.fpphone.fragments.ContactsFragment;
import com.as.fpphone.fragments.FavoriteFragment;
import com.as.fpphone.fragments.RecentFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    ContactsFragment contactsFragment = new ContactsFragment();
    FavoriteFragment favoriteFragment = new FavoriteFragment();
    RecentFragment recentFragment = new RecentFragment();

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Automatically set the dark mode theme for the app, this was done
        // to achieve white status bar icon color
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        // This will make sure that Recent Fragment is opened as the app opens
        getSupportFragmentManager().beginTransaction().replace(R.id.container,recentFragment).commit();

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        bottomNavigationView.setOnItemSelectedListener(menuItem -> {

            if(menuItem.getItemId() == R.id.menu_favorites){

                getSupportFragmentManager().beginTransaction().replace(R.id.container,favoriteFragment).commit();
                return true;

            }
            else if(menuItem.getItemId() == R.id.menu_recent){

                getSupportFragmentManager().beginTransaction().replace(R.id.container,recentFragment).commit();
                return true;

            }
            else if(menuItem.getItemId() == R.id.menu_contacts){

                getSupportFragmentManager().beginTransaction().replace(R.id.container,contactsFragment).commit();
                return true;

            }
            return false;
        });


    }
}