package com.example.myapplication;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class RequestStatusAdapter extends RecyclerView.Adapter<RequestStatusAdapter.ViewHolder> {

    private final List<RequestItem> items;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(RequestItem item);
    }

    public RequestStatusAdapter(List<RequestItem> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView requestIdView, reasonView, statusView;
        public TextView nameView, mobileView, emailView, insuranceView, addressView;
        public Button appealButton;
        public CardView cardView;

        //UI connect
        public ViewHolder(View view) {
            super(view);
            requestIdView = view.findViewById(R.id.textRequestId);
            reasonView = view.findViewById(R.id.textReason);
            statusView = view.findViewById(R.id.textStatus);
            nameView = view.findViewById(R.id.textName);
            mobileView = view.findViewById(R.id.textMobile);
            emailView = view.findViewById(R.id.textEmail);
            insuranceView = view.findViewById(R.id.textInsurance);
            addressView = view.findViewById(R.id.textAddress);
            appealButton = view.findViewById(R.id.appealButton);
            cardView = view.findViewById(R.id.requestCard);
        }

        //data of request
        public void bind(RequestItem item, OnItemClickListener listener) {
            requestIdView.setText("Request ID: " + item.getRequestId());
            reasonView.setText("Reason: " + item.getReason());
            statusView.setText("Status: " + item.getStatus());

            //fields to be changed and which ones are empty in request
            if (item.getName() == null || item.getName().trim().equalsIgnoreCase("") ) {
                nameView.setText("Name: No Change");
            } else {
                nameView.setText("Name: " + item.getName());
            }

            if (item.getMobile() == null  || item.getMobile().trim().equalsIgnoreCase("")) {
                mobileView.setText("Mobile: No Change");
            } else {
                mobileView.setText("Mobile: " + item.getMobile());
            }

            if (item.getEmail() == null || item.getEmail().trim().equalsIgnoreCase("")) {
                emailView.setText("Email: No Change");
            } else {
                emailView.setText("Email: " + item.getEmail());
            }

            if (item.getInsurance() == null  || item.getInsurance().trim().equalsIgnoreCase("")) {
                insuranceView.setText("Insurance Provider: No Change");
            } else {
                insuranceView.setText("Insurance Provider: " + item.getInsurance());
            }

            if (item.getAddress() == null || item.getAddress().trim().equalsIgnoreCase("")) {
                addressView.setText("Address: No Change");
            } else {
                addressView.setText("Address: " + item.getAddress());
            }

            //status colour and appeal button appearance logic(only for rejected)
            switch (item.getStatus()) {
                case "Approved":
                    statusView.setTextColor(Color.parseColor("#4CAF50"));
                    appealButton.setVisibility(View.GONE);
                    break;
                case "Rejected":
                    statusView.setTextColor(Color.parseColor("#F44336"));
                    appealButton.setVisibility(View.VISIBLE);
                    break;
                case "Pending":
                    statusView.setTextColor(Color.parseColor("#FF9800"));
                    appealButton.setVisibility(View.GONE);
                    break;
                case "Appeal Rejected":
                    statusView.setTextColor(Color.parseColor("#000000"));
                    appealButton.setVisibility(View.GONE);
                    break;
                case "Appealed":
                    statusView.setTextColor(Color.parseColor("#9C27B0"));
                    appealButton.setVisibility(View.GONE);
                    break;
                default:
                    statusView.setTextColor(Color.BLACK);
                    appealButton.setVisibility(View.GONE);
                    break;
            }
            appealButton.setOnClickListener(v -> listener.onItemClick(item));
        }
    }

    //inflate request card with data
    @NonNull
    @Override
    public RequestStatusAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_notifaction_card, parent, false);
        return new ViewHolder(view);
    }

    //bind the data
    @Override
    public void onBindViewHolder(@NonNull RequestStatusAdapter.ViewHolder holder, int position) {
        holder.bind(items.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
