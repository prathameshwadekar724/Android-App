package com.example.android.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.android.databinding.ActivitySignInBinding;
import com.example.android.utilities.Constants;
import com.example.android.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SignIn extends AppCompatActivity {
    private ActivitySignInBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());

        setListeners();
    }
    private void setListeners(){
        String userType = preferenceManager.getString(Constants.USER_TYPE);
        if (Constants.USER_TYPE_ADMIN.equals(userType)){
            binding.createAccount.setVisibility(View.GONE);
        }else{
            binding.createAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Constants.USER_TYPE_VOLUNTEER.equals(userType)){
                        startActivity(new Intent(getApplicationContext(), SignUp.class));
                    } else if (Constants.USER_TYPE_ORGANISATION.equals(userType)) {
                        startActivity(new Intent(getApplicationContext(),OrgSignUpActivity.class));
                    }
                }
            });
        }

        binding.signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidDetails()) {
                    if (Constants.USER_TYPE_VOLUNTEER.equals(userType)){
                        volunteerSignIn();
                    } else if (Constants.USER_TYPE_ORGANISATION.equals(userType)) {
                        orgSignIn();
                    } else if (Constants.USER_TYPE_ADMIN.equals(userType)) {
                        adminSignIn();
                    }
                }
            }
        });

    }
    private void adminSignIn(){
        String email = binding.emailId.getText().toString();
        String password = binding.password.getText().toString();
        if (email.equals("admin@gmail.com") && password.equals("123456")){
            preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
            Intent intent = new Intent(getApplicationContext(), AdminHome.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
    private void volunteerSignIn() {
        loading(true);
        String email = binding.emailId.getText().toString();
        String password = binding.password.getText().toString();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        if (user.isEmailVerified()) {
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Constants.KEY_COLLECTION).child(user.getUid());
                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        showToast("Login Successful");
                                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                                        preferenceManager.putString(Constants.KEY_USER_ID, user.getUid());
                                        preferenceManager.putString(Constants.KEY_EMAIL,email);
                                        preferenceManager.putString(Constants.KEY_NAME,snapshot.child(Constants.KEY_NAME).getValue(String.class));
                                        preferenceManager.putString(Constants.KEY_CONTACT,snapshot.child(Constants.KEY_CONTACT).getValue(String.class));
                                        preferenceManager.putString(Constants.KEY_ADDRESS,snapshot.child(Constants.KEY_ADDRESS).getValue(String.class));
                                        preferenceManager.putString(Constants.KEY_GENDER,snapshot.child(Constants.KEY_GENDER).getValue(String.class));
                                        preferenceManager.putString(Constants.KEY_CITY,snapshot.child(Constants.KEY_CITY).getValue(String.class));
                                        preferenceManager.putString(Constants.KEY_STATE,snapshot.child(Constants.KEY_STATE).getValue(String.class));
                                        preferenceManager.putString(Constants.KEY_CODE,snapshot.child(Constants.KEY_CODE).getValue(String.class));
                                        preferenceManager.putString(Constants.KEY_DOB,snapshot.child(Constants.KEY_DOB).getValue(String.class));
                                        preferenceManager.putString(Constants.KEY_OCCUPATION,snapshot.child(Constants.KEY_OCCUPATION).getValue(String.class));
                                        preferenceManager.putString(Constants.KEY_INTEREST,snapshot.child(Constants.KEY_INTEREST).getValue(String.class));
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }else{
                                        showToast("Invalid Credentials");
                                        loading(false);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        } else {
                            auth.signOut();
                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> emailTask) {
                                    if (emailTask.isSuccessful()) {
                                        showEmailVerificationDialog();
                                        loading(false);
                                    } else {
                                        showToast("Failed to send verification email.");
                                        loading(false);
                                    }
                                }
                            });
                            auth.signOut();
                        }
                    } else {
                        showToast("Unable to get user.");
                        loading(false);
                    }
                } else {
                    loading(false);
                    showToast("Unable to sign in. Check your email and password.");
                }
            }
        });
    }
    private void orgSignIn(){
        loading(true);
        String email = binding.emailId.getText().toString();
        String password = binding.password.getText().toString();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        if (user.isEmailVerified()) {
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Constants.KEY_ORGANIZATION).child(user.getUid());
                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        showToast("Login Successful");
                                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                                        preferenceManager.putString(Constants.KEY_ORG_ID, user.getUid());
                                        preferenceManager.putString(Constants.KEY_ORG_EMAIL,email);
                                        preferenceManager.putString(Constants.KEY_ORG_NAME,snapshot.child(Constants.KEY_ORG_NAME).getValue(String.class));
                                        preferenceManager.putString(Constants.KEY_ORG_CONTACT,snapshot.child(Constants.KEY_ORG_CONTACT).getValue(String.class));
                                        preferenceManager.putString(Constants.KEY_ORG_ADDRESS,snapshot.child(Constants.KEY_ORG_ADDRESS).getValue(String.class));
                                        preferenceManager.putString(Constants.KEY_TYPE,snapshot.child(Constants.KEY_TYPE).getValue(String.class));
                                        Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }else {
                                        showToast("Invalid Credentials");
                                        loading(false);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        } else {
                            auth.signOut();
                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> emailTask) {
                                    if (emailTask.isSuccessful()) {
                                        showEmailVerificationDialog();
                                        loading(false);
                                    } else {
                                        showToast("Failed to send verification email.");
                                        loading(false);
                                    }
                                }
                            });
                            auth.signOut();
                        }
                    } else {
                        showToast("Unable to get user.");
                        loading(false);
                    }
                } else {
                    loading(false);
                    showToast("Unable to sign in. Check your email and password.");
                }
            }
        });
    }

    private void showEmailVerificationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignIn.this);
        builder.setTitle("Email not verified");
        builder.setMessage("Please verify your email. A verification email has been sent.");
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private boolean isValidDetails(){
        if (binding.emailId.getText().toString().trim().isEmpty()){
            showToast("Enter Your Email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.emailId.getText().toString()).matches()) {
            showToast("Enter Valid Email");
            return false;
        } else if (binding.password.getText().toString().trim().isEmpty()) {
            showToast("Enter Password");
            return false;
        }else {
            return true;
        }
    }
    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    private void loading(Boolean isLoading){
        if (isLoading){
            binding.signIn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.signIn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}