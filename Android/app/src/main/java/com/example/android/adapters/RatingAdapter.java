package com.example.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.databinding.ItemViewBinding;
import com.example.android.databinding.NotificationItemBinding;
import com.example.android.models.Message;
import com.example.android.models.Rating;
import com.example.android.utilities.Constants;
import com.example.android.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.MyViewHolder> {
    private final List<Message> messages;
    private final Context context;
    private final String userId;
    private final PreferenceManager preferenceManager;
    private final DatabaseReference databaseReference;


    public RatingAdapter(List<Message> messages, Context context, PreferenceManager preferenceManager) {
        this.messages = messages;
        this.context = context;
        this.userId = preferenceManager.getString(Constants.KEY_ORG_ID);
        this.preferenceManager = preferenceManager;
        this.databaseReference = FirebaseDatabase.getInstance().getReference();

    }

    @NonNull
    @Override
    public RatingAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemViewBinding binding = ItemViewBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingAdapter.MyViewHolder holder, int position) {
        holder.getUsers(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ItemViewBinding binding;
        public MyViewHolder(@NonNull ItemViewBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
        void getUsers(Message message){
            binding.textName.setText(message.name);
            checkRatingSubmitted(message);
            binding.submitButton.setText("Submit");

            binding.submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    float rating = binding.ratingBar.getRating();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Constants.KEY_POST_COLLECTION).child(Constants.KEY_POST_RATING);
                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put(Constants.KEY_NAME,message.name);
                    hashMap.put(Constants.KEY_RATINGS,rating);
                    hashMap.put(Constants.KEY_USER_ID,message.userId);
                    hashMap.put(Constants.KEY_ORG_ID,message.orgId);
                    hashMap.put(Constants.KEY_RATING_SUBMIT,true);
                    reference.child(message.userId).child(userId).child(message.postId).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            updateRating(message,rating);
                            binding.ratingBar.setRating(rating);
                            binding.submitButton.setText("Submitted");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
            });
        }
        void checkRatingSubmitted(Message message){
            DatabaseReference reference = databaseReference.child(Constants.KEY_POST_COLLECTION).child(Constants.KEY_POST_RATING).child(message.userId).child(userId).child(message.postId);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Float rating = snapshot.child(Constants.KEY_RATINGS).getValue(Float.class);
                        Boolean isSubmitted = snapshot.child(Constants.KEY_RATING_SUBMIT).getValue(Boolean.class);

                        if (isSubmitted != null && isSubmitted) {
                            if (rating != null) {
                                binding.ratingBar.setRating(rating);
                                binding.ratingBar.setIsIndicator(true);
                                binding.submitButton.setText("Submitted");
                                binding.submitButton.setClickable(false);
                            }
                        } else {
                            binding.submitButton.setText("Submit");
                        }
                    } else {
                        binding.submitButton.setText("Submit");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        void updateRating(Message message, float newRating) {
            DatabaseReference statsReference = databaseReference.child(Constants.KEY_POST_COLLECTION).child(Constants.KEY_AVERAGE).child(message.userId);

            statsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    long count = snapshot.child(Constants.KEY_COUNT).getValue(Long.class) != null ? snapshot.child(Constants.KEY_COUNT).getValue(Long.class) : 0;
                    float total = snapshot.child(Constants.KEY_TOTAL).getValue(Float.class) != null ? snapshot.child(Constants.KEY_TOTAL).getValue(Float.class) : 0;

                    count++;
                    total += newRating;
                    float average = total / count;

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put(Constants.KEY_NAME,message.name);
                    hashMap.put(Constants.KEY_COUNT, count);
                    hashMap.put(Constants.KEY_TOTAL, total);
                    hashMap.put(Constants.KEY_AVERAGE, average);

                    statsReference.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

}
