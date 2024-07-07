package com.example.android.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.android.R;
import com.example.android.adapters.PostAdapter;
import com.example.android.adapters.UserAdapter;
import com.example.android.adapters.UserAdapter2;
import com.example.android.databinding.ActivityAdminHomeBinding;
import com.example.android.listeners.UserListeners;
import com.example.android.models.Post;
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

public class AdminHome extends AppCompatActivity implements UserListeners {
    private ActivityAdminHomeBinding binding;
    private PreferenceManager preferenceManager;
    private List<Post> postList;
    private List<User> userList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        loading(false);
        binding.postList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPost();
            }
        });
        binding.volunteerList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUser();
            }
        });
        binding.organisationList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOrgUser();
            }
        });
    }
    private void getUser(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Constants.KEY_COLLECTION);
        loading(true);
        userList = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                if (snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        User user1 = dataSnapshot.getValue(User.class);
                        userList.add(user1);
                    }
                    if (userList.size()>0){
                        UserAdapter userAdapter = new UserAdapter(userList,AdminHome.this,preferenceManager);
                        binding.recyclerView.setAdapter(userAdapter);
                        binding.recyclerView.setVisibility(View.VISIBLE);
                    }

                }
                loading(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loading(false);
            }
        });
    }
    private void getOrgUser(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Constants.KEY_ORGANIZATION);
        loading(true);
        userList = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                if (snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        User user1 = dataSnapshot.getValue(User.class);
                        userList.add(user1);
                    }
                    if (userList.size()>0){
                        UserAdapter2 userAdapter = new UserAdapter2(userList,AdminHome.this,preferenceManager);
                        binding.recyclerView.setAdapter(userAdapter);
                        binding.recyclerView.setVisibility(View.VISIBLE);
                    }

                }
                loading(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loading(false);
            }
        });
    }
    private void getPost(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Constants.KEY_POST_COLLECTION).child(Constants.KEY_POST_UPLOAD);
        loading(true);
        postList = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Post post = dataSnapshot.getValue(Post.class);
                        postList.add(post);
                    }
                    if (postList.size()>0){
                        PostAdapter postAdapter = new PostAdapter(postList,AdminHome.this,preferenceManager);
                        binding.recyclerView.setAdapter(postAdapter);
                        binding.recyclerView.setVisibility(View.VISIBLE);
                    }
                }
                loading(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loading(false);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.logout){
            preferenceManager.clear();
            startActivity(new Intent(getApplicationContext(), StartActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    public void onUserClicked(User user) {
        String userType = preferenceManager.getString(Constants.USER_TYPE);
        if (Constants.USER_TYPE_ADMIN.equals(userType)){
            Intent intent =new Intent(getApplicationContext(), DetailsActivity.class);
            intent.putExtra(Constants.KEY_USER,user);
            startActivity(intent);
        }
    }
}