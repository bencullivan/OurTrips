package com.alsaeedcullivan.ourtrips.holders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alsaeedcullivan.ourtrips.R;

public class RecPlanHolder extends RecyclerView.ViewHolder {

    private TextView message;
    private TextView name;

    public RecPlanHolder(@NonNull View itemView) {
        super(itemView);
        message = itemView.findViewById(R.id.plan_received_message);
        name = itemView.findViewById(R.id.received_tripper_name);
    }

    // getters


    public TextView getMessage() {
        return message;
    }

    public TextView getName() {
        return name;
    }
}
