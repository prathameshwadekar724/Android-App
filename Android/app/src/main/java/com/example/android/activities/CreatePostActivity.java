package com.example.android.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.CpuUsageInfo;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.example.android.R;
import com.example.android.databinding.ActivityCreatePostBinding;
import com.example.android.utilities.Constants;
import com.example.android.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class CreatePostActivity extends AppCompatActivity {
    private ActivityCreatePostBinding binding;
    private PreferenceManager preferenceManager;
    private String encodedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreatePostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationIcon(R.drawable.round_back);
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
        setListener();
    }
    private void setListener(){
        binding.addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);
            }
        });
        binding.buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFillDetails()){
                    UploadPost();
                }
            }
        });
        binding.viewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (encodedImage!=null){
                    binding.viewPost.setImageResource(0);
                    binding.view1.setVisibility(View.INVISIBLE);
                    binding.view2.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
    private String encodeImage(Bitmap bitmap){
        int previewWidth = bitmap.getWidth();
        int previewHeight = bitmap.getHeight()*previewWidth/bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap,previewWidth,previewHeight,false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes,Base64.DEFAULT);
    }
    private final ActivityResultLauncher<Intent> pickImage=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if(o.getResultCode() == RESULT_OK){
                        Uri imageUri = o.getData().getData();
                        try{
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.viewPost.setImageBitmap(bitmap);
                            binding.view1.setVisibility(View.VISIBLE);
                            binding.view2.setVisibility(View.VISIBLE);
                            encodedImage = encodeImage(bitmap);
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
    );
    private void UploadPost(){
        String userId = preferenceManager.getString(Constants.KEY_ORG_ID);
        String name = preferenceManager.getString(Constants.KEY_ORG_NAME);
        loading(true);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        String id = reference.push().getKey();
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put(Constants.KEY_POST_IMAGE,encodedImage);
        hashMap.put(Constants.KEY_POST_NAME,binding.eventName.getText().toString());
        hashMap.put(Constants.KEY_POST_DETAILS,binding.eventDetails.getText().toString());
        hashMap.put(Constants.KEY_POST_LOCATION,binding.eventLocation.getText().toString());
        hashMap.put(Constants.KEY_POST_DATE,binding.eventDate.getText().toString());
        hashMap.put(Constants.KEY_POST_TIME,binding.eventTime.getText().toString());
        hashMap.put(Constants.KEY_POST_UPLOAD_USERNAME,name);
        hashMap.put(Constants.KEY_POST_ID,id);
        hashMap.put(Constants.KEY_ORG_ID,userId);
        reference.child(Constants.KEY_POST_COLLECTION).child(Constants.KEY_POST_UPLOAD).child(id).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                loading(false);
                preferenceManager.putString(Constants.KEY_POST_ID,id);
                preferenceManager.putString(Constants.KEY_POST_NAME,binding.eventName.getText().toString());
                preferenceManager.putString(Constants.KEY_POST_UPLOAD_USERNAME,name);
                showToast("Post Uploaded");
                Intent intent = new Intent(getApplicationContext(),MainActivity2.class);
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
    }
    private boolean isFillDetails(){
        if (encodedImage==null){
            showToast("Select post image");
            return false;
        } else if (binding.eventName.getText().toString().trim().isEmpty()) {
            showToast("Enter event name");
            return false;
        } else if (binding.eventDetails.getText().toString().trim().isEmpty()) {
            showToast("Enter event details");
            return false;
        } else if (binding.eventLocation.getText().toString().trim().isEmpty()) {
            showToast("Enter event location");
            return false;
        } else if (binding.eventDate.getText().toString().trim().isEmpty()) {
            showToast("Enter event date");
            return false;
        } else if (binding.eventTime.getText().toString().trim().isEmpty()) {
            showToast("Enter event time");
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
            binding.buttonUpload.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonUpload.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}