package com.alsaeedcullivan.ourtrips.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alsaeedcullivan.ourtrips.R;
import com.alsaeedcullivan.ourtrips.models.UserSummary;
import com.alsaeedcullivan.ourtrips.utils.Const;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class TripperAdapter extends ArrayAdapter<UserSummary> {
    private ArrayList<UserSummary> items;
    private Context context;


    public TripperAdapter(@NonNull Context context, int resource, ArrayList<UserSummary> items) {
        super(context, resource, items);
        this.context = context;
        this.items = items;
    }

    // getters //

    @Override
    public int getCount() {
        return super.getCount();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = Objects.requireNonNull(inflater).inflate(R.layout.tripper_item, parent, false);
        }

        // get reference to TextViews
        TextView name = convertView.findViewById(R.id.tripper_name);
        TextView email = convertView.findViewById(R.id.tripper_email);

        // fill TextView with appropriate data
        UserSummary user = items.get(position);
        // friend name
        name.setText(user.getName());
        // friend email
        email.setText(user.getEmail());

        Log.d(Const.TAG, "adapter getView: " + user.getName());
        Log.d(Const.TAG, "adapter getView: " + user.getEmail());

        // return view
        return convertView;
    }

    // adders //

    @Override
    public void add(@Nullable UserSummary object) {
        super.add(object);
        items.add(object);
    }

    @Override
    public void addAll(UserSummary... items) {
        super.addAll(items);
        this.items.addAll(Arrays.asList(items));
    }

    @Override
    public void clear() {
        super.clear();
        items.clear();
    }
}
