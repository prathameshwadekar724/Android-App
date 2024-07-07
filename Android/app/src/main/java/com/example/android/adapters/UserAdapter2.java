package com.example.android.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.databinding.ItemList2Binding;
import com.example.android.listeners.UserListeners;
import com.example.android.models.User;
import com.example.android.utilities.Constants;
import com.example.android.utilities.PreferenceManager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class UserAdapter2 extends RecyclerView.Adapter<UserAdapter2.MyViewHolder> {
    private final List<User> users;
    private  final UserListeners userListeners;
    private final String userType;
    private final String userId;

    public UserAdapter2(List<User> users, UserListeners userListeners, PreferenceManager preferenceManager) {
        this.users = users;
        this.userListeners = userListeners;
        this.userType = preferenceManager.getString(Constants.USER_TYPE);
        this.userId = preferenceManager.getString(Constants.KEY_ORG_ID);
    }

    @NonNull
    @Override
    public UserAdapter2.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemList2Binding binding = ItemList2Binding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter2.MyViewHolder holder, int position) {
        holder.getUserData(users.get(position));

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemList2Binding binding;
        public MyViewHolder(@NonNull ItemList2Binding itemView) {
            super(itemView.getRoot());
            binding=itemView;
        }
        void getUserData(User user){
            if (Constants.USER_TYPE_VOLUNTEER.equals(userType) || Constants.USER_TYPE_ADMIN.equals(userType)) {
                binding.textOrgName.setText(user.orgName);
                binding.textOrgEmail.setText(user.orgEmail);
            } else if (Constants.USER_TYPE_ORGANISATION.equals(userType)) {
                binding.textOrgName.setText(user.name);
                binding.textOrgEmail.setText(user.email);
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
