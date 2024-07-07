package com.example.android.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.android.R;
import com.example.android.adapters.UserAdapter;
import com.example.android.adapters.UserAdapter2;
import com.example.android.databinding.ActivityList2Binding;
import com.example.android.listeners.UserListeners;
import com.example.android.models.User;
import com.example.android.utilities.Constants;
import com.example.android.utilities.PreferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListActivity2 extends AppCompatActivity implements UserListeners {
    private ActivityList2Binding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityList2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationIcon(R.drawable.round_back);
        String userType = preferenceManager.getString(Constants.USER_TYPE);
        if (Constants.USER_TYPE_VOLUNTEER.equals(userType)){
            getOrgUsers();
        } else if (Constants.USER_TYPE_ORGANISATION.equals(userType)) {
            getUsers();
        }
        setListener();
    }
    private void setListener(){
        binding.toolbar.setNavigationOnClickListener( v -> onBackPressed());
    }
    private void getUsers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Constants.KEY_COLLECTION);
        loading(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    List<User> userList = new ArrayList<>();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        User user1 = dataSnapshot.getValue(User.class);
                        userList.add(user1);
                    }
                    if (userList.size()>0){
                        UserAdapter2 userAdapter = new UserAdapter2(userList,ListActivity2.this,preferenceManager);
                        binding.recyclerView.setAdapter(userAdapter);
                        binding.recyclerView.setVisibility(View.VISIBLE);
                    }else{
                        showErrorMessage();
                    }
                    loading(false);
                }else {
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

    private void getOrgUsers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Constants.KEY_ORGANIZATION);
        loading(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    List<User> userList = new ArrayList<>();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        User user1 = dataSnapshot.getValue(User.class);
                        userList.add(user1);
                    }
                    if (userList.size()>0){
                        UserAdapter2 userAdapter = new UserAdapter2(userList,ListActivity2.this,preferenceManager);
                        binding.recyclerView.setAdapter(userAdapter);
                        binding.recyclerView.setVisibility(View.VISIBLE);
                    }else{
                        showErrorMessage();
                    }
                    loading(false);
                }else {
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
        binding.textError.setText(String.format("%s","No User Found"));
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
    public void onUserClicked(User user) {
        String userType = preferenceManager.getString(Constants.USER_TYPE);
        if (Constants.USER_TYPE_VOLUNTEER.equals(userType)){
            Intent intent =new Intent(getApplicationContext(), DetailsActivity2.class);
            intent.putExtra(Constants.KEY_USER,user);
            startActivity(intent);
        } else if (Constants.USER_TYPE_ORGANISATION.equals(userType)) {
            Intent intent =new Intent(getApplicationContext(), DetailsActivity.class);
            intent.putExtra(Constants.KEY_USER,user);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}