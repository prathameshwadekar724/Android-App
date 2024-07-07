package com.example.android.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.R;
import com.example.android.adapters.PostAdapter;
import com.example.android.adapters.UserAdapter;
import com.example.android.adapters.UserAdapter2;
import com.example.android.databinding.ActivityListBinding;
import com.example.android.listeners.UserListeners;
import com.example.android.models.User;
import com.example.android.utilities.Constants;
import com.example.android.utilities.PreferenceManager;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListActivity extends AppCompatActivity implements UserListeners {
    private ActivityListBinding binding;
    private PreferenceManager preferenceManager;
    private List<User> userList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationIcon(R.drawable.round_back);
        String userType = preferenceManager.getString(Constants.USER_TYPE);
        if (Constants.USER_TYPE_VOLUNTEER.equals(userType)){
            getUsers();
        }else if (Constants.USER_TYPE_ORGANISATION.equals(userType)){
            getOrgUser();
        }
        setListener();

    }
    private void setListener(){
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());

    }
    private void getUsers(){
        String userName = preferenceManager.getString(Constants.KEY_NAME);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Constants.KEY_COLLECTION);
        loading(true);
        userList = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                if (snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        String name = dataSnapshot.child(Constants.KEY_NAME).getValue(String.class);
                        if (userName.equals(name)){
                            continue;
                        }
                        User user1 = dataSnapshot.getValue(User.class);
                        userList.add(user1);
                    }
                    if (userList.size()>0){
                        UserAdapter userAdapter = new UserAdapter(userList,ListActivity.this,preferenceManager);
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
    private void getOrgUser(){
        String uName = preferenceManager.getString(Constants.KEY_ORG_NAME);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Constants.KEY_ORGANIZATION);
        loading(true);
        userList = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                if (snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        String name = dataSnapshot.child(Constants.KEY_ORG_NAME).getValue(String.class);
                        preferenceManager.putString(Constants.KEY_ORG_NAME,name);
                        if (uName.equals(name)){
                            continue;
                        }
                        User user1 = dataSnapshot.getValue(User.class);
                        userList.add(user1);
                    }
                    if (userList.size()>0){
                        UserAdapter userAdapter = new UserAdapter(userList,ListActivity.this,preferenceManager);
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
            Intent intent =new Intent(getApplicationContext(), DetailsActivity.class);
            intent.putExtra(Constants.KEY_USER,user);
            startActivity(intent);
        } else if (Constants.USER_TYPE_ORGANISATION.equals(userType)) {
            Intent intent =new Intent(getApplicationContext(), DetailsActivity2.class);
            intent.putExtra(Constants.KEY_USER,user);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}