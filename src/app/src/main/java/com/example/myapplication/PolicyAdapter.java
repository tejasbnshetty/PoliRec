package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;


// the class basially fucntions as a maintener and amends based on policies depending on whether to hold them or display
public class PolicyAdapter extends RecyclerView.Adapter<PolicyAdapter.PolicyViewHolder> {

    private ArrayList<Policy> policyList;

    public PolicyAdapter(ArrayList<Policy> policyList) {
        this.policyList = policyList;
    }

    @Override
    public PolicyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_policy_card, parent, false);
        return new PolicyViewHolder(view);
    }// this creates a new policy adding view

    @Override
    public void onBindViewHolder(PolicyViewHolder holder, int position) {
        Policy policy = policyList.get(position);
        holder.policyNumberText.setText("Policy #" + policy.getPolicyNumber());
        holder.policyDescriptionText.setText(policy.getPolicyDescription());
    }// getting the data of policies and holding on to it

    @Override
    public int getItemCount() {
        return policyList.size();
    }
// returning policy count in total.
    public void updateList(ArrayList<Policy> filteredList) {
        this.policyList = filteredList;
        notifyDataSetChanged();
    }//this updates the list and notifies the data change

    static class PolicyViewHolder extends RecyclerView.ViewHolder {
        // this is the viewcreater and maintains the view for policies
        TextView policyNumberText, policyDescriptionText;
        CardView cardView;

        PolicyViewHolder(View itemView) {
            super(itemView);
            policyNumberText = itemView.findViewById(R.id.policyNumberText);
            policyDescriptionText = itemView.findViewById(R.id.policyDescriptionText);
            cardView = itemView.findViewById(R.id.policyCard);
        }
    }
}
