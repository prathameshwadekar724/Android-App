package com.example.android.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.android.R;
import com.example.android.databinding.ActivitySignUpBinding;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {
    private ActivitySignUpBinding binding;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private PreferenceManager preferenceManager;
    private DatePickerDialog picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager =new PreferenceManager(getApplicationContext());
        String[] options = {"12th", "Diploma", "UG", "PG", "Others"};
        String[] options1 = {"Healthcare", "Education", "Animal Welfare", "Environment and Conservation","Social Service","Others"};
        String[] options2 ={"Male","Female"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.drop_down_item,options2);
        binding.gender.setAdapter(adapter);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this,R.layout.drop_down_item,options);
        binding.occupationText.setAdapter(adapter1);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this,R.layout.drop_down_item,options1);
        binding.interestText.setAdapter(adapter2);
        binding.gender.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                showToast("item: " +item);
            }
        });
        binding.occupationText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                showToast("item: " +item);
            }
        });
        binding.interestText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                showToast("item: " +item);
            }
        });

        binding.date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar= Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);



                picker=new DatePickerDialog(SignUp.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar birthDate = Calendar.getInstance();
                        birthDate.set(year, month, dayOfMonth);
                        Calendar currentDate = Calendar.getInstance();
                        int age = currentDate.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);
                        if (currentDate.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
                            age--;
                        }
                        binding.date.setText(String.valueOf(age+ " Years"));
                    }
                },year,month,day);
                picker.show();
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
    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
                    hashMap.put(Constants.KEY_NAME,binding.textName.getText().toString());
                    hashMap.put(Constants.KEY_CONTACT,binding.contact.getText().toString());
                    hashMap.put(Constants.KEY_ADDRESS,binding.textAddress.getText().toString());
                    hashMap.put(Constants.KEY_GENDER,binding.gender.getText().toString());
                    hashMap.put(Constants.KEY_EMAIL,binding.textEmail.getText().toString());
                    hashMap.put(Constants.KEY_PASSWORD,binding.textPasword.getText().toString());
                    hashMap.put(Constants.KEY_CITY,binding.textCity.getText().toString());
                    hashMap.put(Constants.KEY_STATE,binding.textState.getText().toString());
                    hashMap.put(Constants.KEY_CODE,binding.textCode.getText().toString());
                    hashMap.put(Constants.KEY_DOB,binding.date.getText().toString());
                    hashMap.put(Constants.KEY_OCCUPATION,binding.occupationText.getText().toString());
                    hashMap.put(Constants.KEY_INTEREST,binding.interestText.getText().toString());
                    reference.child(Constants.KEY_COLLECTION).child(user.getUid()).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            loading(false);
                            preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                            preferenceManager.putString(Constants.KEY_USER_ID,user.getUid());
                            preferenceManager.putString(Constants.KEY_EMAIL,binding.textEmail.getText().toString());
                            Intent intent = new Intent(SignUp.this, SignIn.class);
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