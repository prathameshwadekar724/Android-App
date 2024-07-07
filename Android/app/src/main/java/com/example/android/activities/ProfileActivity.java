package com.example.android.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.R;
import com.example.android.databinding.ActivityProfileBinding;
import com.example.android.utilities.Constants;
import com.example.android.utilities.PreferenceManager;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationIcon(R.drawable.round_back);
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showDetails();
    }

    private void showDetails(){
        binding.name.setText(preferenceManager.getString(Constants.KEY_NAME));
        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
        binding.contact.setText(preferenceManager.getString(Constants.KEY_CONTACT));
        binding.textAddress.setText(preferenceManager.getString(Constants.KEY_ADDRESS));
        binding.textEmail.setText(preferenceManager.getString(Constants.KEY_EMAIL));
        binding.gender.setText(preferenceManager.getString(Constants.KEY_GENDER));
        binding.textCity.setText(preferenceManager.getString(Constants.KEY_CITY));
        binding.textState.setText(preferenceManager.getString(Constants.KEY_STATE));
        binding.textCode.setText(preferenceManager.getString(Constants.KEY_CODE));
        binding.date.setText(preferenceManager.getString(Constants.KEY_DOB));
        binding.occupationText.setText(preferenceManager.getString(Constants.KEY_OCCUPATION));
        binding.interestText.setText(preferenceManager.getString(Constants.KEY_INTEREST));
        loading(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.editProfile){
            navigateToEditProfile();
            return true;
        }else if (item.getItemId()==android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void navigateToEditProfile() {
        preferenceManager.putString(Constants.USER_TYPE,Constants.USER_TYPE_VOLUNTEER);
        Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
        startActivity(intent);
    }
    private void loading(boolean isLoading){
        if (isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}