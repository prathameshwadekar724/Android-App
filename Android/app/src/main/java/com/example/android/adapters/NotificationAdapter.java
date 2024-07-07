package com.example.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.databinding.NotificationItemBinding;
import com.example.android.models.Message;
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

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    private final List<Message> messages;
    private final Context context;
    private final String userId;
    private final String userType;
    private final PreferenceManager preferenceManager;
    private final DatabaseReference databaseReference;


    public NotificationAdapter(List<Message> messages, Context context, PreferenceManager preferenceManager) {
        this.messages = messages;
        this.context = context;
        this.userId = preferenceManager.getString(Constants.KEY_ORG_ID);
        this.userType = preferenceManager.getString(Constants.USER_TYPE);
        this.preferenceManager = preferenceManager;
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public NotificationAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        NotificationItemBinding binding = NotificationItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.MyViewHolder holder, int position) {
        if (Constants.USER_TYPE_VOLUNTEER.equals(userType)){
            holder.getReceivedMessage(messages.get(position));
        } else if (Constants.USER_TYPE_ORGANISATION.equals(userType)) {
            holder.getSentMessage(messages.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        NotificationItemBinding binding;
        public MyViewHolder(@NonNull NotificationItemBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
        void getSentMessage(Message message){
            binding.messageText.setText(message.message);
            checkApprovalStatus(message);
            binding.textApprove.setText("Approve");
            binding.textApprove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    approveMessage(message);
                }
            });
        }

        void getReceivedMessage(Message message){
            binding.messageText.setText(message.message);
            binding.textApprove.setVisibility(View.GONE);
        }
        void checkApprovalStatus(Message message){
            databaseReference.child(Constants.KEY_POST_COLLECTION).child(Constants.KEY_POST_APPROVE).child(userId).child(message.postId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Boolean isApproved = snapshot.child(Constants.KEY_IS_USER_APPROVED).getValue(Boolean.class);
                            if (isApproved!=null && isApproved){
                                binding.textApprove.setText("Approved");
                                binding.textApprove.setClickable(false);
                            }else{
                                binding.textApprove.setText("Approve");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
        void approveMessage(Message message){
            DatabaseReference reference = databaseReference.child(Constants.KEY_POST_COLLECTION).child(Constants.KEY_POST_APPROVE);
            HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put(Constants.KEY_POST_ID, message.postId);
            hashMap.put(Constants.KEY_NAME, message.name);
            hashMap.put(Constants.KEY_CITY, message.city);
            hashMap.put(Constants.KEY_USER_ID, message.userId);
            hashMap.put(Constants.KEY_MESSAGE, "Dear " + message.name + ", your application is approved for the " + message.postName + " event by " + message.orgName);
            hashMap.put(Constants.KEY_IS_USER_APPROVED, true);
            reference.child(userId).child(message.postId).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    binding.textApprove.setText("Approved");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }
}
