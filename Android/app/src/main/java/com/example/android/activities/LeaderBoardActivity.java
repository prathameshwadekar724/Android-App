package com.example.android.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.android.R;
import com.example.android.adapters.LeaderBoardAdapter;
import com.example.android.databinding.ActivityLeaderBoardBinding;
import com.example.android.models.Message;
import com.example.android.models.Rating;
import com.example.android.utilities.Constants;
import com.example.android.utilities.PreferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LeaderBoardActivity extends AppCompatActivity {
    private ActivityLeaderBoardBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLeaderBoardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationIcon(R.drawable.round_back);
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
        getData();
    }

    private void getData(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Constants.KEY_POST_COLLECTION).child(Constants.KEY_AVERAGE);
        loading(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    List<Rating> ratingList= new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Rating rating = dataSnapshot.getValue(Rating.class);
                        ratingList.add(rating);
                    }
                    if (ratingList.size()>0){
                        LeaderBoardAdapter leaderBoardAdapter = new LeaderBoardAdapter(ratingList,LeaderBoardActivity.this);
                        binding.boardRecycle.setAdapter(leaderBoardAdapter);
                        binding.boardRecycle.setVisibility(View.VISIBLE);
                    }
                    loading(false);
                }else{
                    loading(false);
                    Toast.makeText(LeaderBoardActivity.this, "error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loading(false);
            }
        });
    }

    private void loading(boolean isLoading){
        if (isLoading){
            binding.boardProgress.setVisibility(View.VISIBLE);
        }else{
            binding.boardProgress.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}