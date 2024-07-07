package com.example.android.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.android.R;
import com.example.android.databinding.ActivityOrgProfileBinding;
import com.example.android.utilities.Constants;
import com.example.android.utilities.PreferenceManager;

public class OrgProfileActivity extends AppCompatActivity {
    private ActivityOrgProfileBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrgProfileBinding.inflate(getLayoutInflater());
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
        binding.name.setText(preferenceManager.getString(Constants.KEY_ORG_NAME));
        binding.textName.setText(preferenceManager.getString(Constants.KEY_ORG_NAME));
        binding.contact.setText(preferenceManager.getString(Constants.KEY_ORG_CONTACT));
        binding.textAddress.setText(preferenceManager.getString(Constants.KEY_ORG_ADDRESS));
        binding.textEmail.setText(preferenceManager.getString(Constants.KEY_ORG_EMAIL));
        binding.interestText.setText(preferenceManager.getString(Constants.KEY_TYPE));
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
        preferenceManager.putString(Constants.USER_TYPE,Constants.USER_TYPE_ORGANISATION);
        Intent intent = new Intent(OrgProfileActivity.this, EditProfileActivity.class);
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