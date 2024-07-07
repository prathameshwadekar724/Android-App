package com.example.android.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.databinding.ItemListBinding;
import com.example.android.listeners.UserListeners;
import com.example.android.models.User;
import com.example.android.utilities.Constants;
import com.example.android.utilities.PreferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {
    private final List<User> users;
    private  final UserListeners userListeners;
    private final String userType;
    private final String userId;

    public UserAdapter(List<User> users, UserListeners userListeners,PreferenceManager preferenceManager) {
        this.users = users;
        this.userListeners = (UserListeners) userListeners;
        this.userType = preferenceManager.getString(Constants.USER_TYPE);
        this.userId = preferenceManager.getString(Constants.KEY_USER_ID);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListBinding binding = ItemListBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.getUserData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ItemListBinding binding;

        public MyViewHolder(@NonNull ItemListBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
        void getUserData(User user){
            if (Constants.USER_TYPE_VOLUNTEER.equals(userType) || Constants.USER_TYPE_ADMIN.equals(userType)) {

                binding.textName.setText(user.name);
                binding.textEmail.setText(user.email);

            } else if (Constants.USER_TYPE_ORGANISATION.equals(userType)) {

                binding.textName.setText(user.orgName);
                binding.textEmail.setText(user.orgEmail);
            }
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userListeners.onUserClicked(user);
                }
            });
        }
    }
}
