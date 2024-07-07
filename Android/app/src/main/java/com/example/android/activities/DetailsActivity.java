package com.example.android.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;

import com.example.android.R;
import com.example.android.databinding.ActivityDetailsBinding;
import com.example.android.models.User;
import com.example.android.utilities.Constants;
import com.example.android.utilities.PreferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailsActivity extends AppCompatActivity {
    private ActivityDetailsBinding binding;
    private PreferenceManager preferenceManager;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
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
            binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
            binding.contact.setText(preferenceManager.getString(Constants.KEY_CONTACT));
            binding.gender.setText(preferenceManager.getString(Constants.KEY_GENDER));
            binding.textAddress.setText(preferenceManager.getString(Constants.KEY_ADDRESS));
            binding.textEmail.setText(preferenceManager.getString(Constants.KEY_EMAIL));
            binding.textCity.setText(preferenceManager.getString(Constants.KEY_CITY));
            binding.textState.setText(preferenceManager.getString(Constants.KEY_STATE));
            binding.textCode.setText(preferenceManager.getString(Constants.KEY_CODE));
            binding.date.setText(preferenceManager.getString(Constants.KEY_DOB));
            binding.occupationText.setText(preferenceManager.getString(Constants.KEY_OCCUPATION));
            binding.interestText.setText(preferenceManager.getString(Constants.KEY_INTEREST));
        } else if (Constants.USER_TYPE_ORGANISATION.equals(userType)) {
            if (user!=null){
                binding.textName.setText(user.name);
                binding.contact.setText(user.contact);
                binding.gender.setText(user.gender);
                binding.textEmail.setText(user.email);
                binding.textAddress.setText(user.address);
                binding.textCity.setText(user.city);
                binding.textState.setText(user.state);
                binding.textCode.setText(user.pinCode);
                binding.date.setText(user.dob);
                binding.occupationText.setText(user.OCCUPATION);
                binding.interestText.setText(user.interest);
            }
        } else if (Constants.USER_TYPE_ADMIN.equals(userType)) {
            if (user!=null){
                binding.textName.setText(user.name);
                binding.contact.setText(user.contact);
                binding.gender.setText(user.gender);
                binding.textEmail.setText(user.email);
                binding.textAddress.setText(user.address);
                binding.textCity.setText(user.city);
                binding.textState.setText(user.state);
                binding.textCode.setText(user.pinCode);
                binding.date.setText(user.dob);
                binding.occupationText.setText(user.OCCUPATION);
                binding.interestText.setText(user.interest);
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}