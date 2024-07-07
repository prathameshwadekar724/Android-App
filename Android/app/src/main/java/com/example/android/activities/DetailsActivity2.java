package com.example.android.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;

import com.example.android.R;
import com.example.android.databinding.ActivityDetails2Binding;
import com.example.android.models.User;
import com.example.android.utilities.Constants;
import com.example.android.utilities.PreferenceManager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DetailsActivity2 extends AppCompatActivity {

    private ActivityDetails2Binding binding;
    private PreferenceManager preferenceManager;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetails2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        user = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationIcon(R.drawable.round_back);
        setListener();
    }
    private void setListener(){
        String userType = preferenceManager.getString(Constants.USER_TYPE);
        binding.textEmail.setMovementMethod(new ScrollingMovementMethod());
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
        if (Constants.USER_TYPE_VOLUNTEER.equals(userType)){
            if (user!=null){
                binding.textName.setText(user.orgName);
                binding.contact.setText(user.orgContact);
                binding.textAddress.setText(user.orgAddress);
                binding.textEmail.setText(user.orgEmail);
                binding.interestText.setText(user.type);
            }
        } else if (Constants.USER_TYPE_ORGANISATION.equals(userType)) {
            binding.textName.setText(preferenceManager.getString(Constants.KEY_ORG_NAME));
            binding.contact.setText(preferenceManager.getString(Constants.KEY_ORG_CONTACT));
            binding.textAddress.setText(preferenceManager.getString(Constants.KEY_ORG_ADDRESS));
            binding.textEmail.setText(preferenceManager.getString(Constants.KEY_ORG_EMAIL));
            binding.interestText.setText(preferenceManager.getString(Constants.KEY_TYPE));
        } else if (Constants.USER_TYPE_ADMIN.equals(userType)) {
            if (user!=null){
                binding.textName.setText(user.orgName);
                binding.contact.setText(user.orgContact);
                binding.textAddress.setText(user.orgAddress);
                binding.textEmail.setText(user.orgEmail);
                binding.interestText.setText(user.type);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}