package com.example.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.databinding.ItemLeaderboardBinding;
import com.example.android.models.Message;
import com.example.android.models.Rating;
import com.example.android.utilities.Constants;
import com.example.android.utilities.PreferenceManager;

import java.util.List;

public class LeaderBoardAdapter extends RecyclerView.Adapter<LeaderBoardAdapter.MyViewHolder> {
    private final List<Rating> ratingList;
    private final Context context;

    public LeaderBoardAdapter(List<Rating> ratingList, Context context) {
        this.ratingList = ratingList;
        this.context = context;
    }

    @NonNull
    @Override
    public LeaderBoardAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLeaderboardBinding binding = ItemLeaderboardBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderBoardAdapter.MyViewHolder holder, int position) {
        holder.getData(ratingList.get(position));
    }

    @Override
    public int getItemCount() {
        return ratingList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ItemLeaderboardBinding binding;
        public MyViewHolder(@NonNull ItemLeaderboardBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
        void getData(Rating rating){
            binding.vName.setText(rating.name);
            binding.ratingBar.setRating(Float.parseFloat(String.valueOf(rating.average)));
            binding.vCount.setText("("+String.valueOf(rating.count)+")");
        }
    }
}
