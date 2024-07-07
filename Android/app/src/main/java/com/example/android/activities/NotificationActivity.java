package com.example.android.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CpuUsageInfo;
import android.view.View;
import android.widget.Toast;

import com.example.android.R;
import com.example.android.adapters.NotificationAdapter;
import com.example.android.databinding.ActivityNotificationBinding;
import com.example.android.models.Message;
import com.example.android.utilities.Constants;
import com.example.android.utilities.PreferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {
    private ActivityNotificationBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        binding.toolbar.setTitle("Notification");
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationIcon(R.drawable.round_back);
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
        String userType = preferenceManager.getString(Constants.USER_TYPE);
        if (Constants.USER_TYPE_VOLUNTEER.equals(userType)){
            getReceivedMessage();
        } else if (Constants.USER_TYPE_ORGANISATION.equals(userType)) {
            getSentMessage();
        }
    }

    private void getReceivedMessage(){
        String userId = preferenceManager.getString(Constants.KEY_USER_ID);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Constants.KEY_POST_COLLECTION).child(Constants.KEY_POST_APPROVE);
        loading(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    List<Message> messageList = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            String id = dataSnapshot1.child(Constants.KEY_USER_ID).getValue(String.class);
                            if (userId.equals(id)){
                                Message message = dataSnapshot1.getValue(Message.class);
                                messageList.add(message);
                            }
                        }
                        if (messageList.size()>0){
                            NotificationAdapter notificationAdapter = new NotificationAdapter(messageList,NotificationActivity.this,preferenceManager);
                            binding.recyclerView.setAdapter(notificationAdapter);
                            binding.recyclerView.setVisibility(View.VISIBLE);

                        }else{
                            showErrorMessage();
                        }
                        loading(false);
                    }
                }else{
                    loading(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loading(false);
                showErrorMessage();
            }
        });
    }
    private void getSentMessage(){
        String id = preferenceManager.getString(Constants.KEY_ORG_ID);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Constants.KEY_POST_COLLECTION).child(Constants.KEY_POST_APPLY);
        loading(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    List<Message> messageList = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            String userId = dataSnapshot1.child(Constants.KEY_ORG_ID).getValue(String.class);
                            if (id.equals(userId)){
                                if (preferenceManager.getBoolean(Constants.KEY_IS_USER_APPROVED)){
                                    continue;
                                }
                                Message message = dataSnapshot1.getValue(Message.class);
                                messageList.add(message);
                            }
                        }
                        if (messageList.size()>0){
                            NotificationAdapter notificationAdapter = new NotificationAdapter(messageList,NotificationActivity.this,preferenceManager);
                            binding.recyclerView.setAdapter(notificationAdapter);
                            binding.recyclerView.setVisibility(View.VISIBLE);
                        }else{
                            showErrorMessage();
                        }
                    }
                    loading(false);

                }else{
                    loading(false);
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