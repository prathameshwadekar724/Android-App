package com.example.android.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.android.R;
import com.example.android.adapters.RatingAdapter;
import com.example.android.databinding.ActivityViewBinding;
import com.example.android.models.Message;
import com.example.android.utilities.Constants;
import com.example.android.utilities.PreferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ViewActivity extends AppCompatActivity {
    private ActivityViewBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        binding.toolbar.setTitle("Participants");
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationIcon(R.drawable.round_back);
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
        getUsers();
    }

    private void getUsers(){
        Intent intent = getIntent();
        String id = intent.getStringExtra("postId");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Constants.KEY_POST_COLLECTION).child(Constants.KEY_POST_APPROVE);
        loading(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    List<Message> messageList = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            String postId = dataSnapshot1.child(Constants.KEY_POST_ID).getValue(String.class);
                            if (postId.equals(id)){
                                Message message = dataSnapshot1.getValue(Message.class);
                                messageList.add(message);
                            }
                        }
                        if (messageList.size()>0){
                            RatingAdapter ratingAdapter = new RatingAdapter(messageList,ViewActivity.this,preferenceManager);
                            binding.recyclerView.setAdapter(ratingAdapter);
                            binding.recyclerView.setVisibility(View.VISIBLE);
                        }
                    }
                    loading(false);
                }else{
                    showErrorMessage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loading(false);
                showErrorMessage();
            }
        });
    }
    private void showErrorMessage(){
        binding.textError.setText(String.format("%s","Empty"));
        binding.textError.setVisibility(View.VISIBLE);
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