package com.alsaeedcullivan.ourtrips.holders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alsaeedcullivan.ourtrips.R;

public class SentPlanHolder extends RecyclerView.ViewHolder {

    private TextView message;

    public SentPlanHolder(@NonNull View itemView) {
        super(itemView);
        message = itemView.findViewById(R.id.plan_sent_message);
    }

    // getter

    public TextView getMessage() {
        return message;
    }
}
