package com.example.android.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.android.R;
import com.example.android.databinding.ActivityEditProfileBinding;
import com.example.android.utilities.Constants;
import com.example.android.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {
    private ActivityEditProfileBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        String userType = preferenceManager.getString(Constants.USER_TYPE);
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationIcon(R.drawable.round_back);
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());

        binding.buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading(true);
                if (userType.equals(Constants.USER_TYPE_VOLUNTEER)){
                    updateProfile();
                } else if (userType.equals(Constants.USER_TYPE_ORGANISATION)) {
                    updateOrgProfile();
                }
            }
        });
        loadData(userType);
    }
    private void loadData(String userType){
        if (userType.equals(Constants.USER_TYPE_VOLUNTEER)){
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
        } else if (userType.equals(Constants.USER_TYPE_ORGANISATION)) {
            binding.genderLayout.setVisibility(View.GONE);
            binding.cityLayout.setVisibility(View.GONE);
            binding.stateLayout.setVisibility(View.GONE);
            binding.codeLayout.setVisibility(View.GONE);
            binding.dateLayout.setVisibility(View.GONE);
            binding.occLayout.setVisibility(View.GONE);
            binding.textName.setText(preferenceManager.getString(Constants.KEY_ORG_NAME));
            binding.contact.setText(preferenceManager.getString(Constants.KEY_ORG_CONTACT));
            binding.textAddress.setText(preferenceManager.getString(Constants.KEY_ORG_ADDRESS));
            binding.textEmail.setText(preferenceManager.getString(Constants.KEY_ORG_EMAIL));
            binding.interestText.setText(preferenceManager.getString(Constants.KEY_TYPE));
        }
        loading(false);
    }
    private void updateProfile(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser!=null){
            firebaseUser.updateEmail(binding.textEmail.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        preferenceManager.putString(Constants.KEY_NAME,binding.textName.getText().toString().trim());
                        preferenceManager.putString(Constants.KEY_CONTACT,binding.contact.getText().toString().trim());
                        preferenceManager.putString(Constants.KEY_ADDRESS,binding.textAddress.getText().toString().trim());
                        preferenceManager.putString(Constants.KEY_EMAIL,binding.textEmail.getText().toString().trim());
                        preferenceManager.putString(Constants.KEY_GENDER,binding.gender.getText().toString().trim());
                        preferenceManager.putString(Constants.KEY_CITY,binding.textCity.getText().toString().trim());
                        preferenceManager.putString(Constants.KEY_STATE,binding.textState.getText().toString().trim());
                        preferenceManager.putString(Constants.KEY_CODE,binding.textCode.getText().toString().trim());
                        preferenceManager.putString(Constants.KEY_POST_DATE,binding.date.getText().toString().trim());
                        preferenceManager.putString(Constants.KEY_OCCUPATION,binding.occupationText.getText().toString().trim());
                        preferenceManager.putString(Constants.KEY_INTEREST,binding.interestText.getText().toString().trim());

                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put(Constants.KEY_NAME,binding.textName.getText().toString().trim());
                        hashMap.put(Constants.KEY_CONTACT,binding.contact.getText().toString().trim());
                        hashMap.put(Constants.KEY_ADDRESS,binding.textAddress.getText().toString().trim());
                        hashMap.put(Constants.KEY_EMAIL,binding.textEmail.getText().toString().trim());
                        hashMap.put(Constants.KEY_GENDER,binding.gender.getText().toString().trim());
                        hashMap.put(Constants.KEY_CITY,binding.textCity.getText().toString().trim());
                        hashMap.put(Constants.KEY_STATE,binding.textState.getText().toString().trim());
                        hashMap.put(Constants.KEY_CODE,binding.textCode.getText().toString().trim());
                        hashMap.put(Constants.KEY_POST_DATE,binding.date.getText().toString().trim());
                        hashMap.put(Constants.KEY_OCCUPATION,binding.occupationText.getText().toString().trim());
                        hashMap.put(Constants.KEY_INTEREST,binding.interestText.getText().toString().trim());

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Constants.KEY_COLLECTION).child(firebaseUser.getUid());
                        reference.setValue(hashMap);

                        onBackPressed();
                        loading(false);
                    }else {
                        Toast.makeText(EditProfileActivity.this, "Failed to update", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    private void updateOrgProfile(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser!=null){
            firebaseUser.updateEmail(binding.textEmail.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        preferenceManager.putString(Constants.KEY_ORG_NAME,binding.textName.getText().toString().trim());
                        preferenceManager.putString(Constants.KEY_ORG_CONTACT,binding.contact.getText().toString().trim());
                        preferenceManager.putString(Constants.KEY_ORG_ADDRESS,binding.textAddress.getText().toString().trim());
                        preferenceManager.putString(Constants.KEY_ORG_EMAIL,binding.textEmail.getText().toString().trim());
                        preferenceManager.putString(Constants.KEY_TYPE,binding.interestText.getText().toString().trim());

                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put(Constants.KEY_ORG_NAME,binding.textName.getText().toString().trim());
                        hashMap.put(Constants.KEY_ORG_CONTACT,binding.contact.getText().toString().trim());
                        hashMap.put(Constants.KEY_ORG_ADDRESS,binding.textAddress.getText().toString().trim());
                        hashMap.put(Constants.KEY_ORG_EMAIL,binding.textEmail.getText().toString().trim());
                        hashMap.put(Constants.KEY_TYPE,binding.interestText.getText().toString().trim());


                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Constants.KEY_ORGANIZATION).child(firebaseUser.getUid());
                        reference.setValue(hashMap);
                        onBackPressed();
                        loading(false);
                    }else {
                        Toast.makeText(EditProfileActivity.this, "Failed to update", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
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