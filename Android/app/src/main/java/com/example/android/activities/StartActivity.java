package com.example.android.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.android.R;
import com.example.android.databinding.ActivityStartBinding;
import com.example.android.utilities.Constants;
import com.example.android.utilities.PreferenceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {

    private ActivityStartBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());

        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            String userType = preferenceManager.getString(Constants.USER_TYPE);
            if (Constants.USER_TYPE_VOLUNTEER.equals(userType)) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            } else if (Constants.USER_TYPE_ORGANISATION.equals(userType)) {
                Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                startActivity(intent);
                finish();
            } else if (Constants.USER_TYPE_ADMIN.equals(userType)) {
                Intent intent = new Intent(getApplicationContext(), AdminHome.class);
                startActivity(intent);
                finish();
            }
        }
        String[] options = {"Admin", "Volunteers", "Organisation"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.drop_down_item, options);
        binding.textComplete.setAdapter(adapter);

        binding.textComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                if (item == options[1]) {
                    preferenceManager.putString(Constants.USER_TYPE, Constants.USER_TYPE_VOLUNTEER);
                    Intent intent = new Intent(getApplicationContext(), SignIn.class);
                    startActivity(intent);
                } else if (item == options[2]) {
                    preferenceManager.putString(Constants.USER_TYPE, Constants.USER_TYPE_ORGANISATION);
                    Intent intent = new Intent(getApplicationContext(), SignIn.class);
                    startActivity(intent);
                } else if (item == options[0]) {
                    preferenceManager.putString(Constants.USER_TYPE,Constants.USER_TYPE_ADMIN);
                    Intent intent = new Intent(getApplicationContext(),SignIn.class);
                    startActivity(intent);
                }

            }
        });

    }
}