package com.alsaeedcullivan.ourtrips.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alsaeedcullivan.ourtrips.R;
import com.alsaeedcullivan.ourtrips.holders.RecPlanHolder;
import com.alsaeedcullivan.ourtrips.holders.SentPlanHolder;
import com.alsaeedcullivan.ourtrips.models.Plan;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class PlanAdapter extends RecyclerView.Adapter {

    private ArrayList<Plan> mPlans;
    private String userId;

    public PlanAdapter(ArrayList<Plan> plans) {
        mPlans = plans;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) userId = "";
        else userId = user.getUid();
    }

    @Override
    public int getItemViewType(int position) {
        // if the plan was made by this user, return 1, if it was made by another user return 0
        // check to see who made the plan
        if (mPlans.get(position).getPlanUserId().equals(userId)) return 1;
        else return 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // if the plan was made by another user
        if (viewType == 0) return new RecPlanHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.received_plan, parent, false));
        // if the plan was made by this user
        return new SentPlanHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sent_plan, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (mPlans.get(position).getPlanUserId().equals(userId)) {
            ((SentPlanHolder) holder).getMessage().setText(mPlans.get(position).getMessage());
        } else {
            ((RecPlanHolder) holder).getMessage().setText(mPlans.get(position).getMessage());
            ((RecPlanHolder) holder).getName().setText(mPlans.get(position).getPlanUserName());
        }
    }

    @Override
    public int getItemCount() {
        return mPlans.size();
    }

    /**
     * setData()
     * sets the list of plans for this adapter
     * @param plans the list of plans
     */
    public void setData(ArrayList<Plan> plans) {
        mPlans.clear();
        mPlans.addAll(plans);
        notifyDataSetChanged();
    }
}
