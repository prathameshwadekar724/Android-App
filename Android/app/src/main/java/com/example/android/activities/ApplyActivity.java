package com.example.android.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import com.example.android.databinding.ActivityApplyBinding;
import com.example.android.utilities.Constants;
import com.example.android.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ApplyActivity extends AppCompatActivity {
    private ActivityApplyBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityApplyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListener();
    }
    private void setListener(){
        Intent intent = getIntent();
        String id = intent.getStringExtra("postId");
        String name = intent.getStringExtra("postName");
        String orgId = intent.getStringExtra("orgId");
        String orgName = intent.getStringExtra("userName");
        String userId = preferenceManager.getString(Constants.KEY_USER_ID);
        String userName = preferenceManager.getString(Constants.KEY_NAME);
        String address = preferenceManager.getString(Constants.KEY_ADDRESS);
        String city = preferenceManager.getString(Constants.KEY_CITY);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Constants.KEY_POST_COLLECTION).child(Constants.KEY_POST_APPLY);
        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put(Constants.KEY_MESSAGE,binding.etMessage.getText().toString());
                hashMap.put(Constants.KEY_POST_NAME,name);
                hashMap.put(Constants.KEY_POST_ID,id);
                hashMap.put(Constants.KEY_NAME,userName);
                hashMap.put(Constants.KEY_ADDRESS,address);
                hashMap.put(Constants.KEY_CITY,city);
                hashMap.put(Constants.KEY_ORG_ID,orgId);
                hashMap.put(Constants.KEY_USER_ID,userId);
                hashMap.put(Constants.KEY_ORG_NAME,orgName);
                reference.child(id).child(userId).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        preferenceManager.putBoolean(Constants.KEY_IS_USER_APPLY,true);
                        Intent intent1 = new Intent(getApplicationContext(),MainActivity.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent1);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ApplyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}