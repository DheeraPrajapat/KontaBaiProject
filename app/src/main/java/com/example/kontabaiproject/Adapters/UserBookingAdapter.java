package com.example.kontabaiproject.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kontabaiproject.Classes.User;
import com.example.kontabaiproject.R;

import java.util.ArrayList;

public class UserBookingAdapter extends RecyclerView.Adapter<UserBookingAdapter.BookingViewHolder>
{
    Context mContext;
    ArrayList<User> arrayList;

    public UserBookingAdapter(Context mContext, ArrayList<User> arrayList) {
        this.mContext = mContext;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.user_taxi_booking,null,false);
        return new BookingViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        User user=arrayList.get(position);
        String rideStatus= user.getStatus();
        String location= user.getPickuplocation();
        switch (rideStatus) {
            case "pending":
                holder.status.setText("pending");
                holder.status.setTextColor(Color.RED);
                break;
            case "rejected":
                holder.status.setText("rejected");
                holder.status.setTextColor(Color.RED);
                break;
            case "accepted":
                holder.status.setText("accepted");
                holder.status.setTextColor(Color.GREEN);
                break;
        }
        holder.pickupLocation.setText(location);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder
    {
        TextView pickupLocation,status;
        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            pickupLocation=itemView.findViewById(R.id.pickupLocation);
            status=itemView.findViewById(R.id.rideStatus);
        }
    }
}
