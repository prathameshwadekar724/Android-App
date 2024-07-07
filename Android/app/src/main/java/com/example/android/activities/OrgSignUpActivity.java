package com.example.android.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.android.R;
import com.example.android.databinding.ActivityOrgSignUpBinding;
import com.example.android.utilities.Constants;
import com.example.android.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OrgSignUpActivity extends AppCompatActivity {

    private ActivityOrgSignUpBinding binding;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrgSignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        String[] options = {"Healthcare", "Education", "Animal Welfare", "Environment and Conservation","Social Service","Others"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this,R.layout.drop_down_item,options);
        binding.type.setAdapter(adapter1);
        binding.type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                showToast("item: "+item);
            }
        });
        setListeners();

    }
    private void setListeners(){
        binding.signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),SignIn.class));
            }
        });
        binding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSignUpValid()){
                    signUp();
                }
            }
        });
    }
    private void signUp(){
        loading(true);
        String email = binding.textEmail.getText().toString();
        String password = binding.textPasword.getText().toString();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser user = auth.getCurrentUser();
                    user.sendEmailVerification();
                    showToast("User Registered Successfully");

                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put(Constants.KEY_ORG_NAME,binding.textName.getText().toString());
                    hashMap.put(Constants.KEY_ORG_CONTACT,binding.contact.getText().toString());
                    hashMap.put(Constants.KEY_ORG_ADDRESS,binding.textAddress.getText().toString());
                    hashMap.put(Constants.KEY_TYPE,binding.type.getText().toString());
                    hashMap.put(Constants.KEY_ORG_EMAIL,binding.textEmail.getText().toString());
                    hashMap.put(Constants.KEY_ORG_PASSWORD,binding.textPasword.getText().toString());
                    reference.child(Constants.KEY_ORGANIZATION).child(user.getUid()).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            loading(false);
                            preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                            preferenceManager.putString(Constants.KEY_ORG_ID,user.getUid());
                            preferenceManager.putString(Constants.KEY_ORG_EMAIL,binding.textEmail.getText().toString());
                            Intent intent = new Intent(OrgSignUpActivity.this, SignIn.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loading(false);
                            showToast(e.getMessage());
                        }
                    });
                }else{
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        showToast(e.getMessage());
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        showToast(e.getMessage());
                    }catch (FirebaseAuthUserCollisionException e){
                        showToast(e.getMessage());
                    }catch (Exception e){
                        showToast(e.getMessage());
                    }
                }
            }
        });
    }
    private boolean isSignUpValid(){
        String mobile = "[6-9][0-9]{9}";
        Matcher matcher;
        Pattern pattern = Pattern.compile(mobile);
        matcher = pattern.matcher(binding.contact.getText().toString());
        if (binding.textName.getText().toString().trim().isEmpty()){
            showToast("Enter Your Name");
            return false;
        } else if (binding.contact.getText().toString().trim().isEmpty()) {
            showToast("Enter Your Contact");
            return false;
        } else if (binding.contact.length()!=10) {
            showToast("Mobile number should be 10 digits");
            binding.contact.requestFocus();
            return false;
        } else if (!matcher.find()) {
            showToast("Mobile number is not valid");
            return false;
        } else if (binding.textAddress.getText().toString().trim().isEmpty()) {
            showToast("Enter Your Address");
            return false;
        } else if (binding.textEmail.getText().toString().trim().isEmpty()) {
            showToast("Enter Your Email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.textEmail.getText().toString()).matches()) {
            showToast("Enter Valid Email");
            return false;
        } else if (binding.textPasword.getText().toString().trim().isEmpty()) {
            showToast("Enter Your Password");
            return false;
        } else if (binding.textPasword.length()<6) {
            showToast("Password length should be at least 6 ");
            return false;
        } else if (!binding.textPasword.getText().toString().equals(binding.textCp.getText().toString())) {
            showToast("Password & confirm Password must be same");
            return false;
        }else {
            return true;
        }
    }
    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    private void loading(boolean isLoading){
        if (isLoading){
            binding.signUp.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.signUp.setVisibility(View.VISIBLE);
        }
    }

}