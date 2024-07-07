package com.example.android.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.R;
import com.example.android.activities.ApplyActivity;
import com.example.android.activities.ViewActivity;
import com.example.android.databinding.ItemPostBinding;
import com.example.android.models.Post;
import com.example.android.utilities.Constants;
import com.example.android.utilities.PreferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {
    private final List<Post> list;
    private final Context context;
    private final String userId;
    private final String userType;

    public PostAdapter(List<Post> list, Context context,PreferenceManager preferenceManager) {
        this.list = list;
        this.context = context;
        this.userId = preferenceManager.getString(Constants.KEY_USER_ID);
        this.userType = preferenceManager.getString(Constants.USER_TYPE);
    }

    @NonNull
    @Override
    public PostAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPostBinding binding = ItemPostBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.MyViewHolder holder, int position) {
        if (Constants.USER_TYPE_VOLUNTEER.equals(userType)){
            holder.getData(list.get(position));
        } else if (Constants.USER_TYPE_ORGANISATION.equals(userType)) {
            holder.getOrgPost(list.get(position));
        } else if (Constants.USER_TYPE_ADMIN.equals(userType)) {
            holder.getData(list.get(position));
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ItemPostBinding binding;
        public MyViewHolder(@NonNull ItemPostBinding itemView) {
            super(itemView.getRoot());
            binding=itemView;
        }
        void getData(Post post){
            binding.applyButton.setText("Apply");
            binding.eventName.setText(post.postName);
            binding.viewPost.setImageBitmap(getUserImage(post.postImage));
            binding.orgName.setText(post.userName);
            binding.eventDetails.setMovementMethod(new ScrollingMovementMethod());
            binding.eventDetails.setText(post.postDetails+"."+"\nLocation: "+post.location+"."+"\nDate: "+post.date+"."+"\nTime: "+post.time+".");
            getLikes(post);
            binding.buttonLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleLikeStatus(post);
                }
            });
            binding.applyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ApplyActivity.class);
                    intent.putExtra("postId",post.postId);
                    intent.putExtra("postName",post.postName);
                    intent.putExtra("userName",post.userName);
                    intent.putExtra("orgId",post.orgId);
                    context.startActivity(intent);
                }
            });

        }
        private void getLikes(Post post) {
            DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference().child(Constants.KEY_POST_COLLECTION).child(Constants.KEY_LIKES_COLLECTION).child(post.postId);
            likesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    long likesCount = snapshot.getChildrenCount();
                    if (Constants.USER_TYPE_VOLUNTEER.equals(userType)){
                        binding.likesCount.setText(likesCount + " likes");

                        if (snapshot.hasChild(userId)) {
                            binding.buttonLike.setImageResource(R.drawable.likedb);
                        } else {
                            binding.buttonLike.setImageResource(R.drawable.likeb);
                        }
                    } else if (Constants.USER_TYPE_ORGANISATION.equals(userType)) {
                        binding.likesCount.setText(likesCount + " likes");
                    } else if (Constants.USER_TYPE_ADMIN.equals(userType)) {
                        binding.applyButton.setVisibility(View.GONE);
                        binding.buttonLike.setImageResource(R.drawable.likedb);
                        binding.buttonLike.setClickable(false);
                        binding.likesCount.setText(likesCount + " likes");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Failed to retrieve likes count: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        private void toggleLikeStatus(Post post) {
            DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference().child(Constants.KEY_POST_COLLECTION).child(Constants.KEY_LIKES_COLLECTION).child(post.postId);
            likesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild(userId)) {
                        likesRef.child(userId).removeValue();
                    } else {
                        likesRef.child(userId).setValue(true);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Failed to toggle like: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        void getOrgPost(Post post){
            binding.applyButton.setText("View Participants");
            binding.eventName.setText(post.postName);
            binding.orgName.setText(post.userName);
            binding.viewPost.setImageBitmap(getUserImage(post.postImage));
            binding.buttonLike.setImageResource(R.drawable.likedb);
            binding.eventDetails.setText(post.postDetails+"."+"\nLocation: "+post.location+"."+"\nDate: "+post.date+"."+"\nTime: "+post.time+".");
            getLikes(post);
            binding.applyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ViewActivity.class);
                    intent.putExtra("postId",post.postId);
                    context.startActivity(intent);
                }
            });
        }
    }
    private Bitmap getUserImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
}
